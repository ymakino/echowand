package echowand.util;

import echowand.util.ConstraintByte;
import echowand.util.Constraint;
import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class ConstraintByteTest {
    
    @Test
    public void testNoConstraintByte() {
        Constraint constraint = new ConstraintByte();
        assertTrue(constraint.isValid(new byte[]{-2}));
        assertTrue(constraint.isValid(new byte[]{-1}));
        assertTrue(constraint.isValid(new byte[]{(byte)0x7e}));
        assertTrue(constraint.isValid(new byte[]{(byte)0x7f}));
    }
    
    @Test
    public void testConstraintByte() {
        Constraint constraint = new ConstraintByte((byte)-1, (byte)0x7e);
        assertFalse(constraint.isValid(new byte[]{-2}));
        assertTrue(constraint.isValid(new byte[]{-1}));
        assertTrue(constraint.isValid(new byte[]{(byte)0x7e}));
        assertFalse(constraint.isValid(new byte[]{(byte)0x7f}));
    }
    
    @Test
    public void testConstraintUnsignedByte() {
        Constraint constraint = new ConstraintByte((byte)1, (byte)0xfe);
        assertFalse(constraint.isValid(new byte[]{0}));
        assertTrue(constraint.isValid(new byte[]{1}));
        assertTrue(constraint.isValid(new byte[]{(byte)0xfe}));
        assertFalse(constraint.isValid(new byte[]{(byte)0xff}));
    }
}
