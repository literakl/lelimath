package lelisoft.com.lelimath.data;

import lelisoft.com.lelimath.helpers.Misc;

/**
 * Badges are awards for user activity
 * Created by Leoš on 15.05.2016.
 * TODO use badge.name() a valueOf
 */
public enum Badge {
    PAGE('B'),
    KNIGHT('S'),
    PALADIN('G'),
    GLADIATOR('B'),
    VIKING('S'),
    SAMURAI('G'),
    RETURNER('B'),
    LONG_DISTANCE_RUNNER('S'),
    MARATHON_RUNNER('G'),
    GOOD_SUMMATION('B'),
    GREAT_SUMMATION('S'),
    EXCELLENT_SUMMATION('G'),
    GOOD_SUBTRACTION('B'),
    GREAT_SUBTRACTION('S'),
    EXCELLENT_SUBTRACTION('G'),
    GOOD_MULTIPLICATION('B'),
    GREAT_MULTIPLICATION('S'),
    EXCELLENT_MULTIPLICATION('G'),
    GOOD_DIVISION('B'),
    GREAT_DIVISION('S'),
    EXCELLENT_DIVISION('G');

    final char type;

    Badge(char type) {
        this.type = type;
    }

    public char getType() {
        return type;
    }

    public String getTitle() {
        return Misc.getResource("badge_title_" + name());
    }
}
