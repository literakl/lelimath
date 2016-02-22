package lelisoft.com.lelimath.logic;

import java.util.List;

import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.view.FormulaResultPair;

/**
 * Puzzle calculations
 * Created by Leoš on 03.12.2015.
 */
public interface PuzzleLogic {

    public void setFormulaDefinition(FormulaDefinition definition);

    public int getFirstOperandMaximumLength();

    public int getSecondOperandMaximumLength();

    public int getResultMaximumLength();

    public String getSampleFormula();

    public Level getLevel();

    public void setLevel(Level level);

    public List<FormulaResultPair> generateFormulaResultPairs(int tiles);

    /**
     * Game complexity
     */
    public enum Level {
        /** maximum is 2x2 */
        TRIVIAL(2, 2),
        /** maximum is 3x3 */
        EASY(3, 3),
        /** maximum is 4x4 */
        NORMAL(4, 4),
        /** maximum is 5x5 */
        HARD(5, 5),
        /** maximum is 6x6 */
        GENIUS (6, 6);

        public int x, y;
        Level(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
