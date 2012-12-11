package echowand.app;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.InternalSubnet;
import echowand.net.Subnet;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class ReadableConverterMapKeyTest {
    
    public ReadableConverterMapKeyTest() {
    }

    /**
     * Test of includes method, of class ReadableConverterMapKey.
     */
    @Test
    public void testIncludes() {
        Subnet subnet = new InternalSubnet();
        ReadableConverterMapKey key1 = new ReadableConverterMapKey(null, null, null, EPC.x80);
        assertTrue(key1.includes(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertTrue(key1.includes(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertTrue(key1.includes(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertTrue(key1.includes(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        
        ReadableConverterMapKey key2 = new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80);
        assertFalse(key2.includes(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertTrue(key2.includes(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertTrue(key2.includes(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertTrue(key2.includes(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        
        ReadableConverterMapKey key3 = new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80);
        assertFalse(key3.includes(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertTrue(key3.includes(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertFalse(key3.includes(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertTrue(key3.includes(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        
        ReadableConverterMapKey key4 = new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80);
        assertFalse(key4.includes(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertFalse(key4.includes(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertFalse(key4.includes(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertTrue(key4.includes(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
    }

    /**
     * Test of isBetterThan method, of class ReadableConverterMapKey.
     */
    @Test
    public void testIsBetterThan() {
        Subnet subnet = new InternalSubnet();
        ReadableConverterMapKey key1 = new ReadableConverterMapKey(null, null, null, EPC.x80);
        assertTrue(key1.isBetterThan(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertFalse(key1.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertFalse(key1.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertFalse(key1.isBetterThan(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        
        ReadableConverterMapKey key2 = new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80);
        assertTrue(key2.isBetterThan(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertFalse(key2.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertTrue(key2.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertFalse(key2.isBetterThan(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        
        ReadableConverterMapKey key3 = new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80);
        assertTrue(key3.isBetterThan(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertTrue(key3.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertTrue(key3.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertFalse(key3.isBetterThan(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        
        ReadableConverterMapKey key4 = new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80);
        assertTrue(key4.isBetterThan(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertTrue(key4.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertTrue(key4.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertTrue(key4.isBetterThan(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        
        ReadableConverterMapKey key5 = new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), null, EPC.x80);
        assertTrue(key5.isBetterThan(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertTrue(key5.isBetterThan(new ReadableConverterMapKey(subnet.getLocalNode(), null, null, EPC.x80)));
        assertFalse(key5.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertTrue(key5.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertTrue(key5.isBetterThan(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), null, EPC.x80)));
        assertFalse(key5.isBetterThan(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        
        ReadableConverterMapKey key6 = new ReadableConverterMapKey(subnet.getLocalNode(), null, null, EPC.x80);
        assertTrue(key6.isBetterThan(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertTrue(key6.isBetterThan(new ReadableConverterMapKey(subnet.getLocalNode(), null, null, EPC.x80)));
        assertFalse(key6.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertFalse(key6.isBetterThan(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertFalse(key6.isBetterThan(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
    }

    /**
     * Test of equals method, of class ReadableConverterMapKey.
     */
    @Test
    public void testEquals() {
        Subnet subnet = new InternalSubnet();
        ReadableConverterMapKey key = new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80);
        
        assertTrue(key.equals(new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertFalse(key.equals(new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80)));
        assertFalse(key.equals(new ReadableConverterMapKey(null, new ClassEOJ("0011"), null, EPC.x80)));
        assertFalse(key.equals(new ReadableConverterMapKey(null, null, null, EPC.x80)));
        assertFalse(key.equals(new ReadableConverterMapKey(subnet.getLocalNode(), null, null, EPC.x80)));
    }

    /**
     * Test of hashCode method, of class ReadableConverterMapKey.
     */
    @Test
    public void testHashCode() {
        Subnet subnet = new InternalSubnet();
        ReadableConverterMapKey key1 = new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80);
        assertTrue(key1.hashCode() == new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), new EOJ("001101"), EPC.x80).hashCode());
        
        ReadableConverterMapKey key2 = new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80);
        assertTrue(key2.hashCode() == new ReadableConverterMapKey(null, new ClassEOJ("0011"), new EOJ("001101"), EPC.x80).hashCode());
        
        ReadableConverterMapKey key3 = new ReadableConverterMapKey(subnet.getLocalNode(), null, new EOJ("001101"), EPC.x80);
        assertTrue(key3.hashCode() == new ReadableConverterMapKey(subnet.getLocalNode(), null, new EOJ("001101"), EPC.x80).hashCode());
        
        ReadableConverterMapKey key4 = new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), null, EPC.x80);
        assertTrue(key4.hashCode() == new ReadableConverterMapKey(subnet.getLocalNode(), new ClassEOJ("0011"), null, EPC.x80).hashCode());
    }
}
