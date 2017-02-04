package lelisoft.com.lelimath.data;

import junit.framework.TestCase;

import lelisoft.com.lelimath.NotSoRandom;

/**
 * Tests
 * Created by Leoš on 06.12.2015.
 */
public class FormulaDefinitionTest extends TestCase {

    public void testValues() {
        Values values;
        try {
            values = Values.parse("", true);
            fail("Empty is forbidden");
        } catch (IllegalArgumentException e) { /* ok */ }

        try {
            values = Values.parse("-", true);
            fail("Incomplete range is forbidden");
        } catch (IllegalArgumentException e) { /* ok */ }

        try {
            values = Values.parse("-3", true);
            fail("Incomplete range is forbidden");
        } catch (IllegalArgumentException e) { /* ok */ }

        try {
            values = Values.parse("3-", true);
            fail("Incomplete range is forbidden");
        } catch (IllegalArgumentException e) { /* ok */ }

        try {
            values = Values.parse("3-1", true);
            fail("Decreasing is forbidden");
        } catch (IllegalArgumentException e) { /* ok */ }

        try {
            values = Values.parse("2,1-3", true);
            fail("Decreasing is forbidden");
        } catch (IllegalArgumentException e) { /* ok */ }

        try {
            values = Values.parse("1-3,2", true);
            fail("Decreasing is forbidden");
        } catch (IllegalArgumentException e) { /* ok */ }

        try {
            values = Values.parse("x", true);
            fail("Must be numbers");
        } catch (IllegalArgumentException e) { /* ok */ }

        values = Values.parse("1,2,4-5,7,8-10", true);
        assertEquals("size", 8, values.getSize());
        assertTrue(values.belongs(1));
        assertTrue(values.belongs(2));
        assertFalse(values.belongs(3));
        assertTrue(values.belongs(4));
        assertTrue(values.belongs(5));
        assertFalse(values.belongs(6));
        assertTrue(values.belongs(7));
        assertTrue(values.belongs(8));
        assertTrue(values.belongs(9));
        assertTrue(values.belongs(10));

        NotSoRandom random = new NotSoRandom(0,1,2,3,4,5,6,7);
        assertEquals(1, values.getRandomValue(random).intValue());
        assertEquals(2, values.getRandomValue(random).intValue());
        assertEquals(4, values.getRandomValue(random).intValue());
        assertEquals(5, values.getRandomValue(random).intValue());
        assertEquals(7, values.getRandomValue(random).intValue());
        assertEquals(8, values.getRandomValue(random).intValue());
        assertEquals(9, values.getRandomValue(random).intValue());
        assertEquals(10, values.getRandomValue(random).intValue());
    }
}
