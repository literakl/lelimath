package lelisoft.com.lelimath.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.view.FormulaResultPair;

/**
 * Base class
 * Created by Leoš on 09.04.2016.
 */
public class GameLogicImpl implements GameLogic {
    private static final Logger log = LoggerFactory.getLogger(GameLogicImpl.class);

    FormulaDefinition definition;
    PuzzleLogic.Level level;

    public FormulaDefinition getFormulaDefinition() {
        return definition;
    }

    public void setFormulaDefinition(FormulaDefinition definition) {
        this.definition = definition;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<FormulaResultPair> generateFormulaResultPairs(int count) {
        log.debug("generateFormulaResultPairs: " + definition + ", count = " + count);
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

    public ArrayList<Formula> generateFormulas() {
        int count = 1;
        switch (level) {
            case TRIVIAL: count = 3; break;
            case EASY: count = 5; break;
            case NORMAL: count = 10; break;
            case HARD: count = 20; break;
            case GENIUS: count = 30; break;
        }
        log.debug("generateFormulas: " + definition + ", count = " + count);

        ArrayList<Formula> list = new ArrayList<>(count + 1);
        Formula formula;

        for (int i = 0; i < count; i++) {
            formula = FormulaGenerator.generateRandomFormula(definition);
            if (formula == null) {
                continue;
            }
            list.add(formula);
        }

        Collections.shuffle(list, Misc.getRandom());
        return list;
    }
}