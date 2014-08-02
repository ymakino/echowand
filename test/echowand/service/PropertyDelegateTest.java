package echowand.service;

import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ymakino
 */
public class PropertyDelegateTest {

    /**
     * Test of getEPC method, of class PropertyDelegate.
     */
    @Test
    public void testGetEPC() {
        PropertyDelegate delegate1 = new PropertyDelegate(EPC.x80, true, false, false);
        assertEquals(EPC.x80, delegate1.getEPC());
        
        PropertyDelegate delegate2 = new PropertyDelegate(EPC.x81, true, true, true);
        assertEquals(EPC.x81, delegate2.getEPC());
    }

    /**
     * Test of isGetEnabled method, of class PropertyDelegate.
     */
    @Test
    public void testIsGetEnabled() {
        PropertyDelegate delegate1 = new PropertyDelegate(EPC.x80, true, false, false);
        assertTrue(delegate1.isGetEnabled());
        
        PropertyDelegate delegate2 = new PropertyDelegate(EPC.x81, false, true, true);
        assertFalse(delegate2.isGetEnabled());
    }

    /**
     * Test of isSetEnabled method, of class PropertyDelegate.
     */
    @Test
    public void testIsSetEnabled() {
        PropertyDelegate delegate1 = new PropertyDelegate(EPC.x80, false, true, false);
        assertTrue(delegate1.isSetEnabled());
        
        PropertyDelegate delegate2 = new PropertyDelegate(EPC.x81, true, false, true);
        assertFalse(delegate2.isSetEnabled());
    }

    /**
     * Test of isNotifyEnabled method, of class PropertyDelegate.
     */
    @Test
    public void testIsNotifyEnabled() {
        PropertyDelegate delegate1 = new PropertyDelegate(EPC.x80, false, false, true);
        assertTrue(delegate1.isNotifyEnabled());
        
        PropertyDelegate delegate2 = new PropertyDelegate(EPC.x81, true, true, false);
        assertFalse(delegate2.isNotifyEnabled());
    }

    /**
     * Test of getUserData method, of class PropertyDelegate.
     */
    @Test
    public void testGetUserData() {
        PropertyDelegate delegate = new PropertyDelegate(EPC.x80, false, false, true);
        assertNull(delegate.getUserData(new LocalObject(new TemperatureSensorInfo()), EPC.x80));
    }

    /**
     * Test of setUserData method, of class PropertyDelegate.
     */
    @Test
    public void testSetUserData() {
        PropertyDelegate delegate = new PropertyDelegate(EPC.x80, false, false, true);
        assertFalse(delegate.setUserData(new LocalObject(new TemperatureSensorInfo()), EPC.x80, new ObjectData((byte)0x30)));
    }
    
}
