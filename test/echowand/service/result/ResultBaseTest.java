package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.InternalSubnet;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import echowand.service.TimestampManager;
import java.util.List;
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
public class ResultBaseTest {
    ResultBaseImpl resultBase;
    Subnet subnet;
    
    public ResultBaseTest() {
    }
    
    @Before
    public void setUp() throws SubnetException {
        subnet = InternalSubnet.startSubnet();
        resultBase = new ResultBaseImpl();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testDone() {
        assertFalse(resultBase.isDone());
        resultBase.finish();
        assertTrue(resultBase.isDone());
        resultBase.finish();
        assertTrue(resultBase.isDone());
    }

    @Test
    public void testIsDone() {
        assertFalse(resultBase.isDone());
        resultBase.finish();
        assertTrue(resultBase.isDone());
    }

    private static class T {
        public long t;
        
        public T() {
            t = System.currentTimeMillis();
        }
        
        public void update() {
            t = System.currentTimeMillis();
        }
    }
    
    @Test
    public void testJoin() throws Exception {
        final T t1 = new T();
        final T t2 = new T();
        
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ResultBaseTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                t1.update();
                resultBase.finish();
            }
        }.start();
        
        resultBase.join();
        t2.update();
        
        assertTrue(t1.t <= t2.t);
    }
    
    public T startAddFrameThread(final ResultBase resultBase, final Frame frame, final int mills) {
        final T t = new T();
        
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(mills);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ResultBaseTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                resultBase.addFrame(frame);
                t.update();
                
                // System.out.println(resultBase.getDataList(new ResultDataSelector()));
            }
        }.start();
        
        return t;
    }
    
    public Frame newFrame1() {
        StandardPayload payload = new StandardPayload();
        payload.setDEOJ(new EOJ("0ef001"));
        payload.setSEOJ(new EOJ("0ef001"));
        payload.setESV(ESV.Get_Res);
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x30)));
        
        CommonFrame commonFrame = new CommonFrame();
        commonFrame.setEDATA(payload);
        
        return new Frame(subnet.getLocalNode(), subnet.getLocalNode(), commonFrame);
    }
    
    public Frame newFrame2() {
        StandardPayload payload = new StandardPayload();
        payload.setDEOJ(new EOJ("001101"));
        payload.setSEOJ(new EOJ("001101"));
        payload.setESV(ESV.Get_Res);
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x30)));
        
        CommonFrame commonFrame = new CommonFrame();
        commonFrame.setEDATA(payload);
        
        return new Frame(subnet.getLocalNode(), subnet.getLocalNode(), commonFrame);
    }
    
    public Frame newFrame3() {
        StandardPayload payload = new StandardPayload();
        payload.setDEOJ(new EOJ("001101"));
        payload.setSEOJ(new EOJ("001101"));
        payload.setESV(ESV.Get_Res);
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x30)));
        payload.addFirstProperty(new Property(EPC.xE0, new Data((byte)0x12, (byte)0x34)));
        
        CommonFrame commonFrame = new CommonFrame();
        commonFrame.setEDATA(payload);
        
        return new Frame(subnet.getLocalNode(), subnet.getLocalNode(), commonFrame);
    }
    
    public Frame newFrame4() {
        StandardPayload payload = new StandardPayload();
        payload.setDEOJ(new EOJ("001101"));
        payload.setSEOJ(new EOJ("001101"));
        payload.setESV(ESV.SetGet);
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x30)));
        payload.addSecondProperty(new Property(EPC.xFF));
        
        CommonFrame commonFrame = new CommonFrame();
        commonFrame.setEDATA(payload);
        
        return new Frame(subnet.getLocalNode(), subnet.getLocalNode(), commonFrame);
    }

    @Test
    public void testWaitData_Selector_int() throws Exception {
        T t1 = new T();
        startAddFrameThread(resultBase, newFrame1(), 1000);
        startAddFrameThread(resultBase, newFrame2(), 2000);
        startAddFrameThread(resultBase, newFrame1(), 3000);
        
        assertTrue(resultBase.waitData(new ResultDataSelector(), 0));
        T t2 = new T();
        
        assertTrue(resultBase.waitData(new ResultDataSelector(new EOJ("0ef001")), 1));
        T t3 = new T();
        
        assertTrue(resultBase.waitData(new ResultDataSelector(new EOJ("001101")), 1));
        T t4 = new T();
        
        assertTrue(resultBase.waitData(new ResultDataSelector(new EOJ("0ef001")), 2));
        T t5 = new T();
        
        resultBase.finish();
        assertFalse(resultBase.waitData(new ResultDataSelector(new EOJ("0ef001")), 3));
        
        assertTrue(t2.t - t1.t < 1000);
        assertTrue(t3.t - t1.t >= 1000);
        assertTrue(t4.t - t1.t >= 2000);
        assertTrue(t5.t - t1.t >= 3000);
    }

    @Test
    public void testWaitData_int() throws Exception {
        T t1 = new T();
        startAddFrameThread(resultBase, newFrame1(), 1000);
        startAddFrameThread(resultBase, newFrame2(), 2000);
        startAddFrameThread(resultBase, newFrame1(), 3000);
        
        assertTrue(resultBase.waitData(0));
        T t2 = new T();
        
        assertTrue(resultBase.waitData(1));
        T t3 = new T();
        
        assertTrue(resultBase.waitData(2));
        T t4 = new T();
        
        resultBase.finish();
        assertFalse(resultBase.waitData(3));
        
        assertTrue(t2.t - t1.t < 1000);
        assertTrue(t3.t - t1.t >= 1000);
        assertTrue(t4.t - t1.t >= 2000);
    }

    @Test
    public void testWaitData_Selector() throws Exception {
        T t1 = new T();
        startAddFrameThread(resultBase, newFrame1(), 1000);
        startAddFrameThread(resultBase, newFrame2(), 2000);
        startAddFrameThread(resultBase, newFrame1(), 3000);
        
        assertTrue(resultBase.waitData(new ResultDataSelector(new EOJ("0ef001"))));
        T t3 = new T();
        
        assertTrue(resultBase.waitData(new ResultDataSelector(new EOJ("001101"))));
        T t4 = new T();
        
        resultBase.finish();
        assertFalse(resultBase.waitData(new ResultDataSelector(new EOJ("001201"))));
        
        assertTrue(t3.t - t1.t >= 1000);
        assertTrue(t4.t - t1.t >= 2000);
    }

    @Test
    public void testWaitData_0args() throws Exception {
        T t1 = new T();
        startAddFrameThread(resultBase, newFrame1(), 1000);
        
        assertTrue(resultBase.waitData());
        T t3 = new T();
        
        resultBase.finish();
        assertTrue(resultBase.waitData());
        
        assertTrue(t3.t - t1.t >= 1000);
        
        resultBase = new ResultBaseImpl();
        resultBase.finish();
        assertFalse(resultBase.waitData());
    }

    @Test
    public void testWaitFrames_Selector_int() throws Exception {
        T t1 = new T();
        startAddFrameThread(resultBase, newFrame1(), 1000);
        startAddFrameThread(resultBase, newFrame2(), 2000);
        startAddFrameThread(resultBase, newFrame1(), 3000);
        
        assertTrue(resultBase.waitFrames(new ResultFrameSelector(), 0));
        T t2 = new T();
        
        assertTrue(resultBase.waitFrames(new ResultFrameSelector(new EOJ("0ef001")), 1));
        T t3 = new T();
        
        assertTrue(resultBase.waitFrames(new ResultFrameSelector(new EOJ("001101")), 1));
        T t4 = new T();
        
        assertTrue(resultBase.waitFrames(new ResultFrameSelector(new EOJ("0ef001")), 2));
        T t5 = new T();
        
        resultBase.finish();
        assertFalse(resultBase.waitFrames(new ResultFrameSelector(new EOJ("0ef001")), 3));
        
        assertTrue(t2.t - t1.t < 1000);
        assertTrue(t3.t - t1.t >= 1000);
        assertTrue(t4.t - t1.t >= 2000);
        assertTrue(t5.t - t1.t >= 3000);
    }

    @Test
    public void testWaitFrames_int() throws Exception {
        T t1 = new T();
        startAddFrameThread(resultBase, newFrame1(), 1000);
        startAddFrameThread(resultBase, newFrame2(), 2000);
        startAddFrameThread(resultBase, newFrame1(), 3000);
        
        assertTrue(resultBase.waitFrames(0));
        T t2 = new T();
        
        assertTrue(resultBase.waitFrames(1));
        T t3 = new T();
        
        assertTrue(resultBase.waitFrames(2));
        T t4 = new T();
        
        resultBase.finish();
        assertFalse(resultBase.waitFrames(3));
        
        assertTrue(t2.t - t1.t < 1000);
        assertTrue(t3.t - t1.t >= 1000);
        assertTrue(t4.t - t1.t >= 2000);
    }

    @Test
    public void testWaitFrame_Selector() throws Exception {
        T t1 = new T();
        startAddFrameThread(resultBase, newFrame1(), 1000);
        startAddFrameThread(resultBase, newFrame2(), 2000);
        startAddFrameThread(resultBase, newFrame1(), 3000);
        
        assertTrue(resultBase.waitFrame(new ResultFrameSelector(new EOJ("0ef001"))));
        T t3 = new T();
        
        assertTrue(resultBase.waitFrame(new ResultFrameSelector(new EOJ("001101"))));
        T t4 = new T();
        
        resultBase.finish();
        assertFalse(resultBase.waitFrame(new ResultFrameSelector(new EOJ("001201"))));
        
        assertTrue(t3.t - t1.t >= 1000);
        assertTrue(t4.t - t1.t >= 2000);
    }

    @Test
    public void testWaitFrame_0args() throws Exception {
        T t1 = new T();
        startAddFrameThread(resultBase, newFrame1(), 1000);
        
        assertTrue(resultBase.waitFrame());
        T t3 = new T();
        
        resultBase.finish();
        assertTrue(resultBase.waitFrame());
        
        assertTrue(t3.t - t1.t >= 1000);
        
        resultBase = new ResultBaseImpl();
        resultBase.finish();
        assertFalse(resultBase.waitFrame());
    }

    @Test
    public void testIsValidSecondProperty() {
        Property p1 = new Property(EPC.x80);
        Property p2 = new Property(EPC.xFF);
        
        assertFalse(resultBase.isValidSecondProperty(p1));
        assertTrue(resultBase.isValidSecondProperty(p2));
    }

    @Test
    public void testHasStandardPayload() {
        assertTrue(resultBase.hasStandardPayload(newFrame1()));
    }

    @Test
    public void testAddRequestFrame_Frame_boolean() {
        Frame frame1 = newFrame1();
        assertTrue(resultBase.addRequestFrame(frame1, true));
        assertTrue(resultBase.getRequestFrame(0).getActualFrame() == frame1);
        assertTrue(resultBase.addRequestFrame(frame1, true));
        assertTrue(resultBase.getRequestFrame(1).getActualFrame() == frame1);
        assertEquals(2, resultBase.countRequestFrames());
        
        Frame frame2 = newFrame2();
        assertTrue(resultBase.addRequestFrame(frame2, false));
        assertEquals(3, resultBase.countRequestFrames());
        assertTrue(resultBase.getRequestFrame(2).getActualFrame() == frame2);
        
        resultBase.finish();
        Frame frame3 = newFrame3();
        assertFalse(resultBase.addRequestFrame(frame3, false));
        assertFalse(resultBase.addRequestFrame(frame3, true));
        assertEquals(3, resultBase.countRequestFrames());
    }

    @Test
    public void testAddRequestFrame_ResultFrame_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.getRequestFrame(0) == resultFrame1);
        assertFalse(resultBase.addRequestFrame(resultFrame1, true));
        
        ResultFrame resultFrame2 = new ResultFrame(newFrame1(), 10);
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        assertTrue(resultBase.getRequestFrame(1) == resultFrame2);
        
        resultBase.finish();
        ResultFrame resultFrame3 = new ResultFrame(newFrame1(), 10);
        assertFalse(resultBase.addRequestFrame(resultFrame3, false));
        assertFalse(resultBase.addRequestFrame(resultFrame3, true));
    }

    @Test
    public void testAddFrame_Frame() {
        Frame frame1 = newFrame1();
        assertTrue(resultBase.addFrame(frame1));
        assertTrue(resultBase.getFrame(0).getActualFrame() == frame1);
        
        resultBase.finish();
        Frame frame2 = newFrame1();
        assertFalse(resultBase.addFrame(frame2));
        assertEquals(1, resultBase.countFrames());
    }

    @Test
    public void testAddFrame_ResultFrame() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.getFrame(0) == resultFrame1);
        
        resultBase.finish();
        ResultFrame resultFrame2 = new ResultFrame(newFrame1(), 10);
        assertFalse(resultBase.addFrame(resultFrame2));
        assertEquals(1, resultBase.countFrames());
    }

    @Test
    public void testCountRequestFrames_0args() {
        assertEquals(0, resultBase.countRequestFrames());
        assertTrue(resultBase.addRequestFrame(newFrame1(), true));
        assertEquals(1, resultBase.countRequestFrames());
    }

    @Test
    public void testCountFrames_0args() {
        assertEquals(0, resultBase.countRequestFrames());
        assertTrue(resultBase.addRequestFrame(newFrame1(), true));
        assertEquals(1, resultBase.countRequestFrames());
    }

    @Test
    public void testGetRequestFrame_int() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame1(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, true));
        assertTrue(resultBase.getRequestFrame(0) == resultFrame1);
        assertTrue(resultBase.getRequestFrame(1) == resultFrame2);
    }

    @Test
    public void testGetRequestFrame_ResultData() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame1(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, true));
        
        ResultData resultData = resultBase.getRequestData(1);
        
        assertTrue(resultBase.getRequestFrame(resultData) == resultFrame2);
    }

    @Test
    public void testGetRequestFrameList_0args() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame1(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, true));
        
        assertEquals(2, resultBase.getRequestFrameList().size());
        assertTrue(resultBase.getRequestFrameList().get(0) == resultFrame1);
        assertTrue(resultBase.getRequestFrameList().get(1) == resultFrame2);
    }

    @Test
    public void testGetRequestFrameList_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame1(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        assertEquals(2, resultBase.getRequestFrameList().size());
        assertTrue(resultBase.getRequestFrameList(true).get(0) == resultFrame1);
        assertTrue(resultBase.getRequestFrameList(false).get(0) == resultFrame2);
    }

    @Test
    public void testGetRequestFrameList_Selector() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame2(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, true));
        
        List<ResultFrame> frames = resultBase.getRequestFrameList(new ResultFrameSelector(new EOJ("001101")));
        assertEquals(1, frames.size());
        assertTrue(frames.get(0) == resultFrame2);
    }

    @Test
    public void testGetFrame_int() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame1(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertTrue(resultBase.getFrame(0) == resultFrame1);
        assertTrue(resultBase.getFrame(1) == resultFrame2);
    }

    @Test
    public void testGetFrame_ResultData() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame1(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        ResultData resultData = resultBase.getData(1);
        
        assertTrue(resultBase.getFrame(resultData) == resultFrame2);
    }

    @Test
    public void testGetFrameList_0args() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame1(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertEquals(2, resultBase.getFrameList().size());
        assertTrue(resultBase.getFrameList().get(0) == resultFrame1);
        assertTrue(resultBase.getFrameList().get(1) == resultFrame2);
    }

    @Test
    public void testGetFrameList_Selector() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame2(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        List<ResultFrame> frames = resultBase.getFrameList(new ResultFrameSelector(new EOJ("001101")));
        assertEquals(1, frames.size());
        assertTrue(frames.get(0) == resultFrame2);
    }

    @Test
    public void testCountData_0args() {
        assertEquals(0, resultBase.countData());
        assertTrue(resultBase.addFrame(newFrame1()));
        assertEquals(1, resultBase.countData());
        assertTrue(resultBase.addFrame(newFrame3()));
        assertEquals(3, resultBase.countData());
    }

    @Test
    public void testGetData_int() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertEquals(new Data((byte)0x30), resultBase.getData(0).getActualData());
        assertEquals(new Data((byte)0x30), resultBase.getData(1).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), resultBase.getData(2).getActualData());
    }

    @Test
    public void testGetDataList_0args() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertEquals(3, resultBase.getDataList().size());
        assertEquals(new Data((byte)0x30), resultBase.getDataList().get(0).getActualData());
        assertEquals(new Data((byte)0x30), resultBase.getDataList().get(1).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), resultBase.getDataList().get(2).getActualData());
    }

    @Test
    public void testGetDataList_Selector() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        List<ResultData> dataList = resultBase.getDataList(new ResultDataSelector(new EOJ("001101")));
        assertEquals(2, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), dataList.get(1).getActualData());
    }

    @Test
    public void testGetDataList_ResultFrame() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        
        assertTrue(resultBase.getDataList(resultFrame2).isEmpty());
        
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertTrue(resultBase.getDataList(new ResultFrame(newFrame1(), 10)).isEmpty());
        
        List<ResultData> dataList1 = resultBase.getDataList(resultFrame1);
        
        assertEquals(1, dataList1.size());
        assertEquals(new Data((byte)0x30), dataList1.get(0).getActualData());
        
        List<ResultData> dataList2 = resultBase.getDataList(resultFrame2);
        
        assertEquals(2, dataList2.size());
        assertEquals(new Data((byte)0x30), dataList2.get(0).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), dataList2.get(1).getActualData());
    }

    @Test
    public void testCountSecondData_0args() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        
        assertEquals(0, resultBase.countSecondData());
        
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertEquals(1, resultBase.countSecondData());
    }

    @Test
    public void testGetSecondData_int() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertEquals(new Data(), resultBase.getSecondData(0).getActualData());
    }

    @Test
    public void testGetSecondDataList_0args() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        
        assertTrue(resultBase.getSecondDataList().isEmpty());
        
        assertTrue(resultBase.addFrame(resultFrame2));
        
        List<ResultData> dataList = resultBase.getSecondDataList();
        
        assertEquals(1, dataList.size());
        assertEquals(EPC.xFF, dataList.get(0).getEPC());
        assertEquals(new Data(), dataList.get(0).getActualData());
    }

    @Test
    public void testGetSecondDataList_Selector() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        
        assertTrue(resultBase.getSecondDataList(new ResultDataSelector(EPC.xFF)).isEmpty());
        
        assertTrue(resultBase.addFrame(resultFrame2));
        
        List<ResultData> dataList1 = resultBase.getSecondDataList(new ResultDataSelector(EPC.xE0));
        assertTrue(dataList1.isEmpty());
        
        List<ResultData> dataList2 = resultBase.getSecondDataList(new ResultDataSelector(EPC.xFF));
        assertEquals(1, dataList2.size());
        assertEquals(EPC.xFF, dataList2.get(0).getEPC());
        assertEquals(new Data(), dataList2.get(0).getActualData());
    }

    @Test
    public void testGetSecondDataList_ResultFrame() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        
        assertTrue(resultBase.getSecondDataList(resultFrame2).isEmpty());
        
        assertTrue(resultBase.addFrame(resultFrame2));
        
        List<ResultData> dataList1 = resultBase.getSecondDataList(resultFrame1);
        assertTrue(dataList1.isEmpty());
        
        List<ResultData> dataList2 = resultBase.getSecondDataList(resultFrame2);
        assertEquals(1, dataList2.size());
        assertEquals(EPC.xFF, dataList2.get(0).getEPC());
        assertEquals(new Data(), dataList2.get(0).getActualData());
    }

    @Test
    public void testCountRequestData_0args() {
        assertEquals(0, resultBase.countRequestData());
        assertTrue(resultBase.addRequestFrame(newFrame1(), true));
        assertEquals(1, resultBase.countRequestData());
        assertTrue(resultBase.addRequestFrame(newFrame3(), true));
        assertEquals(3, resultBase.countRequestData());
    }

    @Test
    public void testGetRequestData_int() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        assertEquals(new Data((byte)0x30), resultBase.getRequestData(0).getActualData());
        assertEquals(new Data((byte)0x30), resultBase.getRequestData(1).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), resultBase.getRequestData(2).getActualData());
    }

    @Test
    public void testGetRequestDataList_0args() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        assertEquals(3, resultBase.getRequestDataList().size());
        assertEquals(new Data((byte)0x30), resultBase.getRequestDataList().get(0).getActualData());
        assertEquals(new Data((byte)0x30), resultBase.getRequestDataList().get(1).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), resultBase.getRequestDataList().get(2).getActualData());
    }

    @Test
    public void testGetRequestDataList_Selector() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        List<ResultData> dataList = resultBase.getRequestDataList(new ResultDataSelector(new EOJ("001101")));
        assertEquals(2, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), dataList.get(1).getActualData());
    }

    @Test
    public void testGetRequestDataList_ResultFrame() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        
        assertTrue(resultBase.getRequestDataList(resultFrame2).isEmpty());
        
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        assertTrue(resultBase.getRequestDataList(new ResultFrame(newFrame1(), 10)).isEmpty());
        
        List<ResultData> dataList = resultBase.getRequestDataList(resultFrame2);
        
        assertEquals(2, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), dataList.get(1).getActualData());
    }

    @Test
    public void testCountRequestSecondData_0args() {
        assertEquals(0, resultBase.countRequestSecondData());
        
        assertTrue(resultBase.addRequestFrame(newFrame1(), true));
        assertEquals(0, resultBase.countRequestSecondData());
        
        assertTrue(resultBase.addRequestFrame(newFrame1(), false));
        assertEquals(0, resultBase.countRequestSecondData());
        
        assertTrue(resultBase.addRequestFrame(newFrame4(), true));
        assertEquals(1, resultBase.countRequestSecondData());
        
        assertTrue(resultBase.addRequestFrame(newFrame4(), false));
        assertEquals(2, resultBase.countRequestSecondData());
    }

    @Test
    public void testGetRequestSecondData_int() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        assertEquals(new Data(), resultBase.getRequestSecondData(0).getActualData());
    }

    @Test
    public void testGetRequestSecondDataList_0args() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        assertEquals(1, resultBase.getRequestSecondDataList().size());
        assertEquals(new Data(), resultBase.getRequestSecondDataList().get(0).getActualData());
    }

    @Test
    public void testGetRequestSecondDataList_Selector() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        List<ResultData> dataList = resultBase.getRequestSecondDataList(new ResultDataSelector(new EOJ("001101")));
        assertEquals(1, dataList.size());
        assertEquals(new Data(), dataList.get(0).getActualData());
    }

    @Test
    public void testGetRequestSecondDataList_ResultFrame() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        
        assertTrue(resultBase.getRequestSecondDataList(resultFrame2).isEmpty());
        
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        assertTrue(resultBase.getRequestSecondDataList(resultFrame1).isEmpty());
        
        List<ResultData> dataList = resultBase.getRequestSecondDataList(resultFrame2);
        
        assertEquals(1, dataList.size());
        assertEquals(new Data(), dataList.get(0).getActualData());
    }

    @Test
    public void testCountRequestFrames_boolean() {
        assertEquals(0, resultBase.countRequestFrames(true));
        assertEquals(0, resultBase.countRequestFrames(false));
        
        assertTrue(resultBase.addRequestFrame(newFrame1(), true));
        assertEquals(1, resultBase.countRequestFrames(true));
        assertEquals(0, resultBase.countRequestFrames(false));
        
        assertTrue(resultBase.addRequestFrame(newFrame1(), false));
        assertEquals(1, resultBase.countRequestFrames(true));
        assertEquals(1, resultBase.countRequestFrames(false));
    }

    @Test
    public void testGetRequestFrame_int_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame1(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.getRequestFrame(0, true) == resultFrame1);
        
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        assertTrue(resultBase.getRequestFrame(0, false) == resultFrame2);
    }

    @Test
    public void testCountRequestData_boolean() {
        assertEquals(0, resultBase.countRequestData());
        
        assertTrue(resultBase.addRequestFrame(newFrame1(), true));
        assertEquals(1, resultBase.countRequestData(true));
        assertEquals(0, resultBase.countRequestData(false));
        
        assertTrue(resultBase.addRequestFrame(newFrame3(), false));
        assertEquals(1, resultBase.countRequestData(true));
        assertEquals(2, resultBase.countRequestData(false));
    }

    @Test
    public void testGetRequestData_int_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertEquals(new Data((byte)0x30), resultBase.getRequestData(0, true).getActualData());
        
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        assertEquals(new Data((byte)0x30), resultBase.getRequestData(0, false).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), resultBase.getRequestData(1, false).getActualData());
    }

    @Test
    public void testGetRequestDataList_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertEquals(1, resultBase.getRequestDataList(true).size());
        assertEquals(0, resultBase.getRequestDataList(false).size());
        assertEquals(new Data((byte)0x30), resultBase.getRequestDataList(true).get(0).getActualData());
        assertTrue(resultBase.getRequestDataList(false).isEmpty());
        
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        assertEquals(1, resultBase.getRequestDataList(true).size());
        assertEquals(2, resultBase.getRequestDataList(false).size());
        assertEquals(new Data((byte)0x30), resultBase.getRequestDataList(true).get(0).getActualData());
        assertEquals(new Data((byte)0x30), resultBase.getRequestDataList(false).get(0).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), resultBase.getRequestDataList(false).get(1).getActualData());
    }

    @Test
    public void testGetRequestDataList_Selector_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        List<ResultData> dataList;
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        dataList = resultBase.getRequestDataList(new ResultDataSelector(new EOJ("0ef001")), true);
        assertEquals(1, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        dataList = resultBase.getRequestDataList(new ResultDataSelector(new EOJ("001101")), true);
        assertEquals(0, dataList.size());
        
        dataList = resultBase.getRequestDataList(new ResultDataSelector(new EOJ("0ef001")), false);
        assertEquals(0, dataList.size());
        dataList = resultBase.getRequestDataList(new ResultDataSelector(new EOJ("001101")), false);
        assertEquals(2, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), dataList.get(1).getActualData());
    }

    @Test
    public void testGetRequestDataList_ResultFrame_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        assertEquals(1, resultBase.getRequestDataList(resultFrame1, true).size());
        assertTrue(resultBase.getRequestDataList(resultFrame2, true).isEmpty());
        assertTrue(resultBase.getRequestDataList(resultFrame1, false).isEmpty());
        assertEquals(2, resultBase.getRequestDataList(resultFrame2, false).size());
        
        List<ResultData> dataList;
        
        dataList = resultBase.getRequestDataList(resultFrame1, true);
        assertEquals(1, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        
        dataList = resultBase.getRequestDataList(resultFrame2, false);
        assertEquals(2, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        assertEquals(new Data((byte)0x12, (byte)0x34), dataList.get(1).getActualData());
    }

    @Test
    public void testCountRequestSecondData_boolean() {
        assertEquals(0, resultBase.countRequestSecondData());
        
        assertTrue(resultBase.addRequestFrame(newFrame1(), true));
        assertEquals(0, resultBase.countRequestSecondData(true));
        assertEquals(0, resultBase.countRequestSecondData(false));
        
        assertTrue(resultBase.addRequestFrame(newFrame1(), false));
        assertEquals(0, resultBase.countRequestSecondData(true));
        assertEquals(0, resultBase.countRequestSecondData(false));
        
        assertTrue(resultBase.addRequestFrame(newFrame4(), true));
        assertEquals(1, resultBase.countRequestSecondData(true));
        assertEquals(0, resultBase.countRequestSecondData(false));
        
        assertTrue(resultBase.addRequestFrame(newFrame4(), false));
        assertEquals(1, resultBase.countRequestSecondData(true));
        assertEquals(1, resultBase.countRequestSecondData(false));
    }

    @Test
    public void testGetRequestSecondData_int_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame4(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertEquals(new Data(), resultBase.getRequestSecondData(0, true).getActualData());
        assertEquals(0, resultBase.countRequestSecondData(false));
        
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        assertEquals(new Data(), resultBase.getRequestSecondData(0, true).getActualData());
        assertEquals(new Data(), resultBase.getRequestSecondData(0, false).getActualData());
    }

    @Test
    public void testGetRequestSecondDataList_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame4(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        
        assertEquals(1, resultBase.getRequestSecondDataList(resultFrame1, true).size());
        assertTrue(resultBase.getRequestSecondDataList(resultFrame2, true).isEmpty());
        assertTrue(resultBase.getRequestSecondDataList(resultFrame1, false).isEmpty());
        assertTrue(resultBase.getRequestSecondDataList(resultFrame2, false).isEmpty());
        
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        assertEquals(1, resultBase.getRequestSecondDataList(resultFrame1, true).size());
        assertTrue(resultBase.getRequestSecondDataList(resultFrame2, true).isEmpty());
        assertTrue(resultBase.getRequestSecondDataList(resultFrame1, false).isEmpty());
        assertEquals(1, resultBase.getRequestSecondDataList(resultFrame2, false).size());
    }

    @Test
    public void testGetRequestSecondDataList_Selector_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame4(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        
        List<ResultData> dataList;
        
        dataList = resultBase.getRequestSecondDataList(new ResultDataSelector(new EOJ("001101")), true);
        assertEquals(1, dataList.size());
        assertEquals(new Data(), dataList.get(0).getActualData());
        assertTrue(resultBase.getRequestSecondDataList(new ResultDataSelector(new EOJ("0ef001")), true).isEmpty());
        assertTrue(resultBase.getRequestSecondDataList(new ResultDataSelector(new EOJ("001101")), false).isEmpty());
        
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        dataList = resultBase.getRequestSecondDataList(new ResultDataSelector(new EOJ("001101")), true);
        assertEquals(1, dataList.size());
        assertEquals(new Data(), dataList.get(0).getActualData());
        assertTrue(resultBase.getRequestSecondDataList(new ResultDataSelector(new EOJ("0ef001")), true).isEmpty());
        dataList = resultBase.getRequestSecondDataList(new ResultDataSelector(new EOJ("001101")), false);
        assertEquals(1, dataList.size());
        assertEquals(new Data(), dataList.get(0).getActualData());
    }

    @Test
    public void testGetRequestSecondDataList_ResultFrame_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame4(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame4(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        
        assertEquals(1, resultBase.getRequestSecondDataList(resultFrame1, true).size());
        assertTrue(resultBase.getRequestSecondDataList(resultFrame2, true).isEmpty());
        assertTrue(resultBase.getRequestSecondDataList(resultFrame1, false).isEmpty());
        assertTrue(resultBase.getRequestSecondDataList(resultFrame2, false).isEmpty());
        
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        assertEquals(1, resultBase.getRequestSecondDataList(resultFrame1, true).size());
        assertTrue(resultBase.getRequestSecondDataList(resultFrame2, true).isEmpty());
        assertTrue(resultBase.getRequestSecondDataList(resultFrame1, false).isEmpty());
        assertEquals(1, resultBase.getRequestSecondDataList(resultFrame2, false).size());
    }
    
    public Frame newSNAFrame1() {
        StandardPayload payload = new StandardPayload();
        payload.setDEOJ(new EOJ("0ef001"));
        payload.setSEOJ(new EOJ("0ef001"));
        payload.setESV(ESV.Get_SNA);
        payload.addFirstProperty(new Property(EPC.x90, new Data((byte)0x30)));
        payload.addFirstProperty(new Property(EPC.xF0));
        
        CommonFrame commonFrame = new CommonFrame();
        commonFrame.setEDATA(payload);
        
        return new Frame(subnet.getLocalNode(), subnet.getLocalNode(), commonFrame);
    }
    
    public Frame newSNAFrame2() {
        StandardPayload payload = new StandardPayload();
        payload.setDEOJ(new EOJ("0ef001"));
        payload.setSEOJ(new EOJ("0ef001"));
        payload.setESV(ESV.SetGet_SNA);
        payload.addFirstProperty(new Property(EPC.x90));
        payload.addFirstProperty(new Property(EPC.xF0, new Data((byte)0x12)));
        payload.addSecondProperty(new Property(EPC.x91, new Data((byte)0x30)));
        payload.addSecondProperty(new Property(EPC.xF1));
        
        CommonFrame commonFrame = new CommonFrame();
        commonFrame.setEDATA(payload);
        
        return new Frame(subnet.getLocalNode(), subnet.getLocalNode(), commonFrame);
    }

    @Test
    public void testCountFrames_boolean() {
        assertEquals(0, resultBase.countFrames(true));
        assertEquals(0, resultBase.countFrames(false));
        
        resultBase.addFrame(newFrame1());
        assertEquals(1, resultBase.countFrames(true));
        assertEquals(0, resultBase.countFrames(false));
        
        resultBase.addFrame(newSNAFrame1());
        assertEquals(1, resultBase.countFrames(true));
        assertEquals(1, resultBase.countFrames(false));
    }

    @Test
    public void testGetRequestFrameList_Selector_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newFrame3(), 10);
        
        assertTrue(resultBase.addRequestFrame(resultFrame1, true));
        assertTrue(resultBase.addRequestFrame(resultFrame2, false));
        
        List<ResultFrame> frames;
        
        frames = resultBase.getRequestFrameList(new ResultFrameSelector(new EOJ("0ef001")), true);
        assertEquals(1, frames.size());
        assertTrue(frames.get(0) == resultFrame1);
        
        frames = resultBase.getRequestFrameList(new ResultFrameSelector(new EOJ("0ef001")), false);
        assertEquals(0, frames.size());
        
        frames = resultBase.getRequestFrameList(new ResultFrameSelector(new EOJ("001101")), true);
        assertEquals(0, frames.size());
        
        frames = resultBase.getRequestFrameList(new ResultFrameSelector(new EOJ("001101")), false);
        assertEquals(1, frames.size());
        assertTrue(frames.get(0) == resultFrame2);
    }

    @Test
    public void testGetFrame_int_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newSNAFrame1(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertTrue(resultBase.getFrame(0, true) == resultFrame1);
        assertTrue(resultBase.getFrame(0, false) == resultFrame2);
    }

    @Test
    public void testGetFrameList_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newSNAFrame1(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertEquals(1, resultBase.getFrameList(true).size());
        assertEquals(1, resultBase.getFrameList(false).size());
        
        assertTrue(resultBase.getFrameList(true).get(0) == resultFrame1);
        assertTrue(resultBase.getFrameList(false).get(0) == resultFrame2);
    }

    @Test
    public void testGetFrameList_Selector_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newSNAFrame1(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        List<ResultFrame> frames;
        
        frames = resultBase.getFrameList(new ResultFrameSelector(new EOJ("0ef001")), true);
        assertEquals(1, frames.size());
        assertTrue(frames.get(0) == resultFrame1);
        frames = resultBase.getFrameList(new ResultFrameSelector(new EOJ("001101")), true);
        assertEquals(0, frames.size());
        
        frames = resultBase.getFrameList(new ResultFrameSelector(new EOJ("0ef001")), false);
        assertEquals(1, frames.size());
        assertTrue(frames.get(0) == resultFrame2);
        frames = resultBase.getFrameList(new ResultFrameSelector(new EOJ("001101")), false);
        assertEquals(0, frames.size());
    }

    @Test
    public void testCountData_boolean() {
        assertEquals(0, resultBase.countData());
        
        assertTrue(resultBase.addFrame(newFrame1()));
        assertEquals(1, resultBase.countData(true));
        assertEquals(0, resultBase.countData(false));
        
        assertTrue(resultBase.addFrame(newSNAFrame1()));
        assertEquals(2, resultBase.countData(true));
        assertEquals(1, resultBase.countData(false));
    }

    @Test
    public void testGetData_int_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newSNAFrame1(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertEquals(new Data((byte)0x30), resultBase.getData(0, true).getActualData());
        assertEquals(new Data(), resultBase.getData(1, true).getActualData());
        
        assertEquals(new Data((byte)0x30), resultBase.getData(0, false).getActualData());
    }

    @Test
    public void testGetDataList_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newSNAFrame1(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertEquals(2, resultBase.getDataList(true).size());
        assertEquals(new Data((byte)0x30), resultBase.getDataList(true).get(0).getActualData());
        assertEquals(new Data(), resultBase.getDataList(true).get(1).getActualData());
        
        assertEquals(1, resultBase.getDataList(false).size());
        assertEquals(new Data((byte)0x30), resultBase.getDataList(true).get(0).getActualData());
    }

    @Test
    public void testGetDataList_Selector_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame2(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newSNAFrame1(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        List<ResultData> dataList;
        
        dataList = resultBase.getDataList(new ResultDataSelector(new EOJ("001101")), true);
        assertEquals(1, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        
        dataList = resultBase.getDataList(new ResultDataSelector(new EOJ("0ef001")), true);
        assertEquals(1, dataList.size());
        assertEquals(new Data(), dataList.get(0).getActualData());
        
        dataList = resultBase.getDataList(new ResultDataSelector(new EOJ("001101")), false);
        assertEquals(0, dataList.size());
        
        dataList = resultBase.getDataList(new ResultDataSelector(new EOJ("0ef001")), false);
        assertEquals(1, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
    }

    @Test
    public void testGetDataList_ResultFrame_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame1(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newSNAFrame1(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        
        assertTrue(resultBase.getDataList(resultFrame2).isEmpty());
        
        assertTrue(resultBase.addFrame(resultFrame2));
        
        assertTrue(resultBase.getDataList(new ResultFrame(newFrame1(), 10), true).isEmpty());
        assertTrue(resultBase.getDataList(new ResultFrame(newFrame1(), 10), false).isEmpty());
        assertTrue(resultBase.getDataList(new ResultFrame(newSNAFrame1(), 10), true).isEmpty());
        assertTrue(resultBase.getDataList(new ResultFrame(newSNAFrame1(), 10), false).isEmpty());
        
        List<ResultData> dataList;
        
        dataList = resultBase.getDataList(resultFrame1, true);
        assertEquals(1, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        
        dataList = resultBase.getDataList(resultFrame1, false);
        assertEquals(0, dataList.size());
        
        dataList = resultBase.getDataList(resultFrame2, true);
        assertEquals(1, dataList.size());
        assertEquals(new Data(), dataList.get(0).getActualData());
        
        dataList = resultBase.getDataList(resultFrame2, false);
        assertEquals(1, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
    }

    @Test
    public void testCountSecondData_boolean() {
        assertEquals(0, resultBase.countSecondData());
        
        assertTrue(resultBase.addFrame(newFrame1()));
        assertEquals(0, resultBase.countSecondData(true));
        assertEquals(0, resultBase.countSecondData(false));
        
        assertTrue(resultBase.addFrame(newSNAFrame2()));
        assertEquals(1, resultBase.countSecondData(true));
        assertEquals(1, resultBase.countSecondData(false));
    }

    @Test
    public void testGetSecondData_int_boolean() {
        assertTrue(resultBase.addFrame(newSNAFrame2()));
        assertEquals(new Data((byte)0x30), resultBase.getSecondData(0, true).getActualData());
        assertEquals(new Data(), resultBase.getSecondData(0, false).getActualData());
    }

    @Test
    public void testGetSecondDataList_boolean() {
        assertEquals(0, resultBase.getSecondDataList(true).size());
        assertEquals(0, resultBase.getSecondDataList(false).size());
        
        assertTrue(resultBase.addFrame(newSNAFrame2()));
        
        List<ResultData> dataList;
        
        dataList = resultBase.getSecondDataList(true);
        assertEquals(1, dataList.size());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        
        dataList = resultBase.getSecondDataList(false);
        assertEquals(1, dataList.size());
        assertEquals(new Data(), dataList.get(0).getActualData());
    }

    @Test
    public void testGetSecondDataList_Selector_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame4(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newSNAFrame2(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        List<ResultData> dataList;
        
        dataList = resultBase.getSecondDataList(new ResultDataSelector(EPC.x91), true);
        assertEquals(1, dataList.size());
        assertEquals(EPC.x91, dataList.get(0).getEPC());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        
        dataList = resultBase.getSecondDataList(new ResultDataSelector(EPC.xF1), true);
        assertTrue(dataList.isEmpty());
        
        dataList = resultBase.getSecondDataList(new ResultDataSelector(EPC.xFF), true);
        assertEquals(1, dataList.size());
        assertEquals(EPC.xFF, dataList.get(0).getEPC());
        assertEquals(new Data(), dataList.get(0).getActualData());
        
        dataList = resultBase.getSecondDataList(new ResultDataSelector(EPC.x91), false);
        assertTrue(dataList.isEmpty());
        
        dataList = resultBase.getSecondDataList(new ResultDataSelector(EPC.xF1), false);
        assertEquals(1, dataList.size());
        assertEquals(EPC.xF1, dataList.get(0).getEPC());
        assertEquals(new Data(), dataList.get(0).getActualData());
        
        dataList = resultBase.getSecondDataList(new ResultDataSelector(EPC.xFF), false);
        assertTrue(dataList.isEmpty());
    }

    @Test
    public void testGetSecondDataList_ResultFrame_boolean() {
        ResultFrame resultFrame1 = new ResultFrame(newFrame4(), 10);
        ResultFrame resultFrame2 = new ResultFrame(newSNAFrame2(), 10);
        
        assertTrue(resultBase.addFrame(resultFrame1));
        assertTrue(resultBase.addFrame(resultFrame2));
        
        List<ResultData> dataList;
        
        dataList = resultBase.getSecondDataList(resultFrame1, true);
        assertEquals(1, dataList.size());
        assertEquals(EPC.xFF, dataList.get(0).getEPC());
        assertEquals(new Data(), dataList.get(0).getActualData());
        
        dataList = resultBase.getSecondDataList(resultFrame2, true);
        assertEquals(1, dataList.size());
        assertEquals(EPC.x91, dataList.get(0).getEPC());
        assertEquals(new Data((byte)0x30), dataList.get(0).getActualData());
        
        dataList = resultBase.getSecondDataList(resultFrame1, false);
        assertTrue(dataList.isEmpty());
        
        dataList = resultBase.getSecondDataList(resultFrame2, false);
        assertEquals(1, dataList.size());
        assertEquals(EPC.xF1, dataList.get(0).getEPC());
        assertEquals(new Data(), dataList.get(0).getActualData());
    }

    public class ResultBaseImpl extends ResultBase<ResultBaseImpl> {
        
        public ResultBaseImpl() {
            super(ResultBaseImpl.class, new TimestampManager());
        }

        @Override
        public boolean isSuccessPayload(StandardPayload payload) {
            return payload.getESV() == ESV.Get_Res;
        }

        @Override
        public boolean isValidPayload(StandardPayload payload) {
            return !payload.getSEOJ().equals(new EOJ("0ef002"));
        }

        @Override
        public boolean isValidProperty(Property property) {
            if (property.getEPC() == EPC.x80 || property.getEPC() == EPC.xE0) {
                return property.getPDC() != 0;
            }
            
            if (property.getEPC() == EPC.x90 || property.getEPC() == EPC.xF0) {
                return property.getPDC() == 0;
            }
            
            return false;
        }
    
        @Override
        public boolean isValidSecondProperty(Property property) {
            if (property.getEPC() == EPC.xFF) {
                return property.getPDC() == 0;
            }
            
            if (property.getEPC() == EPC.x91 || property.getEPC() == EPC.xF1) {
                return property.getPDC() != 0;
            }
            
            return false;
        }
    }
    
    public class TestListener implements ResultListener<ResultBaseImpl> {
        int beginCount = 0;
        int finishCount = 0;
        
        public int getBeginCount() {
            return beginCount;
        }
        
        public int getFinishCount() {
            return finishCount;
        }

        @Override
        public void begin(ResultBaseImpl result) {
            beginCount++;
        }

        @Override
        public void send(ResultBaseImpl result, ResultFrame resultFrame, boolean success) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void send(ResultBaseImpl result, ResultFrame resultFrame, ResultData resultData, boolean success) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void send(ResultBaseImpl result, ResultFrame resultFrame, ResultData resultData, boolean success, boolean isSecond) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void receive(ResultBaseImpl result, ResultFrame resultFrame) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void receive(ResultBaseImpl result, ResultFrame resultFrame, ResultData resultData) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void receive(ResultBaseImpl result, ResultFrame resultFrame, ResultData resultData, boolean isSecond) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void finish(ResultBaseImpl result) {
            finishCount++;
        }
    }
        
    @Test
    public void testFinish() {
        TestListener listener = new TestListener();
        
        resultBase.setResultListener(listener);
        
        assertFalse(resultBase.isDone());
        assertEquals(0, listener.getBeginCount());
        assertEquals(0, listener.getFinishCount());
        
        resultBase.begin();
        
        assertFalse(resultBase.isDone());
        assertEquals(1, listener.getBeginCount());
        assertEquals(0, listener.getFinishCount());
        
        resultBase.finish();
        
        assertTrue(resultBase.isDone());
        assertEquals(1, listener.getBeginCount());
        assertEquals(1, listener.getFinishCount());
        
        resultBase.finish();
        
        assertTrue(resultBase.isDone());
        assertEquals(1, listener.getBeginCount());
        assertEquals(1, listener.getFinishCount());
    }
}
