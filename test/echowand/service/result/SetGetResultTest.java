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
public class SetGetResultTest {

    /**
     * Test of isSuccessPayload method, of class SetGetResult.
     */
    @Test
    public void testIsSuccessPayload() {
        System.out.println("isSuccessPayload");
        StandardPayload payload = new StandardPayload();
        SetGetResult setGetResult = new SetGetResult(new TimestampManager());
        assertEquals(false, setGetResult.isSuccessPayload(payload));
        
        for (ESV esv : ESV.values()) {
            payload.setESV(esv);
            boolean expResult = esv == ESV.SetGet_Res;
            assertEquals(expResult, setGetResult.isSuccessPayload(payload));
        }
    }

    /**
     * Test of isValidPayload method, of class SetGetResult.
     */
    @Test
    public void testIsValidPayload() {
        System.out.println("isValidPayload");
        StandardPayload payload = new StandardPayload();
        SetGetResult setGetResult = new SetGetResult(new TimestampManager());
        assertEquals(false, setGetResult.isValidPayload(payload));
        
        for (ESV esv : ESV.values()) {
            payload.setESV(esv);
            boolean expResult = (esv == ESV.SetGet_Res || esv == ESV.SetGet_SNA);
            assertEquals(expResult, setGetResult.isValidPayload(payload));
        }
    }

    /**
     * Test of isValidProperty method, of class SetGetResult.
     */
    @Test
    public void testIsValidProperty() {
        System.out.println("isValidProperty");
        Property property = new Property();
        SetGetResult setGetResult = new SetGetResult(new TimestampManager());
        
        assertEquals(true, setGetResult.isValidProperty(property));
        
        property.setEPC(EPC.xE0);
        property.setEDT(new Data(new byte[0]));
        assertEquals(true, setGetResult.isValidProperty(property));
        
        property.setEPC(EPC.xE0);
        property.setEDT(new Data(new byte[1]));
        assertEquals(false, setGetResult.isValidProperty(property));
        
        property.setEPC(EPC.xE0);
        property.setEDT(new Data(new byte[255]));
        assertEquals(false, setGetResult.isValidProperty(property));
    }
    
    /**
     * Test of isValidSecondProperty method, of class SetGetResult.
     */
    @Test
    public void testIsValidSecondProperty() {
        System.out.println("isValidSecondProperty");
        Property property = new Property();
        SetGetResult setGetResult = new SetGetResult(new TimestampManager());
        
        assertEquals(false, setGetResult.isValidSecondProperty(property));
        
        property.setEPC(EPC.xE0);
        property.setEDT(new Data(new byte[0]));
        assertEquals(false, setGetResult.isValidSecondProperty(property));
        
        property.setEPC(EPC.xE0);
        property.setEDT(new Data(new byte[1]));
        assertEquals(true, setGetResult.isValidSecondProperty(property));
        
        property.setEPC(EPC.xE0);
        property.setEDT(new Data(new byte[255]));
        assertEquals(true, setGetResult.isValidSecondProperty(property));
        
        property.setEPC(EPC.xE0);
        property.setEDT(new Data(new byte[256]));
        assertEquals(false, setGetResult.isValidSecondProperty(property));
    }
    
}
