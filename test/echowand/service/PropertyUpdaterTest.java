package echowand.service;

import echowand.common.EOJ;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.TooManyObjectsException;
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
public class PropertyUpdaterTest {

    /**
     * Test of getIntervalPeriod method, of class PropertyUpdater.
     */
    @Test
    public void testGetIntervalPeriod() {
        PropertyUpdater updater = new PropertyUpdaterImpl();
        assertEquals(0, updater.getIntervalPeriod());
    }

    /**
     * Test of setIntervalPeriod method, of class PropertyUpdater.
     */
    @Test
    public void testSetIntervalPeriod() {
        PropertyUpdater updater = new PropertyUpdaterImpl();
        
        updater.setIntervalPeriod(5000);
        assertEquals(5000, updater.getIntervalPeriod());
        
        updater.setIntervalPeriod(0);
        assertEquals(0, updater.getIntervalPeriod());
    }

    /**
     * Test of done method, of class PropertyUpdater.
     */
    @Test
    public void testDone() {
        PropertyUpdater updater = new PropertyUpdaterImpl();
        
        assertFalse(updater.isDone());
        
        updater.done();
        
        assertTrue(updater.isDone());
        
        updater.done();
        
        assertTrue(updater.isDone());
    }

    /**
     * Test of isDone method, of class PropertyUpdater.
     */
    @Test
    public void testIsDone() {
        PropertyUpdater updater = new PropertyUpdaterImpl();
        
        assertFalse(updater.isDone());
        
        updater.done();
        
        assertTrue(updater.isDone());
        
        updater.done();
        
        assertTrue(updater.isDone());
    }

    /**
     * Test of doLoopOnce method, of class PropertyUpdater.
     */
    @Test
    public void testDoLoopOnce() throws TooManyObjectsException {
        PropertyUpdaterImpl updater = new PropertyUpdaterImpl();
        ServiceManager serviceManager = new ServiceManager(new InternalSubnet("PropertyUpdaterTest"));
        LocalObjectConfig conf = new LocalObjectConfig(new TemperatureSensorInfo());
        serviceManager.addLocalObjectConfig(conf);
        serviceManager.startService();
        
        LocalObject localObject = serviceManager.getService().getLocalObject(new EOJ("001101"));
        
        updater.setServiceManager(serviceManager);
        updater.setLocalObject(localObject);
        
        assertEquals(0, updater.count);
        assertEquals(0, updater.getIntervalPeriod());
        assertEquals(null, updater.localObject);
        
        updater.doLoopOnce();
        assertEquals(1, updater.count);
        assertEquals(1, updater.getIntervalPeriod());
        assertEquals(localObject, updater.localObject);
        
        updater.doLoopOnce();
        assertEquals(2, updater.count);
        assertEquals(2, updater.getIntervalPeriod());
        assertEquals(localObject, updater.localObject);
    }

    public class PropertyUpdaterImpl extends PropertyUpdater {
        public LocalObject localObject = null;
        public int count =0;

        @Override
        public void loop(LocalObject localObject) {
            this.localObject = localObject;
            count++;
            this.setIntervalPeriod(count);
        }
    }
    
}
