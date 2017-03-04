package lelisoft.com.lelimath.logic;

import java.io.Serializable;

import lelisoft.com.lelimath.data.Expression;
import lelisoft.com.lelimath.helpers.Misc;

/**
 * Implementation for handling all Puzzle calculations
 * Created by Leoš on 03.12.2015.
 */
public class PuzzleLogicImpl extends GameLogicImpl implements PuzzleLogic, Serializable {

    @Override
    public String getSampleFormula() {
        int i, j, k, maxFirst = 2, maxSecond = 2;
        for (Expression expression : definition.getExpressions()) {
            i = Misc.getNumberLength(expression.getFirstOperand().getMaximumValue());
            j = Misc.getNumberLength(expression.getSecondOperand().getMaximumValue());
            k = Misc.getNumberLength(expression.getResult().getMaximumValue());

            if (i == 0) {
                i = Math.max(j, k);
            }
            if (j == 0) {
                j = Math.max(i, k);
            }

            if (i > maxFirst) {
                maxFirst = i;
            }
            if (j > maxSecond) {
                maxSecond = j;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (i = 0; i < maxFirst; i++) {
            sb.append("3");
        }
        sb.append(" + ");
        for (i = 0; i < maxSecond; i++) {
            sb.append("3");
        }
        return sb.toString();
    }
}
