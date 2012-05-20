package echowand.object;

import echowand.common.Data;
import echowand.object.ObjectData;
import java.util.Arrays;
import java.util.LinkedList;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectDataTest {
    
    @Test
    public void testCreation() {
        ObjectData data = new ObjectData(new byte[]{});
        assertEquals(0, data.size());
    }
    
    @Test
    public void testCreationWithBytes() {
        ObjectData data1 = new ObjectData(new byte[]{});
        assertEquals(0, data1.getExtraSize());
    }
    
    @Test
    public void testAccess() {
        LinkedList<Data> l = new LinkedList<Data>();
        for (int i=0; i<10; i++) {
            l.add(new Data((byte)i));
        }
        ObjectData data1 = new ObjectData(l);
        assertEquals(9, data1.getExtraSize());
        for (int i=0; i<9; i++) {
            assertEquals(1, data1.getExtraDataAt(i).size());
            assertEquals(i+1, data1.getExtraDataAt(i).get(0));
        }
    }
    
    @Test
    public void testEquals() {
        LinkedList<Data> l = new LinkedList<Data>();
        for (int i=0; i<10; i++) {
            l.add(new Data((byte)i));
        }
        ObjectData data0 = new ObjectData((byte)0x11);
        ObjectData data1 = new ObjectData(l);
        ObjectData data2 = new ObjectData(l);
        
        assertEquals(9, data1.getExtraSize());
        assertFalse(data0.equals(data1));
        assertFalse(data1.equals(data0));
        assertTrue(data1.equals(data2));
        assertTrue(data2.equals(data1));
        
        
    }
    
    @Test
    public void testCreationWithMultipleData() {
        ObjectData data1 = new ObjectData((byte)0xab);
        assertEquals(0, data1.getExtraSize());
        assertEquals(1, data1.size());
        assertEquals((byte)0xab, data1.get(0));
        
        ObjectData data2 = new ObjectData((byte)0xab, (byte)0xcd);
        assertEquals(0, data2.getExtraSize());
        assertEquals(2, data2.size());
        assertEquals((byte)0xab, data2.get(0));
        assertEquals((byte)0xcd, data2.get(1));
        
        ObjectData data3 = new ObjectData((byte)0x89, (byte)0xab, (byte)0xcd);
        assertEquals(0, data3.getExtraSize());
        assertEquals(3, data3.size());
        assertEquals((byte)0x89, data3.get(0));
        assertEquals((byte)0xab, data3.get(1));
        assertEquals((byte)0xcd, data3.get(2));
        
        ObjectData data4 = new ObjectData((byte)0x12, (byte)0x23, (byte)0x45, (byte)0x67);
        assertEquals(0, data4.getExtraSize());
        assertEquals(4, data4.size());
        assertEquals((byte)0x12, data4.get(0));
        assertEquals((byte)0x23, data4.get(1));
        assertEquals((byte)0x45, data4.get(2));
        assertEquals((byte)0x67, data4.get(3));
    }
    
    @Test
    public void testToBytes() {
        LinkedList<Data> l = new LinkedList<Data>();
        for (int i=0; i<10; i++) {
            l.add(new Data((byte)i));
        }
        
        ObjectData data = new ObjectData(l);
        byte[] b = data.toBytes();
        assertTrue(Arrays.equals(b, new byte[]{0x00}));
        
        l = new LinkedList<Data>();
        for (int i=0; i<9; i++) {
            l.add(new Data((byte)i));
        }
        assertTrue(Arrays.equals(b, new byte[]{0x00}));
        
        l = new LinkedList<Data>();
        l.add(new Data((byte)0x00, (byte)0x11));
        data = new ObjectData(l);
        b = data.toBytes();
        assertFalse(Arrays.equals(b, new byte[]{0x00}));
    }
    
    @Test
    public void testIsEmpty() {
        ObjectData data1 = new ObjectData(new byte[0]);
        assertTrue(data1.isEmpty());
        ObjectData data2 = new ObjectData(new byte[1]);
        assertFalse(data2.isEmpty());
    }
}
