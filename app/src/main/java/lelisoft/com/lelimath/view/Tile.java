package lelisoft.com.lelimath.view;

import lelisoft.com.lelimath.data.Formula;

/**
 * Tile contains formula and visual attributes
 * Created by Leoš on 12.12.2015.
 */
public class Tile {
    FormulaResultPair pair;

    public Tile(FormulaResultPair pair) {
        this.pair = pair;
    }

    public Formula getFormula() {
        return pair.formula;
    }

    public Integer getResult() {
        return pair.result;
    }

    public String getText() {
        if (pair.formula != null) {
            Formula formula = pair.formula;
            return String.valueOf(formula.getFirstOperand()) + " " + formula.getOperator() + " " + formula.getSecondOperand();
        } else {
            return pair.result.toString();
        }
    }

    public boolean matches(Tile tile) {
        if (pair.formula != null) {
            if (tile.pair.result != null) {
                return (pair.formula.getResult().equals(tile.pair.result));
            }
        } else if (pair.result != null) {
            if (tile.pair.formula != null) {
                return (tile.pair.formula.getResult().equals(pair.result));
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Tile{" +
                getText() +
                '}';
    }
}
