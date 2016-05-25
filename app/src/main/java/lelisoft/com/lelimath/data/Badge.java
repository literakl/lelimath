package lelisoft.com.lelimath.data;

import lelisoft.com.lelimath.helpers.Misc;

/**
 * Badges are awards for user activity
 * Created by Leoš on 15.05.2016.
 * TODO use badge.name() a valueOf
 */
public enum Badge {
    PAGE('B', false),
    KNIGHT('S', false),
    PALADIN('G', false),
    GLADIATOR('B', false),
    VIKING('S', false),
    SAMURAI('G', false),
    RETURNER('B', false),
    LONG_DISTANCE_RUNNER('S', false),
    MARATHON_RUNNER('G', false),
    GOOD_SUMMATION('B', false),
    GREAT_SUMMATION('S', false),
    EXCELLENT_SUMMATION('G', false),
    GOOD_SUBTRACTION('B', false),
    GREAT_SUBTRACTION('S', false),
    EXCELLENT_SUBTRACTION('G', false),
    GOOD_MULTIPLICATION('B', false),
    GREAT_MULTIPLICATION('S', false),
    EXCELLENT_MULTIPLICATION('G', false),
    GOOD_DIVISION('B', false),
    GREAT_DIVISION('S', false),
    EXCELLENT_DIVISION('G', false);

    final char type;
    final boolean multipleAwards;

    Badge(char type, boolean multipleAwards) {
        this.type = type;
        this.multipleAwards = multipleAwards;
    }

    public boolean isMultipleAwards() {
        return multipleAwards;
    }

    public char getType() {
        return type;
    }

    public String getTitle() {
        return Misc.getResource("badge_title_" + name());
    }

    public String getDescription() {
        return Misc.getResource("badge_desc_" + name());
    }
}
