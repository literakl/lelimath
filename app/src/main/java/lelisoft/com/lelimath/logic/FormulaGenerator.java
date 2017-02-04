package lelisoft.com.lelimath.logic;

import android.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.OperatorDefinition;
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
        OperatorDefinition operatorDefinition = getOperator(definition.getOperatorDefinitions());

        FormulaPart unknown = getUnknown(definition.getUnknowns());
        Pair<FormulaPart, FormulaPart> parts = findRemainingFormulaParts(unknown);

        Values valuesA = getValues(operatorDefinition, parts.first);
        Values valuesB = getValues(operatorDefinition, parts.second);
        for (int i = 0; i < 100; i++) {
            int valueA = getValue(valuesA, parts.first);
            for (int j = 0; j < 10; j++) {
                if (j > 0 && superRandomMode) {
                    valueA = getValue(valuesA, parts.first);
                }
                int valueB = getValue(valuesB, parts.second);

                Formula found = Solver.solve(operatorDefinition.getOperator(), parts.first, valueA, parts.second, valueB);
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

        log.warn("No formula found for {} using operatorDefinition {}", definition, operatorDefinition.getOperator());
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
                } else {
                    stop = 0;
                    superRandomMode = false;
                }
            }

            list.add(formula);
            previous = formula;
            commitFormula();
        }

        if (order == SequenceOrder.RANDOM) {
            Collections.shuffle(list, Misc.getRandom());
        }
        return list;
    }

    private ArrayList<Formula> generateFixedPairsFormulas(FormulaDefinition definition, int count) {
        log.trace("Starting search for formulas using {}", definition);
        OperatorDefinition operatorDefinition = getOperator(definition.getOperatorDefinitions());
        FormulaPart unknown = getUnknown(definition.getUnknowns());
        Pair<FormulaPart, FormulaPart> parts = findRemainingFormulaParts(unknown);
        ArrayList<Formula> list = new ArrayList<>(count);

        Values valuesA = getValues(operatorDefinition, parts.first);
        Values valuesB = getValues(operatorDefinition, parts.second);
        for (int i = 0; i < count; i++) {
            int valueA = valuesA.getValueAt(i);
            int valueB = valuesB.getValueAt(i);
            Formula found = Solver.solve(operatorDefinition.getOperator(), parts.first, valueA, parts.second, valueB);
            if (found == null) {
                log.warn("Failed to generate formula for {} {} {} {} {}!", operatorDefinition.getOperator(), parts.first, valueA, parts.second, valueB);
                continue;
            }

            boolean valid = checkSolution(found, parts.first, parts.second, operatorDefinition);
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

    /**
     * Verifies that calculated part of formula belongs to allowed values in FormulaDefinition.
     * @param formula calculated Formula
     * @param partA first part of formula
     * @param partB second part of formula
     * @param definition formula definition
     * @return true if formula is valid
     */
    private static boolean checkSolution(Formula formula, FormulaPart partA, FormulaPart partB, OperatorDefinition definition) {
        Values values;
        int value;
        if (partA == FormulaPart.FIRST_OPERAND) {
            if (partB == FormulaPart.SECOND_OPERAND) {
                values = definition.getResult();
                value = formula.getResult();
            } else {
                values = definition.getSecondOperand();
                value = formula.getSecondOperand();
            }
        } else if (partA == FormulaPart.SECOND_OPERAND) {
            if (partB == FormulaPart.FIRST_OPERAND) {
                values = definition.getResult();
                value = formula.getResult();
            } else {
                values = definition.getFirstOperand();
                value = formula.getFirstOperand();
            }
        } else {
            if (partB == FormulaPart.FIRST_OPERAND) {
                values = definition.getSecondOperand();
                value = formula.getSecondOperand();
            } else {
                values = definition.getFirstOperand();
                value = formula.getFirstOperand();
            }
        }
        return values.belongs(value);
    }

    /**
     * Finds complements to unknown formula part
     * @param unknown formula part to be calculated
     * @return two remaning formula parts
     */
    private static Pair<FormulaPart, FormulaPart> findRemainingFormulaParts(FormulaPart unknown) {
        if (unknown == FormulaPart.RESULT) {
            return new Pair<>(FormulaPart.FIRST_OPERAND, FormulaPart.SECOND_OPERAND);
        }

        if (unknown == FormulaPart.FIRST_OPERAND){
            return new Pair<>(FormulaPart.RESULT, FormulaPart.SECOND_OPERAND);
        }

        return new Pair<>(FormulaPart.FIRST_OPERAND, FormulaPart.RESULT);
    }

    private static OperatorDefinition getOperator(List<OperatorDefinition> operators) {
        if (operators == null || operators.isEmpty()) {
            return new OperatorDefinition(Operator.PLUS, Values.DEMO, Values.DEMO, Values.DEMO);
        }
        if (operators.size() == 1) {
            return operators.get(0);
        }
        return operators.get(random.nextInt(operators.size()));
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

    private static Values getValues(OperatorDefinition definition, FormulaPart part) {
        Values values;
        switch (part) {
            case FIRST_OPERAND:
                values = definition.getFirstOperand();
                break;
            case SECOND_OPERAND:
                values = definition.getSecondOperand();
                break;
            default:
                values = definition.getResult();
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
