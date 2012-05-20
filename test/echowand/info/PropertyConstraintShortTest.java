package echowand.info;

import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class PropertyConstraintShortTest {
    
    @Test
    public void testConstraintNoShort() {
        PropertyConstraint constraint = new PropertyConstraintShort();
        assertTrue(constraint.isAcceptable(new byte[]{(byte)0xff, -2}));
        assertTrue(constraint.isAcceptable(new byte[]{(byte)0xff, -1}));
        assertTrue(constraint.isAcceptable(new byte[]{0, (byte)0xff}));
        assertTrue(constraint.isAcceptable(new byte[]{1, 0}));
    }
    
    @Test
    public void testConstraintShort() {
        PropertyConstraint constraint = new PropertyConstraintShort((short)-1, (short)0x00ff);
        assertFalse(constraint.isAcceptable(new byte[]{(byte)0xff, -2}));
        assertTrue(constraint.isAcceptable(new byte[]{(byte)0xff, -1}));
        assertTrue(constraint.isAcceptable(new byte[]{0, (byte)0xff}));
        assertFalse(constraint.isAcceptable(new byte[]{1, 0}));
    }
    
    @Test
    public void testConstraintUnsignedShort() {
        PropertyConstraint constraint = new PropertyConstraintShort((short)0x0001, (short)0x8000);
        assertFalse(constraint.isAcceptable(new byte[]{0, 0}));
        assertTrue(constraint.isAcceptable(new byte[]{0, 1}));
        assertTrue(constraint.isAcceptable(new byte[]{(byte)0x80, 0}));
        assertFalse(constraint.isAcceptable(new byte[]{(byte)0x80, 0x01}));
    }
    
    @Test
    public void testInitialData() {
        PropertyConstraint constraint = new PropertyConstraintShort((short)0x0001, (short)0x8000);
        assertTrue(Arrays.equals(new byte[]{0x00, 0x01}, constraint.getInitialData()));
    }
}
