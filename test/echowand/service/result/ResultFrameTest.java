package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.InternalSubnet;
import echowand.net.Property;
import echowand.net.StandardPayload;
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
public class ResultFrameTest {
    public InternalSubnet subnet;
    
    @Before
    public void setUp() {
        subnet = new InternalSubnet("ResultFrameTest");
    }

    /**
     * Test of toString method, of class ResultData.
     */
    @Test
    public void testToString() {
        Frame frame1 = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), new CommonFrame());
        assertNotNull(frame1.toString());
        
        assertNotNull(newFrame(ESV.Get, 0, new Property(EPC.x80)).toString());
    }
    
    public ResultFrame newFrame(ESV esv, long time, Property... properties) {
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), new CommonFrame());
        StandardPayload payload = new StandardPayload();
        payload.setSEOJ(new EOJ("0ef001"));
        payload.setDEOJ(new EOJ("001101"));
        payload.setESV(esv);
        for (Property property : properties) {
            payload.addFirstProperty(property);
        }
        frame.getCommonFrame().setEDATA(payload);
        return new ResultFrame(frame, time);
    }

    /**
     * Test of equals method, of class ResultData.
     */
    @Test
    public void testEquals() {
        ResultFrame resultFrame = newFrame(ESV.Get, 10, new Property(EPC.x80));
        ResultFrame resultFrame1 = newFrame(ESV.Get, 10, new Property(EPC.x80));
        ResultFrame resultFrame2 = newFrame(ESV.Get, 11, new Property(EPC.x80));
        ResultFrame resultFrame3 = newFrame(ESV.SetI, 10, new Property(EPC.x81, new Data(new byte[]{0x12, 0x34})));
        
        assertTrue(resultFrame.equals(resultFrame));
        assertTrue(resultFrame1.equals(resultFrame1));
        assertTrue(resultFrame2.equals(resultFrame2));
        assertTrue(resultFrame3.equals(resultFrame3));
        
        assertFalse(resultFrame.equals(resultFrame1));
        assertFalse(resultFrame1.equals(resultFrame));
        
        assertFalse(resultFrame.equals(resultFrame2));
        assertFalse(resultFrame2.equals(resultFrame));
        
        assertFalse(resultFrame.equals(resultFrame3));
        assertFalse(resultFrame3.equals(resultFrame));
    }

    /**
     * Test of hashCode method, of class ResultData.
     */
    @Test
    public void testHashCode() {
        ResultData resultData = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, new Data((byte)0x30), 10);
        ResultData resultData1 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, new Data((byte)0x30), 10);
        ResultData resultData2 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, null, 10);
        
        assertTrue(resultData.hashCode() == resultData.hashCode());
        assertTrue(resultData1.hashCode() == resultData1.hashCode());
        assertTrue(resultData2.hashCode() == resultData2.hashCode());
        
        assertFalse(resultData.hashCode() == resultData1.hashCode());
        assertFalse(resultData.hashCode() == resultData2.hashCode());
        assertFalse(resultData1.hashCode() == resultData2.hashCode());
        
        ResultFrame resultFrame = newFrame(ESV.Get, 10, new Property(EPC.x80));
        ResultFrame resultFrame1 = newFrame(ESV.Get, 10, new Property(EPC.x80));
        ResultFrame resultFrame2 = newFrame(ESV.Get, 11, new Property(EPC.x80));
        ResultFrame resultFrame3 = newFrame(ESV.SetI, 10, new Property(EPC.x81, new Data(new byte[]{0x12, 0x34})));
        
        assertTrue(resultFrame.hashCode() == resultFrame.hashCode());
        assertTrue(resultFrame1.hashCode() == resultFrame1.hashCode());
        assertTrue(resultFrame2.hashCode() == resultFrame2.hashCode());
        assertTrue(resultFrame3.hashCode() == resultFrame3.hashCode());
        
        assertFalse(resultFrame.hashCode() == resultFrame1.hashCode());
        assertFalse(resultFrame1.hashCode() == resultFrame.hashCode());
        
        assertFalse(resultFrame.hashCode() == resultFrame2.hashCode());
        assertFalse(resultFrame2.hashCode() == resultFrame.hashCode());
        
        assertFalse(resultFrame.hashCode() == resultFrame3.hashCode());
        assertFalse(resultFrame3.hashCode() == resultFrame.hashCode());
    }
}
