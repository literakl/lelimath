package lelisoft.com.lelimath.logic;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.OperatorDefinition;
import lelisoft.com.lelimath.data.SequenceOrder;
import lelisoft.com.lelimath.data.Values;

/**
 * Logic test
 * Created by leos.literak on 27.2.2015.
 */
public class FormulaGeneratorTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(FormulaGeneratorTest.class);

    public void testRandomGenerator() {
        Values left = Values.fromRange(0, 99);
        Values right = Values.parse("0-9,20-40,50-90");
        Values result = Values.fromList(10, 11, 12);

        FormulaDefinition definition = new FormulaDefinition();
        OperatorDefinition operatorDefinition = new OperatorDefinition(Operator.PLUS, left, right, result);
        definition.addOperator(operatorDefinition);
        definition.addUnknown(FormulaPart.RESULT);

        long start = System.currentTimeMillis();
        ArrayList<Formula> formulas = new FormulaGenerator().generateFormulas(definition, 100);
        long end = System.currentTimeMillis();
        log.debug("Generating {} formulas took {} ms", formulas.size(), end - start);

        for (Formula formula : formulas) {
            assertEquals(FormulaPart.RESULT, formula.getUnknown());
            assertEquals(Operator.PLUS, formula.getOperator());
            assertTrue(formula.getFirstOperand() + formula.getSecondOperand() == formula.getResult());
            assertTrue(formula.getFirstOperand() >=0 && formula.getFirstOperand() <= 99);
            assertTrue(formula.getSecondOperand() >=0 && formula.getSecondOperand() <= 90);
            assertTrue(formula.getResult() >=10 && formula.getResult() <= 12);
        }
    }

    public void testAscendingOrderGenerator() {
        Values left = Values.fromRange(0, 9);
        Values right = Values.parse("1");
        Values result = Values.fromRange(0, 10);

        FormulaDefinition definition = new FormulaDefinition();
        OperatorDefinition operatorDefinition = new OperatorDefinition(Operator.PLUS, left, right, result);
        definition.addOperator(operatorDefinition);
        definition.addUnknown(FormulaPart.RESULT);

        long start = System.currentTimeMillis();
        FormulaGenerator generator = new FormulaGenerator(SequenceOrder.ASCENDING, FormulaPart.FIRST_OPERAND);
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
        Values right = Values.parse("1,2,3,4,5,6,7,8,9");
        Values result = Values.fromRange(0, 10);

        FormulaDefinition definition = new FormulaDefinition();
        OperatorDefinition operatorDefinition = new OperatorDefinition(Operator.MINUS, left, right, result);
        definition.addOperator(operatorDefinition);
        definition.addUnknown(FormulaPart.RESULT);

        long start = System.currentTimeMillis();
        FormulaGenerator generator = new FormulaGenerator(SequenceOrder.DESCENDING, FormulaPart.SECOND_OPERAND);
        ArrayList<Formula> formulas = generator.generateFormulas(definition, 9);
        long end = System.currentTimeMillis();
        log.debug("Generating {} formulas took {} ms", formulas.size(), end - start);

        for (int i = 0; i < 9; i++) {
            Formula formula = formulas.get(i);
            assertTrue(formula.getFirstOperand() - formula.getSecondOperand() == formula.getResult());
            assertEquals(right.getValueAt(8 - i).intValue(), formula.getSecondOperand().intValue());
        }
    }

    public void testFixedPairsGenerator() {
        Values left = Values.fromList(1,2,3,4,5,6,7,8,9,10);
        Values right = Values.parse("1,2,3,4,5,6,7,8,9,10");
        Values result = Values.fromRange(2, 20);

        FormulaDefinition definition = new FormulaDefinition();
        OperatorDefinition operatorDefinition = new OperatorDefinition(Operator.PLUS, left, right, result);
        definition.addOperator(operatorDefinition);
        definition.addUnknown(FormulaPart.RESULT);

        long start = System.currentTimeMillis();
        FormulaGenerator generator = new FormulaGenerator(SequenceOrder.FIXED_PAIRS, null);
        ArrayList<Formula> formulas = generator.generateFormulas(definition, 10);
        long end = System.currentTimeMillis();
        log.debug("Generating {} formulas took {} ms", formulas.size(), end - start);

        for (int i = 0; i < 10; i++) {
            Formula formula = formulas.get(i);
            assertTrue(formula.getFirstOperand() + formula.getSecondOperand() == formula.getResult());
            assertEquals(left.getValueAt(i).intValue(), formula.getFirstOperand().intValue());
            assertEquals(right.getValueAt(i).intValue(), formula.getSecondOperand().intValue());
        }
    }
}
