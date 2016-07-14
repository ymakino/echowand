package echowand.service.result;

import echowand.util.SelectorNone;
import echowand.util.SelectorAny;
import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.InternalSubnet;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.service.ObserveResultProcessor;
import echowand.service.TimestampManager;
import echowand.util.Selector;
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
public class ObserveResultTest {
    public static InternalSubnet subnet;
    public TestFrameSelector selector;
    public ObserveResult result;
    public short nextTID = 0;
    
    static class TestFrameSelector implements Selector<Frame> {
        public boolean result = true;
        @Override
        public boolean match(Frame frame) {
            return result;
        }
    }
    
    public ObserveResultTest() {
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
        selector = new TestFrameSelector();
        ObserveResultProcessor processor = new ObserveResultProcessor();
        result = new ObserveResult(selector, processor, new TimestampManager());
    }
    
    @After
    public void tearDown() {
    }
    
    public void checkFrameDataMap() {
        List<ResultFrame> frameList = result.getFrameList();
        for (ResultFrame resultFrame : frameList) {
            for (ResultData resultData : result.getDataList(resultFrame)) {
                assertTrue(result.getDataList().contains(resultData));
            }
        }
        
        List<ResultData> dataList = result.getDataList();
        for (ResultData resultData: dataList) {
            if (result.getFrame(resultData) != null) {
                assertTrue(result.getFrameList().contains(result.getFrame(resultData)));
            }
        }
    }
    
