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
        assertEquals("12", formula.getUserInput());
        formula.undoAppend();
        formula.undoAppend();
        formula.append('6');
        assertEquals("6", formula.getUserInput());
    }

    public void testSolve() {
        Formula formula = new Formula(12, 5, null, Operator.PLUS, FormulaPart.RESULT);
        assertEquals("17", formula.solve());

        formula = new Formula(12, 5, 17, Operator.PLUS, FormulaPart.OPERATOR);
        assertEquals("+", formula.solve());

        formula = new Formula(null, 5, 17, Operator.PLUS, FormulaPart.FIRST_OPERAND);
        assertEquals("12", formula.solve());

        formula = new Formula(12, null, 17, Operator.PLUS, FormulaPart.SECOND_OPERAND);
        assertEquals("5", formula.solve());

        formula = new Formula(3, 0, null, Operator.PLUS, FormulaPart.RESULT);
        assertEquals("3", formula.solve());
    }

    public void testIsEntryCorrect() {
        Formula formula = new Formula(12, 5, 17, Operator.PLUS, FormulaPart.RESULT);
        formula.append('1');
        formula.append('7');
        assertTrue(formula.isEntryCorrect());

        formula = new Formula(12, 5, 17, Operator.PLUS, FormulaPart.OPERATOR);
        formula.append(Operator.PLUS.toString());
        assertTrue(formula.isEntryCorrect());

        formula = new Formula(12, 5, 17, Operator.PLUS, FormulaPart.FIRST_OPERAND);
        formula.append('1');
        formula.append('2');
        assertTrue(formula.isEntryCorrect());

        formula = new Formula(12, 5, 17, Operator.PLUS, FormulaPart.SECOND_OPERAND);
        formula.append('5');
        assertTrue(formula.isEntryCorrect());

        formula = new Formula(3, 0, 3, Operator.PLUS, FormulaPart.RESULT);
        formula.append('3');
        assertTrue(formula.isEntryCorrect());

        formula = new Formula(3, 0, 3, Operator.PLUS, FormulaPart.OPERATOR);
        formula.setUserEntry(Operator.PLUS.toString());
        assertTrue(formula.isEntryCorrect());
        formula.setUserEntry(Operator.MINUS.toString());
        assertTrue(formula.isEntryCorrect());
        formula.setUserEntry(Operator.MULTIPLY.toString());
        assertFalse(formula.isEntryCorrect());
        formula.setUserEntry(Operator.DIVIDE.toString());
        assertFalse(formula.isEntryCorrect());

        // such formula shall never be generated
        formula = new Formula(3, 0, null, Operator.DIVIDE, FormulaPart.OPERATOR);
        formula.setUserEntry(Operator.DIVIDE.toString());
        assertFalse(formula.isEntryCorrect());

        formula = new Formula(2, 0, 0, Operator.MULTIPLY, FormulaPart.FIRST_OPERAND);
        formula.setUserEntry("0");
        assertTrue(formula.isEntryCorrect());
        formula.setUserEntry("1");
        assertTrue(formula.isEntryCorrect());
        formula.setUserEntry("2");
        assertTrue(formula.isEntryCorrect());

        formula = new Formula(3, 0, 0, Operator.MULTIPLY, FormulaPart.OPERATOR);
        formula.setUserEntry(Operator.PLUS.toString());
        assertFalse(formula.isEntryCorrect());
        formula.setUserEntry(Operator.MINUS.toString());
        assertFalse(formula.isEntryCorrect());
        formula.setUserEntry(Operator.MULTIPLY.toString());
        assertTrue(formula.isEntryCorrect());
        formula.setUserEntry(Operator.DIVIDE.toString());
        assertFalse(formula.isEntryCorrect());
    }
}
