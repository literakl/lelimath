package lelisoft.com.lelimath.logic;

import android.content.Context;

import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;

/**
 * Performs database analysis for certain set of badges and decides if a user is elligible
 * to receive them. Implementation shall pass valid Context and return count of newly awarded badges.
 */
public interface BadgeEvaluator {
    class AwardedBadgesCount {
        public int bronze, silver, gold;

        public AwardedBadgesCount() {
        }

        public AwardedBadgesCount(int bronze, int silver, int gold) {
            this.bronze = bronze;
            this.silver = silver;
            this.gold = gold;
        }

        public void add(AwardedBadgesCount other) {
            this.bronze += other.bronze;
            this.silver += other.silver;
            this.gold += other.gold;
        }
    }

    /**
     * Evaluates current FormulaRecords and optionally rewards new badges.
     * @param badges currently awarded badges
     * @return number of newly awarded badged
     */
    AwardedBadgesCount evaluate(Map<Badge, BadgeAward> badges, Context context);
}
