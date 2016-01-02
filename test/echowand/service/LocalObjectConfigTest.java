package echowand.service;

import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.net.InternalSubnet;
import echowand.object.LocalObject;
import echowand.object.LocalObjectDelegate;
import echowand.object.ObjectData;
import java.util.LinkedList;
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
    
    /**
     * Test of notifyCreation method, of class LocalObjectServiceDelegate.
     */
    @Test
    public void testNotifyCreation() {
        LocalObjectServiceDelegateImpl delegate = new LocalObjectServiceDelegateImpl();
        assertTrue(config.addDelegate(delegate));
        
        LocalObject localObject1 = new LocalObject(new TemperatureSensorInfo());
        config.notifyCreation(localObject1, null);
        assertEquals(1, delegate.objects.size());
        assertEquals(localObject1, delegate.objects.get(0));
        
        LocalObject localObject2 = new LocalObject(new TemperatureSensorInfo());
        config.notifyCreation(localObject2, null);
        assertEquals(2, delegate.objects.size());
        assertEquals(localObject1, delegate.objects.get(0));
        assertEquals(localObject2, delegate.objects.get(1));
    }

    public class LocalObjectServiceDelegateImpl implements LocalObjectServiceDelegate {
        public LinkedList<LocalObject> objects = new LinkedList<LocalObject>();
        public Core core;

        @Override
        public void notifyCreation(LocalObject object, Core core) {
            objects.add(object);
            this.core = core;
        }

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
    
    public class DummyLazyConfiguration implements LocalObjectConfig.LazyConfiguration {
        public int count = 0;
        public LocalObjectConfig config;
        public Core core;
        
        @Override
        public void configure(LocalObjectConfig config, Core core) {
            count++;
            this.config = config;
            this.core = core;
        }
    }

    @Test
    public void testAddLazyConfiguration() {
        LocalObjectConfig.LazyConfiguration configuration = new DummyLazyConfiguration();
        assertTrue(config.addLazyConfiguration(configuration));
    }

    @Test
    public void testCountLazyConfigurations() {
        assertEquals(0, config.countLazyConfigurations());
        
        LocalObjectConfig.LazyConfiguration configuration = new DummyLazyConfiguration();
        assertTrue(config.addLazyConfiguration(configuration));
        
        assertEquals(1, config.countLazyConfigurations());
    }

    @Test
    public void testGetLazyConfiguration() {
        LocalObjectConfig.LazyConfiguration configuration1 = new DummyLazyConfiguration();
        assertTrue(config.addLazyConfiguration(configuration1));
        
        assertEquals(configuration1, config.getLazyConfiguration((0)));
        
        LocalObjectConfig.LazyConfiguration configuration2 = new DummyLazyConfiguration();
        assertTrue(config.addLazyConfiguration(configuration2));
        
        assertEquals(configuration1, config.getLazyConfiguration((0)));
        assertEquals(configuration2, config.getLazyConfiguration((1)));
    }

    @Test
    public void testRemoveLazyConfiguration() {
        LocalObjectConfig.LazyConfiguration configuration1 = new DummyLazyConfiguration();
        LocalObjectConfig.LazyConfiguration configuration2 = new DummyLazyConfiguration();
        
        assertTrue(config.addLazyConfiguration(configuration1));
        
        assertTrue(config.removeLazyConfiguration(configuration1));
        assertFalse(config.removeLazyConfiguration(configuration1));
        
        assertTrue(config.addLazyConfiguration(configuration1));
        assertTrue(config.addLazyConfiguration(configuration2));
        
        assertTrue(config.removeLazyConfiguration((configuration2)));
        assertFalse(config.removeLazyConfiguration((configuration2)));
    }

    @Test
    public void testContainsLazyConfiguration() {
        LocalObjectConfig.LazyConfiguration configuration1 = new DummyLazyConfiguration();
        LocalObjectConfig.LazyConfiguration configuration2 = new DummyLazyConfiguration();
        
        assertFalse(config.containsLazyConfiguration(configuration1));
        assertFalse(config.containsLazyConfiguration(configuration2));
        
        assertTrue(config.addLazyConfiguration(configuration1));
        
        assertTrue(config.containsLazyConfiguration(configuration1));
        assertFalse(config.containsLazyConfiguration(configuration2));
        
        assertTrue(config.addLazyConfiguration(configuration2));
        
        assertTrue(config.containsLazyConfiguration(configuration1));
        assertTrue(config.containsLazyConfiguration(configuration2));
        
        assertTrue(config.removeLazyConfiguration(configuration1));
        
        assertFalse(config.containsLazyConfiguration(configuration1));
        assertTrue(config.containsLazyConfiguration(configuration2));
    }

    @Test
    public void testLazyConfigure() {
        Core core = new Core(new InternalSubnet());
        DummyLazyConfiguration configuration1 = new DummyLazyConfiguration();
        DummyLazyConfiguration configuration2 = new DummyLazyConfiguration();
        
        config.addLazyConfiguration(configuration1);
        config.addLazyConfiguration(configuration2);
        
        config.lazyConfigure(core);
        
        assertEquals(config, configuration1.config);
        assertEquals(core, configuration1.core);
        assertEquals(1, configuration1.count);
        
        assertEquals(config, configuration2.config);
        assertEquals(core, configuration2.core);
        assertEquals(1, configuration2.count);
    }
}
