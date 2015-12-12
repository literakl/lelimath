package lelisoft.com.lelimath.data;

import junit.framework.TestCase;

/**
 * Created by Leoš on 06.12.2015.
 */
public class FormulaDefinitionTest extends TestCase {

    public void testFormulaLengthMinMax() {
        FormulaDefinition fd = new FormulaDefinition();
        fd.setLeftOperand(new Values(0, 99));
        fd.setRightOperand(new Values(-10, -50));
        fd.setResult(new Values(0, 200));
        int length = fd.getFormulaMaximumLength();
        assertEquals("99 + -10 = 200".length(), length);
    }

    public void testFormulaLengthValues() {
        FormulaDefinition fd = new FormulaDefinition();
        fd.setLeftOperand(new Values(new Integer[] {-100, 0, 1, 10, 101}));
        fd.setRightOperand(new Values(new Integer[] {0}));
        fd.setResult(new Values(new Integer[] {99999}));
        int length = fd.getFormulaMaximumLength();
        assertEquals("-101 + 0 = 99999".length(), length);
    }
}
