package lelisoft.com.lelimath.logic.badges;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.logic.BadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.PlayProvider;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

/**
 * Awarding logic for six play count based badges
 * Created by Leoš on 19.05.2016.
 */
public class PlayCountBadgeEvaluator extends BadgeEvaluator {
    private static final Logger log = LoggerFactory.getLogger(PlayCountBadgeEvaluator.class);

    @Override
    public AwardedBadgesCount evaluate(Map<Badge, BadgeAward> badges, Context context) {
        log.debug("evaluate starts");
        if (badges.get(Badge.PALADIN) != null) {
            // already received maximum
            return new AwardedBadgesCount();
        }

        PlayProvider playProvider = new PlayProvider(context);
        BadgeAwardProvider awardProvider = new BadgeAwardProvider(context);
        AwardedBadgesCount badgesCount = new AwardedBadgesCount();
        User user = LeliMathApp.getInstance().getCurrentUser();

        try {
            QueryBuilder<Play, Integer> queryBuilder = setPlayConditions(playProvider, true);
            long count = queryBuilder.countOf();
            if (count > 0 && badges.get(Badge.PAGE) == null) {
                BadgeAward award = createBadgeAward(Badge.PAGE, user);
                queryBuilder = setPlayConditions(playProvider, true);
                queryBuilder.orderBy(Play.ID_COLUMN_NAME, true);
                setPlayId(award, queryBuilder.queryForFirst());
                awardProvider.create(award);
                badgesCount.bronze++;
                log.debug("Badge {} was awarded", Badge.PAGE);
            }

            if (count >= 25 && badges.get(Badge.KNIGHT) == null) {
                BadgeAward award = createBadgeAward(Badge.KNIGHT, user);
                queryBuilder = setPlayConditions(playProvider, true);
                queryBuilder.orderBy(Play.ID_COLUMN_NAME, true).limit(25L);
                setPlayIds(award, queryBuilder.query());
                awardProvider.create(award);
                badgesCount.silver++;
                log.debug("Badge {} was awarded", Badge.KNIGHT);
            }

            if (count >= 100 && badges.get(Badge.PALADIN) == null) {
                BadgeAward award = createBadgeAward(Badge.PALADIN, user);
                queryBuilder = setPlayConditions(playProvider, true);
                queryBuilder.orderBy(Play.ID_COLUMN_NAME, true).limit(100L);
                setPlayIds(award, queryBuilder.query());
                awardProvider.create(award);
                badgesCount.gold++;
                log.debug("Badge {} was awarded", Badge.PALADIN);
            }

            queryBuilder = setPlayConditions(playProvider, false);
            count = queryBuilder.countOf();
            if (count > 0 && badges.get(Badge.GLADIATOR) == null) {
                BadgeAward award = createBadgeAward(Badge.GLADIATOR, user);
                queryBuilder = setPlayConditions(playProvider, false);
                queryBuilder.orderBy(Play.ID_COLUMN_NAME, true);
                setPlayId(award, queryBuilder.queryForFirst());
                awardProvider.create(award);
                badgesCount.bronze++;
                log.debug("Badge {} was awarded", Badge.GLADIATOR);
            }

            if (count >= 25 && badges.get(Badge.VIKING) == null) {
                BadgeAward award = createBadgeAward(Badge.VIKING, user);
                queryBuilder = setPlayConditions(playProvider, false);
                queryBuilder.orderBy(Play.ID_COLUMN_NAME, true).limit(25L);
                setPlayIds(award, queryBuilder.query());
                awardProvider.create(award);
                badgesCount.silver++;
                log.debug("Badge {} was awarded", Badge.VIKING);
            }

            if (count >= 100 && badges.get(Badge.SAMURAI) == null) {
                BadgeAward award = createBadgeAward(Badge.SAMURAI, user);
                queryBuilder = setPlayConditions(playProvider, false);
                queryBuilder.orderBy(Play.ID_COLUMN_NAME, true).limit(100L);
                setPlayIds(award, queryBuilder.query());
                awardProvider.create(award);
                badgesCount.gold++;
                log.debug("Badge {} was awarded", Badge.SAMURAI);
            }

            log.debug("evaluate finished: {}", badgesCount);
            return badgesCount;
        } catch (SQLException e) {
            log.error("Evaluation failed", e);
            return new AwardedBadgesCount();
        }
    }

    public QueryBuilder<Play, Integer> setPlayConditions(PlayProvider provider, boolean easy) throws SQLException {
        QueryBuilder<Play, Integer> builder = provider.queryBuilder();
        Where<Play, Integer> where = builder.where();
        if (easy) {
            where.le(Play.LEVEL_COLUMN_NAME, 6);
        } else {
            where.ge(Play.LEVEL_COLUMN_NAME, 9);
        }
        where.and().eq(Play.FINISHED_COLUMN_NAME, true);
        return builder;
    }
}