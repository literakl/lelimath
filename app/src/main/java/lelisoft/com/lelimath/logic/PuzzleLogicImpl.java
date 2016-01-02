package lelisoft.com.lelimath.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.view.FormulaResultPair;
import lelisoft.com.lelimath.view.Misc;

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

    @Override
    public int getResultMaximumLength() {
        return definition.getValuesMaximumLength(definition.getResult());
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<FormulaResultPair> generateFormulaResultPairs(int tiles) {
        List<FormulaResultPair> pairs;
        Formula formula;

        int formulas = tiles / 2;
        if (tiles % 2 > 0) {
            pairs = new ArrayList<>(tiles - 1);
        } else {
            pairs = new ArrayList<>(tiles);
        }

        for (int i = 0; i < formulas; i++) {
            formula = FormulaGenerator.generateRandomFormula(definition);
            pairs.add(new FormulaResultPair(formula));
            pairs.add(new FormulaResultPair(formula.getResult()));
        }

        Collections.shuffle(pairs, Misc.getRandom());
        return pairs;
    }
}
