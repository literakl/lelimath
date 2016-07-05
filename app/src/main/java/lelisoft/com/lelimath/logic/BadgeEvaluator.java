package lelisoft.com.lelimath.logic;

import android.content.Context;
import android.support.annotation.NonNull;

import com.j256.ormlite.dao.Dao;
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
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.provider.BadgeProgressProvider;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

import static lelisoft.com.lelimath.data.BadgeEvaluation.BADGE_COLUMN_NAME;
import static lelisoft.com.lelimath.data.BadgeEvaluation.USER_COLUMN_NAME;

/**
 * Performs database analysis for certain set of badges and decides if a user is elligible
 * to receive them. Implementation shall pass valid Context and return count of newly awarded badges.
 */
public abstract class BadgeEvaluator {
    private static final Logger log = LoggerFactory.getLogger(BadgeEvaluator.class);

    /**
     * Evaluates current FormulaRecords and optionally rewards new badges.
     * @param badges currently awarded badges
     * @return number of newly awarded badged
     */
    public abstract AwardedBadgesCount evaluate(Map<Badge, List<BadgeAward>> badges, Context context);

    /**
     * Calculates progress for given badge.
     * @param badge searched badge
     * @param ctx context
     * @return BadgeProgress
     */
    public abstract BadgeProgress calculateProgress(Badge badge, Context ctx);

    @NonNull
    protected BadgeAward createBadgeAward(Badge badge, User user) {
        BadgeAward award = new BadgeAward();
        award.setBadge(badge);
        award.setDate(new Date());
        award.setUser(user);
        return award;
    }

    /**
     * Stores badge progress in a database.
     * @param badge this badge
     * @param inProgress true when a user is on his way to getting this badge (again)
     * @param current current progress
     * @param total required count to receive this badge
     * @param user current user
     * @param ctx context
     */
    protected BadgeProgress saveBadgeProgress(Badge badge, boolean inProgress, int current, int total, User user, Context ctx) {
        log.debug("saveBadgeProgress({},{},{},{})", badge, inProgress, current, total);
        BadgeProgress progress = new BadgeProgress(badge, inProgress, current, total);
        progress.setUser(user);
        BadgeProgressProvider provider = new BadgeProgressProvider(ctx);
        provider.createOrUpdate(progress);
//        log.debug("saveBadgeProgress finished"); // cca 10-20 ms
        return progress;
    }

    protected void setPlayId(BadgeAward award, Play play) {
        award.setData(Integer.toString(play.getId()));
    }

    protected void setPlayIds(BadgeAward award, List<Play> plays) {
        StringBuilder sb = new StringBuilder();
        for (Play play : plays) {
            sb.append(Integer.toString(play.getId())).append(',');
        }
        sb.setLength(sb.length() - 1);
        award.setData(sb.toString());
    }

    protected BadgeEvaluation queryLastEvaluation(Badge badge, User user, Dao<BadgeEvaluation, Integer> dao) throws SQLException {
        QueryBuilder<BadgeEvaluation, Integer> builder = dao.queryBuilder();
        builder.where().eq(BADGE_COLUMN_NAME, badge.name()).and().eq(USER_COLUMN_NAME, user.getId());
        builder.orderBy(BadgeEvaluation.ID_COLUMN_NAME, true).limit(1L);
        return builder.queryForFirst();
    }
}
