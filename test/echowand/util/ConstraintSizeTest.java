package echowand.util;

import echowand.util.ConstraintSize;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class ConstraintSizeTest {
    
    @Test
    public void testMinAndMaxSize() {
        ConstraintSize constraint = new ConstraintSize(9, 10);
        assertEquals(9, constraint.getMinSize());
        assertEquals(10, constraint.getMaxSize());
        
        Set<Integer> s = new HashSet<Integer>();
        s.add(1);
        s.add(9);
        s.add(5);
        ConstraintSize constraint2 = new ConstraintSize(s);
        assertEquals(1, constraint2.getMinSize());
        assertEquals(9, constraint2.getMaxSize());
    }
    
    @Test
    public void testDataSize() {
        ConstraintSize constraint = new ConstraintSize(10);
        assertFalse(constraint.isValid(new byte[9]));
        assertTrue(constraint.isValid(new byte[10]));
        assertFalse(constraint.isValid(new byte[11]));
    }
    
    @Test
    public void testVariableDataSize() {
        ConstraintSize constraint = new ConstraintSize(9, 11);
        assertFalse(constraint.isValid(new byte[8]));
        assertTrue(constraint.isValid(new byte[9]));
        assertTrue(constraint.isValid(new byte[10]));
        assertTrue(constraint.isValid(new byte[11]));
        assertFalse(constraint.isValid(new byte[12]));
    }
    
    @Test
    public void testSizeSet() {
        Set<Integer> s = new HashSet<Integer>();
        s.add(1);
        s.add(3);
        ConstraintSize constraint = new ConstraintSize(s);
        assertFalse(constraint.isValid(new byte[0]));
        assertTrue(constraint.isValid(new byte[1]));
        assertFalse(constraint.isValid(new byte[2]));
        assertTrue(constraint.isValid(new byte[3]));
        assertFalse(constraint.isValid(new byte[4]));
    }
    
    @Test
    public void testEmptySizeSet() {
        ConstraintSize constraint = new ConstraintSize(new HashSet<Integer>());
        assertEquals(0, constraint.getMinSize());
        assertEquals(0, constraint.getMaxSize());
    }
}
