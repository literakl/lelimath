package lelisoft.com.lelimath.logic;

import java.util.ArrayList;
import java.util.List;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.view.FormulaResultPair;

/**
 * Game preferences
 * Created by Leoš on 09.04.2016.
 */
public interface GameLogic {

    Level getLevel();

    void setLevel(Level level);

    FormulaDefinition getFormulaDefinition();

    void setFormulaDefinition(FormulaDefinition definition);

    List<FormulaResultPair> generateFormulaResultPairs(int size);

    /**
     * Generates count formula using FormulaDefinition that has been already set.
     * Generator may fail to generate some Formulas so the total number may be smaller.
     * @return valid formulas
     */
    ArrayList<Formula> generateFormulas();

    /**
     * Game complexity
     */
    enum Level {
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
