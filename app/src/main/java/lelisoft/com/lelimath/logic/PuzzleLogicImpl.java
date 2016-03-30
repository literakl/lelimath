package lelisoft.com.lelimath.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.view.FormulaResultPair;
import lelisoft.com.lelimath.helpers.Misc;

/**
 * Implementation for handling all Puzzle calculations
 * Created by Leoš on 03.12.2015.
 */
public class PuzzleLogicImpl implements PuzzleLogic {
    private static final Logger log = LoggerFactory.getLogger(PuzzleLogicImpl.class);
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

    @Override
    public String getSampleFormula() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getFirstOperandMaximumLength(); i++) {
            sb.append("3");
        }
        sb.append(" + ");
        for (int i = 0; i < getSecondOperandMaximumLength(); i++) {
            sb.append("3");
        }
        return sb.toString();
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<FormulaResultPair> generateFormulaResultPairs(int count) {
        log.debug(definition + ", count = " + count);
        List<FormulaResultPair> pairs;
        Formula formula;

        int formulas = count / 2;
        if (count % 2 > 0) {
            pairs = new ArrayList<>(count - 1);
        } else {
            pairs = new ArrayList<>(count);
        }

        for (int i = 0; i < formulas; i++) {
            formula = FormulaGenerator.generateRandomFormula(definition);
            if (formula == null) {
                continue;
            }
            pairs.add(new FormulaResultPair(formula));
            pairs.add(new FormulaResultPair(formula.getResult()));
        }

        Collections.shuffle(pairs, Misc.getRandom());
        return pairs;
    }
}
