package echowand.service;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.InternalSubnet;
import echowand.service.result.CaptureResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class CaptureResultObserverTest {
    
    public InternalSubnet subnet;
    public TestCaptureResult captureResult;
    
    class TestCaptureResult extends CaptureResult {
        
        public Frame sentFrame = null;
        public Frame receivedFrame = null;

        public TestCaptureResult(CaptureResultObserver observer) {
            super(observer);
        }
        
        @Override
        public synchronized boolean addSentFrame(Frame frame) {
            sentFrame = frame;
            return true;
        }
        
        @Override
        public synchronized boolean addReceivedFrame(Frame frame) {
            receivedFrame = frame;
            return true;
        }
    }
    
    public CaptureResultObserverTest() {
        subnet = new InternalSubnet("CaptureResultObserverTest");
    }
    
    @Before
    public void setUp() {
        captureResult = new TestCaptureResult(new CaptureResultObserver());
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testAddCaptureResult() {
        CaptureResultObserver observer = new CaptureResultObserver();
        assertTrue(observer.addCaptureResult(captureResult));
    }

    @Test
    public void testRemoveCaptureResult() {
        CaptureResultObserver observer = new CaptureResultObserver();
        assertFalse(observer.removeCaptureResult(captureResult));
        assertTrue(observer.addCaptureResult(captureResult));
        assertTrue(observer.removeCaptureResult(captureResult));
        assertFalse(observer.removeCaptureResult(captureResult));
    }

    @Test
    public void testNotifySent() {
        CaptureResultObserver observer = new CaptureResultObserver();
        assertTrue(observer.addCaptureResult(captureResult));
        
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), new CommonFrame());
        observer.notifySent(frame, true);
        
        assertEquals(captureResult.sentFrame, frame);
        captureResult.sentFrame = null;
        
        observer.notifySent(frame, false);
        assertNull(captureResult.sentFrame);
    }

    @Test
    public void testNotifyReceived() {
        CaptureResultObserver observer = new CaptureResultObserver();
        assertTrue(observer.addCaptureResult(captureResult));
        
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), new CommonFrame());
        observer.notifyReceived(frame);
        
        assertEquals(captureResult.receivedFrame, frame);
    }
    
}
