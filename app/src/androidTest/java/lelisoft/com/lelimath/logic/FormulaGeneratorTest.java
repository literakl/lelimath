package lelisoft.com.lelimath.logic;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.Expression;
import lelisoft.com.lelimath.data.Values;

import static lelisoft.com.lelimath.data.FormulaPart.RESULT;
import static lelisoft.com.lelimath.data.FormulaPart.THIRD_OPERAND;
import static lelisoft.com.lelimath.data.Operator.MINUS;
import static lelisoft.com.lelimath.data.Operator.MULTIPLY;
import static lelisoft.com.lelimath.data.Operator.PLUS;
import static lelisoft.com.lelimath.data.SequenceOrder.*;

/**
 * Logic test
 * Created by leos.literak on 27.2.2015.
 */
public class FormulaGeneratorTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(FormulaGeneratorTest.class);

    public void testRandomGenerator() {
        Values left = Values.fromRange(0, 99);
        Values right = Values.parse("0-9,20-40,50-90", true);
        Values result = Values.fromList(10, 11, 12);

        FormulaDefinition definition = new FormulaDefinition();
        Expression expression = new Expression(left, PLUS, right, result);
        definition.addExpression(expression);
        definition.addUnknown(RESULT);

        long start = System.currentTimeMillis();
        ArrayList<Formula> formulas = new FormulaGenerator().generateFormulas(definition, 100);
        long end = System.currentTimeMillis();
        log.debug("Generating {} formulas took {} ms", formulas.size(), end - start);

        for (Formula formula : formulas) {
            assertEquals(RESULT, formula.getUnknown());
            assertEquals(PLUS, formula.getOperator());
            assertTrue(formula.getFirstOperand() + formula.getSecondOperand() == formula.getResult());
            assertTrue(formula.getFirstOperand() >=0 && formula.getFirstOperand() <= 99);
            assertTrue(formula.getSecondOperand() >=0 && formula.getSecondOperand() <= 90);
            assertTrue(formula.getResult() >=10 && formula.getResult() <= 12);
        }
    }

    public void testAscendingOrderGenerator() {
        Values left = Values.fromRange(0, 9).setOrder(ASCENDING);
        Values right = Values.parse("1", true);
        Values result = Values.fromRange(0, 10);

        FormulaDefinition definition = new FormulaDefinition();
        Expression expression = new Expression(left, PLUS,  right, result);
        definition.addExpression(expression);
        definition.addUnknown(RESULT);

        long start = System.currentTimeMillis();
        FormulaGenerator generator = new FormulaGenerator();
        ArrayList<Formula> formulas = generator.generateFormulas(definition, 10);
        long end = System.currentTimeMillis();
        log.debug("Generating {} formulas took {} ms", formulas.size(), end - start);

        for (int i = 0; i < 10; i++) {
            Formula formula = formulas.get(i);
            assertTrue(formula.getFirstOperand() + formula.getSecondOperand() == formula.getResult());
            assertEquals(formula.getFirstOperand().intValue(), i);
        }
    }

    public void testDescendingOrderGenerator() {
        Values left = Values.fromList(10);
        Values right = Values.parse("1,2,3,4,5,6,7,8,9", true).setOrder(DESCENDING);
        Values result = Values.fromRange(0, 10);

        FormulaDefinition definition = new FormulaDefinition();
        Expression expression = new Expression(left, MINUS, right, result);
        definition.addExpression(expression);
        definition.addUnknown(RESULT);

        long start = System.currentTimeMillis();
        FormulaGenerator generator = new FormulaGenerator();
        ArrayList<Formula> formulas = generator.generateFormulas(definition, 9);
        long end = System.currentTimeMillis();
        log.debug("Generating {} formulas took {} ms", formulas.size(), end - start);

        for (int i = 0; i < 9; i++) {
            Formula formula = formulas.get(i);
            assertTrue(formula.getFirstOperand() - formula.getSecondOperand() == formula.getResult());
            assertEquals(right.getValueAt(8 - i).intValue(), formula.getSecondOperand().intValue());
        }
    }

    public void testComplexFormula() {
        Values first = Values.fromList(1,2,3,4,5,6,7,8,9,10).setOrder(ASCENDING);
        Values second = Values.parse("1,2,3,4,5,6,7,8,9,10", true).setOrder(DESCENDING);
        Values third = Values.fromRange(20,22);
        Values result = Values.fromRange(20, 2200);

        FormulaDefinition definition = new FormulaDefinition();
        Expression expression = new Expression(first, MULTIPLY, second, result);
        expression.setOperator2(MULTIPLY);
        expression.setThirdOperand(third);
        definition.addExpression(expression);
        definition.addUnknown(THIRD_OPERAND);

        long start = System.currentTimeMillis();
        FormulaGenerator generator = new FormulaGenerator();
        ArrayList<Formula> formulas = generator.generateFormulas(definition, 10);
        long end = System.currentTimeMillis();
        log.debug("Generating {} formulas took {} ms", formulas.size(), end - start);

        for (int i = 0; i < 10; i++) {
            Formula f = formulas.get(i);
            assertEquals((int)f.getResult(), f.getFirstOperand() * f.getSecondOperand() * f.getThirdOperand());
            assertEquals(first.getValueAt(i).intValue(), f.getFirstOperand().intValue());
            assertEquals(second.getValueAt(9 - i).intValue(), f.getSecondOperand().intValue());
        }
    }
}
