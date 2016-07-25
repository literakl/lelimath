package lelisoft.com.lelimath.logic.badges;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.BadgeEvaluation;
import lelisoft.com.lelimath.data.BadgeProgress;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.logic.BadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

import static lelisoft.com.lelimath.data.Badge.EXCELLENT_DIVISION;
import static lelisoft.com.lelimath.data.Badge.EXCELLENT_MULTIPLICATION;
import static lelisoft.com.lelimath.data.Badge.EXCELLENT_SUBTRACTION;
import static lelisoft.com.lelimath.data.Badge.EXCELLENT_SUMMATION;
import static lelisoft.com.lelimath.data.Badge.GOOD_DIVISION;
import static lelisoft.com.lelimath.data.Badge.GOOD_MULTIPLICATION;
import static lelisoft.com.lelimath.data.Badge.GOOD_SUBTRACTION;
import static lelisoft.com.lelimath.data.Badge.GOOD_SUMMATION;
import static lelisoft.com.lelimath.data.Badge.GREAT_DIVISION;
import static lelisoft.com.lelimath.data.Badge.GREAT_MULTIPLICATION;
import static lelisoft.com.lelimath.data.Badge.GREAT_SUBTRACTION;
import static lelisoft.com.lelimath.data.Badge.GREAT_SUMMATION;
import static lelisoft.com.lelimath.data.Operator.*;
import static lelisoft.com.lelimath.data.PlayRecord.*;

/**
 * Logic for badges awarding faultlessness.
 * Created by Leoš on 04.06.2016.
 */
public class CorrectnessBadgeEvaluator extends BadgeEvaluator {
    private static final Logger log = LoggerFactory.getLogger(CorrectnessBadgeEvaluator.class);

    static final String sql = "select count(*) from (select distinct first || second from play_record " +
            "where id > ? and operator=?)";

    @Override
    public AwardedBadgesCount evaluate(Map<Badge, List<BadgeAward>> badges, Context context) {
        try {
            log.debug("evaluate starts");
            BadgeAwardProvider awardProvider = new BadgeAwardProvider(context);
            AwardedBadgesCount badgesCount = new AwardedBadgesCount();
            DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<BadgeEvaluation, Integer> evaluationDao = helper.getBadgeEvaluationDao();
            Dao<PlayRecord, Integer> playRecordDao = helper.getPlayRecordDao();

            Operator[] operators = new Operator[] {PLUS, MINUS, MULTIPLY, DIVIDE};
            Badge[][] bsgBadges = new Badge[][] {
                    {GOOD_SUMMATION, GREAT_SUMMATION, EXCELLENT_SUMMATION},
                    {GOOD_SUBTRACTION, GREAT_SUBTRACTION, EXCELLENT_SUBTRACTION},
                    {GOOD_MULTIPLICATION, GREAT_MULTIPLICATION, EXCELLENT_MULTIPLICATION},
                    {GOOD_DIVISION, GREAT_DIVISION, EXCELLENT_DIVISION}
            };

            for (int i = 0; i < bsgBadges.length; i++) {
                Badge[] searchedBadges = bsgBadges[i];
                performEvaluation(searchedBadges, operators[i], badgesCount, awardProvider,
                        evaluationDao, playRecordDao, context);
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
                case GOOD_SUMMATION:
                    return calculateProgress(badge, Operator.PLUS, 10, ctx);
                case GREAT_SUMMATION:
                    return calculateProgress(badge, Operator.PLUS, 25, ctx);
                case EXCELLENT_SUMMATION:
                    return calculateProgress(badge, Operator.PLUS, 100, ctx);
                case GOOD_SUBTRACTION:
                    return calculateProgress(badge, Operator.MINUS, 10, ctx);
                case GREAT_SUBTRACTION:
                    return calculateProgress(badge, Operator.MINUS, 25, ctx);
                case EXCELLENT_SUBTRACTION:
                    return calculateProgress(badge, Operator.MINUS, 100, ctx);
                case GOOD_MULTIPLICATION:
                    return calculateProgress(badge, Operator.MULTIPLY, 10, ctx);
                case GREAT_MULTIPLICATION:
                    return calculateProgress(badge, Operator.MULTIPLY, 25, ctx);
                case EXCELLENT_MULTIPLICATION:
                    return calculateProgress(badge, Operator.MULTIPLY, 100, ctx);
                case GOOD_DIVISION:
                    return calculateProgress(badge, Operator.DIVIDE, 10, ctx);
                case GREAT_DIVISION:
                    return calculateProgress(badge, Operator.DIVIDE, 25, ctx);
                case EXCELLENT_DIVISION:
                    return calculateProgress(badge, Operator.DIVIDE, 100, ctx);
            }
            throw new RuntimeException("Unhandled badge " + badge);
        } catch (SQLException e) {
            log.error("calculateProgress failed!", e);
            return null;
        }
    }

