package lelisoft.com.lelimath.data;

import lelisoft.com.lelimath.helpers.Misc;

/**
 * Badges are awards for user activity
 * Created by Leoš on 15.05.2016.
 * TODO use badge.name() a valueOf
 */
public enum Badge {
    PAGE("PAGE", 'B'),
    KNIGHT("KNIGHT", 'S'),
    PALADIN("PALADIN", 'G'),
    GLADIATOR("GLADIATOR", 'B'),
    VIKING("VIKING", 'S'),
    SAMURAI("SAMURAI", 'G'),
    RETURNER("RETURNER", 'B'),
    LONG_DISTANCE_RUNNER("LONG_DISTANCE_RUNNER", 'S'),
    MARATHON_RUNNER("MARATHON_RUNNER", 'G'),
    GOOD_SUMMATION("GOOD_SUMMATION", 'B'),
    GREAT_SUMMATION("GREAT_SUMMATION", 'S'),
    EXCELLENT_SUMMATION("EXCELLENT_SUMMATION", 'G'),
    GOOD_SUBTRACTION("GOOD_SUBTRACTION", 'B'),
    GREAT_SUBTRACTION("GREAT_SUBTRACTION", 'S'),
    EXCELLENT_SUBTRACTION("EXCELLENT_SUBTRACTION", 'G'),
    GOOD_MULTIPLICATION("GOOD_MULTIPLICATION", 'B'),
    GREAT_MULTIPLICATION("GREAT_MULTIPLICATION", 'S'),
    EXCELLENT_MULTIPLICATION("EXCELLENT_MULTIPLICATION", 'G'),
    GOOD_DIVISION("GOOD_DIVISION", 'B'),
    GREAT_DIVISION("GREAT_DIVISION", 'S'),
    EXCELLENT_DIVISION("EXCELLENT_DIVISION", 'G');

    final String key;
    final char type;

    Badge(String key, char type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public char getType() {
        return type;
    }

    public String getTitle() {
        return Misc.getResource("badge_title_" + key);
    }

    public static Badge getValue(String s) {
        switch (s) {
            case "PAGE":
                return PAGE;
            case "KNIGHT":
                return KNIGHT;
            case "PALADIN":
                return PALADIN;
            case "GLADIATOR":
                return GLADIATOR;
            case "VIKING":
                return VIKING;
            case "SAMURAI":
                return SAMURAI;
            case "RETURNER":
                return RETURNER;
            case "LONG_DISTANCE_RUNNER":
                return LONG_DISTANCE_RUNNER;
            case "MARATHON_RUNNER":
                return MARATHON_RUNNER;
            case "GOOD_SUMMATION":
                return GOOD_SUMMATION;
            case "GREAT_SUMMATION":
                return GREAT_SUMMATION;
            case "GOOD_SUBTRACTION":
                return GOOD_SUBTRACTION;
            case "GREAT_SUBTRACTION":
                return GREAT_SUBTRACTION;
            case "EXCELLENT_SUBTRACTION":
                return EXCELLENT_SUBTRACTION;
            case "GOOD_MULTIPLICATION":
                return GOOD_MULTIPLICATION;
            case "GREAT_MULTIPLICATION":
                return GREAT_MULTIPLICATION;
            case "EXCELLENT_MULTIPLICATION":
                return EXCELLENT_MULTIPLICATION;
            case "GOOD_DIVISION":
                return GOOD_DIVISION;
            case "EXCELLENT_DIVISION":
                return EXCELLENT_DIVISION;
            case "GREAT_DIVISION":
                return GREAT_DIVISION;
            default:
                throw new IllegalArgumentException("Unknown Badge '" + s + "'!");
        }
    }
}
