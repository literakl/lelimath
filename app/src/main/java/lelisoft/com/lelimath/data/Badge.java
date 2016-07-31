package lelisoft.com.lelimath.data;

import lelisoft.com.lelimath.helpers.Misc;

/**
 * Badges are awards for user activity
 * Created by Leoš on 15.05.2016.
 * TODO use badge.name() a valueOf
 */
public enum Badge {
    PAGE('B', 1, false),
    KNIGHT('S', 25, false),
    PALADIN('G', 100, false),
    GLADIATOR('B', 1, false),
    VIKING('S', 25, false),
    SAMURAI('G',100, false),
    RETURNER('B', 2, false),
    LONG_DISTANCE_RUNNER('S', 7, false),
    MARATHON_RUNNER('G', 30, false),
    GOOD_SUMMATION('B', 10, true),
    GREAT_SUMMATION('S', 25, true),
    EXCELLENT_SUMMATION('G', 100, true),
    GOOD_SUBTRACTION('B', 10, true),
    GREAT_SUBTRACTION('S', 25, true),
    EXCELLENT_SUBTRACTION('G', 100, true),
    GOOD_MULTIPLICATION('B', 10, true),
    GREAT_MULTIPLICATION('S', 25, true),
    EXCELLENT_MULTIPLICATION('G', 100, true),
    GOOD_DIVISION('B', 10, true),
    GREAT_DIVISION('S', 25, true),
    EXCELLENT_DIVISION('G', 100, true);

    final char type;
    final boolean multipleAwards;
    final int required;

    Badge(char type, int required, boolean multipleAwards) {
        this.type = type;
        this.multipleAwards = multipleAwards;
        this.required = required;
    }

    public boolean isMultipleAwards() {
        return multipleAwards;
    }

    public char getType() {
        return type;
    }

    public int getRequired() {
        return required;
    }

    public String getTitle() {
        return Misc.getResource("badge_title_" + name());
    }

    public String getDescription() {
        return Misc.getResource("badge_desc_" + name());
    }
}
