package echowand.service.result;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.InternalSubnet;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.service.CaptureResultObserver;
import echowand.util.Selector;
import java.util.LinkedList;
import java.util.List;
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
public class CaptureResultTest {
    public static InternalSubnet subnet;
    public CaptureResult result;
    public short nextTID = 0;
    
    public CaptureResultTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        subnet = new InternalSubnet("CaptureResultTest");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        CaptureResultObserver observer = new CaptureResultObserver();
        result = new CaptureResult(observer);
    }
    
    @After
    public void tearDown() {
    }
    
    private Frame newFrame() {
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), new CommonFrame());
        frame.getCommonFrame().setTID(nextTID++);
        return frame;
    }
    
    private Frame newFrame(ESV esv, Property... properties) {
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), new CommonFrame());
        frame.getCommonFrame().setTID(nextTID++);
        StandardPayload payload = new StandardPayload();
        payload.setSEOJ(new EOJ("0ef001"));
        payload.setDEOJ(new EOJ("0ef001"));
        payload.setESV(esv);
        for (Property property : properties) {
            payload.addFirstProperty(property);
        }
        frame.getCommonFrame().setEDATA(payload);
        return frame;
    }

    /**
     * Test of stopCapture method, of class CaptureResult.
     */
    @Test
    public void testStopCapture() {
        int count1 = result.countFrames();
        
        result.stopCapture();
        result.addReceivedFrame(newFrame());
        result.addSentFrame(newFrame());
        
        assertEquals(count1, result.countFrames());
    }

    /**
     * Test of isDone method, of class CaptureResult.
     */
    @Test
    public void testIsDone() {
        assertFalse(result.isDone());
        
        result.stopCapture();
        
        assertTrue(result.isDone());
    }

    /**
     * Test of addSentFrame method, of class CaptureResult.
     */
    @Test
    public void testAddSentFrame_Frame() {
        int count1 = result.countFrames();
        int count2 = result.countReceivedFrames();
        int count3 = result.countSentFrames();
        
        Frame frame = newFrame();
        assertTrue(result.addSentFrame(frame));
        assertTrue(result.addSentFrame(frame));
        assertTrue(result.addSentFrame(frame));
        assertTrue(result.addSentFrame(frame));
        
        assertEquals(count1 + 4, result.countFrames());
        assertEquals(count2, result.countReceivedFrames());
        assertEquals(count3 + 4, result.countSentFrames());
    }

    /**
     * Test of addSentFrame method, of class CaptureResult.
     */
    @Test
    public void testAddSentFrame_ResultFrame() {
        int count1 = result.countFrames();
        int count2 = result.countReceivedFrames();
        int count3 = result.countSentFrames();
        
        Frame frame = newFrame(ESV.Get, new Property(EPC.x80));
        ResultFrame resultFrame1 = new ResultFrame(frame, 10);
        ResultFrame resultFrame2 = new ResultFrame(frame, 10);
        assertTrue(result.addSentFrame(resultFrame1));
        assertTrue(result.addSentFrame(resultFrame2));
        assertFalse(result.addSentFrame(resultFrame1));
        assertFalse(result.addSentFrame(resultFrame2));
        
        assertEquals(count1 + 2, result.countFrames());
        assertEquals(count2, result.countReceivedFrames());
        assertEquals(count3 + 2, result.countSentFrames());
    }

    /**
     * Test of addReceivedFrame method, of class CaptureResult.
     */
    @Test
    public void testAddReceivedFrame_ResultFrame() {
        int count1 = result.countFrames();
        int count2 = result.countReceivedFrames();
        int count3 = result.countSentFrames();
        
        Frame frame = newFrame(ESV.Get, new Property(EPC.x80));
        ResultFrame resultFrame1 = new ResultFrame(frame, 10);
        ResultFrame resultFrame2 = new ResultFrame(frame, 10);
        assertTrue(result.addReceivedFrame(resultFrame1));
        assertTrue(result.addReceivedFrame(resultFrame2));
        assertFalse(result.addReceivedFrame(resultFrame1));
        assertFalse(result.addReceivedFrame(resultFrame2));
        
        assertEquals(count1 + 2, result.countFrames());
        assertEquals(count2 + 2, result.countReceivedFrames());
        assertEquals(count3, result.countSentFrames());
    }

    /**
     * Test of addReceivedFrame method, of class CaptureResult.
     */
    @Test
    public void testAddReceivedFrame_Frame() {
        int count1 = result.countFrames();
        int count2 = result.countReceivedFrames();
        int count3 = result.countSentFrames();
        
        Frame frame = newFrame();
        assertTrue(result.addReceivedFrame(frame));
        assertTrue(result.addReceivedFrame(frame));
        assertTrue(result.addReceivedFrame(frame));
        assertTrue(result.addReceivedFrame(frame));
        
        assertEquals(count1 + 4, result.countFrames());
        assertEquals(count2 + 4, result.countReceivedFrames());
        assertEquals(count3, result.countSentFrames());
    }

    /**
     * Test of countFrames method, of class CaptureResult.
     */
    @Test
    public void testCountFrames() {
        int count1 = result.countFrames();
        
        assertTrue(result.addReceivedFrame(newFrame()));
        
        assertEquals(count1 + 1, result.countFrames());
        
        assertTrue(result.addSentFrame(newFrame()));
        
        assertEquals(count1 + 2, result.countFrames());
    }

    /**
     * Test of countSentFrames method, of class CaptureResult.
     */
    @Test
    public void testCountSentFrames() {
        int count1 = result.countFrames();
        
        assertTrue(result.addReceivedFrame(newFrame()));
        
        assertEquals(count1, result.countSentFrames());
        
        assertTrue(result.addSentFrame(newFrame()));
        
        assertEquals(count1 + 1, result.countSentFrames());
    }

    /**
     * Test of countReceivedFrames method, of class CaptureResult.
     */
    @Test
    public void testCountReceivedFrames() {
        int count1 = result.countFrames();
        
        assertTrue(result.addReceivedFrame(newFrame()));
        
        assertEquals(count1 + 1, result.countReceivedFrames());
        
        assertTrue(result.addSentFrame(newFrame()));
        
        assertEquals(count1 + 1, result.countReceivedFrames());
    }

    /**
     * Test of getFrame method, of class CaptureResult.
     */
    @Test
    public void testGetFrame() {
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        
        result.addReceivedFrame(frame1);
        result.addSentFrame(frame2);
        
        assertEquals(frame1, result.getFrame(0).frame);
        assertEquals(frame2, result.getFrame(1).frame);
    }

    /**
     * Test of getSentFrame method, of class CaptureResult.
     */
    @Test
    public void testGetSentFrame() {
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        
        result.addReceivedFrame(frame1);
        result.addSentFrame(frame2);
        
        assertEquals(frame2, result.getSentFrame(0).frame);
    }

    /**
     * Test of getReceivedFrame method, of class CaptureResult.
     */
    @Test
    public void testGetReceivedFrame() {
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        
        assertEquals(frame2, result.getReceivedFrame(0).frame);
    }

    /**
     * Test of getFrameList method, of class CaptureResult.
     */
    @Test
    public void testGetFrameList() {
        assertTrue(result.getFrameList().isEmpty());
        
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        
        List<ResultFrame> resultFrameList = result.getFrameList();
        assertEquals(2, resultFrameList.size());
        assertEquals(frame1, resultFrameList.get(0).frame);
        assertEquals(frame2, resultFrameList.get(1).frame);
    }

    /**
     * Test of getSentFrameList method, of class CaptureResult.
     */
    @Test
    public void testGetSentFrameList() {
        assertTrue(result.getSentFrameList().isEmpty());
        
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        
        List<ResultFrame> resultFrameList = result.getSentFrameList();
        assertEquals(1, resultFrameList.size());
        assertEquals(frame1, resultFrameList.get(0).frame);
    }

    /**
     * Test of getReceivedFrameList method, of class CaptureResult.
     */
    @Test
    public void testGetReceivedFrameList() {
        assertTrue(result.getSentFrameList().isEmpty());
        
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        
        List<ResultFrame> resultFrameList = result.getReceivedFrameList();
        assertEquals(1, resultFrameList.size());
        assertEquals(frame2, resultFrameList.get(0).frame);
    }

    /**
     * Test of removeFrames method, of class CaptureResult.
     */
    @Test
    public void testRemoveFrames() {
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        final LinkedList<ResultFrame> matchFrames = new LinkedList<ResultFrame>();
        
        Selector<ResultFrame> selector = new Selector<ResultFrame>() {
            @Override
            public boolean match(ResultFrame target) {
                return matchFrames.contains(target);
            }
        };
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        result.addSentFrame(frame3);
        result.addReceivedFrame(frame4);
        
        result.removeFrames(selector);
        
        assertEquals(4, result.countFrames());
        assertEquals(2, result.countSentFrames());
        assertEquals(2, result.countReceivedFrames());
        
        matchFrames.addAll(result.getFrameList().subList(1, 3));
        matchFrames.add(new ResultFrame(newFrame(), 10));
        
        result.removeFrames(selector);
        
        assertEquals(2, result.countFrames());
        assertEquals(1, result.countSentFrames());
        assertEquals(1, result.countReceivedFrames());
    }

    /**
     * Test of removeFrame method, of class CaptureResult.
     */
    @Test
    public void testRemoveFrame_ResultFrame() {
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        result.addSentFrame(frame3);
        result.addReceivedFrame(frame4);
        
        assertEquals(4, result.countFrames());
        assertEquals(2, result.countSentFrames());
        assertEquals(2, result.countReceivedFrames());
        
        List<ResultFrame> frameList = result.getFrameList();
        
        assertTrue(result.removeFrame(frameList.get(0)));
        
        assertEquals(3, result.countFrames());
        assertEquals(1, result.countSentFrames());
        assertEquals(2, result.countReceivedFrames());
        
        assertFalse(result.removeFrame(frameList.get(0)));
        
        assertEquals(3, result.countFrames());
        assertEquals(1, result.countSentFrames());
        assertEquals(2, result.countReceivedFrames());
        
        assertTrue(result.removeFrame(frameList.get(1)));
        
        assertEquals(2, result.countFrames());
        assertEquals(1, result.countSentFrames());
        assertEquals(1, result.countReceivedFrames());
    }

    /**
     * Test of removeFrame method, of class CaptureResult.
     */
    @Test
    public void testRemoveFrame_int() {
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        result.addSentFrame(frame3);
        result.addReceivedFrame(frame4);
        
        assertEquals(4, result.countFrames());
        assertEquals(2, result.countSentFrames());
        assertEquals(2, result.countReceivedFrames());
        
        List<ResultFrame> frameList = result.getFrameList();
        
        result.removeFrame(0);
        
        assertEquals(3, result.countFrames());
        assertEquals(1, result.countSentFrames());
        assertEquals(2, result.countReceivedFrames());
        
        result.removeFrame(2);
        
        assertEquals(2, result.countFrames());
        assertEquals(1, result.countSentFrames());
        assertEquals(1, result.countReceivedFrames());
    }

    /**
     * Test of removeSentFrame method, of class CaptureResult.
     */
    @Test
    public void testRemoveSentFrame() {
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        result.addSentFrame(frame3);
        result.addReceivedFrame(frame4);
        
        result.removeSentFrame(1);
        
        assertEquals(3, result.countFrames());
        assertEquals(1, result.countSentFrames());
        assertEquals(2, result.countReceivedFrames());
        
        assertEquals(frame1, result.getSentFrame(0).frame);
        
        result.removeSentFrame(0);
        
        assertEquals(2, result.countFrames());
        assertEquals(0, result.countSentFrames());
        assertEquals(2, result.countReceivedFrames());
    }

    /**
     * Test of removeReceivedFrame method, of class CaptureResult.
     */
    @Test
    public void testRemoveReceivedFrame() {
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        result.addSentFrame(frame3);
        result.addReceivedFrame(frame4);
        
        result.removeReceivedFrame(1);
        
        assertEquals(3, result.countFrames());
        assertEquals(2, result.countSentFrames());
        assertEquals(1, result.countReceivedFrames());
        
        assertEquals(frame1, result.getSentFrame(0).frame);
        
        result.removeReceivedFrame(0);
        
        assertEquals(2, result.countFrames());
        assertEquals(2, result.countSentFrames());
        assertEquals(0, result.countReceivedFrames());
    }

    /**
     * Test of truncateSentFrames method, of class CaptureResult.
     */
    @Test
    public void testTruncateSentFrames() {
        assertEquals(0, result.truncateSentFrames(1));
        
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        result.addSentFrame(frame1);
        result.addSentFrame(frame2);
        result.addSentFrame(frame3);
        result.addSentFrame(frame4);
        
        assertEquals(2, result.truncateSentFrames(2));
        
        assertEquals(2, result.countSentFrames());
        assertEquals(frame3, result.getSentFrame(0).frame);
        assertEquals(frame4, result.getSentFrame(1).frame);
        
        assertEquals(2, result.truncateSentFrames(1000));
        
        assertEquals(0, result.countSentFrames());
    }

    /**
     * Test of removeAllSentFrames method, of class CaptureResult.
     */
    @Test
    public void testRemoveAllSentFrames() {
        assertEquals(0, result.removeAllSentFrames());
        
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        result.addSentFrame(frame3);
        result.addReceivedFrame(frame4);
        
        assertEquals(2, result.removeAllSentFrames());
        
        assertEquals(0, result.countSentFrames());
        assertEquals(2, result.countReceivedFrames());
        assertEquals(2, result.countFrames());
    }

    /**
     * Test of truncateReceivedFrames method, of class CaptureResult.
     */
    @Test
    public void testTruncateReceivedFrames() {
        assertEquals(0, result.truncateReceivedFrames(1));
        
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        result.addReceivedFrame(frame1);
        result.addReceivedFrame(frame2);
        result.addReceivedFrame(frame3);
        result.addReceivedFrame(frame4);
        
        assertEquals(2, result.truncateReceivedFrames(2));
        
        assertEquals(2, result.countReceivedFrames());
        assertEquals(frame3, result.getReceivedFrame(0).frame);
        assertEquals(frame4, result.getReceivedFrame(1).frame);
        
        assertEquals(2, result.truncateReceivedFrames(1000));
        
        assertEquals(0, result.countReceivedFrames());
    }

    /**
     * Test of removeAllReceivedFrames method, of class CaptureResult.
     */
    @Test
    public void testRemoveAllReceivedFrames() {
        assertEquals(0, result.removeAllReceivedFrames());
        
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        result.addSentFrame(frame3);
        result.addReceivedFrame(frame4);
        
        assertEquals(2, result.removeAllReceivedFrames());
        
        assertEquals(2, result.countSentFrames());
        assertEquals(0, result.countReceivedFrames());
        assertEquals(2, result.countFrames());
    }

    /**
     * Test of truncateFrames method, of class CaptureResult.
     */
    @Test
    public void testTruncateFrames() {
        assertEquals(0, result.truncateReceivedFrames(1));
        
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        result.addSentFrame(frame3);
        result.addReceivedFrame(frame4);
        
        assertEquals(2, result.truncateFrames(2));
        
        assertEquals(2, result.countFrames());
        assertEquals(1, result.countSentFrames());
        assertEquals(1, result.countReceivedFrames());
        assertEquals(frame3, result.getFrame(0).frame);
        assertEquals(frame4, result.getFrame(1).frame);
        
        assertEquals(2, result.truncateFrames(1000));
        
        assertEquals(0, result.countReceivedFrames());
    }

    /**
     * Test of removeAllFrames method, of class CaptureResult.
     */
    @Test
    public void testRemoveAllFrames() {
        assertEquals(0, result.removeAllFrames());
        
        Frame frame1 = newFrame();
        Frame frame2 = newFrame();
        Frame frame3 = newFrame();
        Frame frame4 = newFrame();
        
        result.addSentFrame(frame1);
        result.addReceivedFrame(frame2);
        result.addSentFrame(frame3);
        result.addReceivedFrame(frame4);
        
        assertEquals(4, result.removeAllFrames());
        
        assertEquals(0, result.countSentFrames());
        assertEquals(0, result.countReceivedFrames());
        assertEquals(0, result.countFrames());
    }
}
