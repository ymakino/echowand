package echowand.logic;

import echowand.logic.AnnounceTransactionConfig;
import echowand.common.Data;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.StandardPayload;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class AnnounceTransactionConfigTest {
    
    @Test
    public void testCreation() {
        AnnounceTransactionConfig config = new AnnounceTransactionConfig();
        
        // config.addSet(EPC.x80, new byte[]{(byte)0x42});
        
        assertEquals(ESV.INFC, config.getESV());
        config.setResponseRequired(false);
        assertEquals(ESV.INF, config.getESV());
    }
    
    @Test
    public void testAddPayloadProperties() {
        AnnounceTransactionConfig config = new AnnounceTransactionConfig();
        StandardPayload payload;
        
        payload = new StandardPayload();
        config.addPayloadProperties(0, payload);
        assertEquals(0, payload.getFirstOPC());
        assertEquals(0, payload.getSecondOPC());
        
        config.addAnnounce(EPC.x80, new Data((byte)0x42));
        payload = new StandardPayload();
        config.addPayloadProperties(0, payload);
        assertEquals(1, payload.getFirstOPC());
        assertEquals(0, payload.getSecondOPC());
        
        config.addAnnounce(EPC.x88, new Data((byte)0x42));
        payload = new StandardPayload();
        config.addPayloadProperties(0, payload);
        assertEquals(2, payload.getFirstOPC());
        assertEquals(0, payload.getSecondOPC());
    }
}
