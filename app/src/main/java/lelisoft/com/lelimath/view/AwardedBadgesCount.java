package lelisoft.com.lelimath.view;

/**
 * Holder for awarded badges counts
 * Created by Leoš on 01.06.2016.
 */
public class AwardedBadgesCount {
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

    @Override
    public String toString() {
        return "AwardedBadgesCount{" +
                "bronze=" + bronze +
                ", silver=" + silver +
                ", gold=" + gold +
                '}';
    }
}
