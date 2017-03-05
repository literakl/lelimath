package lelisoft.com.lelimath.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lelisoft.com.lelimath.data.Expression;
import lelisoft.com.lelimath.data.FixedExpression;
import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.SequenceOrder;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.helpers.Misc;

import static lelisoft.com.lelimath.data.FormulaPart.FIRST_OPERAND;
import static lelisoft.com.lelimath.data.FormulaPart.RESULT;
import static lelisoft.com.lelimath.data.FormulaPart.SECOND_OPERAND;
import static lelisoft.com.lelimath.data.Operator.PLUS;
import static lelisoft.com.lelimath.data.SequenceOrder.ASCENDING;
import static lelisoft.com.lelimath.data.SequenceOrder.RANDOM;

/**
 * This class is responsible for generating a Formula that matches assignment in a FormulaDefinition.
 * Created by leos.literak on 26.2.2015.
 */
class FormulaGenerator {
    private static final Logger log = LoggerFactory.getLogger(FormulaGenerator.class);

    private static Random random = Misc.getRandom();
    private Integer ascendingPosition, descendingPosition;
    private boolean superRandomMode;

    private Formula generateRandomFormula(FormulaDefinition definition) {
        log.trace("Starting search for formula using {}", definition);

        Expression expression = getExpression(definition);
        FormulaPart unknown = getUnknown(definition.getUnknowns());

        Values valuesA = expression.getFirstOperand();
        Values valuesB = expression.getSecondOperand();
        Values valuesC = expression.getThirdOperand();

        if (expression instanceof FixedExpression) {
            int valueA = getValue(valuesA);
            int valueB = getValue(valuesB);
            Formula found = Solver.solve(expression.getOperator1(), FIRST_OPERAND, valueA, SECOND_OPERAND, valueB);
            if (found == null) {
                log.warn("No formula found for {} using {}", definition, expression);
                return null;
            }

            if (expression.getOperator2() != null) {
                int valueC = getValue(valuesC);
                Formula combined = Solver.solve(expression.getOperator2(), FIRST_OPERAND, found.getResult(), SECOND_OPERAND, valueC);
                if (combined == null) {
                    log.warn("No formula found for {} using {}", definition, expression);
                    return null;
                }

                found.setOperator2(expression.getOperator2());
                found.setThirdOperand(valueC);
                found.setResult(combined.getResult());
            }

            found.setUnknown(unknown);
            logFormula(found, expression, true);
            return found;
        }

        for (int i = 0; i < 100; i++) {
            int valueA = getValue(valuesA);
            for (int j = 0; j < 10; j++) {
                if (j > 0 && superRandomMode) {
                    valueA = getValue(valuesA);
                }
                int valueB = getValue(valuesB);

                Formula found = Solver.solve(expression.getOperator1(), FIRST_OPERAND, valueA, SECOND_OPERAND, valueB);
                if (found == null) {
                    continue;
                }

                if (expression.getOperator2() != null) {
                    int valueC = getValue(valuesC);
                    Formula combined = Solver.solve(expression.getOperator2(), FIRST_OPERAND, found.getResult(), SECOND_OPERAND, valueC);
                    if (combined == null) {
                        continue;
                    }
                    found.setOperator2(expression.getOperator2());
                    found.setThirdOperand(valueC);
                    found.setResult(combined.getResult());
                }

                boolean valid = expression.getResult().belongs(found.getResult());
                logFormula(found, expression, valid);

                if (valid) {
                    found.setUnknown(unknown);
                    log.debug("Generated formula {}", found);
                    return found;
                }
            }
        }

        log.warn("No formula found for {} using {}", definition, expression);
        return null;
    }

    /**
     * Generates a list of Formulas.
     * @param definition definition
     * @param count requested number of formulas
     * @return list with at most *count* formulas
     */
    ArrayList<Formula> generateFormulas(FormulaDefinition definition, int count) {
        ArrayList<Formula> list = new ArrayList<>(count);
        Formula formula, previous = null;

        for (int i = 0, stop = 0; i < count; i++) {
            formula = generateRandomFormula(definition);
            if (formula == null) {
                continue;
            }

            if (previous != null && formula.equals(previous)) {
                if (stop < 5) {
                    log.debug("Duplicate formula skipped");
                    superRandomMode = true;
                    i--;
                    stop++;
                    continue;
                }
            }

            list.add(formula);
            previous = formula;
            stop = 0;
            superRandomMode = false;
            commitFormula();
        }

        return list;
    }

    private int getValue(Values values) {
        SequenceOrder order = values.getOrder();
        if (order == null || order == RANDOM) {
            return values.getRandomValue(random);
        }

        // we assume that all Values have same size
        if (order == ASCENDING) {
            if (ascendingPosition == null) {
                ascendingPosition = 0;
            }
            // if contract is not honored an exception will be raised here
            return values.getValueAt(ascendingPosition);
        } else {
            if (descendingPosition == null){
                descendingPosition = values.getSize() - 1;
            }
            // if contract is not honored an exception will be raised here
            return values.getValueAt(descendingPosition);
        }
    }

    private void commitFormula() {
        if (ascendingPosition != null) {
            ascendingPosition++;
        }
        if (descendingPosition != null) {
            descendingPosition--;
        }
    }

    private Expression getExpression(FormulaDefinition definition) {
        List<Expression> expressions = definition.getExpressions();
        if (expressions == null || expressions.isEmpty()) {
            return new Expression(Values.DEMO, PLUS, Values.DEMO, Values.DEMO);
        }

        Expression firstExpression = expressions.get(0);
        if (firstExpression instanceof FixedExpression) {
            if (ascendingPosition == null) {
                ascendingPosition = 0;
            }
            return expressions.get(ascendingPosition);
        }

        if (expressions.size() == 1) {
            return firstExpression;
        }

        return expressions.get(random.nextInt(expressions.size()));
    }

    private static FormulaPart getUnknown(List<FormulaPart> unknowns) {
        if (unknowns == null || unknowns.isEmpty()) {
            return RESULT;
        }
        if (unknowns.size() == 1) {
            return unknowns.get(0);
        }
        return unknowns.get(random.nextInt(unknowns.size()));
    }

    private void logFormula(Formula found, Expression expression, boolean valid) {
        if (log.isTraceEnabled()) {
            String s = valid ? "valid" : "invalid";
            if (expression.getOperator2() != null) {
                log.trace("{}: {} {} {} {} {} = {}", s, found.getFirstOperand(), found.getOperator(), found.getSecondOperand(), found.getOperator2(), found.getThirdOperand(), found.getResult());
            } else {
                log.trace("{}: {} {} {} = {}", s, found.getFirstOperand(), found.getOperator(), found.getSecondOperand(), found.getResult());
            }
        }
    }

    /**
     * Reserved for tests where values shall not be so random
     * @param random random value
     */
    @SuppressWarnings("unused")
    public static void setRandom(Random random) {
        FormulaGenerator.random = random;
    }
}
