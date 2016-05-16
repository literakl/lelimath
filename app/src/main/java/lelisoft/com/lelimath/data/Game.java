package lelisoft.com.lelimath.data;

/**
 * Game types
 * Created by Leoš on 29.04.2016.
 */
public enum Game {
    PUZZLE("PZ"), FAST_CALC("FC");

    final String key;

    Game(String key) {
        this.key = key;
    }

    public static Game getValue(String s) {
        switch (s) {
            case "PZ":
                return PUZZLE;
            case "FC":
                return FAST_CALC;
            default:
                throw new IllegalArgumentException("Unknown Game '" + s + "'!");
        }
    }
}
