package lelisoft.com.lelimath.data;

import junit.framework.TestCase;

import java.util.Collections;

/**
 * Created by Leoš on 06.12.2015.
 */
public class FormulaDefinitionTest extends TestCase {

    public void testFormulaLengthMinMax() {
        FormulaDefinition fd = new FormulaDefinition();
        OperatorDefinition od = new OperatorDefinition();
        od.setFirstOperand(new Values(0, 99));
        od.setSecondOperand(new Values(-10, -50));
        od.setResult(new Values(0, 200));
        fd.setOperatorDefinitions(Collections.singletonList(od));
        int length = fd.getFormulaMaximumLength();
        assertEquals("99 + -10 = 200".length(), length);
    }

    public void testFormulaLengthValues() {
        FormulaDefinition fd = new FormulaDefinition();
        OperatorDefinition od = new OperatorDefinition();
        od.setFirstOperand(new Values(new Integer[] {-100, 0, 1, 10, 101}));
        od.setSecondOperand(new Values(new Integer[] {10}));
        od.setResult(new Values(new Integer[] {99999}));
        fd.setOperatorDefinitions(Collections.singletonList(od));
        int length = fd.getFormulaMaximumLength();
        assertEquals("-100 + 10 = 99999".length(), length);
    }
}
