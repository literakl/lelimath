package lelisoft.com.lelimath.data;

import junit.framework.TestCase;

import java.math.BigDecimal;

/**
 * Created by Leoš on 8. 2. 2015.
 */
public class FormulaTest extends TestCase {

    public void testContentManipulation() {
        Formula formula = new Formula(null, null, null, null, null);
        formula.append('1');
        formula.append('2');
        assertEquals("12", formula.getEntry());
        formula.removeLastChar();
        formula.removeLastChar();
        formula.append('6');
        assertEquals("6", formula.getEntry());

        // TODO localization
        formula.append(',');
        formula.append(',');
        formula.append('5');
        assertEquals("6,5", formula.getEntry());

        formula.removeLastChar();
        formula.removeLastChar();
        assertEquals("6", formula.getEntry());
    }

    public void testSolvePlus() {
        Formula formula = new Formula(new BigDecimal(12), new BigDecimal(5), null, Operator.PLUS, Unknown.RESULT );
        formula.append('1');
        formula.append('7');
        assertEquals("17", formula.solve());

        formula = new Formula(new BigDecimal(12), new BigDecimal(5), new BigDecimal(17), Operator.PLUS, Unknown.OPERATOR );
        formula.append('+');
        assertEquals("+", formula.solve());

        formula = new Formula(null, new BigDecimal(5), new BigDecimal(17), Operator.PLUS, Unknown.FIRST_OPERAND );
        formula.append('1');
        formula.append('2');
        assertEquals("12", formula.solve());

        formula = new Formula(new BigDecimal(12), null, new BigDecimal(17), Operator.PLUS, Unknown.SECOND_OPERAND );
        formula.append('5');
        assertEquals("5", formula.solve());
    }
}
