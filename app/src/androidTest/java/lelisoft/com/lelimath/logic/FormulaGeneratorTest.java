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
        ArrayList<Formula> formulas = FormulaGenerator.generateFormulas(definition, 100);
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
}
