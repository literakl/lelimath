package lelisoft.com.lelimath.logic.badges;

import android.content.Context;
import android.support.annotation.NonNull;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.logic.BadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.PlayProvider;

/**
 * Awarding logic for
 * Created by Leoš on 19.05.2016.
 */
public class PlayCountBadgeEvaluator implements BadgeEvaluator {
    private static final Logger log = LoggerFactory.getLogger(PlayCountBadgeEvaluator.class);

    @Override
    public AwardedBadgesCount evaluate(Map<Badge, BadgeAward> badges, Context context) {
        log.debug("evaluate");
        if (badges.get(Badge.PALADIN) != null) {
            // already received maximum
            return new AwardedBadgesCount();
        }

        PlayProvider playProvider = new PlayProvider(context);
        BadgeAwardProvider awardProvider = new BadgeAwardProvider(context);
        AwardedBadgesCount badgesCount = new AwardedBadgesCount();
        User user = LeliMathApp.getInstance().getCurrentUser();

        try {
            QueryBuilder<Play, Integer> queryBuilder = playProvider.queryBuilder();
            Where<Play, Integer> where = queryBuilder.where().le(Play.LEVEL_COLUMN_NAME, 6).and().eq(Play.FINISHED_COLUMN_NAME, true);
            log.debug("where = {}", where);
            long count = where.countOf();
            if (count > 0 && badges.get(Badge.PAGE) == null) {
                BadgeAward award = createBadgeAward(Badge.PAGE, user);
                awardProvider.create(award);
                badgesCount.bronze++;
                log.debug("Badge {} was awarded", Badge.PAGE);
            }

            if (count >= 25 && badges.get(Badge.KNIGHT) == null) {
                BadgeAward award = createBadgeAward(Badge.KNIGHT, user);
                awardProvider.create(award);
                badgesCount.silver++;
                log.debug("Badge {} was awarded", Badge.KNIGHT);
            }

            if (count >= 100 && badges.get(Badge.PALADIN) == null) {
                BadgeAward award = createBadgeAward(Badge.PAGE, user);
                awardProvider.create(award);
                badgesCount.gold++;
                log.debug("Badge {} was awarded", Badge.PALADIN);
            }

            count = queryBuilder.where().ge(Play.LEVEL_COLUMN_NAME, 9).and().eq(Play.FINISHED_COLUMN_NAME, true).countOf();
            if (count > 0 && badges.get(Badge.GLADIATOR) == null) {
                BadgeAward award = createBadgeAward(Badge.GLADIATOR, user);
                awardProvider.create(award);
                badgesCount.bronze++;
                log.debug("Badge {} was awarded", Badge.GLADIATOR);
            }

            if (count >= 25 && badges.get(Badge.VIKING) == null) {
                BadgeAward award = createBadgeAward(Badge.VIKING, user);
                awardProvider.create(award);
                badgesCount.silver++;
                log.debug("Badge {} was awarded", Badge.VIKING);
            }

            if (count >= 100 && badges.get(Badge.SAMURAI) == null) {
                BadgeAward award = createBadgeAward(Badge.SAMURAI, user);
                awardProvider.create(award);
                badgesCount.gold++;
                log.debug("Badge {} was awarded", Badge.SAMURAI);
            }

            return badgesCount;
        } catch (SQLException e) {
            log.error("Evaluation failed", e);
            return new AwardedBadgesCount();
        }
    }

    @NonNull
    private BadgeAward createBadgeAward(Badge badge, User user) {
        BadgeAward award = new BadgeAward();
        award.setBadge(badge);
        award.setDate(new Date());
        award.setUser(user);
        return award;
    }
}
