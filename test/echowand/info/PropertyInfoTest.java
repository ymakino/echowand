package echowand.info;

import echowand.util.ConstraintSize;
import echowand.common.EPC;
import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class PropertyInfoTest {

    /**
     * Test of equals method, of class PropertyInfo.
     */
    @Test
    public void testEquals() {
        Object prop = null;
        PropertyInfo instance = new PropertyInfo(EPC.x88, true, true, true, 10);
        boolean result = instance.equals(prop);
        assertEquals(false, result);
        
        PropertyInfo instance2 = new PropertyInfo(EPC.x88, true, true, true, 10);
        boolean result2 = instance.equals(instance2);
        assertEquals(true, result2);
        
        PropertyInfo instance3 = new PropertyInfo(EPC.x89, true, true, true, 10);
        boolean result3 = instance.equals(instance3);
        assertEquals(false, result3);
    }
    
    @Test
    public void testAcceptable() {
        PropertyInfo instance = new PropertyInfo(EPC.x88, true, true, true, 10);
        assertFalse(instance.constraint.isValid(new byte[9]));
        assertTrue(instance.constraint.isValid(new byte[10]));
        assertFalse(instance.constraint.isValid(new byte[11]));
        
        PropertyInfo instance2 = new PropertyInfo(EPC.x88, true, true, true, 10, new ConstraintSize(9, 11));
        assertFalse(instance2.constraint.isValid(new byte[8]));
        assertTrue(instance2.constraint.isValid(new byte[9]));
        assertTrue(instance2.constraint.isValid(new byte[10]));
        assertTrue(instance2.constraint.isValid(new byte[11]));
        assertFalse(instance2.constraint.isValid(new byte[12]));
    }
    
    @Test
    public void testAcceptableData() {
        PropertyInfo instance = new PropertyInfo(EPC.x88, true, true, true, new byte[10]);
        assertFalse(instance.constraint.isValid(new byte[9]));
        assertTrue(instance.constraint.isValid(new byte[10]));
        assertFalse(instance.constraint.isValid(new byte[11]));
        
        PropertyInfo instance2 = new PropertyInfo(EPC.x88, true, true, true, new byte[9], new ConstraintSize(9, 11));
        assertFalse(instance2.constraint.isValid(new byte[8]));
        assertTrue(instance2.constraint.isValid(new byte[9]));
        assertTrue(instance2.constraint.isValid(new byte[10]));
        assertTrue(instance2.constraint.isValid(new byte[11]));
        assertFalse(instance2.constraint.isValid(new byte[12]));
    }
    
    @Test
    public void testInitialData() {
        PropertyInfo p1 = new PropertyInfo(EPC.x88, true, true, true, new byte[]{(byte)0x11, (byte)0x22}, new ConstraintSize(9, 11));
        assertTrue(Arrays.equals(new byte[]{(byte)0x11, (byte)0x22}, p1.initialData));
        
        PropertyInfo p2 = new PropertyInfo(EPC.x88, true, true, true, 4, new ConstraintSize(9, 11));
        assertTrue(Arrays.equals(new byte[4], p2.initialData));
        
        PropertyInfo p3 = new PropertyInfo(EPC.x88, true, true, true, new byte[]{(byte)0x11, (byte)0x22});
        assertTrue(Arrays.equals(new byte[]{(byte)0x11, (byte)0x22}, p3.initialData));
        
        PropertyInfo p4 = new PropertyInfo(EPC.x88, true, true, true, 4);
        assertTrue(Arrays.equals(new byte[4], p4.initialData));
    }
}
