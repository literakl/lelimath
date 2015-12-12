package lelisoft.com.lelimath.logic;

import java.util.List;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;

/**
 * Implementation for handling all Puzzle calculations
 * Created by Leoš on 03.12.2015.
 */
public class PuzzleLogicImpl implements PuzzleLogic {
    FormulaDefinition definition;
    PuzzleLogic.Level level;

    public void setFormulaDefinition(FormulaDefinition definition) {
        this.definition = definition;
    }

    public int getFirstOperandMaximumLength() {
        return definition.getValuesMaximumLength(definition.getFirstOperand());
    }

    public int getSecondOperandMaximumLength() {
        return definition.getValuesMaximumLength(definition.getSecondOperand());
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public List<Formula> generateFormulas() {
//        Formula formula = FormulaGenerator.generateRandomFormula(getFormulaDefinition());
        return null;
    }
}
