package echowand.service;

import echowand.object.LocalObject;
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
public class PropertyUpdaterThreadTest {
    
    private void testRun1(int interval, int sleep1, int sleep2, int mincount) throws InterruptedException {
        PropertyUpdaterImpl updater = new PropertyUpdaterImpl();
        updater.setIntervalPeriod(interval);
        PropertyUpdaterThread updaterThread = new PropertyUpdaterThread(updater);
        updaterThread.start();
        
        Thread.sleep(sleep1);
        
        updater.done();
        int count1 = updater.count;
        
        assertTrue(count1 >= mincount);
        
        Thread.sleep(sleep2);
        int count2 = updater.count;
        
        System.out.println("Count1: " + count1);
        System.out.println("Count2: " + count2);
        
        assertTrue(count1 == count2);
    }

    /**
     * Test of run method, of class PropertyUpdaterThread.
     */
    @Test
    public void testRun() throws InterruptedException {
        testRun1(0, 1000, 500, 10000);
        testRun1(1, 1000, 500, 500);
        testRun1(700, 1000, 1000, 2);
    }
    
    public class PropertyUpdaterImpl extends PropertyUpdater {
        public LocalObject localObject = null;
        public int count =0;

        @Override
        public void loop(LocalObject localObject) {
            this.localObject = localObject;
            count++;
        }
    }
}
