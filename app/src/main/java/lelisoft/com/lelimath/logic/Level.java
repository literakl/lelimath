package lelisoft.com.lelimath.logic;

import java.io.Serializable;

/**
 * Game complexity
 */
public class Level implements Serializable {
    /** maximum is 2x2 */
    public static final Level TRIVIAL = new Level(2, 2, 3),
    /** maximum is 3x3 */
    EASY = new Level(3, 3, 5),
    /** maximum is 4x4 */
    NORMAL = new Level(4, 4, 8),
    /** maximum is 5x5 */
    HARD = new Level(5, 5, 12),
    /** maximum is 6x6 */
    GENIUS = new Level(6, 6, 16);

    /** puzzle dimensions */
    public int x, y;
    /* calc formula count */
    public int count;

    private Level(int x, int y, int count) {
        this.x = x;
        this.y = y;
        this.count = count;
    }

    public static Level getCustom(int count) {
        double sqrt = Math.sqrt(count);
        int cSqrt = (int) Math.ceil(sqrt), fSqrt = (int) Math.floor(sqrt);
        if (cSqrt == fSqrt) {
            return new Level(cSqrt, cSqrt, count);
        }

        int diffA = Math.abs(count - cSqrt * cSqrt);
        int diffB = Math.abs(count - cSqrt * fSqrt);
        int diffC = Math.abs(count - fSqrt * fSqrt);
        if (diffA < diffB) {
            if (diffA < diffC) {
                return new Level(cSqrt, cSqrt, count);
            } else {
                return new Level(fSqrt, fSqrt, count);
            }
        } else {
            if (diffB < diffC) {
                return new Level(cSqrt, fSqrt, count);
            } else {
                return new Level(fSqrt, fSqrt, count);
            }
        }
    }

    public static Level valueOf(String name) {
        switch (name) {
            case "TRIVIAL":
                return TRIVIAL;
            case "EASY":
                return EASY;
            case "NORMAL":
                return NORMAL;
            case "HARD":
                return HARD;
            case "GENIUS":
                return GENIUS;
        }
        throw new IllegalArgumentException("Unknown Level " + name);
    }
}
