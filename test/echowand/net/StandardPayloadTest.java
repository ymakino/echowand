package echowand.net;

import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.InvalidDataException;
import echowand.common.Data;
import echowand.common.ESV;
import echowand.common.EOJ;
import echowand.common.EPC;
import java.nio.ByteBuffer;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class StandardPayloadTest {
    
    @Test
    public void testPayloadCreation() {
        StandardPayload payload = new StandardPayload();
        payload.setSEOJ(new EOJ("123456"));
        assertEquals(new EOJ("123456"), payload.getSEOJ());
        payload.setDEOJ(new EOJ("abcdef"));
        assertEquals(new EOJ("abcdef"), payload.getDEOJ());
        
        payload.setESV(ESV.SetI);
        assertEquals(ESV.SetI, payload.getESV());
    }
    
    @Test
    public void testPayloadCreationWithData() {
        EOJ seoj = new EOJ("123456");
        EOJ deoj = new EOJ("abcdef");
        StandardPayload payload = new StandardPayload(seoj, deoj, ESV.SetI);;
        assertEquals(new EOJ("123456"), payload.getSEOJ());
        assertEquals(new EOJ("abcdef"), payload.getDEOJ());
        assertEquals(ESV.SetI, payload.getESV());
    }
    
    @Test
    public void testProperty() {
        StandardPayload payload = new StandardPayload();
        payload.setSEOJ(new EOJ("123456"));
        payload.setDEOJ(new EOJ("abcdef"));
        payload.setESV(ESV.Get);
        
        Property p1 = new Property(EPC.x80, new Data((byte)0x32));
        assertEquals(0, payload.getSecondOPC());
        payload.addSecondProperty(p1);
        assertEquals(1, payload.getSecondOPC());
        
        Property p2 = new Property(EPC.x81);
        assertEquals(0, payload.getFirstOPC());
        payload.addFirstProperty(p2);
        assertEquals(1, payload.getFirstOPC());
        
        Property p3 = payload.getSecondPropertyAt(0);
        assertEquals(1, p3.getPDC());
        assertEquals(1, p3.getEDT().size());
        assertEquals((byte)0x32, p3.getEDT().get(0));
        
        Property p4 = payload.getFirstPropertyAt(0);
        assertEquals(0, p4.getPDC());
        
        assertEquals(10, payload.size());
        byte[] bytes = payload.toBytes();
        assertEquals((byte)0x12, bytes[0]);
        assertEquals((byte)0x34, bytes[1]);
        assertEquals((byte)0x56, bytes[2]);
        assertEquals((byte)0xab, bytes[3]);
        assertEquals((byte)0xcd, bytes[4]);
        assertEquals((byte)0xef, bytes[5]);
        assertEquals(ESV.Get.toByte(), bytes[6]);
        assertEquals((byte)0x01, bytes[7]);
        assertEquals((byte)0x81, bytes[8]);
        assertEquals((byte)0x00, bytes[9]);
        
        payload.setESV(ESV.SetGet);
        assertEquals(14, payload.size());
        bytes = payload.toBytes();
        assertEquals(ESV.SetGet.toByte(), bytes[6]);
        assertEquals((byte)0x01, bytes[10]);
        assertEquals((byte)0x80, bytes[11]);
        assertEquals((byte)0x01, bytes[12]);
        assertEquals((byte)0x32, bytes[13]);
    }
    
    @Test
    public void testFromBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new EOJ("123456").toBytes());
        buffer.put(new EOJ("abcdef").toBytes());
        buffer.put(ESV.Get.toByte());
        buffer.put((byte)0x01);
        buffer.put(new Property(EPC.x80).toBytes());
        try {
            StandardPayload p = new StandardPayload(buffer.array());
            ByteBuffer newBuffer = ByteBuffer.wrap(p.toBytes());
            buffer.position(0);
            newBuffer.position(0);
            assertEquals(0, buffer.compareTo(newBuffer));
        } catch (InvalidDataException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test(expected=InvalidDataException.class)
    public void testInvalidEmptyPayload() throws InvalidDataException {
        new StandardPayload(new byte[]{});
    }
    
    @Test(expected=InvalidDataException.class)
    public void testInvalidPropertyPayload() throws InvalidDataException {
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put(new EOJ("123456").toBytes());
        buffer.put(new EOJ("abcdef").toBytes());
        buffer.put(ESV.Get.toByte());
        buffer.put((byte)0x01);
        buffer.put((byte)0x7f);
        new StandardPayload(buffer.array());
    }
}
