package echowand.net;

import echowand.net.CommonFrame;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.SimplePayload;
import echowand.net.InvalidDataException;
import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import java.nio.ByteBuffer;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class CommonFrameTest {
    
    @Test
    public void testCreation() {
        CommonFrame f = new CommonFrame();
        assertEquals((byte)0x10, f.getEHD1());
        assertTrue(f.isEchonetLite());
        assertEquals((byte)0x82, f.getEHD2());
        assertFalse(f.isStandardPayload());
        assertEquals((short)0x0000, f.getTID());
        assertTrue(f.getEDATA() instanceof SimplePayload);
        assertEquals(0, f.getEDATA().toBytes().length);
        
        assertEquals((short)0x0000, f.getTID());
        f.setTID((short)0x0110);
        assertEquals((short)0x0110, f.getTID());
        
        f = new CommonFrame(new EOJ("001101"), new EOJ("001201"), ESV.Get);
        assertEquals((byte)0x10, f.getEHD1());
        assertTrue(f.isEchonetLite());
        assertEquals((byte)0x81, f.getEHD2());
        assertTrue(f.isStandardPayload());
        assertEquals((short)0x0000, f.getTID());
        assertTrue(null != f.getEDATA());
    }
    
    @Test
    public void testPayload() {
        CommonFrame f = new CommonFrame();
        StandardPayload p = new StandardPayload();
        f.setEDATA(p);
        assertEquals(p, f.getEDATA());
    }
    
    @Test
    public void testToBytes() {
        CommonFrame f = new CommonFrame();
        StandardPayload p = new StandardPayload();
        p.setSEOJ(new EOJ("001101"));
        p.setDEOJ(new EOJ("002201"));
        p.setESV(ESV.SetC);
        f.setEDATA(p);
        f.toBytes();
    }
    
    @Test
    public void testGetCreationFromBytes() throws InvalidDataException {
        ByteBuffer buffer = ByteBuffer.allocate(14);
        buffer.put((byte)0x10);
        buffer.put((byte)0x81);
        buffer.putShort((short)0x0001);
        buffer.put(new EOJ("123456").toBytes());
        buffer.put(new EOJ("abcdef").toBytes());
        buffer.put(ESV.Get.toByte());
        buffer.put((byte)0x01);
        buffer.put(new Property(EPC.x80).toBytes());
        CommonFrame f = new CommonFrame(buffer.array());
        ByteBuffer newBuffer = ByteBuffer.wrap(f.toBytes());
        buffer.position(0);
        newBuffer.position(0);
        
        assertEquals(14, newBuffer.capacity());
        assertEquals(0, buffer.compareTo(newBuffer));
    }
    
    @Test
    public void testSetGetCreationFromBytes() throws InvalidDataException {
        ByteBuffer buffer = ByteBuffer.allocate(19);
        buffer.put((byte)0x10);
        buffer.put((byte)0x81);
        buffer.putShort((short)0x1234);
        buffer.put(new EOJ("123456").toBytes());
        buffer.put(new EOJ("abcdef").toBytes());
        buffer.put(ESV.SetGet.toByte());
        buffer.put((byte)0x01);
        buffer.put(new Property(EPC.x88, new Data((byte)0x12, (byte)0x34)).toBytes());
        buffer.put((byte)0x01);
        buffer.put(new Property(EPC.x80).toBytes());
        CommonFrame f = new CommonFrame(buffer.array());
        ByteBuffer newBuffer = ByteBuffer.wrap(f.toBytes());
        buffer.position(0);
        newBuffer.position(0);
        
        assertEquals(0, buffer.compareTo(newBuffer));
    }
    
    @Test
    public void testCreationWithInfo() {
        EOJ seoj = new EOJ("001101");
        EOJ deoj = new EOJ("002201");
        CommonFrame frame = new CommonFrame(seoj, deoj, ESV.SetC);
        StandardPayload payload = frame.getEDATA(StandardPayload.class);
        assertEquals(new EOJ("001101"), payload.getSEOJ());
        assertEquals(new EOJ("002201"), payload.getDEOJ());
        assertEquals(ESV.SetC, payload.getESV());
    }
    
    @Test(expected=InvalidDataException.class)
    public void testCreationWithEmptyBytes() throws InvalidDataException {
        new CommonFrame(new byte[]{});
    }
    
    @Test
    public void testCreationWithInvalidBytes() throws InvalidDataException {
        ByteBuffer buffer = ByteBuffer.allocate(19);
        buffer.put((byte)0x10);
        buffer.put((byte)0x81);
        buffer.putShort((short)0x1234);
        buffer.put(new EOJ("123456").toBytes());
        buffer.put(new EOJ("abcdef").toBytes());
        buffer.put(ESV.SetGet.toByte());
        buffer.put((byte)0x01);
        buffer.put(new Property(EPC.x88, new Data((byte)0x12, (byte)0x34)).toBytes());
        buffer.put((byte)0x01);
        buffer.put(new Property(EPC.x80).toBytes());
        CommonFrame f = new CommonFrame(buffer.array());
        ByteBuffer newBuffer = ByteBuffer.wrap(f.toBytes());
        buffer.position(0);
        newBuffer.position(0);
        
        assertEquals(0, buffer.compareTo(newBuffer));
    }
    
    @Test
    public void testChangePayloadType() {
        CommonFrame f = new CommonFrame();
        assertEquals((byte)0x82, f.getEHD2());
        assertFalse(f.isStandardPayload());
        
        f.setEDATA(new SimplePayload());
        assertEquals((byte)0x82, f.getEHD2());
        assertFalse(f.isStandardPayload());
        
        f.setEDATA(new StandardPayload());
        assertEquals((byte)0x81, f.getEHD2());
        assertTrue(f.isStandardPayload());
        
        f.setEDATA(new SimplePayload());
        assertEquals((byte)0x82, f.getEHD2());
        assertFalse(f.isStandardPayload());
        
    }
    
    @Test
    public void testGetEDATA() {
        CommonFrame commonFrame1 = new CommonFrame();
        assertNotNull(commonFrame1.getEDATA());
        assertNull(commonFrame1.getEDATA(StandardPayload.class));
        assertNotNull(commonFrame1.getEDATA(SimplePayload.class));
        
        CommonFrame commonFrame2 = new CommonFrame(new EOJ("0ef001"), new EOJ("0ef001"), ESV.Get);
        assertNotNull(commonFrame2.getEDATA());
        assertNotNull(commonFrame2.getEDATA(StandardPayload.class));
        assertNull(commonFrame2.getEDATA(SimplePayload.class));
    }
}
