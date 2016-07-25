package lelisoft.com.lelimath.logic.badges;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.BadgeEvaluation;
import lelisoft.com.lelimath.data.BadgeProgress;
import lelisoft.com.lelimath.logic.BadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

import static lelisoft.com.lelimath.data.Badge.LONG_DISTANCE_RUNNER;
import static lelisoft.com.lelimath.data.Badge.MARATHON_RUNNER;
import static lelisoft.com.lelimath.data.Badge.RETURNER;

/**
 * Logic for three badges awarding user stamina.
 * Created by Leoš on 26.05.2016.
 */
public class StaminaBadgeEvaluator extends BadgeEvaluator {
    private static final Logger log = LoggerFactory.getLogger(StaminaBadgeEvaluator.class);

    static final String sql = "select strftime('%Y-%m-%d', date), count(*) from play_record where correct=1 " +
            "and date > ? group by strftime('%Y%m%d', date) order by 1 desc";

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public AwardedBadgesCount evaluate(Map<Badge, List<BadgeAward>> badges, Context ctx) {
        try {
            log.debug("evaluate starts");
            BadgeAwardProvider awardProvider = new BadgeAwardProvider(ctx);
            AwardedBadgesCount badgesCount = new AwardedBadgesCount();
            DatabaseHelper helper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
            Dao<BadgeEvaluation, Integer> evaluationDao = helper.getBadgeEvaluationDao();

            boolean bronzeAwarded = badges.containsKey(RETURNER);
            boolean silverAwarded = badges.containsKey(LONG_DISTANCE_RUNNER);
            boolean evaluateSilver = bronzeAwarded && ! silverAwarded;
            boolean evaluateGold = silverAwarded && ! badges.containsKey(MARATHON_RUNNER);

            BadgeEvaluation silverEvaluation = queryLastEvaluation(LONG_DISTANCE_RUNNER, evaluationDao);
            if (evaluateSilver && silverEvaluation != null && silverEvaluation.getLastWrongDate() != null) {
                long diff = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - silverEvaluation.getLastWrongDate().getTime());
                if (diff < 7) {
                    evaluateSilver = evaluateGold = false;
                }
            }

            BadgeEvaluation goldEvaluation = queryLastEvaluation(MARATHON_RUNNER, evaluationDao);
            if (evaluateGold && goldEvaluation != null && goldEvaluation.getLastWrongId() != null) {
                long diff = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - goldEvaluation.getLastWrongDate().getTime());
                if (diff < 30) {
                    evaluateGold = false;
                }
            }

            if (! bronzeAwarded) {
                BadgeEvaluation bronzeEvaluation = queryLastEvaluation(RETURNER, evaluationDao);
                if (performEvaluation(RETURNER, 2, bronzeEvaluation, awardProvider, evaluationDao, ctx)) {
                    badgesCount.bronze++;
                }
            }

            if (evaluateSilver) {
                if (performEvaluation(LONG_DISTANCE_RUNNER, 7, silverEvaluation, awardProvider, evaluationDao, ctx)) {
                    badgesCount.silver++;
                }
            }

            if (evaluateGold) {
                if (performEvaluation(MARATHON_RUNNER, 30, goldEvaluation, awardProvider, evaluationDao, ctx)) {
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

    @Override
    public BadgeProgress calculateProgress(Badge badge, Context ctx) {
        try {
            log.debug("calculateProgress({}) starts", badge);
            switch (badge) {
                case RETURNER:
                    return calculateProgress(badge, 2, ctx);
                case LONG_DISTANCE_RUNNER:
                    return calculateProgress(badge, 7, ctx);
                case MARATHON_RUNNER:
                    return calculateProgress(badge, 30, ctx);
            }
            throw new RuntimeException("Unhandled badge " + badge);
        } catch (SQLException e) {
            log.error("calculateProgress failed!", e);
            return null;
        }
    }

    private boolean performEvaluation(Badge badge, int count, BadgeEvaluation evaluation,
                                      BadgeAwardProvider awardProvider, Dao<BadgeEvaluation, Integer> evaluationDao,
                                      Context ctx) throws SQLException {
        if (evaluation == null) {
            evaluation = new BadgeEvaluation();
            evaluation.setBadge(badge);
        }
        evaluation.setDate(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String today = format.format(calendar.getTime());

        Calendar sinceCal = Calendar.getInstance();
        sinceCal.setTime(calendar.getTime());
        sinceCal.add(Calendar.DATE, 1 - count);
        log.debug("Starting date is {}", sinceCal.getTime());
        String since = format.format(sinceCal.getTime());

//        select strftime('%Y-%m-%d', date), count(*) from play_record where date > date('now','-8 day') group by strftime('%Y%m%d', date);
        String searchedDate = today;
        int processed = 0;
        GenericRawResults<String[]> results = evaluationDao.queryRaw(sql, since);
        for (String[] dayStats : results) {
            if (! searchedDate.equals(dayStats[0])) {
                // missing date
                evaluation.setLastWrongDate(calendar.getTime());
                evaluationDao.createOrUpdate(evaluation);
                return false;
            }

            if (Integer.parseInt(dayStats[1]) < 10) {
                // low count
                // do not store current day as wrong, it can improve
                if (! dayStats[0].equals(today)) {
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
            evaluation.setLastWrongDate(null);
            evaluationDao.createOrUpdate(evaluation);
            BadgeAward award = createBadgeAward(badge);
            awardProvider.create(award);
            saveBadgeProgress(badge, false, 0, 0, ctx);
            log.debug("Badge {} was awarded", badge);
            return true;
        } else {
            // missing date
            evaluation.setLastWrongDate(calendar.getTime());
            evaluationDao.createOrUpdate(evaluation);
            return false;
        }
    }

    private BadgeProgress calculateProgress(Badge badge, int required, Context ctx) throws SQLException {
        BadgeAwardProvider awardProvider = new BadgeAwardProvider(ctx);
        DatabaseHelper helper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
        Dao<BadgeEvaluation, Integer> evaluationDao = helper.getBadgeEvaluationDao();

        List<BadgeAward> awards = awardProvider.getAwards(badge);
        if (! awards.isEmpty()) {
            // these badges are one time
            BadgeProgress progress = saveBadgeProgress(badge, false, 0, 0, ctx);
            log.debug("calculateProgress({}) finished", badge);
            return progress;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String today = format.format(calendar.getTime());

        Calendar sinceCal = Calendar.getInstance();
        sinceCal.setTime(calendar.getTime());
        sinceCal.add(Calendar.DATE, 1 - required);
        log.debug("Starting date is {}", sinceCal.getTime());
        String since = format.format(sinceCal.getTime());

        int processed = 0;
        String searchedDate = today;
        GenericRawResults<String[]> results = evaluationDao.queryRaw(sql, since);
        for (String[] dayStats : results) {
            if (! searchedDate.equals(dayStats[0])) {
                // missing date
                break;
            }

            if (Integer.parseInt(dayStats[1]) < 10) {
                // low count
                break;
            }

            processed++;
            calendar.add(Calendar.DATE, -1);
            searchedDate = format.format(calendar.getTime());
        }

        BadgeProgress progress = saveBadgeProgress(badge, true, processed, required, ctx);
        log.debug("calculateProgress({}) finished", badge);
        return progress;
    }
}
