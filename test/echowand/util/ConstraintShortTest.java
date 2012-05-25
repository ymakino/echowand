package echowand.util;

import echowand.util.ConstraintShort;
import echowand.util.Constraint;
import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class ConstraintShortTest {
    
    @Test
    public void testConstraintNoShort() {
        Constraint constraint = new ConstraintShort();
        assertTrue(constraint.isValid(new byte[]{(byte)0xff, -2}));
        assertTrue(constraint.isValid(new byte[]{(byte)0xff, -1}));
        assertTrue(constraint.isValid(new byte[]{0, (byte)0xff}));
        assertTrue(constraint.isValid(new byte[]{1, 0}));
    }
    
    @Test
    public void testConstraintShort() {
        Constraint constraint = new ConstraintShort((short)-1, (short)0x00ff);
        assertFalse(constraint.isValid(new byte[]{(byte)0xff, -2}));
        assertTrue(constraint.isValid(new byte[]{(byte)0xff, -1}));
        assertTrue(constraint.isValid(new byte[]{0, (byte)0xff}));
        assertFalse(constraint.isValid(new byte[]{1, 0}));
    }
    
    @Test
    public void testConstraintUnsignedShort() {
        Constraint constraint = new ConstraintShort((short)0x0001, (short)0x8000);
        assertFalse(constraint.isValid(new byte[]{0, 0}));
        assertTrue(constraint.isValid(new byte[]{0, 1}));
        assertTrue(constraint.isValid(new byte[]{(byte)0x80, 0}));
        assertFalse(constraint.isValid(new byte[]{(byte)0x80, 0x01}));
    }
}
