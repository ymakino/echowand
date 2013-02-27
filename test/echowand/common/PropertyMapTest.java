package echowand.common;

import echowand.common.EPC;
import echowand.common.PropertyMap;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class PropertyMapTest {
    
    @Test
    public void testSetAndUnset() {
        byte[] data = new byte[]{(byte)0x01, (byte)0x80};
        
        PropertyMap map = new PropertyMap(data);
        assertEquals(1, map.count());
        assertTrue(map.isSet(EPC.x80));
        
        map.set(EPC.x88);
        assertEquals(2, map.count());
        assertTrue(map.isSet(EPC.x88));
        
        map.unset(EPC.x80);
        assertEquals(1, map.count());
        assertFalse(map.isSet(EPC.x80));
        
        map.set(EPC.xFF);
        assertEquals(2, map.count());
        assertTrue(map.isSet(EPC.xFF));
        
        map.set(EPC.Invalid);
        assertEquals(2, map.count());
        assertFalse(map.isSet(EPC.Invalid));
        
        map.unset(EPC.Invalid);
        assertEquals(2, map.count());
        
        map.set(EPC.Invalid);
        assertEquals(2, map.count());
        assertFalse(map.isSet(EPC.Invalid));
        
        map.unset(EPC.Invalid);
        assertEquals(2, map.count());
        
        map.unset(EPC.x88);
        assertEquals(1, map.count());
        
        for (int i=0xb0; i<0xcf; i++) {
            assertFalse(map.isSet(EPC.fromByte((byte)i)));
            map.set(EPC.fromByte((byte)i));
            assertTrue(map.isSet(EPC.fromByte((byte)i)));
        }
        assertEquals(0x20, map.count());
        
        byte[] newData = map.toBytes();
        assertEquals(0x20, newData[0]);
        for (int i=1; i<16; i++) {
            assertEquals(0x18, newData[i]);
        }
        assertEquals((byte)0x88, newData[16]);
    }
    
    
    @Test
    public void testCreation() {
        byte[] data1 = new byte[]{
            0x10,
            0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
            0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
        PropertyMap map1 = new PropertyMap(data1);
        assertEquals(16, map1.count());
        for (int i=0x80; i<=0x8f; i++) {
            assertTrue(map1.isSet(EPC.fromByte((byte)i)));
        }
        for (int i=0x90; i<=0xff; i++) {
            assertFalse(map1.isSet(EPC.fromByte((byte)i)));
        }
        
        data1[0] = (byte)128;
        for (int i=1; i<=16; i++) {
            data1[i] = (byte)0xff;
        }
        map1 = new PropertyMap(data1);
        assertEquals(128, map1.count());
        for (int i=0x80; i<=0xff; i++) {
            assertTrue(map1.isSet(EPC.fromByte((byte)i)));
        }
        
        byte[] data2 = new byte[] {
            (byte)0x04, (byte)0x80, (byte)0xa4, (byte)0xda, (byte)0xff
        };
        PropertyMap map2 = new PropertyMap(data2);
        assertEquals(4, map2.count());
        assertTrue(map2.isSet(EPC.x80));
        assertTrue(map2.isSet(EPC.xA4));
        assertTrue(map2.isSet(EPC.xDA));
        assertTrue(map2.isSet(EPC.xFF));
    }
    
    @Test
    public void testSupportInvalid17MapAllZero() {
        byte[] data1 = new byte[]{
            0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        PropertyMap map1 = new PropertyMap(data1);
        
        assertEquals(0, map1.count());
        
        for (int i=0x80; i<=0xff; i++) {
            assertFalse(map1.isSet(EPC.fromByte((byte)i)));
        }
    }
    
    @Test
    public void testSupportInvalid17Map() {
        byte[] data1 = new byte[]{
            0x0f,
            0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
            0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00};
        PropertyMap map1 = new PropertyMap(data1);
        
        assertEquals(15, map1.count());
        
        for (int i=0x80; i<=0x8f; i++) {
            if (i == 0x8f) {
                assertFalse(map1.isSet(EPC.fromByte((byte)0x8f)));
            } else {
                assertTrue(map1.isSet(EPC.fromByte((byte)i)));
            }
        }
        for (int i=0x90; i<=0xff; i++) {
            assertFalse(map1.isSet(EPC.fromByte((byte)i)));
        }
    }
}
