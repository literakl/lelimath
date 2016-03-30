package lelisoft.com.lelimath.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Values;

/**
 * This class is responsible for generating a Formula that matches assignment in a FormulaDefinition.
 * Created by leos.literak on 26.2.2015.
 */
public class FormulaGenerator {
    private static final Logger log = LoggerFactory.getLogger(FormulaGenerator.class);

    static Random random = new Random(System.currentTimeMillis());

    public static Formula generateRandomFormula(FormulaDefinition definition) {
        log.trace("Starting search for formula using " + definition);
        Operator operator = getOperator(definition.getOperators());
        List<FormulaPart> parts = sortFormulaParts(definition);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                FormulaPart partA = parts.get(j);
                FormulaPart partB = parts.get((j < 2) ? j + 1 : 0);
                int valueA = getValue(definition, partA);
                for (int k = 0; k < 10; k++) {
                    int valueB = getValue(definition, partB);
                    if (valueB == 0 && operator == Operator.DIVIDE) {
                        continue;
                    }
                    Formula found = Solver.solve(operator, partA, valueA, partB, valueB);
                    if (found == null) {
                        continue;
                    }
                    boolean valid = checkSolution(found, partA, partB, definition);
                    if (log.isTraceEnabled()) {
                        log.trace(valid + " " +
                        found.getFirstOperand() +  " " + found.getOperator() + " " +
                        found.getSecondOperand() + " = " + found.getResult());
                    }
                    if (valid) {
                        found.setUnknown(getUnknown(definition.getUnknowns()));
                        log.debug("Generated formula " + found);
                        return found;
                    }
                }
            }
        }

        log.warn("No formula found for " + definition);
        return null;
    }

    /**
     * Verifies that calculated part of formula belongs to allowed values in FormulaDefinition.
     * @param formula calculated Formula
     * @param partA first part of formula
     * @param partB second part of formula
     * @param definition formula definition
     * @return true if formula is valid
     */
    private static boolean checkSolution(Formula formula, FormulaPart partA, FormulaPart partB, FormulaDefinition definition) {
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
     * Creates list of formula parts in ascending order by number of potential values.
     * The idea is that it is easier to find a complement from bigger set than from smaller set.
     * E.g. if there are 3 values for result and 10 values for both operands then you will
     * sooner find second operand for random result and operand (3 : 100)
     * than finding result for both random operands (10 : 30) /for multiply operator/.
     * @param definition formula definition
     * @return ordered set of left, right operand and result
     */
    static List<FormulaPart> sortFormulaParts(FormulaDefinition definition) {
        int a = definition.getFirstOperand().getRange();
        int b = definition.getSecondOperand().getRange();
        int c = definition.getResult().getRange();
        List<FormulaPart> parts = new ArrayList<>(3);
        parts.add(FormulaPart.FIRST_OPERAND);
        parts.add((b < a) ? 0 : 1, FormulaPart.SECOND_OPERAND);
        if (c < b && c < a) {
            parts.add(0, FormulaPart.RESULT);
        } else if (c > a && c > b) {
            parts.add(2, FormulaPart.RESULT);
        } else {
            parts.add(1, FormulaPart.RESULT);
        }
        return parts;
    }

    static Operator getOperator(List<Operator> operators) {
        if (operators == null || operators.isEmpty()) {
            return Operator.PLUS;
        }
        if (operators.size() == 1) {
            return operators.get(0);
        }
        return operators.get(random.nextInt(operators.size()));
    }

    static FormulaPart getUnknown(List<FormulaPart> unknowns) {
        if (unknowns == null || unknowns.isEmpty()) {
            return FormulaPart.RESULT;
        }
        if (unknowns.size() == 1) {
            return unknowns.get(0);
        }
        return unknowns.get(random.nextInt(unknowns.size()));
    }

    /**
     * @return select random value from FormulaPart
     */
    static Integer getValue(FormulaDefinition definition, FormulaPart part) {
        Values values;
        if (part.equals(FormulaPart.FIRST_OPERAND)) {
            values = definition.getFirstOperand();
        } else if (part.equals(FormulaPart.SECOND_OPERAND)) {
            values = definition.getSecondOperand();
        } else {
            values = definition.getResult();
        }

        List<Integer> listing = values.getListing();
        if (listing != null) {
            return listing.get(random.nextInt(listing.size()));
        }

        int diff = values.getMaxValue() - values.getMinValue() + 1;
        return random.nextInt(diff) + values.getMinValue();
    }

    /**
     * Reserved for tests where values shall not be so random
     * @param random random value
     */
    public static void setRandom(Random random) {
        FormulaGenerator.random = random;
    }
}
