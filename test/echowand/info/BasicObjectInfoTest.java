package echowand.info;

import echowand.common.EPC;
import echowand.info.DeviceObjectInfo;
import echowand.info.ObjectInfo;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class BasicObjectInfoTest {
    
    @Test
    public void testBase() {
        DeviceObjectInfo objectInfo = new DeviceObjectInfo();
        assertEquals(EPC.x80, objectInfo.getAtIndex(0).epc);
        assertEquals(1, objectInfo.getAtIndex(0).initialData.length);
        assertEquals((byte)0x30, objectInfo.getAtIndex(0).initialData[0]);
        
        assertEquals(EPC.x80, objectInfo.get(EPC.x80).epc);
        assertEquals(1, objectInfo.get(EPC.x80).initialData.length);
        assertEquals((byte)0x30, objectInfo.get(EPC.x80).initialData[0]);
        
        assertTrue(objectInfo.get(EPC.x80).gettable);
        assertFalse(objectInfo.get(EPC.x80).settable);
        assertTrue(objectInfo.get(EPC.x80).observable);
    }

    @Test
    public void testInvalidEPC() {
        DeviceObjectInfo objectInfo = new DeviceObjectInfo();
        assertFalse(objectInfo.get(EPC.xFF).gettable);
        assertFalse(objectInfo.get(EPC.xFF).settable);
        assertFalse(objectInfo.get(EPC.xFF).observable);
        
        byte[] getMap = objectInfo.get(EPC.x9F).initialData;
        assertEquals(8, getMap[0]);
        assertEquals((byte)0x80, getMap[1]);
        assertEquals((byte)0x81, getMap[2]);
        assertEquals((byte)0x82, getMap[3]);
        assertEquals((byte)0x88, getMap[4]);
        assertEquals((byte)0x8A, getMap[5]);
        assertEquals((byte)0x9D, getMap[6]);
        assertEquals((byte)0x9E, getMap[7]);
        assertEquals((byte)0x9F, getMap[8]);
    }
}
