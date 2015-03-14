package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.InternalSubnet;
import echowand.net.Node;
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
public class ResultDataTest {
    public InternalSubnet subnet;
    
    @Before
    public void setUp() {
        subnet = new InternalSubnet("ResultDataTest");
    }

    /**
     * Test of toString method, of class ResultData.
     */
    @Test
    public void testToString() {
        ResultData resultData1 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, new Data((byte)0x30), 10);
        assertNotNull(resultData1.toString());
        
        ResultData resultData2 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, new Data((byte)0x30));
        assertNotNull(resultData2.toString());
        
        ResultData resultData3 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, null, 10);
        assertNotNull(resultData3.toString());
        
        ResultData resultData4 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, null);
        assertNotNull(resultData4.toString());
    }

    /**
     * Test of equals method, of class ResultData.
     */
    @Test
    public void testEquals() {
        ResultData resultData = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, new Data((byte)0x30), 10);
        ResultData resultData1 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, new Data((byte)0x30), 10);
        ResultData resultData2 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, new Data((byte)0x30), 11);
        ResultData resultData3 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, null, 10);
        
        assertTrue(resultData.equals(resultData1));
        assertTrue(resultData1.equals(resultData));
        
        assertFalse(resultData.equals(resultData2));
        assertFalse(resultData2.equals(resultData));
        
        assertFalse(resultData.equals(resultData3));
        assertFalse(resultData3.equals(resultData));
    }

    /**
     * Test of hashCode method, of class ResultData.
     */
    @Test
    public void testHashCode() {
        ResultData resultData = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, new Data((byte)0x30), 10);
        ResultData resultData1 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, new Data((byte)0x30), 10);
        ResultData resultData2 = new ResultData(subnet.getLocalNode(), new EOJ("001101"), EPC.x80, new Data((byte)0x30), 10);
        ResultData resultData3 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x81, new Data((byte)0x30), 10);
        ResultData resultData4 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, new Data((byte)0x30), 11);
        ResultData resultData5 = new ResultData(subnet.getLocalNode(), new EOJ("0ef001"), EPC.x80, null, 10);
        
        assertTrue(resultData.hashCode() == resultData1.hashCode());
        assertFalse(resultData.hashCode() == resultData2.hashCode());
        assertFalse(resultData.hashCode() == resultData3.hashCode());
        assertFalse(resultData.hashCode() == resultData4.hashCode());
        assertFalse(resultData.hashCode() == resultData5.hashCode());
    }
    
}
