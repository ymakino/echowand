package echowand.util;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class ConstraintIntTest {
    
    @Test
    public void testConstraintNoShort() {
        Constraint constraint = new ConstraintInt();
        assertTrue(constraint.isValid(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, -2}));
        assertTrue(constraint.isValid(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, -1}));
        assertTrue(constraint.isValid(new byte[]{0, 0, 0, (byte)0xff}));
        assertTrue(constraint.isValid(new byte[]{1, 0, 0, 0}));
    }
    
    @Test
    public void testConstraintShort() {
        Constraint constraint = new ConstraintInt(-1, 0x00ffffff);
        assertFalse(constraint.isValid(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, -2}));
        assertTrue(constraint.isValid(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, -1}));
        assertTrue(constraint.isValid(new byte[]{0, (byte)0xff, (byte)0xff, (byte)0xff}));
        assertFalse(constraint.isValid(new byte[]{1, 0, 0, 0}));
    }
    
    @Test
    public void testConstraintUnsignedShort() {
        Constraint constraint = new ConstraintInt(0x00000001, 0x80000000);
        assertFalse(constraint.isValid(new byte[]{0, 0, 0, 0}));
        assertTrue(constraint.isValid(new byte[]{0, 0, 0, 1}));
        assertTrue(constraint.isValid(new byte[]{(byte)0x80, 0, 0, 0}));
        assertFalse(constraint.isValid(new byte[]{(byte)0x80, 0, 0, 0x01}));
    }
}
