package echowand.service;

import echowand.common.ClassEOJ;
import echowand.common.EPC;
import echowand.info.ObjectInfo;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.InternalSubnet;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import echowand.object.LocalObject;
import echowand.object.LocalObjectManager;
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
public class CoreTest {
    private Core core;
    private Subnet subnet;

    private Core newCore() {
        subnet = new InternalSubnet();
        core =  new Core(subnet);
        return core;
    }

    /**
     * Test of addLocalObjectConfig method, of class Core.
     */
    @Test
    public void testAddLocalObjectConfig() throws TooManyObjectsException {
        ObjectInfo info = new TemperatureSensorInfo();
        LocalObjectConfig config = new LocalObjectConfig(info);
        Core core = newCore();
        boolean result = core.addLocalObjectConfig(config);
        assertEquals(true, result);
        
        core.startService();
        
        LocalObjectManager manager = core.getLocalObjectManager();
        System.out.println(manager);
        assertEquals(1, manager.getDeviceObjects().size());
        LocalObject object = manager.getDeviceObjects().get(0);
        
        assertEquals(info.getClassEOJ(), object.getEOJ().getClassEOJ());
        
        for (int i=0x80; i<=0xFF; i++) {
            EPC epc = EPC.fromByte((byte)i);
            assertEquals(info.get(epc).gettable, object.isGettable(epc));
            assertEquals(info.get(epc).settable, object.isSettable(epc));
            assertEquals(info.get(epc).observable, object.isObservable(epc));
        }
    }

    /**
     * Test of removeLocalObjectConfig method, of class Core.
     */
    @Test
    public void testRemoveLocalObjectConfig() {
        ObjectInfo info = new TemperatureSensorInfo();
        LocalObjectConfig config = new LocalObjectConfig(info);
        Core core = newCore();
        
        boolean result = core.removeLocalObjectConfig(config);
        assertEquals(false, result);
        
        core.addLocalObjectConfig(config);
        
        result = core.removeLocalObjectConfig(config);
        assertEquals(true, result);
        
        result = core.removeLocalObjectConfig(config);
        assertEquals(false, result);
    }

    /**
     * Test of getSubnet method, of class Core.
     */
    @Test
    public void testGetSubnet() throws SubnetException {
        Core core = newCore();
        assertEquals(subnet, core.getSubnet());
        
        try {
            core = new Core();
            assertTrue(core.getSubnet() instanceof CaptureSubnet);
            Inet4Subnet subnet = (Inet4Subnet)((CaptureSubnet)core.getSubnet()).getInternalSubnet();
            subnet.stopService();
        } catch (SubnetException ex) {
            System.out.println("testGetSubnet: Unable to create a default Core()");
        }
    }

    /**
     * Test of getTransactionManager method, of class Core.
     */
    @Test
    public void testGetTransactionManager() throws TooManyObjectsException {
        Core core = newCore();
        assertNull(core.getTransactionManager());
        core.startService();
        assertNotNull(core.getTransactionManager());
    }

    /**
     * Test of getRemoteObjectManager method, of class Core.
     */
    @Test
    public void testGetRemoteObjectManager() throws TooManyObjectsException {
        Core core = newCore();
        assertNull(core.getRemoteObjectManager());
        core.startService();
        assertNotNull(core.getRemoteObjectManager());
    }

    /**
     * Test of getLocalObjectManager method, of class Core.
     */
    @Test
    public void testGetLocalObjectManager() throws TooManyObjectsException {
        Core core = newCore();
        assertNull(core.getLocalObjectManager());
        core.startService();
        assertNotNull(core.getLocalObjectManager());
    }

    /**
     * Test of getNodeProfileObject method, of class Core.
     */
    @Test
    public void testGetNodeProfileObject() throws TooManyObjectsException {
        Core core = newCore();
        assertNull(core.getNodeProfileObject());
        core.initialize();
        assertNotNull(core.getNodeProfileObject());
    }

    /**
     * Test of getRequestDispatcher method, of class Core.
     */
    @Test
    public void testGetRequestDispatcher() throws TooManyObjectsException {
        Core core = newCore();
        assertNull(core.getRequestDispatcher());
        core.startService();
        assertTrue(core.getRequestDispatcher() != null);
    }