    private Frame newFrame(ESV esv) {
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), new CommonFrame());
        frame.getCommonFrame().setTID(nextTID++);
        
        StandardPayload payload = new StandardPayload();
        payload.setSEOJ(new EOJ("0ef001"));
        payload.setDEOJ(new EOJ("0ef001"));
        payload.setESV(ESV.INF);
        payload.addFirstProperty(new Property(EPC.x80, new Data(new byte[]{0x30})));
        frame.getCommonFrame().setEDATA(payload);
        
        return frame;
    }
    
    private Frame newFrame2(ESV esv) {
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), new CommonFrame());
        frame.getCommonFrame().setTID(nextTID++);
        
        StandardPayload payload = new StandardPayload();
        payload.setSEOJ(new EOJ("0ef001"));
        payload.setDEOJ(new EOJ("0ef001"));
        payload.setESV(ESV.INF);
        payload.addFirstProperty(new Property(EPC.x80, new Data(new byte[]{0x31})));
        payload.addFirstProperty(new Property(EPC.x81, new Data(new byte[]{0x40})));
        frame.getCommonFrame().setEDATA(payload);
        
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
    
    private Frame newSimpleFrame() {
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), new CommonFrame());
        frame.getCommonFrame().setTID(nextTID++);
        return frame;
    }

    /**
     * Test of enableDataList method, of class ObserveResult.
     */
    @Test
    public void testEnableDataList() {
        assertTrue(result.isDataListEnabled());
        result.enableDataList();
        assertTrue(result.isDataListEnabled());
    }

    /**
     * Test of disableDataList method, of class ObserveResult.
     */
    @Test
    public void testDisableDataList() {
        assertTrue(result.isDataListEnabled());
        result.disableDataList();
        assertFalse(result.isDataListEnabled());
        result.disableDataList();
        assertFalse(result.isDataListEnabled());
    }

    /**
     * Test of isDataListEnabled method, of class ObserveResult.
     */
    @Test
    public void testIsDataListEnabled() {
        assertTrue(result.isDataListEnabled());
        result.disableDataList();
        assertFalse(result.isDataListEnabled());
        result.enableDataList();
        assertTrue(result.isDataListEnabled());
    }

    /**
     * Test of enableFrameList method, of class ObserveResult.
     */
    @Test
    public void testEnableFrameList() {
        assertTrue(result.isFrameListEnabled());
        result.enableFrameList();
        assertTrue(result.isFrameListEnabled());
    }

    /**
     * Test of disableFrameList method, of class ObserveResult.
     */
    @Test
    public void testDisableFrameList() {
        assertTrue(result.isFrameListEnabled());
        result.disableFrameList();
        assertFalse(result.isFrameListEnabled());
        result.disableFrameList();
        assertFalse(result.isFrameListEnabled());
    }

    /**
     * Test of isFrameListEnabled method, of class ObserveResult.
     */
    @Test
    public void testIsFrameListEnabled() {
        assertTrue(result.isFrameListEnabled());
        result.disableFrameList();
        assertFalse(result.isFrameListEnabled());
        result.enableFrameList();
        assertTrue(result.isFrameListEnabled());
    }

    /**
     * Test of stopObserve method, of class ObserveResult.
     */
    @Test
    public void testStopObserve() {
        result.addFrame(newFrame(ESV.INF));
        
        assertEquals(1, result.countData());
        assertEquals(1, result.countFrames());
        
        result.stopObserve();
        
        assertEquals(1, result.countData());
        assertEquals(1, result.countFrames());
    }

    /**
     * Test of isDone method, of class ObserveResult.
     */
    @Test
    public void testIsDone() {
        assertFalse(result.isDone());
        result.stopObserve();
        assertTrue(result.isDone());
        result.stopObserve();
        assertTrue(result.isDone());
    }

    /**
     * Test of shouldReceive method, of class ObserveResult.
     */
    @Test
    public void testShouldReceive() {
        selector.result = true;
        for (int i=0; i<ESV.values().length; i++) {
            ESV esv = ESV.values()[i];
            assertTrue(result.shouldReceive(newFrame(esv)));
        }
        assertTrue(result.shouldReceive(newSimpleFrame()));
        
        selector.result = false;
        for (int i=0; i<ESV.values().length; i++) {
            ESV esv = ESV.values()[i];
            assertFalse(result.shouldReceive(newFrame(esv)));
        }
        assertFalse(result.shouldReceive(newSimpleFrame()));
    }

    /**
     * Test of hasStandardPayload method, of class ObserveResult.
     */
    @Test
    public void testHasStandardPayload() {
        for (int i=0; i<ESV.values().length; i++) {
            ESV esv = ESV.values()[i];
            assertTrue(result.hasStandardPayload(newFrame(esv)));
        }
        assertFalse(result.hasStandardPayload(newSimpleFrame()));
    }

    /**
     * Test of addFrame method, of class ObserveResult.
     */
    @Test
    public void testAddFrame_Frame() {
        assertEquals(0, result.countFrames());
        result.addFrame(newFrame(ESV.INF));
        checkFrameDataMap();
        assertEquals(1, result.countFrames());
        result.addFrame(newFrame(ESV.INFC));
        checkFrameDataMap();
        assertEquals(2, result.countFrames());
        result.addFrame(newSimpleFrame());
        checkFrameDataMap();
        assertEquals(2, result.countFrames());
        
        Frame frame = newFrame(ESV.Get);
        result.addFrame(frame);
        result.addFrame(frame);
        assertEquals(4, result.countFrames());
    }

    /**
     * Test of addFrame method, of class ObserveResult.
     */
    @Test
    public void testAddFrame_ResultFrame() {
        assertEquals(0, result.countFrames());
        assertTrue(result.addFrame(new ResultFrame(newFrame(ESV.INF), 10)));
        checkFrameDataMap();
        assertEquals(1, result.countFrames());
        assertTrue(result.addFrame(new ResultFrame(newFrame(ESV.INFC), 10)));
        checkFrameDataMap();
        assertEquals(2, result.countFrames());
        assertFalse(result.addFrame(new ResultFrame(newSimpleFrame(), 10)));
        checkFrameDataMap();
        assertEquals(2, result.countFrames());
        
        ResultFrame resultFrame = new ResultFrame(newFrame(ESV.Get), 10);
        assertTrue(result.addFrame(resultFrame));
        assertFalse(result.addFrame(resultFrame));
        assertEquals(3, result.countFrames());
    }

    /**
     * Test of countFrames method, of class ObserveResult.
     */
    @Test
    public void testCountFrames() {
        assertEquals(0, result.countFrames());
        result.addFrame(newFrame(ESV.INF));
        assertEquals(1, result.countFrames());
        result.addFrame(newFrame2(ESV.INF));
        assertEquals(2, result.countFrames());
    }

    /**
     * Test of getFrame method, of class ObserveResult.
     */
    @Test
    public void testGetFrame_int() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        
        assertEquals(frame1, result.getFrame(0).frame);
        assertEquals(frame2, result.getFrame(1).frame);
    }

    /**
     * Test of getFrame method, of class ObserveResult.
     */
    @Test
    public void testGetFrame_ResultData() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        
        assertEquals(3, result.countData());
        
        ResultData resultData1 = result.getData(0);
        assertEquals(frame1, result.getFrame(resultData1).frame);
        
        ResultData resultData2 = result.getData(1);
        assertEquals(frame2, result.getFrame(resultData2).frame);
        
        ResultData resultData3 = result.getData(2);
        assertEquals(frame2, result.getFrame(resultData3).frame);
    }

    /**
     * Test of getFrameList method, of class ObserveResult.
     */
    @Test
    public void testGetFrameList() {
        assertTrue(result.getFrameList().isEmpty());
        
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        
        assertEquals(frame1, result.getFrameList().get(0).frame);
        assertEquals(frame2, result.getFrameList().get(1).frame);
    }

    /**
     * Test of countData method, of class ObserveResult.
     */
    @Test
    public void testCountData() {
        assertEquals(0, result.countData());
        result.addFrame(newFrame(ESV.INF));
        assertEquals(1, result.countData());
        result.addFrame(newFrame2(ESV.INF));
        assertEquals(3, result.countData());
    }

    /**
     * Test of getData method, of class ObserveResult.
     */
    @Test
    public void testGetData() {
        result.addFrame(newFrame(ESV.INF));
        assertEquals(new EOJ("0ef001"), result.getData(0).eoj);
        assertEquals(EPC.x80, result.getData(0).epc);
        assertEquals(subnet.getLocalNode(), result.getData(0).node);
        assertEquals(new Data(new byte[]{0x30}), result.getData(0).data);
        assertEquals(result.getFrame(0).time, result.getData(0).time);
        
        result.addFrame(newFrame2(ESV.INF));
        assertEquals(new EOJ("0ef001"), result.getData(1).eoj);
        assertEquals(EPC.x80, result.getData(1).epc);
        assertEquals(subnet.getLocalNode(), result.getData(1).node);
        assertEquals(new Data(new byte[]{0x31}), result.getData(1).data);
        assertEquals(result.getFrame(1).time, result.getData(1).time);
        
        assertEquals(new EOJ("0ef001"), result.getData(1).eoj);
        assertEquals(EPC.x81, result.getData(2).epc);
        assertEquals(subnet.getLocalNode(), result.getData(2).node);
        assertEquals(new Data(new byte[]{0x40}), result.getData(2).data);
        assertEquals(result.getFrame(1).time, result.getData(2).time);
    }

    /**
     * Test of getDataList method, of class ObserveResult.
     */
    @Test
    public void testGetDataList_0args() {
        assertTrue(result.getDataList().isEmpty());
        
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        assertEquals(1, result.getDataList().size());
        result.addFrame(frame2);
        assertEquals(3, result.getDataList().size());
    }

    /**
     * Test of getDataList method, of class ObserveResult.
     */
    @Test
    public void testGetDataList_ResultDataSelector() {
        Selector<ResultData> selectorAny = new SelectorAny<ResultData>();
        
        Selector<ResultData> selectorNone = new SelectorNone<ResultData>();
        
        Selector<ResultData> selectorEPCx80 = new Selector<ResultData>() {
            @Override
            public boolean match(ResultData resultData) {
                return resultData.epc == EPC.x80;
            }
        };
        
        assertTrue(result.getDataList().isEmpty());
        
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        assertEquals(1, result.getDataList(selectorAny).size());
        assertEquals(0, result.getDataList(selectorNone).size());
        assertEquals(1, result.getDataList(selectorEPCx80).size());
        
        result.addFrame(frame2);
        assertEquals(3, result.getDataList(selectorAny).size());
        assertEquals(0, result.getDataList(selectorNone).size());
        assertEquals(2, result.getDataList(selectorEPCx80).size());
    }

    /**
     * Test of getDataList method, of class ObserveResult.
     */
    @Test
    public void testGetDataList_ResultFrame() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        
        ResultFrame resultFrame1 = result.getFrame(0);
        List<ResultData> list1 = result.getDataList(resultFrame1);
        assertEquals(1, list1.size());
        assertEquals(EPC.x80, list1.get(0).epc);
        
        ResultFrame resultFrame2 = result.getFrame(1);
        List<ResultData> list2 = result.getDataList(resultFrame2);
        assertEquals(2, list2.size());
        assertEquals(EPC.x80, list2.get(0).epc);
        assertEquals(EPC.x81, list2.get(1).epc);
    }

    /**
     * Test of truncateData method, of class ObserveResult.
     */
    @Test
    public void testTruncateData() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        checkFrameDataMap();
        
        assertEquals(3, result.countData());
        assertEquals(2, result.truncateData(2));
        assertEquals(1, result.countData());
        assertEquals(1, result.truncateData(10));
        assertEquals(0, result.countData());
        
        assertEquals(2, result.countFrames());
    }

    /**
     * Test of removeData method, of class ObserveResult.
     */
    @Test
    public void testRemoveData_ResultData() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        
        ResultData resultData = result.getData(2);
        assertTrue(result.getDataList().contains(resultData));
        result.removeData(resultData);
        assertFalse(result.getDataList().contains(resultData));
        
        assertEquals(2, result.countFrames());
    }

    /**
     * Test of removeData method, of class ObserveResult.
     */
    @Test
    public void testRemoveData_ResultFrame() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        checkFrameDataMap();
        
        ResultFrame resultFrame = result.getFrame(1);
        result.removeData(resultFrame);
        checkFrameDataMap();
        assertEquals(1, result.countData());
        assertEquals(EPC.x80, result.getData(0).epc);
        assertEquals((byte)0x30, result.getData(0).data.get(0));
        
        assertEquals(2, result.countFrames());
    }

    /**
     * Test of removeData method, of class ObserveResult.
     */
    @Test
    public void testRemoveData_int() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        checkFrameDataMap();
        
        result.removeData(1);
        checkFrameDataMap();
        
        assertEquals(2, result.countData());
        assertEquals((byte)0x30, result.getData(0).data.get(0));
        assertEquals((byte)0x40, result.getData(1).data.get(0));
        
        assertEquals(2, result.countFrames());
    }

    /**
     * Test of removeAll method, of class ObserveResult.
     */
    @Test
    public void testRemoveAll() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        
        result.removeAll();
        assertEquals(0, result.countData());
        assertEquals(0, result.countFrames());
    }

    /**
     * Test of removeAllData method, of class ObserveResult.
     */
    @Test
    public void testRemoveAllData() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        checkFrameDataMap();
        
        result.removeAllData();
        checkFrameDataMap();
        
        assertEquals(0, result.countData());
        assertEquals(2, result.countFrames());
    }

    /**
     * Test of truncateFrames method, of class ObserveResult.
     */
    @Test
    public void testTruncateFrames() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        checkFrameDataMap();
        
        assertEquals(2, result.countFrames());
        
        assertEquals(2, result.truncateFrames(2));
        assertEquals(0, result.countFrames());
        checkFrameDataMap();
        
        assertEquals(0, result.truncateFrames(10));
        assertEquals(0, result.countFrames());
        checkFrameDataMap();
        
        assertEquals(3, result.countData());
    }

    /**
     * Test of removeFrame method, of class ObserveResult.
     */
    @Test
    public void testRemoveFrame_ResultFrame() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        checkFrameDataMap();
        
        ResultFrame resultFrame = result.getFrame(0);
        
        result.removeFrame(resultFrame);
        checkFrameDataMap();
        
        assertEquals(1, result.countFrames());
        assertEquals(frame2, result.getFrame(0).frame);
        
        assertEquals(3, result.countData());
    }

    /**
     * Test of removeFrame method, of class ObserveResult.
     */
    @Test
    public void testRemoveFrame_ResultData() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        checkFrameDataMap();
        
        ResultData resultData = result.getData(2);
        
        result.removeFrame(resultData);
        checkFrameDataMap();
        
        assertEquals(1, result.countFrames());
        assertEquals(frame1, result.getFrame(0).frame);
        
        assertEquals(3, result.countData());
    }

    /**
     * Test of removeFrame method, of class ObserveResult.
     */
    @Test
    public void testRemoveFrame_int() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        checkFrameDataMap();
        
        result.removeFrame(0);
        checkFrameDataMap();
        
        assertEquals(1, result.countFrames());
        assertEquals(frame2, result.getFrame(0).frame);
        
        assertEquals(3, result.countData());
    }

    /**
     * Test of removeAllFrames method, of class ObserveResult.
     */
    @Test
    public void testRemoveAllFrames() {
        Frame frame1 = newFrame(ESV.INF);
        Frame frame2 = newFrame2(ESV.INFC);
        
        result.addFrame(frame1);
        result.addFrame(frame2);
        checkFrameDataMap();
        
        result.removeAllFrames();
        checkFrameDataMap();
        
        assertEquals(3, result.countData());
        assertEquals(0, result.countFrames());
    }
    
    @Test
    public void testDataFrameMap() {
        Frame frame = newFrame(ESV.INF, new Property(EPC.x80, new Data(new byte[]{0x30})), new Property(EPC.xE0, new Data(new byte[]{(byte)0xff})));
        ResultFrame resultFrame1 = new ResultFrame(frame, 10);
        ResultFrame resultFrame2 = new ResultFrame(frame, 10);
        assertTrue(result.addFrame(resultFrame1));
        assertTrue(result.addFrame(resultFrame2));
        
        assertEquals(2, result.countFrames());
        assertEquals(4, result.countData());
        
        ResultData d = result.getData(0);
        ResultData d1 = result.getData(2);
        ResultData d2 = result.getData(3);
        
        assertEquals(resultFrame1, result.getFrame(0));
        assertEquals(resultFrame2, result.getFrame(1));
        
        assertEquals(2, result.removeData(resultFrame1));
        assertEquals(2, result.countFrames());
        assertEquals(resultFrame1, result.getFrame(0));
        assertEquals(resultFrame2, result.getFrame(1));
        
        assertFalse(result.removeFrame(d));
        assertEquals(resultFrame1, result.getFrame(0));
        assertEquals(resultFrame2, result.getFrame(1));
        
        assertTrue(result.removeFrame(resultFrame1));
        assertEquals(1, result.countFrames());
        assertEquals(resultFrame2, result.getFrame(0));
        
        assertEquals(2, result.countData());
        assertEquals(d1, result.getData(0));
        assertEquals(d2, result.getData(1));
        
        assertTrue(result.addFrame(resultFrame1));
        assertTrue(result.removeFrame(d1));
        assertEquals(1, result.countFrames());
        assertEquals(resultFrame1, result.getFrame(0));
        
        assertEquals(4, result.countData());
        assertEquals(0, result.removeData(resultFrame2));
    }
}
