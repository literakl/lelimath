package lelisoft.com.lelimath.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;

/**
 * Solver solves mathematical formulas.
 * Created by leos.literak on 26.2.2015.
 */
public class Solver {
    private static final Logger log = LoggerFactory.getLogger(Solver.class);

    public static Integer evaluate(Integer first, Operator operator, Integer second) {
        switch (operator) {
            case PLUS:
                return first + second;
            case MINUS:
                return first - second;
            case MULTIPLY:
                return first * second;
            case DIVIDE: {
                if (second == 0) {
                    log.trace("Division by zero");
                    return null;
                }
                if (first % second != 0) {
                    log.trace("Decimal result");
                    return null; // only whole numbers are allowed, e.g. 13/4
                }
                return first / second; // e.g. 12/4
            }
        }
        return null;
    }

    /**
     * This method tries to solve solution assigned with parameters. It returns null
     * if the solution is a number with decimal places.
     * @param operator operator
     * @param partA first formula part
     * @param valueA value for first formula part
     * @param partB second formula part
     * @param valueB value for second formula part
     * @return complete Formula or null
     */
    public static Formula solve(Operator operator, FormulaPart partA, int valueA,
                                FormulaPart partB, int valueB) {
        if (log.isTraceEnabled()) {
            log.trace("Operator " + operator + " " + partA + " = " + valueA + ", " + partB + " = " + valueB);
        }
        Integer first = null, second = null, result = null;
        if (partA == FormulaPart.FIRST_OPERAND) {
            first = valueA;
        } else if (partA == FormulaPart.SECOND_OPERAND) {
            second = valueA;
        } else {
            result = valueA;
        }

        if (partB == FormulaPart.FIRST_OPERAND) {
            first = valueB;
        } else if (partB == FormulaPart.SECOND_OPERAND) {
            second = valueB;
        } else {
            result = valueB;
        }

        if (first != null && second != null) {
            result = evaluate(first, operator, second);
        } else {
            if (operator == Operator.PLUS || operator == Operator.MULTIPLY) {
                if (first == null) {
                    first = evaluate(result, operator.negate(), second);
                } else {
                    second = evaluate(result, operator.negate(), first);
                }
            } else {
                if (first == null) {
                    first = evaluate(result, operator.negate(), second);
                } else {
                    second = evaluate(first, operator, result);
                }
            }
        }

        if (operator == Operator.DIVIDE && second != null && second == 0) {
            log.trace("Division by zero detected");
            second = null;
        }

        if (first != null && second != null && result != null) {
            return new Formula(first, second, result, operator, null);
        } else {
            return null;
        }
    }
}
