package lelisoft.com.lelimath.logic.badges;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.BadgeEvaluation;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.logic.BadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

import static lelisoft.com.lelimath.data.Badge.LONG_DISTANCE_RUNNER;
import static lelisoft.com.lelimath.data.Badge.MARATHON_RUNNER;
import static lelisoft.com.lelimath.data.Badge.RETURNER;
import static lelisoft.com.lelimath.data.BadgeEvaluation.BADGE_COLUMN_NAME;
import static lelisoft.com.lelimath.data.BadgeEvaluation.USER_COLUMN_NAME;

/**
 * Logic for three badges awarding user stamina.
 * Created by Leoš on 26.05.2016.
 */
public class StaminaBadgeEvaluator extends BadgeEvaluator {
    private static final Logger log = LoggerFactory.getLogger(StaminaBadgeEvaluator.class);

    @Override
    public AwardedBadgesCount evaluate(Map<Badge, BadgeAward> badges, Context context) {
        try {
            log.debug("evaluate starts");
            BadgeAwardProvider awardProvider = new BadgeAwardProvider(context);
            AwardedBadgesCount badgesCount = new AwardedBadgesCount();
            User user = LeliMathApp.getInstance().getCurrentUser();
            DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<BadgeEvaluation, Integer> evaluationDao = helper.getBadgeEvaluationDao();

            boolean bronzeAwarded = badges.containsKey(RETURNER);
            boolean silverAwarded = badges.containsKey(LONG_DISTANCE_RUNNER);
            boolean evaluateSilver = bronzeAwarded && ! silverAwarded;
            boolean evaluateGold = silverAwarded && ! badges.containsKey(MARATHON_RUNNER);

            BadgeEvaluation silverEvaluation = queryLast(LONG_DISTANCE_RUNNER, user, evaluationDao);
            if (evaluateSilver && silverEvaluation != null && silverEvaluation.getLastWrongDate() != null) {
                long diff = TimeUnit.DAYS.toDays(System.currentTimeMillis() - silverEvaluation.getLastWrongDate().getTime());
                if (diff < 7) {
                    evaluateSilver = evaluateGold = false;
                }
            }

            BadgeEvaluation goldEvaluation = queryLast(MARATHON_RUNNER, user, evaluationDao);
            if (evaluateGold && goldEvaluation != null && goldEvaluation.getLastWrongId() != null) {
                long diff = TimeUnit.DAYS.toDays(System.currentTimeMillis() - goldEvaluation.getLastWrongDate().getTime());
                if (diff < 30) {
                    evaluateGold = false;
                }
            }

            if (! bronzeAwarded) {
                BadgeEvaluation bronzeEvaluation = queryLast(RETURNER, user, evaluationDao);
                if (performEvaluation(RETURNER, 2, bronzeEvaluation, user, awardProvider, evaluationDao)) {
                    badgesCount.bronze++;
                }
            }

            if (evaluateSilver) {
                if (performEvaluation(LONG_DISTANCE_RUNNER, 7, silverEvaluation, user, awardProvider, evaluationDao)) {
                    badgesCount.silver++;
                }
            }

            if (evaluateGold) {
                if (performEvaluation(MARATHON_RUNNER, 30, goldEvaluation, user, awardProvider, evaluationDao)) {
                    badgesCount.gold++;
                }
            }

            log.debug("evaluate finished: {}", badgesCount);
            return badgesCount;
        } catch (SQLException e) {
            log.error("evaluate failed!", e);
            return new AwardedBadgesCount();
        }
    }

    private boolean performEvaluation(Badge badge, int count, BadgeEvaluation evaluation, User user,
                                   BadgeAwardProvider awardProvider, Dao<BadgeEvaluation, Integer> evaluationDao) throws SQLException {
        if (evaluation == null) {
            evaluation = new BadgeEvaluation();
            evaluation.setUser(user);
            evaluation.setBadge(badge);
        }
        evaluation.setDate(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(calendar.getTime());

        Calendar sinceCal = Calendar.getInstance();
        sinceCal.setTime(calendar.getTime());
        sinceCal.add(Calendar.DATE, 1 - count);
        log.debug("Starting date is {}", sinceCal.getTime());
        String since = format.format(sinceCal.getTime());

//        select strftime('%Y-%m-%d', date), count(*) from play_record where date > date('now','-8 day') group by strftime('%Y%m%d', date);
        String sql = "select strftime('%Y-%m-%d', date), count(*) from play_record where correct=1 " +
                "and date > ? group by strftime('%Y%m%d', date) order by 1 desc";
        GenericRawResults<String[]> results = evaluationDao.queryRaw(sql, since);

        String searchedDate = today;
        int processed = 0;
        for (String[] dayStats : results) {
            if (! searchedDate.equals(dayStats[0])) {
                // missing date
                evaluation.setProgress(processed);
                evaluation.setLastWrongDate(calendar.getTime());
                evaluationDao.createOrUpdate(evaluation);
                return false;
            }

            if (Integer.parseInt(dayStats[1]) < 10) {
                // low count
                // do not store current day as wrong, it can improve
                if (! dayStats[0].equals(today)) {
                    evaluation.setProgress(processed);
                    evaluation.setLastWrongDate(calendar.getTime());
                    evaluationDao.createOrUpdate(evaluation);
                }
                return false;
            }

            processed++;
            calendar.add(Calendar.DATE, -1);
            searchedDate = format.format(calendar.getTime());
        }

        if (processed == count) {
            evaluation.setProgress(processed);
            evaluation.setLastWrongDate(null);
            evaluationDao.createOrUpdate(evaluation);

            BadgeAward award = createBadgeAward(badge, user);
            awardProvider.create(award);
            log.debug("Badge {} was awarded", badge);
            return true;
        } else {
            // missing date
            evaluation.setProgress(processed);
            evaluation.setLastWrongDate(calendar.getTime());
            evaluationDao.createOrUpdate(evaluation);
            return false;
        }
    }

    private BadgeEvaluation queryLast(Badge badge, User user, Dao<BadgeEvaluation, Integer> dao) throws SQLException {
        QueryBuilder<BadgeEvaluation, Integer> builder = dao.queryBuilder();
        builder.where().eq(BADGE_COLUMN_NAME, badge.name()).and().eq(USER_COLUMN_NAME, user.getId());
        builder.orderBy(BadgeEvaluation.ID_COLUMN_NAME, true).limit(1L);
        return builder.queryForFirst();
    }
}
