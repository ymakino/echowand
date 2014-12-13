package echowand.net;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class SimpleSynchronousQueueTest {

    @Test
    public void testTake() throws Exception {
        final SimpleSynchronousQueue<String> queue = new SimpleSynchronousQueue<String>();
        final String data = "dummy";
        
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    queue.put(data);
                } catch (InvalidQueueException ex) {
                    fail();
                } catch (InterruptedException ex) {
                    fail();
                }
            }
        }.start();
        
        assertEquals(data, queue.take());
        
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SimpleSynchronousQueueTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                queue.disable();
            }
        }.start();
        
        try {
            queue.take();
            fail();
        } catch (InvalidQueueException ex) {
        } catch (InterruptedException ex) {
            fail();
        }
    }

    @Test
    public void testPut() throws Exception {
        final SimpleSynchronousQueue<String> queue = new SimpleSynchronousQueue<String>();
        final String data = "dummy";
        
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    assertTrue(data == queue.take());
                } catch (InvalidQueueException ex) {
                    fail();
                } catch (InterruptedException ex) {
                    fail();
                }
            }
        }.start();
        
        queue.put(data);
        
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SimpleSynchronousQueueTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                queue.disable();
            }
        }.start();
        
        try {
            queue.put(data);
            fail();
        } catch (InvalidQueueException ex) {
        } catch (InterruptedException ex) {
            fail();
        }
    }
    
    @Test
    public void testEnable() throws InvalidQueueException, InterruptedException {
        final SimpleSynchronousQueue<String> queue = new SimpleSynchronousQueue<String>();
        final String data = "dummy";
        
        queue.disable();
        queue.enable();
        
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    queue.put(data);
                } catch (InvalidQueueException ex) {
                    fail();
                } catch (InterruptedException ex) {
                    fail();
                }
            }
        }.start();
        
        assertEquals(data, queue.take());
    }
    
    @Test
    public void testDisable() throws InterruptedException {
        final SimpleSynchronousQueue<String> queue = new SimpleSynchronousQueue<String>();
        final String data = "dummy";
        
        assertTrue(queue.isEnabled());
        queue.disable();
        assertFalse(queue.isEnabled());
        
        try {
            queue.put(data);
            fail();
        } catch (InvalidQueueException ex) {
        }
        
        try {
            queue.take();
            fail();
        } catch (InvalidQueueException ex) {
        }
    }

    @Test
    public void testIsEnabled() {
        SimpleSynchronousQueue<String> queue = new SimpleSynchronousQueue<String>();
        
        assertTrue(queue.isEnabled());
        queue.disable();
        assertFalse(queue.isEnabled());
    }
}
