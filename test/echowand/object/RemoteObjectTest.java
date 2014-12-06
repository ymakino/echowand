package echowand.object;

import echowand.object.RemoteObject;
import echowand.object.RemoteObjectObserver;
import echowand.object.EchonetObjectException;
import echowand.object.ObjectData;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.InternalSubnet;
import echowand.net.SubnetException;
import echowand.net.Subnet;
import echowand.common.EOJ;
import echowand.common.ESV;
import echowand.common.EPC;
import echowand.common.PropertyMap;
import echowand.common.Data;
import echowand.logic.TransactionManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class RemoteObjectTest {
    
    private Data data = new Data((byte)0x41);
    
    public Frame createGetFrame(Subnet subnet, Frame reqFrame) {
        short tid = reqFrame.getCommonFrame().getTID();
        CommonFrame cf = new CommonFrame(new EOJ("001101"), new EOJ("0EF001"), ESV.Get_Res);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        EPC epc = ((StandardPayload)reqFrame.getCommonFrame().getEDATA()).getFirstPropertyAt(0).getEPC();
        if (epc == EPC.x80) {
            payload.addFirstProperty(new Property(EPC.x80, data));
        } else if (epc == EPC.x80) {
            payload.addFirstProperty(new Property(EPC.xE0, new Data((byte)0x12, (byte)0x34)));
        } else if (epc == EPC.x9D) {
            PropertyMap pmap = new PropertyMap();
            pmap.set(EPC.x80);
            payload.addFirstProperty(new Property(EPC.x9D, new Data(pmap.toBytes())));
        } else if (epc == EPC.x9E) {
            PropertyMap pmap = new PropertyMap();
            pmap.set(EPC.x80);
            payload.addFirstProperty(new Property(EPC.x9E, new Data(pmap.toBytes())));
        } else if (epc == EPC.x9F) {
            PropertyMap pmap = new PropertyMap();
            pmap.set(EPC.x80);
            pmap.set(EPC.x9D);
            pmap.set(EPC.x9E);
            pmap.set(EPC.x9F);
            pmap.set(EPC.xE0);
            payload.addFirstProperty(new Property(EPC.x9F, new Data(pmap.toBytes())));
        } else if (epc == EPC.xE0) {
            Data bigData = new Data(new byte[253]);
            payload.addFirstProperty(new Property(EPC.xE0, bigData));
        }
        cf.setTID(tid);
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame createGetFailFrame(Subnet subnet, Frame reqFrame) {
        short tid = reqFrame.getCommonFrame().getTID();
        StandardPayload reqPayload = (StandardPayload)reqFrame.getCommonFrame().getEDATA();
        CommonFrame cf = new CommonFrame(new EOJ("001101"), new EOJ("0EF001"), ESV.Get_SNA);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        EPC epc = ((StandardPayload)reqFrame.getCommonFrame().getEDATA()).getFirstPropertyAt(0).getEPC();
        payload.addFirstProperty(reqPayload.getFirstPropertyAt(0));
        cf.setTID(tid);
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame createSetFrame(Subnet subnet, Frame reqFrame) {
        short tid = reqFrame.getCommonFrame().getTID();
        StandardPayload reqPayload = (StandardPayload)reqFrame.getCommonFrame().getEDATA();
        data = reqPayload.getFirstPropertyAt(0).getEDT();
        CommonFrame cf = new CommonFrame(new EOJ("001101"), new EOJ("0EF001"), ESV.Set_Res);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80));
        cf.setTID(tid);
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame createSetFailFrame(Subnet subnet, Frame reqFrame) {
        short tid = reqFrame.getCommonFrame().getTID();
        StandardPayload reqPayload = (StandardPayload)reqFrame.getCommonFrame().getEDATA();
        data = reqPayload.getFirstPropertyAt(0).getEDT();
        CommonFrame cf = new CommonFrame(new EOJ("001101"), new EOJ("0EF001"), ESV.SetC_SNA);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(reqPayload.getFirstPropertyAt(0));
        cf.setTID(tid);
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    public Frame createAnnoFrame(Subnet subnet, Frame reqFrame) {
        short tid = reqFrame.getCommonFrame().getTID();
        StandardPayload reqPayload = (StandardPayload)reqFrame.getCommonFrame().getEDATA();
        data = reqPayload.getFirstPropertyAt(0).getEDT();
        CommonFrame cf = new CommonFrame(new EOJ("001101"), new EOJ("0EF001"), ESV.INF);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80));
        cf.setTID(tid);
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        return frame;
    }
    
    class ResponseThread extends Thread {
        private Subnet subnet;
        private TransactionManager transactionManager;
        private boolean doLoop;
        
        public ResponseThread(Subnet subnet, TransactionManager transactionManager) {
            this(subnet, transactionManager, false);
        }
        
        public ResponseThread(Subnet subnet, TransactionManager transactionManager, boolean doLoop) {
            this.subnet = subnet;
            this.transactionManager = transactionManager;
            this.doLoop = doLoop;
        }
        
        @Override
        public void run() {
            try {
                do {
                    Frame frame = subnet.receive();
                    StandardPayload payload = (StandardPayload) frame.getCommonFrame().getEDATA();
                    switch (payload.getESV()) {
                        case Get:
                            if (payload.getFirstPropertyAt(0).getEPC() == EPC.xE1) {
                                transactionManager.process(subnet, createGetFailFrame(subnet, frame), false);
                            } else {
                                transactionManager.process(subnet, createGetFrame(subnet, frame), false);
                            }
                            break;
                        case SetC:
                            if (payload.getFirstPropertyAt(0).getEPC() == EPC.x80) {
                                transactionManager.process(subnet, createSetFrame(subnet, frame), false);
                            } else if (payload.getFirstPropertyAt(0).getEPC() == EPC.xE0) {
                                transactionManager.process(subnet, createSetFailFrame(subnet, frame), false);
                            }
                            break;
                    }
                } while (doLoop);
            } catch (SubnetException e) {
                e.printStackTrace();
                fail();
            }
        }
    }
    
    @Test
    public void testGet() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObject object = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        assertEquals(subnet, object.getSubnet());
        assertEquals(transactionManager, object.getListener());
        assertEquals(subnet.getLocalNode(), object.getNode());
        assertEquals(new EOJ("001101"), object.getEOJ());
        try {
            new ResponseThread(subnet, transactionManager).start();
            ObjectData data = object.getData(EPC.x80);
            assertEquals(new ObjectData((byte) 0x41), data);
            
            new ResponseThread(subnet, transactionManager).start();
            ObjectData bigData = object.getData(EPC.xE0);
            assertEquals(new ObjectData(new byte[253]), bigData);
        } catch (EchonetObjectException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test(expected=EchonetObjectException.class)
    public void testGetFail() throws EchonetObjectException {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObject object = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        assertEquals(subnet, object.getSubnet());
        assertEquals(transactionManager, object.getListener());
        assertEquals(subnet.getLocalNode(), object.getNode());
        assertEquals(new EOJ("001101"), object.getEOJ());

        new ResponseThread(subnet, transactionManager).start();
        ObjectData bigData = object.getData(EPC.xE1);
    }

    @Test
    public void testSet() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObject object = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        assertEquals(subnet, object.getSubnet());
        assertEquals(subnet.getLocalNode(), object.getNode());
        assertEquals(new EOJ("001101"), object.getEOJ());
        try {
            new ResponseThread(subnet, transactionManager).start();
            assertTrue(object.setData(EPC.x80, new ObjectData((byte) 0x42)));
        } catch (EchonetObjectException e) {
            e.printStackTrace();
            fail();
        }
        try {
            new ResponseThread(subnet, transactionManager).start();
            ObjectData data = object.getData(EPC.x80);
            assertEquals(new ObjectData((byte) 0x42), data);
        } catch (EchonetObjectException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testInvalidSet() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObject object = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);

        try {
            new ResponseThread(subnet, transactionManager).start();
            assertTrue(object.setData(EPC.x80, new ObjectData((byte) 0x42)));
            new ResponseThread(subnet, transactionManager).start();
            assertFalse(object.setData(EPC.xE0, new ObjectData((byte) 0x42)));
            object.setTimeout(500);
            assertFalse(object.setData(EPC.xE1, new ObjectData((byte) 0x42)));
        } catch (EchonetObjectException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void setTimeout() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObject object = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        assertTrue(object.setTimeout(1));
        assertTrue(object.setTimeout(60000));
        assertFalse(object.setTimeout(0));
        assertFalse(object.setTimeout(-100));
    }
    
    private class DummyRemoteObjectObserver implements RemoteObjectObserver {
        public EPC epc;
        public ObjectData data;
        
        @Override
        public void notifyData(RemoteObject object, EPC epc, ObjectData data) {
            this.epc = epc;
            this.data = data;
        }
    }
    
    @Test
    public void testAddAndRemoveObserver() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObject object = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        RemoteObjectObserver observer1 = new DummyRemoteObjectObserver();
        RemoteObjectObserver observer2 = new DummyRemoteObjectObserver();
        object.addObserver(observer1);
        assertEquals(1, object.countObservers());
        object.addObserver(observer2);
        assertEquals(2, object.countObservers());
        object.removeObserver(observer1);
        assertEquals(1, object.countObservers());
        object.removeObserver(observer2);
        assertEquals(0, object.countObservers());
    }
    
    @Test
    public void testAnno() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObject object = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        DummyRemoteObjectObserver observer = new DummyRemoteObjectObserver();
        object.addObserver(observer);
        object.notifyData(EPC.x80, new ObjectData((byte)0x41));
        
        assertEquals(EPC.x80, observer.epc);
        assertEquals(new ObjectData((byte)0x41), observer.data);
    }
    
    @Test
    public void testObserveData() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObject object = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        
        try {
            object.observeData(EPC.x80);
            Frame frame = subnet.receiveNoWait();
            CommonFrame commonFrame = frame.getCommonFrame();
            StandardPayload payload = (StandardPayload) commonFrame.getEDATA();
            assertEquals(ESV.INF_REQ, payload.getESV());
            assertEquals(EPC.x80, payload.getFirstPropertyAt(0).getEPC());
            assertEquals((byte)0, payload.getFirstPropertyAt(0).getPDC());
        } catch (EchonetObjectException e) {
            e.printStackTrace();
            fail();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testAccessPermission() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObject object = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        try {
            new ResponseThread(subnet, transactionManager).start();
            assertTrue(object.isGettable(EPC.x80));
            new ResponseThread(subnet, transactionManager).start();
            assertTrue(object.isGettable(EPC.xE0));
            new ResponseThread(subnet, transactionManager).start();
            assertFalse(object.isSettable(EPC.xE0));
            new ResponseThread(subnet, transactionManager).start();
            assertFalse(object.isObservable(EPC.xE0));
            new ResponseThread(subnet, transactionManager, true).start();
            assertTrue(object.contains(EPC.x80));
            assertTrue(object.contains(EPC.x9D));
            assertTrue(object.contains(EPC.x9E));
            assertTrue(object.contains(EPC.x9F));
            assertTrue(object.contains(EPC.xE0));
            assertFalse(object.contains(EPC.x81));
            assertFalse(object.contains(EPC.xF0));
        } catch (EchonetObjectException e) {
            e.printStackTrace();
            fail();
        }
    }
}
