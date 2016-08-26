package echowand.service.result;

import echowand.common.Data;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.service.TimestampManager;
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
public class GetResultTest {

    /**
     * Test of isSuccessPayload method, of class GetResult.
     */
    @Test
    public void testIsSuccessPayload() {
        System.out.println("isSuccessPayload");
        StandardPayload payload = new StandardPayload();
        GetResult getResult = new GetResult(new TimestampManager());
        assertEquals(false, getResult.isSuccessPayload(payload));
        
        for (ESV esv : ESV.values()) {
            payload.setESV(esv);
            boolean expResult = esv == ESV.Get_Res;
            assertEquals(expResult, getResult.isSuccessPayload(payload));
        }
    }

    /**
     * Test of isValidPayload method, of class GetResult.
     */
    @Test
    public void testIsValidPayload() {
        System.out.println("isValidPayload");
        StandardPayload payload = new StandardPayload();
        GetResult getResult = new GetResult(new TimestampManager());
        assertEquals(false, getResult.isValidPayload(payload));
        
        for (ESV esv : ESV.values()) {
            payload.setESV(esv);
            boolean expResult = (esv == ESV.Get_Res || esv == ESV.Get_SNA);
            assertEquals(expResult, getResult.isValidPayload(payload));
        }
    }

    /**
     * Test of isValidProperty method, of class GetResult.
     */
    @Test
    public void testIsValidProperty() {
        System.out.println("isValidProperty");
        Property property = new Property();
        GetResult getResult = new GetResult(new TimestampManager());
        
        assertEquals(false, getResult.isValidProperty(property));
        
        property.setEPC(EPC.xE0);
        property.setEDT(new Data(new byte[0]));
        assertEquals(false, getResult.isValidProperty(property));
        
        property.setEPC(EPC.xE0);
        property.setEDT(new Data(new byte[1]));
        assertEquals(true, getResult.isValidProperty(property));
        
        property.setEPC(EPC.xE0);
        property.setEDT(new Data(new byte[255]));
        assertEquals(true, getResult.isValidProperty(property));
    }
    
}
