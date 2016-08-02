package lelisoft.com.lelimath.helpers;

import java.util.Comparator;

import lelisoft.com.lelimath.data.BadgeProgress;

/**
 * Compares badgeProgress by remaning formulas. Multi-day badges are sorted at the end.
 */
public class BadgeProgressComparator implements Comparator<BadgeProgress> {
    @Override
    public int compare(BadgeProgress lhs, BadgeProgress rhs) {
        switch (lhs.getBadge()) {
            case RETURNER: {
                switch (rhs.getBadge()) {
                    case RETURNER: return 0;
                    case LONG_DISTANCE_RUNNER: return -1;
                    case MARATHON_RUNNER: return -1;
                    default: return 1;
                }
            }
            case LONG_DISTANCE_RUNNER: {
                switch (rhs.getBadge()) {
                    case RETURNER: return 1;
                    case LONG_DISTANCE_RUNNER: return 0;
                    case MARATHON_RUNNER: return -1;
                    default: return 1;
                }
            }
            case MARATHON_RUNNER: {
                switch (rhs.getBadge()) {
                    case RETURNER: return 1;
                    case LONG_DISTANCE_RUNNER: return 1;
                    case MARATHON_RUNNER: return 0;
                    default: return 1;
                }
            }
            default:
                return lhs.calculateRemainingFormulas() - rhs.calculateRemainingFormulas();
        }
    }
}
