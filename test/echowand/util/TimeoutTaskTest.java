/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echowand.util;

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
public class TimeoutTaskTest {

    /**
     * Test of isTimedOut method, of class TimeoutTask.
     */
    @Test
    public void testIsTimedOut() throws InterruptedException {
        System.out.println("isTimedOut");
        TimeoutTask instance = new TimeoutTask(1000);
        
        instance.start();
        
        assertFalse(instance.isTimedOut());
        
        Thread.sleep(1010);
        
        assertTrue(instance.isTimedOut());
    }

    /**
     * Test of isInterrupted method, of class TimeoutTask.
     */
    @Test
    public void testIsInterrupted() throws InterruptedException {
        System.out.println("isInterrupted");
        TimeoutTask instance = new TimeoutTask(1000);
        
        instance.start();
        
        assertFalse(instance.isInterrupted());
        
        Thread.sleep(1010);
        
        assertFalse(instance.isInterrupted());
        
        instance.interrupt();
        
        assertFalse(instance.isInterrupted());
    }

    /**
     * Test of interrupt method, of class TimeoutTask.
     */
    @Test
    public void testInterrupt() throws InterruptedException {
        System.out.println("interrupt");
        TimeoutTask instance = new TimeoutTask(1000);
        
        instance.start();
        
        assertFalse(instance.isInterrupted());
        
        Thread.sleep(500);
        
        assertFalse(instance.isInterrupted());
        
        assertTrue(instance.interrupt());
        
        assertTrue(instance.isInterrupted());
        
        Thread.sleep(500);
        
        assertFalse(instance.isTimedOut());
    }
    
    @Test
    public void testInterrupt2() throws InterruptedException {
        System.out.println("interrupt2");
        TimeoutTask instance = new TimeoutTask(1000);
        
        instance.start();
        
        assertFalse(instance.isInterrupted());
        
        Thread.sleep(1010);
        
        assertTrue(instance.isTimedOut());
        
        assertFalse(instance.isInterrupted());
        
        assertFalse(instance.interrupt());
        
        assertFalse(instance.isInterrupted());
    }
    
    private static class DummyObserver implements TimeoutObserver {
        public boolean timedOut = false;
        @Override
        public void notifyTimeout(TimeoutTask timeoutTask) {
            timedOut = true;
        }
    }
    
    @Test
    public void testObserver() throws InterruptedException {
        System.out.println("observer");
        
        DummyObserver observer = new DummyObserver();
                
        TimeoutTask instance = new TimeoutTask(observer, 1000);
        instance.start();
        
        Thread.sleep(500);
        
        assertFalse(observer.timedOut);
        
        Thread.sleep(510);
        
        assertTrue(observer.timedOut);
    }
    
    @Test
    public void testObserver2() throws InterruptedException {
        System.out.println("observer2");
        
        DummyObserver observer = new DummyObserver();
                
        TimeoutTask instance = new TimeoutTask(observer, 1000);
        instance.start();
        
        Thread.sleep(500);
        
        assertTrue(instance.interrupt());
        assertFalse(observer.timedOut);
        
        Thread.sleep(510);
        
        assertFalse(observer.timedOut);
    }
}
