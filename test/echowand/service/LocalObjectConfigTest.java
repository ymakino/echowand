package echowand.service;

import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.object.LocalObject;
import echowand.object.LocalObjectDelegate;
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
public class LocalObjectConfigTest {
    
    private TemperatureSensorInfo info;
    private LocalObjectConfig config;
    
    private class DummyLocalObjectDelegate implements LocalObjectDelegate {

        @Override
        public void getData(GetState result, LocalObject object, EPC epc) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData curData) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData curData, ObjectData oldData) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    private class DummyPropertyUpdater extends PropertyUpdater {

        @Override
        public void loop(LocalObject localObject) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    @Before
    public void setUp() {
        info = new TemperatureSensorInfo();
        config = new LocalObjectConfig(info);
    }

    /**
     * Test of getObjectInfo method, of class LocalObjectConfig.
     */
    @Test
    public void testGetObjectInfo() {
        assertEquals(info, config.getObjectInfo());
    }

    /**
     * Test of countDelegates method, of class LocalObjectConfig.
     */
    @Test
    public void testCountDelegates() {
        assertEquals(0, config.countDelegates());
        LocalObjectDelegate delegate = new DummyLocalObjectDelegate();
        assertTrue(config.addDelegate(delegate));
        assertEquals(1, config.countDelegates());
        assertTrue(config.addDelegate(delegate));
        assertEquals(2, config.countDelegates());
    }

    /**
     * Test of addDelegate method, of class LocalObjectConfig.
     */
    @Test
    public void testAddDelegate() {
        LocalObjectDelegate delegate = new DummyLocalObjectDelegate();
        assertTrue(config.addDelegate(delegate));
        assertTrue(config.addDelegate(delegate));
    }

    /**
     * Test of removeDelegate method, of class LocalObjectConfig.
     */
    @Test
    public void testRemoveDelegate() {
        LocalObjectDelegate delegate = new DummyLocalObjectDelegate();
        assertFalse(config.removeDelegate(delegate));
        assertTrue(config.addDelegate(delegate));
        assertTrue(config.removeDelegate(delegate));
        assertFalse(config.removeDelegate(delegate));
    }

    /**
     * Test of getDelegate method, of class LocalObjectConfig.
     */
    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetDelegateError() {
        config.getDelegate(0);
    }
    
    @Test
    public void testGetDelegate() {
        LocalObjectDelegate delegate1 = new DummyLocalObjectDelegate();
        LocalObjectDelegate delegate2 = new DummyLocalObjectDelegate();
        assertTrue(config.addDelegate(delegate1));
        assertTrue(config.addDelegate(delegate2));
        assertEquals(delegate1, config.getDelegate(0));
        assertEquals(delegate2, config.getDelegate(1));
    }

    /**
     * Test of countPropertyDelegates method, of class LocalObjectConfig.
     */
    @Test
    public void testCountPropertyDelegates() {
        assertEquals(0, config.countPropertyDelegates());
        PropertyDelegate delegate = new PropertyDelegate(EPC.x80, true, false, false);
        assertTrue(config.addPropertyDelegate(delegate));
        assertEquals(1, config.countPropertyDelegates());
        assertTrue(config.addPropertyDelegate(delegate));
        assertEquals(2, config.countPropertyDelegates());
    }

    /**
     * Test of addPropertyDelegate method, of class LocalObjectConfig.
     */
    @Test
    public void testAddPropertyDelegate() {
        PropertyDelegate delegate = new PropertyDelegate(EPC.x80, true, false, false);
        assertTrue(config.addPropertyDelegate(delegate));
        assertTrue(config.addPropertyDelegate(delegate));
    }

    /**
     * Test of removePropertyDelegate method, of class LocalObjectConfig.
     */
    @Test
    public void testRemovePropertyDelegate() {
        PropertyDelegate delegate = new PropertyDelegate(EPC.x80, true, false, false);
        assertFalse(config.removePropertyDelegate(delegate));
        assertTrue(config.addPropertyDelegate(delegate));
        assertTrue(config.removePropertyDelegate(delegate));
        assertFalse(config.removePropertyDelegate(delegate));
    }

    /**
     * Test of getPropertyDelegate method, of class LocalObjectConfig.
     */
    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetPropertyDelegateError() {
        config.getPropertyDelegate(0);
    }
    
    @Test
    public void testGetPropertyDelegate() {
        PropertyDelegate delegate1 = new PropertyDelegate(EPC.x80, true, false, false);
        PropertyDelegate delegate2 = new PropertyDelegate(EPC.x80, true, false, false);
        assertTrue(config.addPropertyDelegate(delegate1));
        assertTrue(config.addPropertyDelegate(delegate2));
        assertEquals(delegate1, config.getPropertyDelegate(0));
        assertEquals(delegate2, config.getPropertyDelegate(1));
    }

    /**
     * Test of countPropertyUpdaters method, of class LocalObjectConfig.
     */
    @Test
    public void testCountPropertyUpdaters() {
        assertEquals(0, config.countPropertyUpdaters());
        DummyPropertyUpdater updater = new DummyPropertyUpdater();
        assertTrue(config.addPropertyUpdater(updater));
        assertEquals(1, config.countPropertyUpdaters());
        assertTrue(config.addPropertyUpdater(updater));
        assertEquals(2, config.countPropertyUpdaters());
    }

    /**
     * Test of addPropertyUpdater method, of class LocalObjectConfig.
     */
    @Test
    public void testAddPropertyUpdater() {
        DummyPropertyUpdater updater = new DummyPropertyUpdater();
        assertTrue(config.addPropertyUpdater(updater));
        assertTrue(config.addPropertyUpdater(updater));
    }

    /**
     * Test of removePropertyUpdater method, of class LocalObjectConfig.
     */
    @Test
    public void testRemovePropertyUpdater() {
        DummyPropertyUpdater updater = new DummyPropertyUpdater();
        assertFalse(config.removePropertyUpdater(updater));
        assertTrue(config.addPropertyUpdater(updater));
        assertTrue(config.removePropertyUpdater(updater));
        assertFalse(config.removePropertyUpdater(updater));
    }

    /**
     * Test of getPropertyUpdater method, of class LocalObjectConfig.
     */
    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetPropertyUpdaterError() {
        config.getPropertyUpdater(0);
    }
    
    @Test
    public void testGetPropertyUpdater() {
        DummyPropertyUpdater updater1 = new DummyPropertyUpdater();
        DummyPropertyUpdater updater2 = new DummyPropertyUpdater();
        assertTrue(config.addPropertyUpdater(updater1));
        assertTrue(config.addPropertyUpdater(updater2));
        assertEquals(updater1, config.getPropertyUpdater(0));
        assertEquals(updater2, config.getPropertyUpdater(1));
    }
    
}
