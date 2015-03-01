package lelisoft.com.lelimath.logic;

import junit.framework.TestCase;

import java.util.List;

import lelisoft.com.lelimath.data.Formula;
import lelisoft.com.lelimath.data.FormulaDefinition;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.Operator;
import lelisoft.com.lelimath.data.Values;

/**
 * Logic test
 * Created by leos.literak on 27.2.2015.
 */
public class FormulaGeneratorTest extends TestCase {

    public void testRandomGenerator() {
        FormulaGenerator.setDebug(true);
        Values left = new Values(0, 90);
        Values right = new Values(3, 60);
        Values result = new Values().add(10).add(11).add(12);

        FormulaDefinition definition = new FormulaDefinition();
        definition.setLeftOperand(left);
        definition.setRightOperand(right);
        definition.setResult(result);
        definition.addOperator(Operator.PLUS);
        definition.addUnknown(FormulaPart.RESULT);

        for (int i = 0; i < 10; i++) {
            Formula formula = FormulaGenerator.generateRandomFormula(definition);
            if (formula == null) {
                continue;
            }
            assertEquals(FormulaPart.RESULT, formula.getUnknown());
            assertEquals(Operator.PLUS, formula.getOperator());
            assertTrue(formula.getFirstOperand() + formula.getSecondOperand() == formula.getResult());
            assertTrue(formula.getFirstOperand() >=0 && formula.getFirstOperand() <= 90);
            assertTrue(formula.getSecondOperand() >=3 && formula.getSecondOperand() <= 60);
            assertTrue(formula.getResult() >=10 && formula.getResult() <= 12);
        }
    }

    public void testOrderingFormulaParts() {
        Values left = new Values(0, 9);
        Values right = new Values(3, 6);
        Values result = new Values();
        result.add(10).add(11).add(12);
        assertEquals(10, left.getRange());
        assertEquals(4, right.getRange());
        assertEquals(3, result.getRange());

        FormulaDefinition definition = new FormulaDefinition();
        definition.setLeftOperand(left);
        definition.setRightOperand(right);
        definition.setResult(result);
        List<FormulaPart> parts = FormulaGenerator.sortFormulaParts(definition);
        assertEquals(FormulaPart.RESULT, parts.get(0));
        assertEquals(FormulaPart.SECOND_OPERAND, parts.get(1));
        assertEquals(FormulaPart.FIRST_OPERAND, parts.get(1));
        assertEquals(FormulaPart.RESULT, parts.get(2));
    }
}
