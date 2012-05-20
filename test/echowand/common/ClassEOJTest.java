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
public class ClassEOJTest {
    
    public ClassEOJ eojs1[] = new ClassEOJ[3];
    public ClassEOJ eojs2[] = new ClassEOJ[3];
    public ClassEOJ node_eoj;
    
    @Before
    public void setUp() {
        try {
            eojs1[0] = new ClassEOJ((byte)0x00, (byte)0x00);
            eojs1[1] = new ClassEOJ((byte)0x12, (byte)0x34);
            eojs1[2] = new ClassEOJ((byte)0xff, (byte)0xff);

            eojs2[0] = new ClassEOJ("0000");
            eojs2[1] = new ClassEOJ("1234");
            eojs2[2] = new ClassEOJ("fFFf");
            node_eoj = new ClassEOJ((byte)0x0E, (byte)0xF0);
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
    
    @Test
    public void testGetter() {
        assertEquals((byte)0x00, eojs1[0].getClassGroupCode());
        assertEquals((byte)0x00, eojs1[0].getClassCode());
        
        assertEquals((byte)0x00, eojs2[0].getClassGroupCode());
        assertEquals((byte)0x00, eojs2[0].getClassCode());
        
        assertEquals((byte)0x12, eojs1[1].getClassGroupCode());
        assertEquals((byte)0x34, eojs1[1].getClassCode());
        
        assertEquals((byte)0x12, eojs2[1].getClassGroupCode());
        assertEquals((byte)0x34, eojs2[1].getClassCode());
        
        assertEquals((byte)0xff, eojs1[2].getClassGroupCode());
        assertEquals((byte)0xff, eojs1[2].getClassCode());
        
        assertEquals((byte)0xff, eojs2[2].getClassGroupCode());
        assertEquals((byte)0xff, eojs2[2].getClassCode());
        
        assertEquals(0x0000, eojs1[0].intValue());
        assertEquals(0x1234, eojs1[1].intValue());
        assertEquals(0xffff, eojs1[2].intValue());
        
        assertEquals(0x0000, eojs2[0].intValue());
        assertEquals(0x1234, eojs2[1].intValue());
        assertEquals(0xffff, eojs2[2].intValue());
        
        assertEquals("0000", eojs1[0].toString());
        assertEquals("1234", eojs1[1].toString());
        assertEquals("ffff", eojs1[2].toString());
        
        assertEquals("0000", eojs2[0].toString());
        assertEquals("1234", eojs2[1].toString());
        assertEquals("ffff", eojs2[2].toString());
        
        assertTrue(Arrays.equals(new byte[]{(byte)0x00, (byte)0x00}, eojs1[0].toBytes()));
        assertTrue(Arrays.equals(new byte[]{(byte)0x12, (byte)0x34}, eojs1[1].toBytes()));
        assertTrue(Arrays.equals(new byte[]{(byte)0xff, (byte)0xff}, eojs1[2].toBytes()));
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
        assertEquals(2, beoj.length);
        assertEquals(0x12, beoj[0]);
        assertEquals(0x34, beoj[1]);
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
    public void testClassGroup() {
        for (int i=0; i<0xff; i++) {
            ClassEOJ ceoj = new ClassEOJ((byte)i, (byte)0x00);
            switch (ceoj.getClassGroupCode()) {
                case (byte)0x00:
                case (byte)0x01:
                case (byte)0x02:
                case (byte)0x03:
                case (byte)0x04:
                case (byte)0x05:
                case (byte)0x06:
                    assertFalse(ceoj.isProfileObject());
                    assertFalse(ceoj.isNodeProfileObject());
                    assertTrue(ceoj.isDeviceObject());
                    break;
                case (byte)0x0E:
                    assertTrue(ceoj.isProfileObject());
                    if (ceoj.getClassCode() == (byte) 0xf0) {
                        assertTrue(ceoj.isNodeProfileObject());
                    } else {
                        assertFalse(ceoj.isNodeProfileObject());
                    }
                    assertFalse(ceoj.isDeviceObject());
                    break;
                default:
                    assertFalse(ceoj.isProfileObject());
                    assertFalse(ceoj.isNodeProfileObject());
                    assertFalse(ceoj.isDeviceObject());
            }
        }
    }
}