    /**
     * Test of getMainLoop method, of class Core.
     */
    @Test
    public void testGetMainLoop() throws TooManyObjectsException {
        Core core = newCore();
        assertNull(core.getMainLoop());
        core.startService();
        assertTrue(core.getMainLoop() != null);
    }

    /**
     * Test of getSetGetRequestProcessor method, of class Core.
     */
    @Test
    public void testGetSetGetRequestProcessor() throws TooManyObjectsException {
        Core core = newCore();
        assertNull(core.getSetGetRequestProcessor());
        core.startService();
        assertTrue(core.getSetGetRequestProcessor() != null);
    }

    /**
     * Test of getAnnounceRequestProcessor method, of class Core.
     */
    @Test
    public void testGetAnnounceRequestProcessor() throws TooManyObjectsException {
        Core core = newCore();
        assertNull(core.getAnnounceRequestProcessor());
        core.startService();
        assertTrue(core.getAnnounceRequestProcessor() != null);
    }

    /**
     * Test of getObserveResultProsessor method, of class Core.
     */
    @Test
    public void testGetObserveResultProsessor() throws TooManyObjectsException {
        Core core = newCore();
        assertNull(core.getObserveResultProsessor());
        core.startService();
        assertTrue(core.getObserveResultProsessor() != null);
    }

    /**
     * Test of getCaptureFrameObserver method, of class Core.
     */
    @Test
    public void testGetCaptureFrameObserver() throws TooManyObjectsException {
        Core core = newCore();
        assertNull(core.getCaptureResultObserver());
        core.startService();
        assertTrue(core.getCaptureResultObserver() != null);
    }

    /**
     * Test of isInitialized method, of class Core.
     */
    @Test
    public void testIsInitialized() throws TooManyObjectsException {
        Core core = newCore();
        assertFalse(core.isInitialized());
        core.initialize();
        assertTrue(core.isInitialized());
    }

    /**
     * Test of isInService method, of class Core.
     */
    @Test
    public void testIsInService() throws TooManyObjectsException {
        Core core = newCore();
        assertFalse(core.isInService());
        core.initialize();
        assertFalse(core.isInService());
        core.startService();
        assertTrue(core.isInService());
    }

    /**
     * Test of isCaptureEnabled method, of class Core.
     */
    @Test
    public void testIsCaptureEnabled() throws TooManyObjectsException {
        Core core = newCore();
    
        assertFalse(core.isCaptureEnabled());
        
        core = new Core(new CaptureSubnet(new InternalSubnet()));
        
        assertFalse(core.isCaptureEnabled());
        core.initialize();
        assertTrue(core.isCaptureEnabled());
            
        
        try {
            core = new Core();
            assertFalse(core.isCaptureEnabled());
            core.initialize();
            assertTrue(core.isCaptureEnabled());
            Inet4Subnet subnet = (Inet4Subnet)((CaptureSubnet)core.getSubnet()).getInternalSubnet();
            subnet.stopService();
        } catch (SubnetException ex) {
            System.out.println("testIsCaptureEnabled: Unable to create a default Core()");
        }
    }

    /**
     * Test of startService method, of class Core.
     */
    @Test
    public void testInitialize() throws Exception {
        Core core = newCore();
        assertFalse(core.isInitialized());
        assertTrue(core.initialize());
        assertTrue(core.isInitialized());
        assertFalse(core.initialize());
        assertTrue(core.isInitialized());
    }

    /**
     * Test of startService method, of class Core.
     */
    @Test
    public void testStartService() throws Exception {
        Core core = newCore();
        assertFalse(core.isInService());
        assertFalse(core.isInitialized());
        assertTrue(core.startService());
        assertTrue(core.isInService());
        assertTrue(core.isInitialized());
        assertFalse(core.startService());
        
        core = newCore();
        assertFalse(core.isInService());
        assertFalse(core.isInitialized());
        assertTrue(core.initialize());
        assertFalse(core.isInService());
        assertTrue(core.isInitialized());
        assertTrue(core.startService());
        assertTrue(core.isInService());
        assertTrue(core.isInitialized());
    }

    @Test
    public void testGetNodeProfileObjectConfig() {
        core = newCore();
        assertEquals(new ClassEOJ("0ef0"), core.getNodeProfileObjectConfig().getObjectInfo().getClassEOJ());
    }
}
