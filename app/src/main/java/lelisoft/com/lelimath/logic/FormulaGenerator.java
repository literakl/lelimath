package lelisoft.com.lelimath.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lelisoft.com.lelimath.data.Expression;
import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.SequenceOrder;
import lelisoft.com.lelimath.data.Values;
import lelisoft.com.lelimath.helpers.Misc;

/**
 * This class is responsible for generating a Formula that matches assignment in a FormulaDefinition.
 * Created by leos.literak on 26.2.2015.
 */
class FormulaGenerator {
    private static final Logger log = LoggerFactory.getLogger(FormulaGenerator.class);

    private static Random random = Misc.getRandom();

    private SequenceOrder order;
    private FormulaPart orderedPart;
    private Integer position;
    private boolean superRandomMode;

    FormulaGenerator(SequenceOrder order, FormulaPart orderedPart) {
        this.orderedPart = orderedPart;
        if (order == null) {
            this.order = SequenceOrder.RANDOM;
        } else {
            this.order = order;
        }
    }

    FormulaGenerator() {
        this.order = SequenceOrder.RANDOM;
    }

    private Formula generateRandomFormula(FormulaDefinition definition) {
        log.trace("Starting search for formula using {}", definition);

        Expression expression = getExpression(definition.getExpressions());
        FormulaPart unknown = getUnknown(definition.getUnknowns());

        Values valuesA = expression.getFirstOperand();
        Values valuesB = expression.getSecondOperand();
        Values valuesC = expression.getThirdOperand();
        for (int i = 0; i < 100; i++) {
            int valueA = getValue(valuesA, FormulaPart.FIRST_OPERAND);
            for (int j = 0; j < 10; j++) {
                if (j > 0 && superRandomMode) {
                    valueA = getValue(valuesA, FormulaPart.FIRST_OPERAND);
                }
                int valueB = getValue(valuesB, FormulaPart.SECOND_OPERAND);

                Formula found = Solver.solve(expression.getOperator1(), FormulaPart.FIRST_OPERAND, valueA, FormulaPart.SECOND_OPERAND, valueB);
                if (found == null) {
                    continue;
                }

                if (expression.getOperator2() != null) {
                    int valueC = getValue(valuesC, FormulaPart.THIRD_OPERAND);
                    Formula combined = Solver.solve(expression.getOperator2(), FormulaPart.SECOND_OPERAND, found.getResult(), FormulaPart.THIRD_OPERAND, valueC);
                    if (combined == null) {
                        continue;
                    }
                    found.setOperator2(expression.getOperator2());
                    found.setThirdOperand(valueC);
                    found.setResult(combined.getResult());
                }

                boolean valid = expression.getResult().belongs(found.getResult());
                if (log.isTraceEnabled()) {
                    String s = valid ? "valid" : "invalid";
                    if (expression.getOperator2() != null) {
                        log.trace("{}: {} {} {} {} {} = {}", s, found.getFirstOperand(), found.getOperator(), found.getSecondOperand(), found.getOperator2(), found.getThirdOperand(), found.getResult());
                    } else {
                        log.trace("{}: {} {} {} = {}", s, found.getFirstOperand(), found.getOperator(), found.getSecondOperand(), found.getResult());
                    }
                }

                if (valid) {
                    found.setUnknown(unknown);
                    log.debug("Generated formula {}", found);
                    return found;
                }
            }
        }

        log.warn("No formula found for {} using operatorDefinition {}", definition, expression.getOperator1());
        return null;
    }
/*

    private Formula generateComplexRandomFormula(FormulaDefinition definition) {
        Expression expression = getExpression(definition.getExpressions());
        FormulaPart unknown = getUnknown(definition.getUnknowns());
        Values valuesA = expression.getFirstOperand();
        Values valuesB = expression.getSecondOperand();
        Values valuesC = expression.getThirdOperand();
        for (int i = 0; i < 100; i++) {
            int valueA = getValue(valuesA, parts.first);
            for (int j = 0; j < 10; j++) {
                if (j > 0 && superRandomMode) {
                    valueA = getValue(valuesA, parts.first);
                }
                int valueB = getValue(valuesB, parts.second);
                for (int k = 0; k < 10; k++) {
                    int valueC = getValue(valuesB, parts.second);

                    Formula found = Solver.solve(definition.getOperator(), parts.first, valueA, parts.second, valueB);
                    if (found == null) {
                        continue;
                    }

                    boolean valid = checkSolution(found, parts.first, parts.second, operatorDefinition);
                    if (log.isTraceEnabled()) {
                        log.trace("{}: {} {} {} = {}", valid, found.getFirstOperand(), found.getOperator(), found.getSecondOperand(), found.getResult());
                    }

                    if (valid) {
                        found.setUnknown(unknown);
                        log.debug("Generated formula {}", found);
                        return found;
                    }
                }
            }
        }
        return null;
    }
*/

