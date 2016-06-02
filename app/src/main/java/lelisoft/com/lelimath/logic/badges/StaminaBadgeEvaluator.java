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
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.logic.BadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import lelisoft.com.lelimath.provider.PlayRecordProvider;
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
            PlayRecordProvider playRecordProvider = new PlayRecordProvider(context);
            AwardedBadgesCount badgesCount = new AwardedBadgesCount();
            User user = LeliMathApp.getInstance().getCurrentUser();
            DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<BadgeEvaluation, Integer> evaluationDao = helper.getBadgeEvaluationDao();

            boolean bronzeAwarded = badges.containsKey(RETURNER);
            boolean silverAwarded = badges.containsKey(LONG_DISTANCE_RUNNER);
            boolean evaluateSilver = bronzeAwarded && ! silverAwarded;
            boolean evaluateGold = silverAwarded && ! badges.containsKey(MARATHON_RUNNER);

            BadgeEvaluation silverEvaluation = queryLast(LONG_DISTANCE_RUNNER, user, evaluationDao);
            if (evaluateSilver && silverEvaluation != null && silverEvaluation.getLastWrongId() != null) {
                PlayRecord wrong = playRecordProvider.getById(silverEvaluation.getLastWrongId());
                long diff = TimeUnit.DAYS.toDays(System.currentTimeMillis() - wrong.getDate().getTime());
                if (diff < 25) {
                    evaluateSilver = evaluateGold = false;
                }
            }

            BadgeEvaluation goldEvaluation = queryLast(MARATHON_RUNNER, user, evaluationDao);
            if (evaluateGold && goldEvaluation != null && goldEvaluation.getLastWrongId() != null) {
                PlayRecord wrong = playRecordProvider.getById(goldEvaluation.getLastWrongId());
                long diff = TimeUnit.DAYS.toDays(System.currentTimeMillis() - wrong.getDate().getTime());
                if (diff < 100) {
                    evaluateGold = false;
                }
            }


            Calendar calendar = Calendar.getInstance();
            calendar.clear(Calendar.HOUR);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
            String today = format.format(calendar.getTime());

            if (! bronzeAwarded) {
                calendar.add(Calendar.DATE, -1);
                log.debug("Date is {}", calendar);
                String sql = "select strftime('%Y-%m-%d', date), count(*) from play_record where correct=1 " +
                        "and date > ? group by strftime('%Y%m%d', date) order by 1 desc";
                GenericRawResults<String[]> results = evaluationDao.queryRaw(sql, format.format(calendar.getTime()));
                String searchedDate = today;
                int processed = 0;
                for (String[] dayStats : results) {
                    if (! searchedDate.equals(dayStats[0])) {
                        BadgeEvaluation evaluation = new BadgeEvaluation();
                        evaluation.setDate(new Date());
                        evaluation.setUser(user);
                        evaluation.setBadge(Badge.RETURNER);
                        evaluation.setLastWrongDate(calendar.getTime());
                        // missing date
                        break;
                    }
                    if (Integer.parseInt(dayStats[1]) < 10) {
                        // low count
                        // do not store current day as wrong, it can improve
                        break;
                    }

                    processed++;
                    calendar.add(Calendar.DATE, -1);
                    searchedDate = format.format(calendar.getTime());
                }

                if (processed == 2) {
                    BadgeAward award = createBadgeAward(Badge.RETURNER, user);
                    awardProvider.create(award);
                    badgesCount.bronze++;
                    log.debug("Badge {} was awarded", Badge.RETURNER);
                }
            }

            if (evaluateSilver) {
                calendar.add(Calendar.DATE, -24);
                log.debug("Date is {}", calendar);

//                BadgeAward award = createBadgeAward(Badge.LONG_DISTANCE_RUNNER, user);
//                awardProvider.create(award);
//                badgesCount.silver++;
//                log.debug("Badge {} was awarded", Badge.LONG_DISTANCE_RUNNER);
            }

            if (evaluateGold) {
                calendar.add(Calendar.DATE, -99);
                log.debug("Date is {}", calendar);

//                BadgeAward award = createBadgeAward(Badge.MARATHON_RUNNER, user);
//                awardProvider.create(award);
//                badgesCount.gold++;
//                log.debug("Badge {} was awarded", Badge.MARATHON_RUNNER);
            }

//        select strftime('%Y-%m-%d', date), count(*) from play_record where date > date('now','-8 day') group by strftime('%Y%m%d', date);

            log.debug("evaluate finished: {}", badgesCount);
            return badgesCount;
        } catch (SQLException e) {
            log.error("Evaluate failed!", e);
            return new AwardedBadgesCount();
        }
    }

    private BadgeEvaluation queryLast(Badge badge, User user, Dao<BadgeEvaluation, Integer> dao) throws SQLException {
        QueryBuilder<BadgeEvaluation, Integer> builder = dao.queryBuilder();
        builder.where().eq(BADGE_COLUMN_NAME, badge.name()).and().eq(USER_COLUMN_NAME, user.getId());
        builder.orderBy(BadgeEvaluation.ID_COLUMN_NAME, true).limit(1L);
        return builder.queryForFirst();
    }
}
