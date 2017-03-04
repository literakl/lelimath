package lelisoft.com.lelimath.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
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
class GameLogicImpl implements GameLogic, Serializable {
    private static final Logger log = LoggerFactory.getLogger(GameLogicImpl.class);

    FormulaDefinition definition;
    private Level level;

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
        log.debug("generateFormulaResultPairs: {}, count = {}", definition, count);
        ArrayList<Formula> formulas = generateFormulas(count / 2);
        List<FormulaResultPair> pairs = new ArrayList<>(formulas.size() * 2);
        for (Formula formula : formulas) {
            pairs.add(new FormulaResultPair(formula));
            pairs.add(new FormulaResultPair(formula.getResult()));
        }

        Collections.shuffle(pairs, Misc.getRandom());
        return pairs;
    }

    public ArrayList<Formula> generateFormulas() {
        return generateFormulas(level.count);
    }

    private ArrayList<Formula> generateFormulas(int count) {
        log.debug("generateFormulas: {}, count = {}", definition, count);
        FormulaGenerator generator = new FormulaGenerator();
        return generator.generateFormulas(definition, count);
    }
}
