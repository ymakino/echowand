package echowand.logic;

import echowand.common.EOJ;
import echowand.logic.TransactionConfig;
import echowand.common.ESV;
import echowand.net.InternalSubnet;
import echowand.net.StandardPayload;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
class TestTransactionConfig extends TransactionConfig {

    @Override
    public ESV getESV() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getCountPayloads() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addPayloadProperties(int index, StandardPayload payload) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
public class TransactionConfigTest {
    
    @Test
    public void testSetSenderAndReceiver() {
        InternalSubnet subnet = new InternalSubnet();
        TestTransactionConfig config = new TestTransactionConfig();
        assertEquals(null, config.getSenderNode());
        assertEquals(null, config.getReceiverNode());
        config.setSenderNode(subnet.getLocalNode());
        config.setReceiverNode(subnet.getGroupNode());
        assertEquals(subnet.getLocalNode(), config.getSenderNode());
        assertEquals(subnet.getGroupNode(), config.getReceiverNode());
    }
    
    @Test
    public void testSetSourceAndDestination() {
        InternalSubnet subnet = new InternalSubnet();
        TestTransactionConfig config = new TestTransactionConfig();
        assertEquals(null, config.getSourceEOJ());
        assertEquals(null, config.getDestinationEOJ());
        config.setSourceEOJ(new EOJ("123456"));
        config.setDestinationEOJ(new EOJ("789abc"));
        assertEquals(new EOJ("123456"), config.getSourceEOJ());
        assertEquals(new EOJ("789abc"), config.getDestinationEOJ());
    }
}
