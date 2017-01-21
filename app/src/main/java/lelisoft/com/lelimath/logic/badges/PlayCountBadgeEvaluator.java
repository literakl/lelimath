package lelisoft.com.lelimath.logic.badges;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.BadgeProgress;
import lelisoft.com.lelimath.data.Columns;
import lelisoft.com.lelimath.data.Play;
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
    public AwardedBadgesCount evaluate(Context context) {
        log.debug("evaluate starts");
        Map<Badge, List<BadgeAward>> badges = LeliMathApp.getInstance().getBadges();
        if (badges.get(Badge.PALADIN) != null && badges.get(Badge.SAMURAI) != null) {
            // already received maximum
            return new AwardedBadgesCount();
        }

        PlayProvider playProvider = new PlayProvider(context);
        BadgeAwardProvider awardProvider = new BadgeAwardProvider(context);
        AwardedBadgesCount badgesCount = new AwardedBadgesCount();

        try {
            QueryBuilder<Play, Integer> queryBuilder = setPlayConditions(playProvider, true);
            long count = queryBuilder.countOf();
            if (count > 0 && badges.get(Badge.PAGE) == null) {
                BadgeAward award = createBadgeAward(Badge.PAGE);
                queryBuilder = setPlayConditions(playProvider, true);
                queryBuilder.orderBy(Columns.ID, true);
                setPlayId(award, queryBuilder.queryForFirst());
                awardProvider.create(award);
                LeliMathApp.getInstance().addBadgeAward(award);
                saveBadgeProgress(Badge.PAGE, false, 0, 0, context);
                badgesCount.bronze++;
                log.debug("Badge {} was awarded", Badge.PAGE);
            }

            if (count >= 25 && badges.get(Badge.KNIGHT) == null) {
                BadgeAward award = createBadgeAward(Badge.KNIGHT);
                queryBuilder = setPlayConditions(playProvider, true);
                queryBuilder.orderBy(Columns.ID, true).limit(25L);
                setPlayIds(award, queryBuilder.query());
                awardProvider.create(award);
                LeliMathApp.getInstance().addBadgeAward(award);
                saveBadgeProgress(Badge.KNIGHT, false, 0, 0, context);
                badgesCount.silver++;
                log.debug("Badge {} was awarded", Badge.KNIGHT);
            }

            if (count >= 100 && badges.get(Badge.PALADIN) == null) {
                BadgeAward award = createBadgeAward(Badge.PALADIN);
                queryBuilder = setPlayConditions(playProvider, true);
                queryBuilder.orderBy(Columns.ID, true).limit(100L);
                setPlayIds(award, queryBuilder.query());
                awardProvider.create(award);
                LeliMathApp.getInstance().addBadgeAward(award);
                saveBadgeProgress(Badge.PALADIN, false, 0, 0, context);
                badgesCount.gold++;
                log.debug("Badge {} was awarded", Badge.PALADIN);
            }

            queryBuilder = setPlayConditions(playProvider, false);
            count = queryBuilder.countOf();
            if (count > 0 && badges.get(Badge.GLADIATOR) == null) {
                BadgeAward award = createBadgeAward(Badge.GLADIATOR);
                queryBuilder = setPlayConditions(playProvider, false);
                queryBuilder.orderBy(Columns.ID, true);
                setPlayId(award, queryBuilder.queryForFirst());
                awardProvider.create(award);
                LeliMathApp.getInstance().addBadgeAward(award);
                saveBadgeProgress(Badge.GLADIATOR, false, 0, 0, context);
                badgesCount.bronze++;
                log.debug("Badge {} was awarded", Badge.GLADIATOR);
            }

            if (count >= 25 && badges.get(Badge.VIKING) == null) {
                BadgeAward award = createBadgeAward(Badge.VIKING);
                queryBuilder = setPlayConditions(playProvider, false);
                queryBuilder.orderBy(Columns.ID, true).limit(25L);
                setPlayIds(award, queryBuilder.query());
                awardProvider.create(award);
                LeliMathApp.getInstance().addBadgeAward(award);
                saveBadgeProgress(Badge.VIKING, false, 0, 0, context);
                badgesCount.silver++;
                log.debug("Badge {} was awarded", Badge.VIKING);
            }

            if (count >= 100 && badges.get(Badge.SAMURAI) == null) {
                BadgeAward award = createBadgeAward(Badge.SAMURAI);
                queryBuilder = setPlayConditions(playProvider, false);
                queryBuilder.orderBy(Columns.ID, true).limit(100L);
                setPlayIds(award, queryBuilder.query());
                awardProvider.create(award);
                LeliMathApp.getInstance().addBadgeAward(award);
                saveBadgeProgress(Badge.SAMURAI, false, 0, 0, context);
                badgesCount.gold++;
                log.debug("Badge {} was awarded", Badge.SAMURAI);
            }

            log.debug("evaluate finished: {}", badgesCount);
            return badgesCount;
        } catch (SQLException e) {
            log.error("Evaluation failed", e);
            Crashlytics.logException(e);
            return new AwardedBadgesCount();
        }
    }

    @Override
    public BadgeProgress calculateProgress(Badge badge, Context ctx) {
        try {
            log.debug("calculateProgress({}) starts", badge);
            switch (badge) {
                case PAGE:
                    return calculateProgress(badge, 1, true, ctx);
                case KNIGHT:
                    return calculateProgress(badge, 25, true, ctx);
                case PALADIN:
                    return calculateProgress(badge, 100, true, ctx);
                case GLADIATOR:
                    return calculateProgress(badge, 1, false, ctx);
                case VIKING:
                    return calculateProgress(badge, 25, false, ctx);
                case SAMURAI:
                    return calculateProgress(badge, 100, false, ctx);
            }
            throw new RuntimeException("Unhandled badge " + badge);
        } catch (SQLException e) {
            log.error("calculateProgress failed!", e);
            Crashlytics.logException(e);
            return null;
        }
    }

    private BadgeProgress calculateProgress(Badge badge, int required, boolean easy, Context ctx) throws SQLException {
        PlayProvider playProvider = new PlayProvider(ctx);
        BadgeAwardProvider awardProvider = new BadgeAwardProvider(ctx);

        List<BadgeAward> awards = awardProvider.getAwards(badge);
        if (! awards.isEmpty()) {
            // these badges are one time
            BadgeProgress progress = saveBadgeProgress(badge, false, 0, 0, ctx);
            log.debug("calculateProgress({}) finished", badge);
            return progress;
        }

        QueryBuilder<Play, Integer> queryBuilder = setPlayConditions(playProvider, easy);
        long count = queryBuilder.countOf();
        BadgeProgress progress = saveBadgeProgress(badge, true, (int) count, required, ctx);
        log.debug("calculateProgress({}) finished", badge);
        return progress;
    }

    private QueryBuilder<Play, Integer> setPlayConditions(PlayProvider provider, boolean easy) throws SQLException {
        QueryBuilder<Play, Integer> builder = provider.queryBuilder();
        Where<Play, Integer> where = builder.where();
        if (easy) {
            where.le(Columns.LEVEL, 6);
        } else {
            where.ge(Columns.LEVEL, 9);
        }
        where.and().eq(Columns.FINISHED, true);
        return builder;
    }
}
