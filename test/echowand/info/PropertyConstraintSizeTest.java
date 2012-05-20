package echowand.info;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class PropertyConstraintSizeTest {
    
    @Test
    public void testMinAndMaxSize() {
        PropertyConstraintSize constraint = new PropertyConstraintSize(9, 10);
        assertEquals(9, constraint.getMinSize());
        assertEquals(10, constraint.getMaxSize());
        
        Set<Integer> s = new HashSet<Integer>();
        s.add(1);
        s.add(9);
        s.add(5);
        PropertyConstraintSize constraint2 = new PropertyConstraintSize(s);
        assertEquals(1, constraint2.getMinSize());
        assertEquals(9, constraint2.getMaxSize());
    }
    
    @Test
    public void testDataSize() {
        PropertyConstraintSize constraint = new PropertyConstraintSize(10);
        assertFalse(constraint.isAcceptable(new byte[9]));
        assertTrue(constraint.isAcceptable(new byte[10]));
        assertFalse(constraint.isAcceptable(new byte[11]));
    }
    
    @Test
    public void testVariableDataSize() {
        PropertyConstraintSize constraint = new PropertyConstraintSize(9, 11);
        assertFalse(constraint.isAcceptable(new byte[8]));
        assertTrue(constraint.isAcceptable(new byte[9]));
        assertTrue(constraint.isAcceptable(new byte[10]));
        assertTrue(constraint.isAcceptable(new byte[11]));
        assertFalse(constraint.isAcceptable(new byte[12]));
    }
    
    @Test
    public void testSizeSet() {
        Set<Integer> s = new HashSet<Integer>();
        s.add(1);
        s.add(3);
        PropertyConstraintSize constraint = new PropertyConstraintSize(s);
        constraint.setInitialData(new byte[]{0x12, 0x34});
        assertFalse(constraint.isAcceptable(new byte[0]));
        assertTrue(constraint.isAcceptable(new byte[1]));
        assertFalse(constraint.isAcceptable(new byte[2]));
        assertTrue(constraint.isAcceptable(new byte[3]));
        assertFalse(constraint.isAcceptable(new byte[4]));
    }
    
    @Test
    public void testEmptySizeSet() {
        PropertyConstraintSize constraint = new PropertyConstraintSize(new HashSet<Integer>());
        assertEquals(0, constraint.getMinSize());
        assertEquals(0, constraint.getMaxSize());
    }
    
    @Test
    public void testInitialData() {
        PropertyConstraintSize constraint1 = new PropertyConstraintSize(0);
        assertTrue(Arrays.equals(new byte[]{}, constraint1.getInitialData()));
        
        PropertyConstraintSize constraint2 = new PropertyConstraintSize(1);
        assertTrue(Arrays.equals(new byte[]{0x00}, constraint2.getInitialData()));
        
        PropertyConstraintSize constraint3 = new PropertyConstraintSize(2);
        assertTrue(Arrays.equals(new byte[]{0x00, 0x00}, constraint3.getInitialData()));
        
        PropertyConstraintSize constraint4 = new PropertyConstraintSize(1, new byte[]{0x12, 0x34});
        assertTrue(Arrays.equals(new byte[]{0x12, 0x34}, constraint4.getInitialData()));
        
        PropertyConstraintSize constraint5 = new PropertyConstraintSize(1, 3, new byte[]{0x56, 0x78});
        assertTrue(Arrays.equals(new byte[]{0x56, 0x78}, constraint5.getInitialData()));
        
        Set<Integer> s = new HashSet<Integer>();
        s.add(1);
        s.add(3);
        PropertyConstraintSize constraint6 = new PropertyConstraintSize(s, new byte[]{(byte)0x9a, (byte)0xbc});
        assertTrue(Arrays.equals(new byte[]{(byte)0x9a, (byte)0xbc}, constraint6.getInitialData()));
    }
    
    @Test
    public void testUpdateInitialData() {
        PropertyConstraintSize constraint = new PropertyConstraintSize(0);
        constraint.setInitialData(new byte[]{0x12, 0x34});
        assertTrue(Arrays.equals(new byte[]{0x12, 0x34}, constraint.getInitialData()));
    }
}
