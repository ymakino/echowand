package echowand.logic;

import echowand.logic.SetGetTransactionConfig;
import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.InternalSubnet;
import echowand.net.StandardPayload;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class SetGetTransactionConfigTest {
    
    @Test
    public void testESVSet() {
        SetGetTransactionConfig config = new SetGetTransactionConfig();
        
        config.addSet(EPC.x80, new Data((byte)0x42));
        
        assertEquals(ESV.SetC, config.getESV());
        config.setResponseRequired(false);
        assertEquals(ESV.SetI, config.getESV());
    }
    
    @Test
    public void testESVGet() {
        SetGetTransactionConfig config = new SetGetTransactionConfig();
        
        config.addGet(EPC.xE0);
        
        assertEquals(ESV.Get, config.getESV());
        
        config.setAnnouncePreferred(true);
        
        assertEquals(ESV.INF_REQ, config.getESV());
    }
    
    @Test
    public void testESVSetGet() {
        SetGetTransactionConfig config = new SetGetTransactionConfig();
        
        config.addSet(EPC.x80, new Data((byte)0x42));
        assertEquals(ESV.SetC, config.getESV());
        config.setResponseRequired(false);
        assertEquals(ESV.SetI, config.getESV());
        config.addGet(EPC.xE0);
        
        assertEquals(ESV.SetGet, config.getESV());
    }
    
    @Test
    public void testResponseRequired() {
        SetGetTransactionConfig config = new SetGetTransactionConfig();
        
        assertTrue(config.isResponseRequired());
        config.setResponseRequired(false);
        assertFalse(config.isResponseRequired());
        
        config.getESV();
    }
    
    @Test
    public void testAddPayloadProperties() {
        SetGetTransactionConfig config = new SetGetTransactionConfig();
        StandardPayload payload;
        
        payload = new StandardPayload();
        config.addPayloadProperties(0, payload);
        assertEquals(0, payload.getFirstOPC());
        assertEquals(0, payload.getSecondOPC());
        
        config.addSet(EPC.x80, new Data((byte)0x42));
        payload = new StandardPayload();
        config.addPayloadProperties(0, payload);
        assertEquals(1, payload.getFirstOPC());
        assertEquals(0, payload.getSecondOPC());
        
        config.addSet(EPC.x88, new Data((byte)0x42));
        payload = new StandardPayload();
        config.addPayloadProperties(0, payload);
        assertEquals(2, payload.getFirstOPC());
        assertEquals(0, payload.getSecondOPC());
        
        config.addGet(EPC.x80);
        payload = new StandardPayload();
        config.addPayloadProperties(0, payload);
        assertEquals(2, payload.getFirstOPC());
        assertEquals(1, payload.getSecondOPC());
        
        config.addGet(EPC.x88);
        payload = new StandardPayload();
        config.addPayloadProperties(0, payload);
        assertEquals(2, payload.getFirstOPC());
        assertEquals(2, payload.getSecondOPC());
    }
    
    @Test
    public void testSetSenderAndReceiver() {
        InternalSubnet subnet = new InternalSubnet();
        SetGetTransactionConfig config = new SetGetTransactionConfig();
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
        SetGetTransactionConfig config = new SetGetTransactionConfig();
        assertEquals(null, config.getSourceEOJ());
        assertEquals(null, config.getDestinationEOJ());
        config.setSourceEOJ(new EOJ("123456"));
        config.setDestinationEOJ(new EOJ("789abc"));
        assertEquals(new EOJ("123456"), config.getSourceEOJ());
        assertEquals(new EOJ("789abc"), config.getDestinationEOJ());
    }
}
