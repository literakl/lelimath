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
import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.BadgeEvaluation;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.helpers.LeliMathApp;
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
                performEvaluation(searchedBadges, operators[i], user, badgesCount, awardProvider,
                        evaluationDao, playRecordDao);
            }

            log.debug("evaluate finished: {}", badgesCount);
            return badgesCount;
        } catch (SQLException e) {
            log.error("evaluate failed!", e);
            return new AwardedBadgesCount();
        }
    }

    private void performEvaluation(Badge[] bsgBadges, Operator operator, User user,
                                   AwardedBadgesCount badgesCount,
                                   BadgeAwardProvider awardProvider,
                                   Dao<BadgeEvaluation, Integer> evaluationDao,
                                   Dao<PlayRecord, Integer> playRecordDao) throws SQLException {
        log.debug("performEvaluation({})", operator);

        PlayRecord incorrectRecord = findLastIncorrectPlayRecord(operator, user, playRecordDao);
        int lastIncorrectId = (incorrectRecord != null) ? incorrectRecord.getId() : 0;
//        long correctCount = countCorrectSince(PLUS, lastIncorrectId, user, playRecordDao);

        String sql = "select count(*) from (select distinct first || second from play_record " +
                "where id > ? and user_id=? and operator=?)";
        GenericRawResults<String[]> results = evaluationDao.queryRaw(sql, Integer.toString(lastIncorrectId),
                user.getId().toString(), operator.value);
        int distinctCorrectCount = Integer.parseInt(results.getFirstResult()[0]);

        BadgeEvaluation bronzeEvaluation, silverEvaluation, goldEvaluation;

        boolean awardBronze = distinctCorrectCount >= 10;
        if (awardBronze) {
            bronzeEvaluation = queryLastEvaluation(bsgBadges[0], user, evaluationDao);
            if (! wasCurrentRowAwarded(bronzeEvaluation, lastIncorrectId)) {
                awardBadge(bsgBadges[0], 10, bronzeEvaluation, lastIncorrectId, user, awardProvider, evaluationDao);
                badgesCount.bronze++;
            }
            // intentionally not creating negative BadgeEvaluation as it does not bring any value
        }

        boolean awardSilver = distinctCorrectCount >= 25;
        if (awardSilver) {
            silverEvaluation = queryLastEvaluation(bsgBadges[1], user, evaluationDao);
           if (! wasCurrentRowAwarded(silverEvaluation, lastIncorrectId)) {
               awardBadge(bsgBadges[1], 25, silverEvaluation, lastIncorrectId, user, awardProvider, evaluationDao);
               badgesCount.silver++;
           }
        }

        boolean awardGold = distinctCorrectCount >= 100;
        if (awardGold) {
            goldEvaluation = queryLastEvaluation(bsgBadges[2], user, evaluationDao);
            if (! wasCurrentRowAwarded(goldEvaluation, lastIncorrectId)) {
                awardBadge(bsgBadges[2], 100, goldEvaluation, lastIncorrectId, user, awardProvider, evaluationDao);
                badgesCount.gold++;
            }
        }
    }

    private boolean wasCurrentRowAwarded(BadgeEvaluation evaluation, int lastIncorrectId) {
        return evaluation != null && evaluation.getLastAwardedId() > lastIncorrectId;
    }

    private void awardBadge(Badge badge, int count, BadgeEvaluation evaluation, int lastIncorrectId, User user,
                            BadgeAwardProvider awardProvider, Dao<BadgeEvaluation, Integer> evaluationDao) throws SQLException {
        if (evaluation == null) {
            evaluation = new BadgeEvaluation();
            evaluation.setUser(user);
            evaluation.setBadge(badge);
        }

        evaluation.setDate(new Date());
        evaluation.setProgress(count);
        evaluation.setLastAwardedId(lastIncorrectId + 1); // simple hack to make wasCurrentRowAwarded() work
        evaluation.setLastWrongId(null);
        evaluationDao.createOrUpdate(evaluation);

        BadgeAward award = createBadgeAward(badge, user);
        awardProvider.create(award);
        log.debug("Badge {} was awarded", badge);
    }

/*
    private long countCorrectSince(Operator operator, int lastIncorrectId, User user, Dao<PlayRecord, Integer> playRecordDao) throws SQLException {
        // select count(*) from play_record where id > coalesce((select id from play_record where correct = 0), 0)
        return playRecordDao.queryBuilder().where().gt(ID_COLUMN_NAME, lastIncorrectId).and()
                .eq(USER_COLUMN_NAME, user.getId()). and().eq(OPERATOR_COLUMN_NAME, operator.value)
                .countOf();
    }
*/

    private PlayRecord findLastIncorrectPlayRecord(Operator operator, User user, Dao<PlayRecord, Integer> playRecordDao) throws SQLException {
        QueryBuilder<PlayRecord, Integer> queryBuilder = playRecordDao.queryBuilder();
        queryBuilder.where().eq(USER_COLUMN_NAME, user.getId()).and().eq(CORRECT_COLUMN_NAME, false)
                .and().eq(OPERATOR_COLUMN_NAME, operator.value);
        queryBuilder.orderBy(ID_COLUMN_NAME, false).limit(1L);
        return queryBuilder.queryForFirst();
    }
}
