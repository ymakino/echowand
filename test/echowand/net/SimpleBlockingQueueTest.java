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
public class SimpleBlockingQueueTest {

    @Test
    public void testAdd() throws Exception {
        final SimpleBlockingQueue<String> queue = new SimpleBlockingQueue<String>();
        final String data = "dummy";
        
        queue.add(data);
        
        assertEquals(data, queue.take());
    }

    @Test
    public void testTake() throws Exception {
        final SimpleBlockingQueue<String> queue = new SimpleBlockingQueue<String>();
        final String data1 = "dummy1";
        final String data2 = "dummy2";
        
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    queue.add(data1);
                    queue.add(data2);
                } catch (InvalidQueueException ex) {
                    fail();
                } catch (InterruptedException ex) {
                    fail();
                }
            }
        }.start();
        
        assertTrue(data1 == queue.take());
        assertTrue(data2 == queue.take());
        
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
    public void testIsEmpty() throws InvalidQueueException, InterruptedException {
        final SimpleBlockingQueue<String> queue = new SimpleBlockingQueue<String>();
        final String data = "dummy";
        
        assertTrue(queue.isEmpty());
        queue.add(data);
        assertFalse(queue.isEmpty());
        queue.add(data);
        assertFalse(queue.isEmpty());
        queue.take();
        assertFalse(queue.isEmpty());
        queue.take();
        assertTrue(queue.isEmpty());
    }
    
    @Test
    public void testEnable() throws InvalidQueueException, InterruptedException {
        final SimpleBlockingQueue<String> queue = new SimpleBlockingQueue<String>();
        final String data = "dummy";
        
        queue.disable();
        queue.enable();
        
        queue.add(data);
        
        assertEquals(data, queue.take());
    }

    @Test
    public void testDisable() {
        final SimpleBlockingQueue<String> queue = new SimpleBlockingQueue<String>();
        final String data = "dummy";
        
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SimpleBlockingQueueTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                queue.disable();
            }
        }.start();
        
        try {
            queue.take();
            fail();
        } catch (InterruptedException ex) {
            fail();
        } catch (InvalidQueueException ex) {
        }
        
        try {
            queue.add(data);
            fail();
        } catch (InvalidQueueException ex) {
        }
    }

    @Test
    public void testIsEnabled() {
        SimpleBlockingQueue<String> queue = new SimpleBlockingQueue<String>();
        
        assertTrue(queue.isEnabled());
        queue.disable();
        assertFalse(queue.isEnabled());
    }
}
