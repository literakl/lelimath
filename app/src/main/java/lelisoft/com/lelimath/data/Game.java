package lelisoft.com.lelimath.data;

import com.google.gson.annotations.SerializedName;

/**
 * Game types
 * Created by Leoš on 29.04.2016.
 */
public enum Game {
    @SerializedName("PZ") PUZZLE("PZ"),
    @SerializedName("FC") FAST_CALC("FC");

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
