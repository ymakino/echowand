package echowand.object;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.InternalSubnet;
import echowand.net.SubnetException;
import echowand.net.Subnet;
import echowand.common.Data;
import echowand.common.ESV;
import echowand.common.EPC;
import echowand.common.EOJ;
import echowand.object.SetGetRequestProcessor;
import echowand.info.BaseObjectInfo;
import echowand.object.LocalObjectManager;
import echowand.object.ObjectData;
import echowand.object.LocalObject;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.RequestDispatcher;
import echowand.logic.TooManyObjectsException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class SetGetRequestProcessorTest {
    public LocalObjectManager manager;
    public InternalSubnet subnet;
    public SetGetRequestProcessor processor;
    public LocalObject object;
    
    @Before
    public void setUp() {
        manager = new LocalObjectManager();
        subnet = new InternalSubnet();
        processor = new SetGetRequestProcessor(manager);
        
        BaseObjectInfo objectInfo = new TemperatureSensorInfo();
        objectInfo.add(EPC.x80, true, true, true, 1);
        objectInfo.add(EPC.xE0, true, true, false, 1);
        object = new LocalObject(objectInfo);
        object.forceSetData(EPC.x80, new ObjectData((byte) 0x41));
        object.forceSetData(EPC.xE0, new ObjectData((byte) 0x12, (byte) 0x34));
        try {
            manager.add(object);
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testCreation() {
        LocalObjectManager manager = new LocalObjectManager();
        SetGetRequestProcessor processor = new SetGetRequestProcessor(manager);
    }
    
    @Test
    public void testAddRequestProcessor() {
        LocalObjectManager manager = new LocalObjectManager();
        SetGetRequestProcessor processor = new SetGetRequestProcessor(manager);
        RequestDispatcher listener = new RequestDispatcher();
        listener.addRequestProcessor(processor);
        listener.removeRequestProcessor(processor);
    }
    
    public Frame createFrameSetI(Subnet subnet) {
        CommonFrame cf = new CommonFrame(new EOJ("002201"), new EOJ("001101"), ESV.SetI);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x11)));
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame createFrameSetC(Subnet subnet) {
        CommonFrame cf = new CommonFrame(new EOJ("002201"), new EOJ("001101"), ESV.SetC);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x42)));
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame createFrameGet(Subnet subnet) {
        CommonFrame cf = new CommonFrame(new EOJ("0EF001"), new EOJ("001101"), ESV.Get);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80));
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame createFrameSetGet(Subnet subnet) {
        CommonFrame cf = new CommonFrame(new EOJ("002201"), new EOJ("001101"), ESV.SetGet);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x41)));
        payload.addSecondProperty(new Property(EPC.xE0));
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame createFrameINF_REQ1(Subnet subnet) {
        CommonFrame cf = new CommonFrame(new EOJ("002201"), new EOJ("001101"), ESV.INF_REQ);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80));
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame createFrameINF_REQ2(Subnet subnet) {
        CommonFrame cf = new CommonFrame(new EOJ("002201"), new EOJ("001101"), ESV.INF_REQ);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.xE0));
        payload.addFirstProperty(new Property(EPC.x80));
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame createFrameInvalidEOJSetGet(Subnet subnet) {
        CommonFrame cf = new CommonFrame(new EOJ("002201"), new EOJ("123456"), ESV.SetGet);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x41)));
        payload.addSecondProperty(new Property(EPC.xE0));
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame recvWithoutError(InternalSubnet subnet) {
        try {
            Frame frame = subnet.recvNoWait();
            if (frame == null) {
                fail();
            }
            return frame;
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
            return null;
        }
    }
    
    @Test
    public void testProcessSetI() {
        processor.processSetI(subnet, createFrameSetI(subnet), false);
        assertEquals(1, object.getData(EPC.x80).size());
        assertEquals((byte)0x11, object.getData(EPC.x80).get(0));
    }
     
    @Test
    public void testProcessSetC() {
        processor.processSetC(subnet, createFrameSetC(subnet), false);
        assertEquals(1, object.getData(EPC.x80).size());
        assertEquals((byte)0x42, object.getData(EPC.x80).get(0));
        Frame frame = recvWithoutError(subnet);
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        assertEquals(ESV.Set_Res, payload.getESV());
        assertEquals(1, payload.getFirstOPC());
        assertEquals(EPC.x80, payload.getFirstPropertyAt(0).getEPC());
        assertEquals((byte)0x00, payload.getFirstPropertyAt(0).getPDC());
    }
    
    @Test
    public void testProcessGet() {
        processor.processGet(subnet, createFrameGet(subnet), false);
        Frame frame = recvWithoutError(subnet);
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        assertEquals(ESV.Get_Res, payload.getESV());
        assertEquals(1, payload.getFirstOPC());
        assertEquals(EPC.x80, payload.getFirstPropertyAt(0).getEPC());
        assertEquals((byte)0x01, payload.getFirstPropertyAt(0).getPDC());
        assertEquals((byte)0x41, payload.getFirstPropertyAt(0).getEDT().get(0));
    }
    
    @Test
    public void testProcessSetGet() {
        processor.processSetGet(subnet, createFrameSetGet(subnet), false);
        Frame frame = recvWithoutError(subnet);
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        assertEquals(ESV.SetGet_Res, payload.getESV());
        assertEquals(1, payload.getFirstOPC());
        assertEquals(EPC.x80, payload.getFirstPropertyAt(0).getEPC());
        assertEquals((byte)0x00, payload.getFirstPropertyAt(0).getPDC());
        assertEquals(1, payload.getSecondOPC());
        assertEquals(EPC.xE0, payload.getSecondPropertyAt(0).getEPC());
        assertEquals((byte)0x02, payload.getSecondPropertyAt(0).getPDC());
        assertEquals((byte)0x12, payload.getSecondPropertyAt(0).getEDT().get(0));
        assertEquals((byte)0x34, payload.getSecondPropertyAt(0).getEDT().get(1));
    }
    
    @Test
    public void testProcessINF_REQ() {
        processor.processINF_REQ(subnet, createFrameINF_REQ1(subnet), false);
        Frame frame = recvWithoutError(subnet);
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        assertEquals(ESV.INF, payload.getESV());
        assertEquals(1, payload.getFirstOPC());
        
        assertEquals(EPC.x80, payload.getFirstPropertyAt(0).getEPC());
        assertEquals((byte)0x01, payload.getFirstPropertyAt(0).getPDC());
        assertEquals((byte)0x41, payload.getFirstPropertyAt(0).getEDT().get(0));
    }
    
    @Test
    public void testProcessINF_REQ_Fail() {
        processor.processINF_REQ(subnet, createFrameINF_REQ2(subnet), false);
        Frame frame = recvWithoutError(subnet);
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        assertEquals(ESV.INF_SNA, payload.getESV());
        assertEquals(2, payload.getFirstOPC());
        assertEquals(EPC.xE0, payload.getFirstPropertyAt(0).getEPC());
        assertEquals((byte)0x00, payload.getFirstPropertyAt(0).getPDC());
        
        assertEquals(EPC.x80, payload.getFirstPropertyAt(1).getEPC());
        assertEquals((byte)0x01, payload.getFirstPropertyAt(1).getPDC());
        assertEquals((byte)0x41, payload.getFirstPropertyAt(1).getEDT().get(0));
    }
    
    @Test
    public void testProcessInvalidEOJSetGet() {
        processor.processSetGet(subnet, createFrameInvalidEOJSetGet(subnet), false);
        processor.processSetGet(subnet, createFrameSetGet(subnet), false);
        Frame frame = recvWithoutError(subnet);
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        assertEquals(ESV.SetGet_Res, payload.getESV());
        assertEquals(1, payload.getFirstOPC());
        assertEquals(EPC.x80, payload.getFirstPropertyAt(0).getEPC());
        assertEquals((byte)0x00, payload.getFirstPropertyAt(0).getPDC());
        assertEquals(1, payload.getSecondOPC());
        assertEquals(EPC.xE0, payload.getSecondPropertyAt(0).getEPC());
        assertEquals((byte)0x02, payload.getSecondPropertyAt(0).getPDC());
        assertEquals((byte)0x12, payload.getSecondPropertyAt(0).getEDT().get(0));
        assertEquals((byte)0x34, payload.getSecondPropertyAt(0).getEDT().get(1));
    }
    
    @Test
    public void testProcessGetMulti() {
        try {
            manager.add(new LocalObject(new TemperatureSensorInfo()));
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }

        Frame reqFrame = createFrameGet(subnet);
        StandardPayload reqPayload = (StandardPayload)reqFrame.getCommonFrame().getEDATA();
        EOJ newDEOJ = reqPayload.getDEOJ().getEOJWithInstanceCode((byte)0x00);
        reqPayload.setDEOJ(newDEOJ);
        processor.processGet(subnet, reqFrame, false);
        Frame frame = recvWithoutError(subnet);
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        assertEquals(ESV.Get_Res, payload.getESV());
        assertEquals(new EOJ("001101"), payload.getSEOJ());
        assertEquals(1, payload.getFirstOPC());
        assertEquals(EPC.x80, payload.getFirstPropertyAt(0).getEPC());
        assertEquals((byte)0x01, payload.getFirstPropertyAt(0).getPDC());
        assertEquals((byte)0x41, payload.getFirstPropertyAt(0).getEDT().get(0));
        
        frame = recvWithoutError(subnet);
        payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        assertEquals(ESV.Get_Res, payload.getESV());
        assertEquals(new EOJ("001102"), payload.getSEOJ());
    }
}
