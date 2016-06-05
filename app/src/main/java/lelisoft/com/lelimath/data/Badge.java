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
    GOOD_SUMMATION('B', true),
    GREAT_SUMMATION('S', true),
    EXCELLENT_SUMMATION('G', true),
    GOOD_SUBTRACTION('B', true),
    GREAT_SUBTRACTION('S', true),
    EXCELLENT_SUBTRACTION('G', true),
    GOOD_MULTIPLICATION('B', true),
    GREAT_MULTIPLICATION('S', true),
    EXCELLENT_MULTIPLICATION('G', true),
    GOOD_DIVISION('B', true),
    GREAT_DIVISION('S', true),
    EXCELLENT_DIVISION('G', true);

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
