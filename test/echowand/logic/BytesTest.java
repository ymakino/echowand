package echowand.logic;

import echowand.common.Data;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class BytesTest {
    public Data bytes;
    
    @Before
    public void setUp() {
        bytes = new Data(new byte[]{(byte)0x12, (byte)0x34});
    }

    @Test
    public void testGet() {
        assertEquals((byte)0x12, bytes.get(0));
        assertEquals((byte)0x34, bytes.get(1));
    }
    
    @Test
    public void testSize() {
        assertEquals(2, bytes.size());
    }
    
    @Test
    public void testIsEmpty() {
        assertFalse(bytes.isEmpty());
    }
    
    @Test
    public void testToBytes() {
        assertTrue(Arrays.equals(new byte[]{(byte)0x12, (byte)0x34}, bytes.toBytes()));
    }
}