    private void performEvaluation(Badge[] bsgBadges, Operator operator,
                                   AwardedBadgesCount badgesCount,
                                   BadgeAwardProvider awardProvider,
                                   Dao<BadgeEvaluation, Integer> evaluationDao,
                                   Dao<PlayRecord, Integer> playRecordDao, Context ctx) throws SQLException {
        log.debug("performEvaluation({})", operator);

        PlayRecord incorrectRecord = findLastIncorrectPlayRecord(operator, playRecordDao);
        int lastIncorrectId = (incorrectRecord != null) ? incorrectRecord.getId() : 0;
        GenericRawResults<String[]> results = evaluationDao.queryRaw(sql, Integer.toString(lastIncorrectId), operator.value);
        int distinctCorrectCount = Integer.parseInt(results.getFirstResult()[0]);

        BadgeEvaluation bronzeEvaluation, silverEvaluation, goldEvaluation;

        boolean awardBronze = distinctCorrectCount >= 10;
        if (awardBronze) {
            bronzeEvaluation = queryLastEvaluation(bsgBadges[0], evaluationDao);
            if (! wasCurrentRowAwarded(bronzeEvaluation, lastIncorrectId)) {
                awardBadge(bsgBadges[0], bronzeEvaluation, lastIncorrectId, awardProvider, evaluationDao);
                setProgressAfterBadge(bsgBadges[0], distinctCorrectCount - 10, ctx);
                badgesCount.bronze++;
            }
            // intentionally not creating negative BadgeEvaluation as it does not bring any value
        }

        boolean awardSilver = distinctCorrectCount >= 25;
        if (awardSilver) {
            silverEvaluation = queryLastEvaluation(bsgBadges[1], evaluationDao);
            if (! wasCurrentRowAwarded(silverEvaluation, lastIncorrectId)) {
               awardBadge(bsgBadges[1], silverEvaluation, lastIncorrectId, awardProvider, evaluationDao);
               setProgressAfterBadge(bsgBadges[1], distinctCorrectCount - 25, ctx);
               badgesCount.silver++;
            }
        }

        boolean awardGold = distinctCorrectCount >= 100;
        if (awardGold) {
            goldEvaluation = queryLastEvaluation(bsgBadges[2], evaluationDao);
            if (! wasCurrentRowAwarded(goldEvaluation, lastIncorrectId)) {
                awardBadge(bsgBadges[2], goldEvaluation, lastIncorrectId, awardProvider, evaluationDao);
                setProgressAfterBadge(bsgBadges[2], distinctCorrectCount - 100, ctx);
                badgesCount.gold++;
            }
        }
    }

    private BadgeProgress calculateProgress(Badge badge, Operator operator, int required, Context ctx) throws SQLException {
        DatabaseHelper helper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
        Dao<BadgeEvaluation, Integer> evaluationDao = helper.getBadgeEvaluationDao();
        Dao<PlayRecord, Integer> playRecordDao = helper.getPlayRecordDao();

        BadgeEvaluation evaluation = queryLastEvaluation(badge, evaluationDao);
        PlayRecord incorrectRecord = findLastIncorrectPlayRecord(operator, playRecordDao);
        int index = (incorrectRecord != null) ? incorrectRecord.getId() : 0;
        if (evaluation != null && evaluation.getLastAwardedId() > index) {
            // already awarded but strike has not been broken => waiting for mistake to restart
            log.debug("calculateProgress({}) finished", badge);
            return null;
        }

        GenericRawResults<String[]> results = evaluationDao.queryRaw(sql, Integer.toString(index), operator.value);
        int count = Integer.parseInt(results.getFirstResult()[0]);
        BadgeProgress progress = saveBadgeProgress(badge, true, count, required, ctx);
        log.debug("calculateProgress({}) finished", badge);
        return progress;
    }

    private boolean wasCurrentRowAwarded(BadgeEvaluation evaluation, int lastIncorrectId) {
        return evaluation != null && evaluation.getLastAwardedId() > lastIncorrectId;
    }

    private void awardBadge(Badge badge, BadgeEvaluation evaluation, int lastIncorrectId,
                            BadgeAwardProvider awardProvider, Dao<BadgeEvaluation, Integer> evaluationDao) throws SQLException {
        if (evaluation == null) {
            evaluation = new BadgeEvaluation();
            evaluation.setBadge(badge);
        }

        evaluation.setDate(new Date());
        evaluation.setLastAwardedId(lastIncorrectId + 1); // simple hack to make wasCurrentRowAwarded() work
        evaluation.setLastWrongId(null);
        evaluationDao.createOrUpdate(evaluation);

        BadgeAward award = createBadgeAward(badge);
        awardProvider.create(award);
        log.debug("Badge {} was awarded", badge);
    }

    /**
     * Stores a progress after awarding a badge. If there were more correct formulas than required
     * a badge progress in running state is created.
     */
    private void setProgressAfterBadge(Badge badge, int oversize, Context ctx) {
        if (oversize == 0) {
            saveBadgeProgress(badge, false, 0, 0, ctx);
//        } else {
//             we would have to detect if the row was broken with a mistake since this badge was awarded
        }
    }

/*
    private long countCorrectSince(Operator operator, int lastIncorrectId, User user, Dao<PlayRecord, Integer> playRecordDao) throws SQLException {
        // select count(*) from play_record where id > coalesce((select id from play_record where correct = 0), 0)
        return playRecordDao.queryBuilder().where().gt(ID_COLUMN_NAME, lastIncorrectId).and()
                .eq(USER_COLUMN_NAME, user.getId()). and().eq(OPERATOR_COLUMN_NAME, operator.value)
                .countOf();
    }
*/

    private PlayRecord findLastIncorrectPlayRecord(Operator operator, Dao<PlayRecord, Integer> playRecordDao) throws SQLException {
        QueryBuilder<PlayRecord, Integer> queryBuilder = playRecordDao.queryBuilder();
        queryBuilder.where().eq(CORRECT_COLUMN_NAME, false).and().eq(OPERATOR_COLUMN_NAME, operator.value);
        queryBuilder.orderBy(ID_COLUMN_NAME, false).limit(1L);
        return queryBuilder.queryForFirst();
    }
}
