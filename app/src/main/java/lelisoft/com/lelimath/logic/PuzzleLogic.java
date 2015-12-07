package lelisoft.com.lelimath.logic;

import java.util.List;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;

/**
 * Puzzle calculations
 * Created by Leoš on 03.12.2015.
 */
public interface PuzzleLogic {

    public void setFormulaDefinition(FormulaDefinition definition);

    public void setLevel(Level level);

    public List<Formula> generateFormulas();

    public enum Level {
        /** maximum is 2x2 */
        TRIVIAL,
        /** maximum is 3x3 */
        EASY,
        /** maximum is 4x4 */
        NORMAL,
        /** maximum is 5x5 */
        HARD,
        /** maximum is 6x6 */
        GENIUS
    }
}
