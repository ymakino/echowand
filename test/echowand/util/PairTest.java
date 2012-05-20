package echowand.util;

import echowand.util.Pair;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class PairTest {

    /**
     * Test of toString method, of class Pair.
     */
    @Test
    public void testToString() {
        Pair<String, String> instance = new Pair<String, String>("S1", "S2");
        assertEquals("Pair(S1, S2)", instance.toString());
    }

    /**
     * Test of hashCode method, of class Pair.
     */
    @Test
    public void testHashCode() {
        Pair<String, String> instance1 = new Pair<String, String>("S1", "S2");
        Pair<String, String> instance2 = new Pair<String, String>("S1", "S2");
        Pair<String, String> instance3 = new Pair<String, String>("S1", "SS2");
        Pair<String, String> instance4 = new Pair<String, String>("SS1", "S2");
        assertTrue(instance1.hashCode() == instance2.hashCode());
        assertFalse(instance1.hashCode() == instance3.hashCode());
        assertFalse(instance1.hashCode() == instance4.hashCode());
    }

    /**
     * Test of equals method, of class Pair.
     */
    @Test
    public void testEquals() {
        Pair<String, String> instance1 = new Pair<String, String>("S1", "S2");
        Pair<String, String> instance2 = new Pair<String, String>("S1", "S2");
        Pair<String, String> instance3 = new Pair<String, String>("S1", "SS2");
        Pair<String, String> instance4 = new Pair<String, String>("SS1", "S2");
        assertTrue(instance1.equals(instance2));
        assertFalse(instance1.equals(instance3));
        assertFalse(instance1.equals(instance4));
    }
}
