package echowand.object;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.LocalSubnet;
import echowand.net.SubnetException;
import echowand.common.Data;
import echowand.common.ESV;
import echowand.common.EPC;
import echowand.common.EOJ;
import echowand.object.AnnounceRequestProcessor;
import echowand.object.LocalObjectManager;
import echowand.object.RemoteObjectManager;
import echowand.object.LocalObject;
import echowand.info.NodeProfileInfo;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.TooManyObjectsException;
import echowand.logic.TransactionManager;
import echowand.object.RemoteObject;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class AnnounceRequestProcessorTest {
    
    public Frame processFrame(LocalSubnet subnet, AnnounceRequestProcessor listener, EOJ seoj, EOJ deoj, ESV esv) {
        CommonFrame cf = new CommonFrame(seoj, deoj, esv);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.x80, new Data((byte)0x42)));
        
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        switch (esv) {
            case INF: listener.processINF(subnet, frame, false); break;
            case INFC: listener.processINFC(subnet, frame, false); break;
            default: fail();
        }
        try {
            return subnet.recvNoWait();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
            return null;
        }
    }
    
    @Test
    public void testProcessFrameToInvalidEOJ() {
        LocalSubnet subnet = new LocalSubnet();
        LocalObjectManager localManager = new LocalObjectManager();
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        AnnounceRequestProcessor listener = new AnnounceRequestProcessor(localManager, remoteManager);
        
        Frame frame;
        frame = processFrame(subnet, listener, new EOJ("001101"), new EOJ("0ef001"), ESV.INF);
        assertEquals(null, frame);
        
        frame = processFrame(subnet, listener, new EOJ("001101"), new EOJ("0ef001"), ESV.INFC);
        assertEquals(null, frame);
    }
    
    @Test
    public void testProcessFrame() {
        LocalSubnet subnet = new LocalSubnet();
        LocalObjectManager localManager = new LocalObjectManager();
        try {
            localManager.add(new LocalObject(new NodeProfileInfo()));
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        TransactionManager transactionManager = new TransactionManager(subnet);
        remoteManager.add(new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager));
        AnnounceRequestProcessor listener = new AnnounceRequestProcessor(localManager, remoteManager);

        Frame frame;
        frame = processFrame(subnet, listener, new EOJ("001101"), new EOJ("0ef001"), ESV.INF);
        assertEquals(null, frame);
        
        frame = processFrame(subnet, listener, new EOJ("001101"), new EOJ("0ef001"), ESV.INFC);
        assertTrue(frame != null);
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        assertEquals(new EOJ("0ef001"), payload.getSEOJ());
        assertEquals(new EOJ("001101"), payload.getDEOJ());
        assertEquals(ESV.INFC_Res, payload.getESV());
        assertEquals(1, payload.getFirstOPC());
        assertEquals(EPC.x80, payload.getFirstPropertyAt(0).getEPC());
        assertEquals(0, payload.getFirstPropertyAt(0).getPDC());
        
        frame = processFrame(subnet, listener, new EOJ("001101"), new EOJ("0ef002"), ESV.INFC);
        assertEquals(null, frame);
    }
    
    @Test
    public void testProcessFrameToMulti() {
        LocalSubnet subnet = new LocalSubnet();
        LocalObjectManager localManager = new LocalObjectManager();
        TemperatureSensorInfo info = new TemperatureSensorInfo();
        try {
            localManager.add(new LocalObject(info));
            localManager.add(new LocalObject(info));
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        AnnounceRequestProcessor listener = new AnnounceRequestProcessor(localManager, remoteManager);

        Frame frame;
        frame = processFrame(subnet, listener, new EOJ("001101"), new EOJ("001100"), ESV.INF);
        assertTrue(frame == null);
        
        frame = processFrame(subnet, listener, new EOJ("001101"), new EOJ("001101"), ESV.INFC);
        assertTrue(frame != null);
        try {
            frame = subnet.recvNoWait();
            assertTrue(frame == null);
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        frame = processFrame(subnet, listener, new EOJ("001101"), new EOJ("001100"), ESV.INFC);
        assertTrue(frame != null);
        try {
            frame = subnet.recvNoWait();
            assertTrue(frame != null);
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
    }
}
