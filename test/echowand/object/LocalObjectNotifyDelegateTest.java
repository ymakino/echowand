package echowand.object;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.InternalSubnet;
import echowand.net.SubnetException;
import echowand.common.ESV;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TransactionManager;
import echowand.info.DeviceObjectInfo;
import echowand.info.ObjectInfo;
import echowand.info.TemperatureSensorInfo;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class LocalObjectNotifyDelegateTest {
    
    @Test
    public void testAnnounce() throws SubnetException {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        LocalObjectNotifyDelegate delegate = new LocalObjectNotifyDelegate(subnet, transactionManager);
        DeviceObjectInfo objectInfo = new TemperatureSensorInfo();
        objectInfo.add(EPC.x80, true, true, true, new byte[]{(byte)0x42});
        LocalObject object = new LocalObject(objectInfo);
        
        LocalObjectDelegate.NotifyState result = new LocalObjectDelegate.NotifyState();
        delegate.notifyDataChanged(result, object, EPC.x80, new ObjectData((byte)0x41), new ObjectData((byte)0x40));
        assertTrue(result.isDone());
        assertFalse(result.isFail());
        
        Frame frame = subnet.recvNoWait();
        assertTrue(frame != null);
        CommonFrame cf = frame.getCommonFrame();
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        assertEquals(ESV.INF, payload.getESV());
        assertEquals(subnet.getLocalNode(), frame.getSender());
        assertEquals(subnet.getGroupNode(), frame.getReceiver());
        assertEquals(new EOJ("001101"), payload.getSEOJ());
        assertEquals(new EOJ("0ef001"), payload.getDEOJ());
        assertEquals(1, payload.getFirstOPC());
        Property property = payload.getFirstPropertyAt(0);
        assertEquals(EPC.x80, property.getEPC());
        assertEquals(1, property.getPDC());
        assertEquals(1, property.getEDT().size());
        assertEquals((byte)0x41, property.getEDT().get(0));
    }
    
    @Test
    public void testNoAnnounceWithoutChanges() throws SubnetException {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        LocalObjectNotifyDelegate delegate = new LocalObjectNotifyDelegate(subnet, transactionManager);
        DeviceObjectInfo objectInfo = new TemperatureSensorInfo();
        objectInfo.add(EPC.x80, true, true, true, new byte[]{(byte)0x42});
        LocalObject object = new LocalObject(objectInfo);
        
        LocalObjectDelegate.NotifyState result = new LocalObjectDelegate.NotifyState();
        delegate.notifyDataChanged(result, object, EPC.x80, new ObjectData((byte)0x41), new ObjectData((byte)0x41));
        assertTrue(result.isDone());
        assertFalse(result.isFail());
        
        Frame frame = subnet.recvNoWait();
        assert(frame == null);
    }
    
    @Test
    public void testNotAnnounce() throws SubnetException {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        LocalObjectNotifyDelegate delegate = new LocalObjectNotifyDelegate(subnet, transactionManager);
        DeviceObjectInfo objectInfo = new TemperatureSensorInfo();
        objectInfo.add(EPC.x80, true, true, false, new byte[]{(byte)0x42});
        LocalObject object = new LocalObject(objectInfo);
        
        LocalObjectDelegate.NotifyState result = new LocalObjectDelegate.NotifyState();
        delegate.notifyDataChanged(result, object, EPC.x80, new ObjectData((byte)0x41), new ObjectData((byte)0x41));
        assertTrue(result.isDone());
        assertFalse(result.isFail());
        
        Frame frame = subnet.recvNoWait();
        assertTrue(frame == null);
    }
}
