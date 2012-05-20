package echowand.info;

import echowand.common.EPC;
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
        assertFalse(instance.isAcceptable(new byte[9]));
        assertTrue(instance.isAcceptable(new byte[10]));
        assertFalse(instance.isAcceptable(new byte[11]));
        
        PropertyInfo instance2 = new PropertyInfo(EPC.x88, true, true, true, new PropertyConstraintSize(9, 11));
        assertFalse(instance2.isAcceptable(new byte[8]));
        assertTrue(instance2.isAcceptable(new byte[9]));
        assertTrue(instance2.isAcceptable(new byte[10]));
        assertTrue(instance2.isAcceptable(new byte[11]));
        assertFalse(instance2.isAcceptable(new byte[12]));
    }
    
    @Test
    public void testAcceptableData() {
        PropertyInfo instance = new PropertyInfo(EPC.x88, true, true, true, new byte[10]);
        assertFalse(instance.isAcceptable(new byte[9]));
        assertTrue(instance.isAcceptable(new byte[10]));
        assertFalse(instance.isAcceptable(new byte[11]));
        
        PropertyInfo instance2 = new PropertyInfo(EPC.x88, true, true, true, new PropertyConstraintSize(9, 11), new byte[9]);
        assertFalse(instance2.isAcceptable(new byte[8]));
        assertTrue(instance2.isAcceptable(new byte[9]));
        assertTrue(instance2.isAcceptable(new byte[10]));
        assertTrue(instance2.isAcceptable(new byte[11]));
        assertFalse(instance2.isAcceptable(new byte[12]));
    }
}