    /**
     * Generates a list of Formulas.
     * @param definition definition
     * @param count requested number of formulas
     * @return list with at most *count* formulas
     */
    ArrayList<Formula> generateFormulas(FormulaDefinition definition, int count) {
        ArrayList<Formula> list = new ArrayList<>(count);
        Formula formula, previous = null;

        if (order == SequenceOrder.FIXED_PAIRS) {
            return generateFixedPairsFormulas(definition, count);
        }

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

    private ArrayList<Formula> generateFixedPairsFormulas(FormulaDefinition definition, int count) {
        log.trace("Starting search for formulas using {}", definition);
        Expression expression = getExpression(definition.getExpressions());
        FormulaPart unknown = getUnknown(definition.getUnknowns());
        ArrayList<Formula> list = new ArrayList<>(count);

        Values valuesA = getValues(expression, FormulaPart.FIRST_OPERAND);
        Values valuesB = getValues(expression, FormulaPart.SECOND_OPERAND);
        for (int i = 0; i < count; i++) {
            int valueA = valuesA.getValueAt(i);
            int valueB = valuesB.getValueAt(i);
            Formula found = Solver.solve(expression.getOperator1(), FormulaPart.FIRST_OPERAND, valueA, FormulaPart.SECOND_OPERAND, valueB);
            if (found == null) {
                log.warn("Failed to generate formula for {} {} {} {} {}!", expression.getOperator1(), FormulaPart.FIRST_OPERAND, valueA, FormulaPart.SECOND_OPERAND, valueB);
                continue;
            }

            boolean valid = expression.getResult().belongs(found.getResult());
            if (log.isTraceEnabled()) {
                log.trace("{}: {} {} {} = {}", valid, found.getFirstOperand(), found.getOperator(), found.getSecondOperand(), found.getResult());
            }

            if (valid) {
                found.setUnknown(unknown);
                log.debug("Generated formula {}", found);
                list.add(found);
            }
        }

        return list;
    }

    private int getValue(Values values, FormulaPart part) {
        if (order == SequenceOrder.RANDOM || orderedPart != part) {
            return values.getRandomValue(random);
        }

        if (position == null) {
            if (order == SequenceOrder.ASCENDING) {
                position = 0;
            } else {
                position = values.getSize() - 1;
            }
        }

        return values.getValueAt(position);
    }

    private void commitFormula() {
        if (order == SequenceOrder.RANDOM) {
            return;
        }

        if (order == SequenceOrder.ASCENDING) {
            position++;
        } else {
            position--;
        }
    }

    private static Expression getExpression(List<Expression> expressions) {
        if (expressions == null || expressions.isEmpty()) {
            return new Expression(Values.DEMO, Operator.PLUS, Values.DEMO, Values.DEMO);
        }
        if (expressions.size() == 1) {
            return expressions.get(0);
        }
        return expressions.get(random.nextInt(expressions.size()));
    }

    private static FormulaPart getUnknown(List<FormulaPart> unknowns) {
        if (unknowns == null || unknowns.isEmpty()) {
            return FormulaPart.RESULT;
        }
        if (unknowns.size() == 1) {
            return unknowns.get(0);
        }
        return unknowns.get(random.nextInt(unknowns.size()));
    }

    private static Values getValues(Expression expression, FormulaPart part) {
        Values values;
        switch (part) {
            case FIRST_OPERAND:
                values = expression.getFirstOperand();
                break;
            case SECOND_OPERAND:
                values = expression.getSecondOperand();
                break;
            case THIRD_OPERAND:
                values = expression.getThirdOperand();
                break;
            default:
                values = expression.getResult();
        }

        if (values == Values.UNDEFINED) {
            log.warn("Undefined values for {}", part);
        }

        return values;
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
