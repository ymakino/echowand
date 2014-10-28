package echowand.service;

import echowand.info.TemperatureSensorInfo;
import echowand.net.InternalSubnet;
import echowand.object.LocalObject;
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
public class LocalObjectUpdaterTest {
    private LocalObject localObject;
    private Core core;
    
    private class DummyPropertyUpdater extends PropertyUpdater {
        public int count = 0;
        
        @Override
        public void loop(LocalObject localObject) {
            count++;
        }
    }
    
    @Before
    public void setUp() {
        TemperatureSensorInfo info = new TemperatureSensorInfo();
        localObject = new LocalObject(info);
        core = new Core(new InternalSubnet("LocalObjectUpdaterTest"));
    }

    /**
     * Test of getLocalObject method, of class LocalObjectUpdater.
     */
    @Test
    public void testGetLocalObject() {
        LocalObjectUpdater updater = new LocalObjectUpdater(localObject, core);
        assertEquals(localObject, updater.getLocalObject());
    }

    /**
     * Test of getCore method, of class LocalObjectUpdater.
     */
    @Test
    public void testGetCore() {
        LocalObjectUpdater updater = new LocalObjectUpdater(localObject, core);
        assertEquals(core, updater.getCore());
    }

    /**
     * Test of countPropertyUpdaters method, of class LocalObjectUpdater.
     */
    @Test
    public void testCountPropertyUpdaters() {
        LocalObjectUpdater updater = new LocalObjectUpdater(localObject, core);
        assertEquals(0, updater.countPropertyUpdaters());
    }

    /**
     * Test of addPropertyUpdater method, of class LocalObjectUpdater.
     */
    @Test
    public void testAddPropertyUpdater() {
        LocalObjectUpdater updater = new LocalObjectUpdater(localObject, core);
        assertEquals(0, updater.countPropertyUpdaters());
        
        updater.addPropertyUpdater(new DummyPropertyUpdater());
        assertEquals(1, updater.countPropertyUpdaters());
        
        updater.addPropertyUpdater(new DummyPropertyUpdater());
        assertEquals(2, updater.countPropertyUpdaters());
    }

    /**
     * Test of removePropertyUpdater method, of class LocalObjectUpdater.
     */
    @Test
    public void testRemovePropertyUpdater() {
        DummyPropertyUpdater propertyUpdater1 = new DummyPropertyUpdater();
        DummyPropertyUpdater propertyUpdater2 = new DummyPropertyUpdater();
        
        LocalObjectUpdater updater = new LocalObjectUpdater(localObject, core);
        
        updater.addPropertyUpdater(propertyUpdater1);
        updater.addPropertyUpdater(propertyUpdater2);
        
        updater.removePropertyUpdater(propertyUpdater1);
        
        assertEquals(propertyUpdater2, updater.getPropertyUpdater(0));
        
        assertEquals(1, updater.countPropertyUpdaters());
        
        updater.removePropertyUpdater(propertyUpdater2);
        
        assertEquals(0, updater.countPropertyUpdaters());
    }

    /**
     * Test of getPropertyUpdater method, of class LocalObjectUpdater.
     */
    @Test
    public void testGetPropertyUpdater() {
        DummyPropertyUpdater propertyUpdater1 = new DummyPropertyUpdater();
        DummyPropertyUpdater propertyUpdater2 = new DummyPropertyUpdater();
        
        LocalObjectUpdater updater = new LocalObjectUpdater(localObject, core);
        
        updater.addPropertyUpdater(propertyUpdater1);
        updater.addPropertyUpdater(propertyUpdater2);
        
        assertEquals(propertyUpdater1, updater.getPropertyUpdater(0));
        assertEquals(propertyUpdater2, updater.getPropertyUpdater(1));
        
        updater.removePropertyUpdater(propertyUpdater1);
        
        assertEquals(propertyUpdater2, updater.getPropertyUpdater(0));
    }

    /**
     * Test of run method, of class LocalObjectUpdater.
     */
    @Test
    public void testRun() throws InterruptedException {
        DummyPropertyUpdater propertyUpdater1 = new DummyPropertyUpdater();
        DummyPropertyUpdater propertyUpdater2 = new DummyPropertyUpdater();
        
        propertyUpdater1.setIntervalPeriod(400);
        propertyUpdater2.setIntervalPeriod(200);
        
        LocalObjectUpdater updater = new LocalObjectUpdater(localObject, core);
        
        updater.addPropertyUpdater(propertyUpdater1);
        updater.addPropertyUpdater(propertyUpdater2);
        
        new Thread(updater).start();
        
        Thread.sleep(2000);
        
        propertyUpdater1.done();
        propertyUpdater2.done();
        
        int count1 = propertyUpdater1.count;
        int count2 = propertyUpdater2.count;
        
        System.out.println("Count1: " + count1);
        System.out.println("Count2: " + count2);
        
        assertTrue(count1 <= 5);
        assertTrue(count2 <= 10);
        
        assertTrue(count1 * 2 <= count2);
    }
    
}
