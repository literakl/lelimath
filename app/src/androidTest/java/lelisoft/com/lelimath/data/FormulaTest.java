package lelisoft.com.lelimath.data;

import junit.framework.TestCase;

/**
 * Test
 * Created by Leoš on 8. 2. 2015.
 */
public class FormulaTest extends TestCase {

    public void testContentManipulation() {
        Formula formula = new Formula(null, null, null, null, null);
        formula.append('1');
        formula.append('2');
        assertEquals("12", formula.getUserEntry());
        formula.undoAppend();
        formula.undoAppend();
        formula.append('6');
        assertEquals("6", formula.getUserEntry());
    }

    public void testSolvePlus() {
        Formula formula = new Formula(12, 5, null, Operator.PLUS, FormulaPart.RESULT);
        formula.append('1');
        formula.append('7');
        assertEquals("17", formula.solve());
        assertTrue(formula.isEntryCorrect());

        formula = new Formula(12, 5, 17, Operator.PLUS, FormulaPart.OPERATOR);
        formula.append('+');
        assertEquals("+", formula.solve());
        assertTrue(formula.isEntryCorrect());

        formula = new Formula(null, 5, 17, Operator.PLUS, FormulaPart.FIRST_OPERAND);
        formula.append('1');
        formula.append('2');
        assertEquals("12", formula.solve());
        assertTrue(formula.isEntryCorrect());

        formula = new Formula(12, null, 17, Operator.PLUS, FormulaPart.SECOND_OPERAND);
        formula.append('5');
        assertEquals("5", formula.solve());
        assertTrue(formula.isEntryCorrect());
    }
}
