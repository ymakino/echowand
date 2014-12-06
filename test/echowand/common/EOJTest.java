package echowand.common;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.*;
/**
 *
 * @author Yoshiki Makino
 */
public class EOJTest {
    
    public EOJ eojs1[] = new EOJ[3];
    public EOJ eojs2[] = new EOJ[3];
    public EOJ node_eoj;
    
    public EOJTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        try {
            eojs1[0] = new EOJ((byte)0x00, (byte)0x00, (byte)0x00);
            eojs1[1] = new EOJ((byte)0x12, (byte)0x34, (byte)0x56);
            eojs1[2] = new EOJ((byte)0xff, (byte)0xff, (byte)0xff);

            eojs2[0] = new EOJ("000000");
            eojs2[1] = new EOJ("123456");
            eojs2[2] = new EOJ("fFFfFf");
            node_eoj = new EOJ((byte)0x0E, (byte)0xF0, (byte)0x01);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_fail_creation1() {
        EOJ err = new EOJ("G11111");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_fail_creation2() {
        EOJ err = new EOJ("111G11");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_fail_creation3() {
        EOJ err = new EOJ("11111G");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_fail_creation4() {
        EOJ err = new EOJ("");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_fail_creation5() {
        EOJ err = new EOJ("11111");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_fail_creation6() {
        EOJ err = new EOJ("1111111");
    }
    
    
    
    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void testGetter() {
        assertEquals((byte)0x00, eojs1[0].getClassGroupCode());
        assertEquals((byte)0x00, eojs1[0].getClassCode());
        assertEquals((byte)0x00, eojs1[0].getInstanceCode());
        
        assertEquals((byte)0x00, eojs2[0].getClassGroupCode());
        assertEquals((byte)0x00, eojs2[0].getClassCode());
        assertEquals((byte)0x00, eojs2[0].getInstanceCode());
        
        assertEquals((byte)0x12, eojs1[1].getClassGroupCode());
        assertEquals((byte)0x34, eojs1[1].getClassCode());
        assertEquals((byte)0x56, eojs1[1].getInstanceCode());
        
        assertEquals((byte)0x12, eojs2[1].getClassGroupCode());
        assertEquals((byte)0x34, eojs2[1].getClassCode());
        assertEquals((byte)0x56, eojs2[1].getInstanceCode());
        
        assertEquals((byte)0xff, eojs1[2].getClassGroupCode());
        assertEquals((byte)0xff, eojs1[2].getClassCode());
        assertEquals((byte)0xff, eojs1[2].getInstanceCode());
        
        assertEquals((byte)0xff, eojs2[2].getClassGroupCode());
        assertEquals((byte)0xff, eojs2[2].getClassCode());
        assertEquals((byte)0xff, eojs2[2].getInstanceCode());
        
        assertEquals(0x000000, eojs1[0].intValue());
        assertEquals(0x123456, eojs1[1].intValue());
        assertEquals(0xffffff, eojs1[2].intValue());
        
        assertEquals(0x000000, eojs2[0].intValue());
        assertEquals(0x123456, eojs2[1].intValue());
        assertEquals(0xffffff, eojs2[2].intValue());
        
        assertEquals("000000", eojs1[0].toString());
        assertEquals("123456", eojs1[1].toString());
        assertEquals("ffffff", eojs1[2].toString());
        
        assertEquals("000000", eojs2[0].toString());
        assertEquals("123456", eojs2[1].toString());
        assertEquals("ffffff", eojs2[2].toString());
        
        assertEquals((byte)0x00, eojs1[0].getClassEOJ().getClassGroupCode());
        assertEquals((byte)0x00, eojs1[0].getClassEOJ().getClassCode());
        
        assertEquals((byte)0x12, eojs1[1].getClassEOJ().getClassGroupCode());
        assertEquals((byte)0x34, eojs1[1].getClassEOJ().getClassCode());
        
        assertEquals((byte)0xff, eojs1[2].getClassEOJ().getClassGroupCode());
        assertEquals((byte)0xff, eojs1[2].getClassEOJ().getClassCode());
    }
    
    @Test
    public void testEquals() {
        assertTrue(eojs1[0].equals(eojs1[0]));
        assertFalse(eojs1[0].equals(eojs1[1]));
        assertFalse(eojs1[0].equals(eojs1[2]));
        assertFalse(eojs1[1].equals(eojs2[0]));
        assertTrue(eojs1[1].equals(eojs2[1]));
        assertFalse(eojs1[1].equals(eojs2[2]));
        assertFalse(eojs1[2].equals(eojs2[0]));
        assertFalse(eojs1[2].equals(eojs2[1]));
        assertTrue(eojs1[2].equals(eojs2[2]));
        
        assertTrue(eojs2[0].equals(eojs2[0]));
        assertFalse(eojs2[0].equals(eojs2[1]));
        assertFalse(eojs2[0].equals(eojs2[2]));
        assertFalse(eojs2[1].equals(eojs1[0]));
        assertTrue(eojs2[1].equals(eojs1[1]));
        assertFalse(eojs2[1].equals(eojs1[2]));
        assertFalse(eojs2[2].equals(eojs1[0]));
        assertFalse(eojs2[2].equals(eojs1[1]));
        assertTrue(eojs2[2].equals(eojs1[2]));
    }
    
    @Test
    public void testToBytes() {
        byte[] beoj = eojs1[1].toBytes();
        assertEquals(0x12, beoj[0]);
        assertEquals(0x34, beoj[1]);
        assertEquals(0x56, beoj[2]);
    }
    
    @Test
    public void testCreationWithBytes() {
        EOJ eoj = new EOJ(new byte[]{(byte)0x01,
                                     (byte)0x02,
                                     (byte)0x03,
                                     (byte)0x04}, 1);
        assertEquals(0x02, eoj.getClassGroupCode());
        assertEquals(0x03, eoj.getClassCode());
        assertEquals(0x04, eoj.getInstanceCode());
    }
    
    @Test
    public void testIsMemberOf() {
        EOJ eoj = new EOJ("0EF001");
        
        assertTrue(eoj.isMemberOf(new ClassEOJ("0EF0")));
        assertFalse(eoj.isMemberOf(new ClassEOJ("0EF1")));
        assertFalse(eoj.isMemberOf(new ClassEOJ("1EF0")));
    }
    
    @Test
    public void testIsAllInstance() {
        assertTrue(new EOJ("0EF000").isAllInstance());
        assertFalse(new EOJ("0EF001").isAllInstance());
    }
    
    @Test
    public void testGetEOJWithInstanceCode() {
        assertEquals(new EOJ("001101"), new EOJ("001100").getEOJWithInstanceCode((byte)0x01));
        assertEquals(new EOJ("013003"), new EOJ("013001").getEOJWithInstanceCode((byte)0x03));
        assertEquals(new EOJ("0EF000"), new EOJ("0EF002").getEOJWithInstanceCode((byte)0x00));
    }
    
    @Test
    public void testGetAllInstanceEOJ() {
        assertEquals(new EOJ("0EF000"), new EOJ("0EF001").getAllInstanceEOJ());
        assertTrue(new EOJ("0EF001").getAllInstanceEOJ().isAllInstance());
    }
    
    @Test
    public void testClassGroup() {
        for (int i=0; i<0xff; i++) {
            EOJ eoj = new EOJ((byte)i, (byte)0x00, (byte)0x01);
            switch (eoj.getClassGroupCode()) {
                case (byte)0x00:
                case (byte)0x01:
                case (byte)0x02:
                case (byte)0x03:
                case (byte)0x04:
                case (byte)0x05:
                case (byte)0x06:
                    assertFalse(eoj.isProfileObject());
                    assertFalse(eoj.isNodeProfileObject());
                    assertTrue(eoj.isDeviceObject());
                    break;
                case (byte)0x0E:
                    assertTrue(eoj.isProfileObject());
                    if (eoj.getClassCode() == (byte) 0xf0) {
                        assertTrue(eoj.isNodeProfileObject());
                    } else {
                        assertFalse(eoj.isNodeProfileObject());
                    }
                    assertFalse(eoj.isDeviceObject());
                    break;
                default:
                    assertFalse(eoj.isProfileObject());
                    assertFalse(eoj.isNodeProfileObject());
                    assertFalse(eoj.isDeviceObject());
            }
        }
    }
}
