package echowand.app;

import echowand.object.ObjectData;
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
public class ReadableConverterRealTest {
    
    public ReadableConverterRealTest() {
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

    private void testDataToString(ObjectData data, int precision, String expected) {
        ReadableConverterReal conv = new ReadableConverterReal(precision);
        assertEquals(expected, conv.dataToString(data));
    }
    
    @Test
    public void testZero() {
        for (int i=1; i < 100; i++) {
            testDataToString(new ObjectData((byte) 0x00), i, "0");
            testDataToString(new ObjectData((byte) 0x00, (byte) 0x00), i, "0");
            testDataToString(new ObjectData((byte) 0x00, (byte) 0x00, (byte) 0x00), i, "0");
        }
    }
    
    @Test
    public void testOne() {
        testDataToString(new ObjectData((byte) 0x01), 10, "0.1");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x01), 10, "0.1");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x00, (byte) 0x01), 10, "0.1");
    }
    
    @Test
    public void test255() {
        testDataToString(new ObjectData((byte) 0xff), 10, "-0.1");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0xff), 10, "25.5");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x00, (byte) 0xff), 10, "25.5");
    }
    
    @Test
    public void test256() {
        testDataToString(new ObjectData((byte) 0x01, (byte) 0x00), 10, "25.6");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x01, (byte) 0x00), 10, "25.6");
    }
    
    @Test
    public void test65535() {
        testDataToString(new ObjectData((byte) 0xff, (byte) 0xff), 10, "-0.1");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0xff, (byte) 0xff), 10, "6553.5");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xff), 10, "6553.5");
    }
    
    @Test
    public void test65536() {
        testDataToString(new ObjectData((byte) 0x01, (byte) 0x00, (byte) 0x00), 10, "6553.6");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00), 10, "6553.6");
    }
    
    @Test
    public void testNegateOne() {
        testDataToString(new ObjectData((byte) 0xff), 10, "-0.1");
        testDataToString(new ObjectData((byte) 0xff, (byte) 0xff), 10, "-0.1");
        testDataToString(new ObjectData((byte) 0xff, (byte) 0xff, (byte) 0xff), 10, "-0.1");
    }
    
    @Test
    public void testNegate256() {
        testDataToString(new ObjectData((byte) 0xff, (byte) 0x00), 10, "-25.6");
        testDataToString(new ObjectData((byte) 0xff, (byte) 0xff, (byte) 0x00), 10, "-25.6");
    }
    
    @Test
    public void testPrecision() {
        testDataToString(new ObjectData((byte) 0x01), 2, "0.5");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x01), 4, "0.25");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x00, (byte) 0x01), 8, "0.125");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x00, (byte) 0x01), 100, "0.01");
        testDataToString(new ObjectData((byte) 0x01, (byte) 0x00), 2, "128");
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x01, (byte) 0x00), 4, "64");
        
        testDataToString(new ObjectData((byte) 0xff), 2, "-0.5");
        testDataToString(new ObjectData((byte) 0xff, (byte) 0xff), 4, "-0.25");
        testDataToString(new ObjectData((byte) 0xff, (byte) 0xff, (byte) 0xff), 8, "-0.125");
        testDataToString(new ObjectData((byte) 0xff, (byte) 0xff, (byte) 0xff), 100, "-0.01");
        testDataToString(new ObjectData((byte) 0xff, (byte) 0x00), 2, "-128");
        testDataToString(new ObjectData((byte) 0xff, (byte) 0xff, (byte) 0x00), 4, "-64");
        
        testDataToString(new ObjectData((byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xff), 1000, "65.535");
        testDataToString(new ObjectData((byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x01), 1000, "-65.535");
    }
}
