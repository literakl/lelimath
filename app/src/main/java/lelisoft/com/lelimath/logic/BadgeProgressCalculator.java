package lelisoft.com.lelimath.logic;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeProgress;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.logic.badges.CorrectnessBadgeEvaluator;
import lelisoft.com.lelimath.logic.badges.PlayCountBadgeEvaluator;
import lelisoft.com.lelimath.logic.badges.StaminaBadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeProgressProvider;

/**
 * Calculates a progress for each badge
 * Created by Leoš on 04.07.2016.
 */
public class BadgeProgressCalculator {
    private static final Logger log = LoggerFactory.getLogger(BadgeProgressCalculator.class);

    /**
     * Finds a progress for given badge. If it does not exist or it is too old, new progress is calculated.
     * @param badge requested badge
     * @param ctx context
     * @return progress for this badge
     */
    public static BadgeProgress getProgress(Badge badge, Context ctx) {
        long lastFormula = LeliMathApp.getInstance().getLastFormulaDate();
        BadgeProgressProvider provider = new BadgeProgressProvider(ctx);
        return getProgress(badge, lastFormula, provider, ctx);
    }

    protected static BadgeProgress getProgress(Badge badge, long lastFormula, BadgeProgressProvider provider, Context ctx) {
        BadgeProgress progress = provider.getById(badge);
        if (progress != null && progress.getDate().getTime() > lastFormula) {
            return progress;
        }

        switch (badge) {
            case PAGE:
            case KNIGHT:
            case PALADIN:
            case GLADIATOR:
            case VIKING:
            case SAMURAI:
                return new PlayCountBadgeEvaluator().calculateProgress(badge, ctx);
            case RETURNER:
            case LONG_DISTANCE_RUNNER:
            case MARATHON_RUNNER:
                return new StaminaBadgeEvaluator().calculateProgress(badge, ctx);
            case GOOD_SUMMATION:
            case GREAT_SUMMATION:
            case EXCELLENT_SUMMATION:
            case GOOD_SUBTRACTION:
            case GREAT_SUBTRACTION:
            case EXCELLENT_SUBTRACTION:
            case GOOD_MULTIPLICATION:
            case GREAT_MULTIPLICATION:
            case EXCELLENT_MULTIPLICATION:
            case GOOD_DIVISION:
            case GREAT_DIVISION:
            case EXCELLENT_DIVISION:
                return new CorrectnessBadgeEvaluator().calculateProgress(badge, ctx);
        }
        throw new IllegalArgumentException("Missing handler for badge " + badge);
    }

    /**
     * Recalculates badge progress for all badges.
     * @param ctx context
     */
    public static void refresh(Context ctx) {
        log.debug("refresh() starts");
        long lastFormula = LeliMathApp.getInstance().getLastFormulaDate();
        BadgeProgressProvider provider = new BadgeProgressProvider(ctx);

        for (Badge badge : Badge.values()) {
            try {
                getProgress(badge, lastFormula, provider, ctx);
            } catch (Exception e) {
                log.error("Calculating progress for badge {} failed", badge, e);
            }
        }
        log.debug("refresh() finished");
    }
}
