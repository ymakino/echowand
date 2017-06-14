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
    
    public class PropertyUpdaterImpl extends PropertyUpdater {
        public LocalObject localObject = null;
        public int count =0;

        @Override
        public void loop(LocalObject localObject) {
            this.localObject = localObject;
            count++;
        }
    }
    
    private void testRun1(int delay, int interval, int sleep1, int sleep2, int mincount, int maxcount) throws InterruptedException {
        PropertyUpdaterImpl updater = new PropertyUpdaterImpl();
        updater.setDelay(delay);
        updater.setIntervalPeriod(interval);
        PropertyUpdaterThread updaterThread = new PropertyUpdaterThread(updater);
        updaterThread.start();
        
        Thread.sleep(sleep1);
        
        updater.finish();
        int count1 = updater.count;
        System.out.println("Count1: " + count1);
        
        assertTrue("count1 >= " + mincount, count1 >= mincount);
        assertTrue("count1 <= " + maxcount, count1 <= maxcount);
        
        Thread.sleep(sleep2);
        int count2 = updater.count;
        System.out.println("Count2: " + count2);
        
        assertTrue(count1 == count2);
    }

    /**
     * Test of run method, of class PropertyUpdaterThread.
     */
    @Test
    public void testRunWithoutDelay() throws InterruptedException {
        testRun1(0, 0, 1000, 500, 10000, Integer.MAX_VALUE);
        testRun1(0, 1, 1000, 500, 980, 1020);
        testRun1(0, 700, 1000, 1000, 2, 2);
    }
    
    @Test
    public void testRunWithDelay() throws InterruptedException {
        testRun1(500, 0, 1000, 500, 10000, Integer.MAX_VALUE);
        testRun1(500, 1, 1000, 500, 480, 520);
        testRun1(500, 700, 1000, 1000, 1, 1);
    }
}
