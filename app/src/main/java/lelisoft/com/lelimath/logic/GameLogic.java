package lelisoft.com.lelimath.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.view.FormulaResultPair;

/**
 * Game preferences
 * Created by Leoš on 09.04.2016.
 */
public interface GameLogic extends Serializable {

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
}
