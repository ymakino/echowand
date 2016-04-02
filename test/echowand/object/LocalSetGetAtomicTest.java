package echowand.object;

import echowand.common.Data;
import echowand.common.EPC;
import echowand.info.DeviceObjectInfo;
import echowand.net.Property;
import echowand.info.TemperatureSensorInfo;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class LocalSetGetAtomicTest {
    
    public TemperatureSensorInfo getWritableTemperatureSensorInfo() {
        TemperatureSensorInfo t = new TemperatureSensorInfo();
        t.add(EPC.x80, true, true, true, 1);
        t.add(EPC.x81, true, true, false, 2);
        t.add(EPC.xE0, true, true, false, 2);
        return t;
    }
    
    public TemperatureSensorInfo getTemperatureSensorInfoWitnNoLocationInfo() {
        TemperatureSensorInfo t = new TemperatureSensorInfo();
        t.add(EPC.x81, false, false, false, 2);
        return t;
    }
    
    @Test
    public void testSet() {
        LocalObject object = new LocalObject(getWritableTemperatureSensorInfo());
        LocalSetGetAtomic localSetAtomic = new LocalSetGetAtomic(object);
        localSetAtomic.addSet(new Property(EPC.x80, new Data((byte)0x41)));
        localSetAtomic.addSet(new Property(EPC.xE0, new Data((byte)0x12, (byte)0x34)));
        assertFalse(localSetAtomic.isDone());
        localSetAtomic.run();
        assertTrue(localSetAtomic.isDone());
        List<Property> result = localSetAtomic.getSetResult();
        assertEquals(2, result.size());
        assertEquals(EPC.x80, result.get(0).getEPC());
        assertEquals(0, result.get(0).getPDC());
        assertEquals(EPC.xE0, result.get(1).getEPC());
        assertEquals(0, result.get(1).getPDC());
        assertTrue(localSetAtomic.isSuccess());
    }
    
    @Test
    public void testGet() {
        LocalObject object = new LocalObject(new TemperatureSensorInfo());
        object.setData(EPC.x80, new ObjectData((byte)0x41));
        object.setData(EPC.xE0, new ObjectData((byte)0x12, (byte)0x34));
        LocalSetGetAtomic localGetAtomic = new LocalSetGetAtomic(object);
        localGetAtomic.addGet(new Property(EPC.x80));
        localGetAtomic.addGet(new Property(EPC.xE0));
        assertFalse(localGetAtomic.isDone());
        localGetAtomic.run();
        assertTrue(localGetAtomic.isDone());
        List<Property> result = localGetAtomic.getGetResult();
        assertEquals(2, result.size());
        assertEquals(EPC.x80, result.get(0).getEPC());
        assertEquals(1, result.get(0).getPDC());
        assertEquals(EPC.xE0, result.get(1).getEPC());
        assertEquals(2, result.get(1).getPDC());
        assertTrue(localGetAtomic.isSuccess());
    }
    
    
    @Test
    public void testSetGet() {
        TemperatureSensorInfo info = getWritableTemperatureSensorInfo();
        info.add(EPC.xE0, true, true, true, 2);
        info.add(EPC.x88, true, false, true, new byte[]{(byte)0x33});
        
        LocalObject object = new LocalObject(info);
        assertTrue(object.setData(EPC.x80, new ObjectData((byte)0x41)));
        LocalSetGetAtomic localSetGetAtomic = new LocalSetGetAtomic(object);
        localSetGetAtomic.addSet(new Property(EPC.x80, new Data((byte)0x41)));
        localSetGetAtomic.addSet(new Property(EPC.xE0, new Data((byte)0x12, (byte)0x34)));
        localSetGetAtomic.addGet(new Property(EPC.x88));
        localSetGetAtomic.addGet(new Property(EPC.x9D));
        assertFalse(localSetGetAtomic.isDone());
        localSetGetAtomic.run();
        assertTrue(localSetGetAtomic.isDone());
        
        List<Property> setResult = localSetGetAtomic.getSetResult();
        assertEquals(2, setResult.size());
        assertEquals(EPC.x80, setResult.get(0).getEPC());
        assertEquals(0, setResult.get(0).getPDC());
        assertEquals(EPC.xE0, setResult.get(1).getEPC());
        assertEquals(0, setResult.get(1).getPDC());
        
        List<Property> getResult = localSetGetAtomic.getGetResult();
        assertEquals(2, getResult.size());
        assertEquals(EPC.x88, getResult.get(0).getEPC());
        assertEquals(1, getResult.get(0).getPDC());
        assertEquals(EPC.x9D, getResult.get(1).getEPC());
        assertEquals(4, getResult.get(1).getPDC());
        
        assertTrue(localSetGetAtomic.isSuccess());
    }
    
    @Test
    public void testFailure() {
        LocalObject setObject = new LocalObject(getWritableTemperatureSensorInfo());
        LocalSetGetAtomic localSetAtomic = new LocalSetGetAtomic(setObject);
        localSetAtomic.addSet(new Property(EPC.x81, new Data((byte)0x41)));
        assertFalse(localSetAtomic.isDone());
        localSetAtomic.run();
        assertTrue(localSetAtomic.isDone());
        List<Property> setResult = localSetAtomic.getSetResult();
        assertEquals(1, setResult.size());
        assertEquals(EPC.x81, setResult.get(0).getEPC());
        assertEquals(1, setResult.get(0).getPDC());
        assertFalse(localSetAtomic.isSuccess());
        
        LocalObject getObject = new LocalObject(getTemperatureSensorInfoWitnNoLocationInfo());
        LocalSetGetAtomic localGetAtomic = new LocalSetGetAtomic(getObject);
        localGetAtomic.addGet(new Property(EPC.x81));
        assertFalse(localGetAtomic.isDone());
        localGetAtomic.run();
        assertTrue(localGetAtomic.isDone());
        List<Property> getResult = localGetAtomic.getGetResult();
        assertEquals(1, getResult.size());
        assertEquals(EPC.x81, getResult.get(0).getEPC());
        assertEquals(0, getResult.get(0).getPDC());
        assertFalse(localGetAtomic.isSuccess());
        
        LocalObject object = new LocalObject(getTemperatureSensorInfoWitnNoLocationInfo());
        LocalSetGetAtomic localAtomic = new LocalSetGetAtomic(object);
        localAtomic.addSet(new Property(EPC.x81, new Data((byte)0x11, (byte)0x22)));
        localAtomic.addGet(new Property(EPC.x81));
        assertFalse(localAtomic.isDone());
        localAtomic.run();
        assertTrue(localAtomic.isDone());
        List<Property> result1 = localAtomic.getSetResult();
        assertEquals(1, result1.size());
        assertEquals(EPC.x81, result1.get(0).getEPC());
        assertEquals(2, result1.get(0).getPDC());
        List<Property> result2 = localAtomic.getGetResult();
        assertEquals(1, result2.size());
        assertEquals(EPC.x81, result2.get(0).getEPC());
        assertEquals(0, result2.get(0).getPDC());
        assertFalse(localAtomic.isSuccess());
    }
    
    @Test
    public void testReset() {
        LocalObject object = new LocalObject(getWritableTemperatureSensorInfo());
        object.setData(EPC.x80, new ObjectData((byte)0x41));
        object.setData(EPC.x81, new ObjectData((byte)0x12, (byte)0x34));
        LocalSetGetAtomic localSetGetAtomic = new LocalSetGetAtomic(object);
        localSetGetAtomic.addSet(new Property(EPC.x80, new Data((byte)0x41)));
        localSetGetAtomic.addSet(new Property(EPC.xE0, new Data((byte)0x12, (byte)0x34)));
        localSetGetAtomic.addGet(new Property(EPC.x88));
        localSetGetAtomic.addGet(new Property(EPC.x9D));
        localSetGetAtomic.run();
        
        assertTrue(localSetGetAtomic.isDone());
        localSetGetAtomic.initialize();
        assertFalse(localSetGetAtomic.isDone());
        
        assertEquals(0, localSetGetAtomic.getGetResult().size());
        assertEquals(0, localSetGetAtomic.getSetResult().size());
        
        localSetGetAtomic.addSet(new Property(EPC.x80, new Data((byte)0x42)));
        localSetGetAtomic.addSet(new Property(EPC.xE0, new Data((byte)0xab, (byte)0xcd)));
        localSetGetAtomic.addGet(new Property(EPC.x88));
        localSetGetAtomic.addGet(new Property(EPC.x9D));
        localSetGetAtomic.run();
        
        List<Property> setResult = localSetGetAtomic.getSetResult();
        assertEquals(2, setResult.size());
        assertEquals(EPC.x80, setResult.get(0).getEPC());
        assertEquals(0, setResult.get(0).getPDC());
        assertEquals(EPC.xE0, setResult.get(1).getEPC());
        assertEquals(0, setResult.get(1).getPDC());
        
        List<Property> getResult = localSetGetAtomic.getGetResult();
        assertEquals(2, getResult.size());
        assertEquals(EPC.x88, getResult.get(0).getEPC());
        assertEquals(1, getResult.get(0).getPDC());
        assertEquals(EPC.x9D, getResult.get(1).getEPC());
        assertEquals(3, getResult.get(1).getPDC());
        
        assertEquals((byte)0x42, object.getData(EPC.x80).get(0));
        assertEquals((byte)0xab, object.getData(EPC.xE0).get(0));
        assertEquals((byte)0xcd, object.getData(EPC.xE0).get(1));
        
        assertTrue(localSetGetAtomic.isSuccess());
    }
        
    private void testAnnouncePrivate(boolean gettable, boolean observable) {
        DeviceObjectInfo objectInfo = getWritableTemperatureSensorInfo();
        objectInfo.add(EPC.xE1, gettable, false, observable, 1);
        LocalObject object = new LocalObject(objectInfo);
        object.forceSetData(EPC.x80, new ObjectData((byte)0x41));
        object.forceSetData(EPC.xE0, new ObjectData((byte)0x12, (byte)0x34));
        LocalSetGetAtomic localSetGetAtomic = new LocalSetGetAtomic(object);
        localSetGetAtomic.addGet(new Property(EPC.x80));
        localSetGetAtomic.addGet(new Property(EPC.xE0));
        localSetGetAtomic.addGet(new Property(EPC.xE1));
        localSetGetAtomic.setAnnounce(true);
        localSetGetAtomic.run();
        
        List<Property> getResult = localSetGetAtomic.getGetResult();
        assertEquals(3, getResult.size());
        assertEquals(EPC.x80, getResult.get(0).getEPC());
        assertEquals(1, getResult.get(0).getPDC());
        assertEquals(1, getResult.get(0).getEDT().size());
        assertEquals(EPC.xE0, getResult.get(1).getEPC());
        assertEquals(2, getResult.get(1).getPDC());
        assertEquals(2, getResult.get(1).getEDT().size());
        assertEquals(EPC.xE1, getResult.get(2).getEPC());
        assertEquals(1, getResult.get(2).getPDC());
        assertEquals(1, getResult.get(2).getEDT().size());
    }
    
    @Test
    public void testAnnounce() {
        testAnnouncePrivate(true, true);
        testAnnouncePrivate(true, false);
        testAnnouncePrivate(false, true);
    }
    
    private void testExtraDataPrivate(boolean gettable, boolean observable) {
        DeviceObjectInfo objectInfo = getWritableTemperatureSensorInfo();
        objectInfo.add(EPC.xE1, gettable, false, observable, 1);
        LocalObject object = new LocalObject(objectInfo);
        LinkedList<Data> dataList = new LinkedList<Data>();
        
        Data data1 = new Data((byte)0x12, (byte)0x34);
        Data data2 = new Data((byte)0x56, (byte)0x78);
        dataList.add(data1);
        dataList.add(data2);
        
        object.forceSetData(EPC.xE1, new ObjectData(dataList));
        
        LocalSetGetAtomic localSetGetAtomic1 = new LocalSetGetAtomic(object);
        localSetGetAtomic1.addGet(new Property(EPC.xE1));
        localSetGetAtomic1.run();
        
        if (gettable) {
            List<Property> getResult1 = localSetGetAtomic1.getGetResult();
            assertEquals(1, getResult1.size());
            assertEquals(EPC.xE1, getResult1.get(0).getEPC());
            assertEquals(data1, getResult1.get(0).getEDT());
        } else {
            List<Property> getResult1 = localSetGetAtomic1.getGetResult();
            assertEquals(1, getResult1.size());
            assertEquals(EPC.xE1, getResult1.get(0).getEPC());
            assertEquals(new Data(), getResult1.get(0).getEDT());
        }
        
        LocalSetGetAtomic localSetGetAtomic2 = new LocalSetGetAtomic(object);
        localSetGetAtomic2.addGet(new Property(EPC.xE1));
        localSetGetAtomic2.setAnnounce(true);
        localSetGetAtomic2.run();
        
        List<Property> getResult2 = localSetGetAtomic2.getGetResult();
        assertEquals(2, getResult2.size());
        assertEquals(EPC.xE1, getResult2.get(0).getEPC());
        assertEquals(data1, getResult2.get(0).getEDT());
        assertEquals(EPC.xE1, getResult2.get(1).getEPC());
        assertEquals(data2, getResult2.get(1).getEDT());
    }
    
    @Test
    public void testExtraData() {
        testExtraDataPrivate(true, true);
        testExtraDataPrivate(true, false);
        testExtraDataPrivate(false, true);
    }
    
    public class AtomicTestDelegate extends LocalObjectDefaultDelegate {
            private int count = 0;

            @Override
            public synchronized void getData(LocalObjectDelegate.GetState result, LocalObject object, EPC epc) {
            }

            @Override
            public synchronized void setData(LocalObjectDelegate.SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData curData) {
            }

            @Override
            public void notifyDataChanged(LocalObjectDelegate.NotifyState result, LocalObject object, EPC epc, ObjectData curData, ObjectData oldData) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LocalSetGetAtomicTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
    
    @Test
    public void testAtomically() throws InterruptedException {
        DeviceObjectInfo objectInfo = getWritableTemperatureSensorInfo();
        LocalObject object = new LocalObject(objectInfo);
        object.forceSetData(EPC.x80, new ObjectData((byte)0x41));
        object.forceSetData(EPC.xE0, new ObjectData((byte)0x12, (byte)0x34));
        
        AtomicTestDelegate delegate = new AtomicTestDelegate();
        object.addDelegate(delegate);
        
        final LocalSetGetAtomic atomic1 = new LocalSetGetAtomic(object);
        Data data1 = new Data((byte)0x31);
        atomic1.addSet(new Property(EPC.x80, data1));
        atomic1.addGet(new Property(EPC.x80));
        
        final LocalSetGetAtomic atomic2 = new LocalSetGetAtomic(object);
        Data data2 = new Data((byte)0x32);
        atomic2.addSet(new Property(EPC.x80, data2));
        atomic2.addGet(new Property(EPC.x80));
        
        Thread t1 = new Thread(atomic1);
        Thread t2 = new Thread(atomic2);
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
        
        assertEquals(data1, atomic1.getGetResult().get(0).getEDT());
        assertEquals(data2, atomic2.getGetResult().get(0).getEDT());
    }
}
