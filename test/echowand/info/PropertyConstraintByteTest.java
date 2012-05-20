package echowand.info;

import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class PropertyConstraintByteTest {
    
    @Test
    public void testNoConstraintByte() {
        PropertyConstraint constraint = new PropertyConstraintByte();
        assertTrue(constraint.isAcceptable(new byte[]{-2}));
        assertTrue(constraint.isAcceptable(new byte[]{-1}));
        assertTrue(constraint.isAcceptable(new byte[]{(byte)0x7e}));
        assertTrue(constraint.isAcceptable(new byte[]{(byte)0x7f}));
    }
    
    @Test
    public void testConstraintByte() {
        PropertyConstraint constraint = new PropertyConstraintByte((byte)-1, (byte)0x7e);
        assertFalse(constraint.isAcceptable(new byte[]{-2}));
        assertTrue(constraint.isAcceptable(new byte[]{-1}));
        assertTrue(constraint.isAcceptable(new byte[]{(byte)0x7e}));
        assertFalse(constraint.isAcceptable(new byte[]{(byte)0x7f}));
    }
    
    @Test
    public void testConstraintUnsignedByte() {
        PropertyConstraint constraint = new PropertyConstraintByte((byte)1, (byte)0xfe);
        assertFalse(constraint.isAcceptable(new byte[]{0}));
        assertTrue(constraint.isAcceptable(new byte[]{1}));
        assertTrue(constraint.isAcceptable(new byte[]{(byte)0xfe}));
        assertFalse(constraint.isAcceptable(new byte[]{(byte)0xff}));
    }
    
    @Test
    public void testInitialData() {
        PropertyConstraint constraint = new PropertyConstraintByte((byte)1, (byte)0xfe);
        assertTrue(Arrays.equals(new byte[]{0x01}, constraint.getInitialData()));
    }
}
