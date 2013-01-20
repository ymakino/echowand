package echowand.info;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class PropertyConstraintTemperatureTest {
    
    public PropertyConstraintTemperatureTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of isValid method, of class PropertyConstraintTemperature.
     */
    @Test
    public void testIsValid() {
        PropertyConstraintTemperature instance = new PropertyConstraintTemperature();
        assertEquals(true, instance.isValid(new byte[]{(byte)0x00, (byte)0x00}));
        
        
        assertEquals(true, instance.isValid(new byte[]{(byte)0x7f, (byte)0xfd}));
        assertEquals(false, instance.isValid(new byte[]{(byte)0x7f, (byte)0xfe}));
        
        assertEquals(true, instance.isValid(new byte[]{(byte)0xf5, (byte)0x54}));
        assertEquals(false, instance.isValid(new byte[]{(byte)0xf5, (byte)0x53}));
        
        
        assertEquals(false, instance.isValid(new byte[]{(byte)0x7f, (byte)0xfe}));
        assertEquals(true, instance.isValid(new byte[]{(byte)0x7f, (byte)0xff}));
        assertEquals(true, instance.isValid(new byte[]{(byte)0x80, (byte)0x00}));
        assertEquals(false, instance.isValid(new byte[]{(byte)0x80, (byte)0x01}));
    }
}
