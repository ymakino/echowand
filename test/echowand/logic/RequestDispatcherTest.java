package echowand.logic;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.SimplePayload;
import echowand.net.Node;
import echowand.net.InternalSubnet;
import echowand.net.SubnetException;
import echowand.net.Subnet;
import echowand.logic.RequestProcessor;
import echowand.logic.TooManyObjectsException;
import echowand.common.Data;
import echowand.common.ESV;
import echowand.logic.RequestDispatcher;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.object.SetGetRequestProcessor;
import echowand.info.DeviceObjectInfo;
import echowand.object.LocalObjectManager;
import echowand.object.LocalObject;
import echowand.info.TemperatureSensorInfo;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class RequestDispatcherTest {
    
    public Frame receiveWithoutError(InternalSubnet subnet) {
        try {
            return subnet.receiveNoWait();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
            return null;
        }
    }
    
    public void doTestSetGetRequest(ESV reqESV, ESV resESV, EPC epc1, Data data1, EPC epc2) {
        doTestSetGetRequest(reqESV, resESV, epc1, data1, epc2, true);
    }
    
    public void doTestSetGetRequest(ESV reqESV, ESV resESV, EPC epc1, Data data1, EPC epc2, boolean useValidEOJ) {
        LocalObjectManager manager = new LocalObjectManager();
        DeviceObjectInfo objectInfo = new TemperatureSensorInfo();
        objectInfo.add(EPC.x80, true, true, false, 1);
        LocalObject object = new LocalObject(objectInfo);
        try {
            manager.add(object);
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
        SetGetRequestProcessor processor = new SetGetRequestProcessor(manager);
        InternalSubnet subnet = new InternalSubnet();
        Node local = subnet.getLocalNode();
        Node group = subnet.getGroupNode();
        CommonFrame frame = new CommonFrame();
        StandardPayload payload = new StandardPayload();
        payload.setSEOJ(new EOJ("002201"));
        if (useValidEOJ) {
            payload.setDEOJ(new EOJ("001101"));
        } else {
            payload.setDEOJ(new EOJ("001201"));
        }
        payload.setESV(reqESV);
        if (epc1 != null) {
            if (data1 != null) {
                payload.addFirstProperty(new Property(epc1, data1));
            } else {
                payload.addFirstProperty(new Property(epc1));
            }
        }
        if (epc2 != null) {
            payload.addSecondProperty(new Property(epc2));
        }
        frame.setEDATA(payload);
        frame.setTID((short)1234);
        RequestDispatcher dispatcher = new RequestDispatcher();
        dispatcher.addRequestProcessor(processor);
        if (useValidEOJ) {
            assertTrue(dispatcher.process(subnet, new Frame(local, group, frame), false));
        } else {
            assertFalse(dispatcher.process(subnet, new Frame(local, group, frame), false));
        }
        Frame res = receiveWithoutError(subnet);
        
        if (useValidEOJ) {
            assertTrue(res != null);
            StandardPayload resp = res.getCommonFrame().getEDATA(StandardPayload.class);
            assertEquals(resESV, resp.getESV());
            assertEquals((short)1234, res.getCommonFrame().getTID());
        } else {
            assertTrue(res == null);
        }
    }
    
    @Test
    public void testGetRequest() {
        doTestSetGetRequest(ESV.Get, ESV.Get_Res, EPC.x80, null, null);
    }
    
    @Test
    public void testSetCRequest() {
        doTestSetGetRequest(ESV.SetC, ESV.Set_Res, EPC.x80, new Data((byte)0x42), null);
    }
    
    @Test
    public void testSetIRequest() {
        doTestSetGetRequest(ESV.SetI, null, EPC.x80, new Data((byte)0x42), null, false);
    }
    
    @Test
    public void testSetGetRequest() {
        doTestSetGetRequest(ESV.SetGet, ESV.SetGet_Res, EPC.x80, new Data((byte)0x42), EPC.xE0);
    }
    
    @Test
    public void testInvalidGetRequest() {
        doTestSetGetRequest(ESV.Get, ESV.Get_SNA, EPC.xE1, null, null);
    }
    
    @Test
    public void testInvalidSetCRequest() {
        doTestSetGetRequest(ESV.SetC, ESV.SetC_SNA, EPC.xE1, new Data((byte)0x42), null);
    }
    
    @Test
    public void testInvalidSetIRequest() {
        // doTestSetGetRequest(ESV.SetI, ESV.SetI_SNA, EPC.x80, new Data((byte)0x42), null);
        doTestSetGetRequest(ESV.SetI, ESV.SetI_SNA, EPC.x80, new Data(new byte[]{0x42, 0x11}), null);
    }
    
    @Test
    public void testInvalidSetGetRequest() {
        doTestSetGetRequest(ESV.SetGet, ESV.SetGet_SNA, EPC.x83, new Data((byte)0x42), EPC.xE0);
        doTestSetGetRequest(ESV.SetGet, ESV.SetGet_SNA, EPC.x80, new Data((byte)0x42), EPC.xE1);
    }
    
    
    @Test
    public void testUnknownEOJGetRequest() {
        doTestSetGetRequest(ESV.Get, null, EPC.x80, null, null, false);
    }
    
    @Test
    public void testUnknownEOJSetCRequest() {
        doTestSetGetRequest(ESV.SetC, null, EPC.x80, new Data((byte)0x42), null, false);
    }
    
    @Test
    public void testUnknownEOJSetIRequest() {
        doTestSetGetRequest(ESV.SetI, null, EPC.x80, new Data((byte)0x42), null, false);
    }
    
    @Test
    public void testUnknownEOJSetGetRequest() {
        doTestSetGetRequest(ESV.SetGet, null, EPC.x80, new Data((byte)0x42), EPC.xE0, false);
    }
    
    @Test
    public void testNotStandardPayload() {
        InternalSubnet subnet = new InternalSubnet();
        CommonFrame commonFrame = new CommonFrame(new EOJ("001101"), new EOJ("001101"), ESV.Invalid);
        
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), commonFrame);
        RequestDispatcher dispatcher = new RequestDispatcher();
        dispatcher.addRequestProcessor(new ReturnTrueRequestProcessor());
        assertFalse(dispatcher.process(subnet, frame, false));
        
        
        commonFrame.setEDATA(new SimplePayload());
        assertFalse(dispatcher.process(subnet, frame, false));
    }
}

class ReturnTrueRequestProcessor implements RequestProcessor {
    @Override
    public boolean processSetI(Subnet subnet, Frame frame, boolean processed){ return true; }
    @Override
    public boolean processSetC(Subnet subnet, Frame frame, boolean processed){ return true; }
    @Override
    public boolean processGet(Subnet subnet, Frame frame, boolean processed){ return true; }
    @Override
    public boolean processSetGet(Subnet subnet, Frame frame, boolean processed){ return true; }
    @Override
    public boolean processINF_REQ(Subnet subnet, Frame frame, boolean processed){ return true; }
    @Override
    public boolean processINF(Subnet subnet, Frame frame, boolean processed){ return true; }
    @Override
    public boolean processINFC(Subnet subnet, Frame frame, boolean processed){ return true; }
}
