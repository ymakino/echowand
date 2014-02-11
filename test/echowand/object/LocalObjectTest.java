package echowand.object;

import echowand.object.LocalObject;
import echowand.object.LocalObjectDelegate;
import echowand.object.ObjectData;
import echowand.info.DeviceObjectInfo;
import echowand.info.PropertyInfo;
import echowand.info.TemperatureSensorInfo;
import echowand.info.ObjectInfo;
import echowand.info.HomeAirConditionerInfo;
import echowand.common.EOJ;
import echowand.common.EPC;
import static org.junit.Assert.*;
import org.junit.*;


/**
 *
 * @author Yoshiki Makino
 */

class DummyDelegate implements LocalObjectDelegate {
    private ObjectData data;
    
    public DummyDelegate(ObjectData data) {
        this.data = data;
    }
    
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
        result.setGetData(data);
    }
    
    public LocalObject lastObject;
    public EPC lastEPC;
    public ObjectData lastData;
    
    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
        lastObject = object;
        lastEPC = epc;
        lastData = newData;
        
        if (data != null) {
            assertEquals(data, oldData);
        }
        
        assertEquals(object.getData(epc), oldData);
        
        data = newData;
    }

    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
    }
}

class SetFailDelegate implements LocalObjectDelegate {
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
    }
    
    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
        result.setFail();
    }

    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
    }
}

class GetFailDelegate implements LocalObjectDelegate {
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
        result.setFail();
    }
    
    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
    }

    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
    }
}

class SetSameDelegate implements LocalObjectDelegate {
    private ObjectData data;
    
    public SetSameDelegate(ObjectData data) {
        this.data = data;
    }
    
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
    }
    
    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
        result.setSetData(data, data);
    }

    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
    }
}

class SetDoneDelegate implements LocalObjectDelegate {
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
    }
    
    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
        result.setDone();
    }

    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
    }
}

class GetDoneDelegate implements LocalObjectDelegate {
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
        result.setDone();
    }
    
    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
    }

    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
    }
}

class CountDelegate implements LocalObjectDelegate {
    public int countGet = 0;
    public int countSet = 0;
    public int countNotify = 0;
    
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
        countGet++;
    }
    
    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
        countSet++;
    }

    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
        countNotify++;
    }
    
    public int getCountGet() {
        return countGet;
    }
    
    public int getCountSet() {
        return countSet;
    }
    
    public int getCountNotify() {
        return countNotify;
    }
}

public class LocalObjectTest {
    
    @Test
    public void testCreation() {
        ObjectInfo info = new HomeAirConditionerInfo();
        LocalObject object = new LocalObject(info);
        assertEquals(new EOJ("013001"), object.getEOJ());
    }
    
    @Test
    public void testSetAndGet() {
        DeviceObjectInfo info = new HomeAirConditionerInfo();
        info.add(new PropertyInfo(EPC.x80, true, true, false, 1));
        LocalObject object = new LocalObject(info);
        
        assertEquals(1, object.getData(EPC.x80).size());
        assertEquals(new ObjectData((byte)0x00), object.getData(EPC.x80));
        assertTrue(object.setData(EPC.x80, new ObjectData((byte)0x31)));
        ObjectData data = object.getData(EPC.x80);
        assertEquals(1, data.size());
        assertEquals(0x31, data.get(0));
        
        assertEquals(info.getClassEOJ().getEOJWithInstanceCode((byte)0x01), object.getEOJ());
    }
    
    @Test
    public void testSetInvalidEPCAndData() {
        DeviceObjectInfo info = new HomeAirConditionerInfo();
        info.add(new PropertyInfo(EPC.x80, true, true, false, 1));
        LocalObject object = new LocalObject(info);
        
        assertFalse(object.setData(EPC.Invalid, new ObjectData((byte)0x00)));
        assertFalse(object.setData(EPC.xFF, new ObjectData((byte)0x00)));
        assertFalse(object.setData(EPC.x80, new ObjectData((byte)0x00, (byte)0x00)));
        assertTrue(object.setData(EPC.x80, new ObjectData((byte)0x00)));
    }
    
    @Test
    public void testUpdateInstanceCode() {
        ObjectInfo info = new HomeAirConditionerInfo();
        LocalObject object = new LocalObject(info);
        assertEquals(new EOJ("013001"), object.getEOJ());
        object.setInstanceCode((byte)0x02);
        assertEquals(new EOJ("013002"), object.getEOJ());
        object.setInstanceCode((byte)0xef);
        assertEquals(new EOJ("0130ef"), object.getEOJ());
    }
    
    @Test
    public void testPermission() {
        ObjectInfo info = new HomeAirConditionerInfo();
        LocalObject object = new LocalObject(info);
        
        assertTrue(object.isGettable(EPC.xB0));
        assertTrue(object.isGettable(EPC.x80));
        
        assertTrue(object.isSettable(EPC.xB0));
        assertTrue(object.isSettable(EPC.x80));
        
        assertTrue(object.isObservable(EPC.xB0));
        assertFalse(object.isObservable(EPC.xB1));
        assertTrue(object.isObservable(EPC.x80));
        
        assertTrue(object.contains(EPC.x80));
        assertFalse(object.contains(EPC.xFF));
    }
    
    @Test
    public void testDelegateSetData() {
        DeviceObjectInfo info = new HomeAirConditionerInfo();
        info.add(new PropertyInfo(EPC.x80, true, true, false, 1));
        LocalObject object = new LocalObject(info);
        
        DummyDelegate delegate = new DummyDelegate(null);
        object.addDelegate(delegate);
        object.setData(EPC.x80, new ObjectData((byte)0x11));
        assertEquals(object, delegate.lastObject);
        assertEquals(EPC.x80, delegate.lastEPC);
        assertEquals(1, delegate.lastData.size());
        assertEquals((byte)0x11, delegate.lastData.get(0));
        
        
        object.setData(EPC.xB0, new ObjectData((byte)0x43));
        assertEquals(object, delegate.lastObject);
        assertEquals(EPC.xB0, delegate.lastEPC);
        assertEquals(1, delegate.lastData.size());
        assertEquals((byte)0x43, delegate.lastData.get(0));
        
        object.removeDelegate(delegate);
        object.setData(EPC.xB1, new ObjectData((byte)0xab));
        assertEquals(object, delegate.lastObject);
        assertEquals(EPC.xB0, delegate.lastEPC);
        assertEquals(1, delegate.lastData.size());
        assertEquals((byte)0x43, delegate.lastData.get(0));
    }
    
    @Test
    public void testDelegateSetDataFail() {
        DeviceObjectInfo info = new HomeAirConditionerInfo();
        info.add(new PropertyInfo(EPC.x80, true, true, true, 1));
        LocalObject object = new LocalObject(info);
        
        SetFailDelegate delegate = new SetFailDelegate();
        CountDelegate notifyDelegate = new CountDelegate();
        
        object.addDelegate(delegate);
        object.addDelegate(notifyDelegate);
        boolean result = object.setData(EPC.x80, new ObjectData((byte)0x11));
        
        assertFalse(result);
        assertFalse(new ObjectData((byte)0x11).equals(object.getData(EPC.x80)));
        assertEquals(0, notifyDelegate.getCountNotify());
    }
    
    @Test
    public void testDelegateSetDataDone() {
        DeviceObjectInfo info = new HomeAirConditionerInfo();
        info.add(new PropertyInfo(EPC.x80, true, true, true, 1));
        LocalObject object = new LocalObject(info);
        
        SetDoneDelegate delegate = new SetDoneDelegate();
        CountDelegate countDelegate = new CountDelegate();
        
        object.addDelegate(delegate);
        object.addDelegate(countDelegate);
        boolean result = object.setData(EPC.x80, new ObjectData((byte)0x11));
        
        assertTrue(result);
        assertEquals(new ObjectData((byte)0x11), object.getData(EPC.x80));
        assertEquals(0, countDelegate.getCountSet());
    }
    
    @Test
    public void testDelegateGetData() {
        ObjectInfo info = new HomeAirConditionerInfo();
        LocalObject object = new LocalObject(info);
        
        DummyDelegate delegate1 = new DummyDelegate(new ObjectData((byte)0x11));
        object.addDelegate(delegate1);
        assertEquals(1, object.getData(EPC.x80).size());
        assertEquals((byte)0x11, object.getData(EPC.x80).get(0));
        
        
        DummyDelegate delegate2 = new DummyDelegate(new ObjectData((byte)0x22, (byte)0x33));
        object.addDelegate(delegate2);
        assertEquals(2, object.getData(EPC.xB0).size());
        assertEquals((byte)0x22, object.getData(EPC.xB0).get(0));
        assertEquals((byte)0x33, object.getData(EPC.xB0).get(1));
        
        object.removeDelegate(delegate2);
        assertEquals(1, object.getData(EPC.xB0).size());
        assertEquals((byte)0x11, object.getData(EPC.x80).get(0));
        
        object.setInternalData(EPC.x80, new ObjectData((byte)0x56, (byte)0x78));
        assertEquals(2, object.getInternalData(EPC.x80).size());
        assertEquals(0x56, object.getInternalData(EPC.x80).get(0));
        assertEquals(0x78, object.getInternalData(EPC.x80).get(1));
    }
    
    @Test
    public void testDelegateGetDataFail() {
        DeviceObjectInfo info = new HomeAirConditionerInfo();
        info.add(new PropertyInfo(EPC.x80, true, true, true, 1));
        LocalObject object = new LocalObject(info);
        
        GetFailDelegate delegate = new GetFailDelegate();
        
        object.addDelegate(delegate);
        ObjectData data = object.getData(EPC.x80);
        
        assertNull(data);
    }
    
    @Test
    public void testDelegateGetDataDone() {
        DeviceObjectInfo info = new HomeAirConditionerInfo();
        info.add(new PropertyInfo(EPC.x80, true, true, true, new byte[]{0x11}));
        LocalObject object = new LocalObject(info);
        
        GetDoneDelegate delegate = new GetDoneDelegate();
        CountDelegate countDelegate = new CountDelegate();
        
        object.addDelegate(delegate);
        object.addDelegate(countDelegate);
        ObjectData result = object.getData(EPC.x80);
        
        assertEquals(new ObjectData((byte)0x11), result);
        assertEquals(0, countDelegate.getCountGet());
    }
    
    @Test
    public void testDelegateSetDataSame() {
        DeviceObjectInfo info = new HomeAirConditionerInfo();
        info.add(new PropertyInfo(EPC.x80, true, true, true, 1));
        LocalObject object = new LocalObject(info);
        
        CountDelegate notifyDelegate = new CountDelegate();
        
        object.addDelegate(notifyDelegate);
        
        boolean result1 = object.setData(EPC.x80, new ObjectData((byte)0x11));
        assertTrue(result1);
        assertEquals(new ObjectData((byte)0x11), object.getData(EPC.x80));
        assertEquals(1, notifyDelegate.getCountNotify());
        
        boolean result2 = object.setData(EPC.x80, new ObjectData((byte)0x11));
        assertTrue(result2);
        assertEquals(new ObjectData((byte)0x11), object.getData(EPC.x80));
        assertEquals(1, notifyDelegate.getCountNotify());
        
        boolean result3 = object.setData(EPC.x80, new ObjectData((byte)0x12));
        assertTrue(result3);
        assertEquals(new ObjectData((byte)0x12), object.getData(EPC.x80));
        assertEquals(2, notifyDelegate.getCountNotify());
        
        SetSameDelegate delegate = new SetSameDelegate(new ObjectData((byte)0x55));
        object.addDelegate(delegate);
        
        boolean result4 = object.setData(EPC.x80, new ObjectData((byte)0x13));
        assertTrue(result4);
        assertEquals(new ObjectData((byte)0x55), object.getData(EPC.x80));
        assertEquals(2, notifyDelegate.getCountNotify());
    }
    
    @Test
    public void testSetAndGet2() {
        DeviceObjectInfo info = new HomeAirConditionerInfo();
        info.add(new PropertyInfo(EPC.x80, true, true, false, 1));
        LocalObject object = new LocalObject(info);
        
        assertEquals(1, object.getData(EPC.x80).size());
        assertEquals((byte)0x00, object.getData(EPC.x80).get(0));
        assertTrue(object.setData(EPC.x80, new ObjectData((byte)0x31)));
        ObjectData data = object.getData(EPC.x80);
        assertEquals(1, data.size());
        assertEquals(0x31, data.get(0));
        
        assertEquals(info.getClassEOJ().getEOJWithInstanceCode((byte)0x01), object.getEOJ());
    }
    
    @Test
    public void testSetWithoutPermission() {
        ObjectInfo info = new TemperatureSensorInfo();
        LocalObject object = new LocalObject(info);
        
        assertFalse(object.setData(EPC.x80, new ObjectData((byte)0x12, (byte)0x34)));
        assertTrue(object.forceSetData(EPC.x80, new ObjectData((byte)0x12, (byte)0x34)));
    }
    
    @Test
    public void testDelegateNotifyData() {
        DeviceObjectInfo info = new HomeAirConditionerInfo();
        info.add(new PropertyInfo(EPC.x80, true, true, true, 1));
        LocalObject object = new LocalObject(info);
        
        CountDelegate notifyDelegate = new CountDelegate();
        
        object.addDelegate(notifyDelegate);
        boolean result = object.setData(EPC.x80, new ObjectData((byte)0x11));
        
        assertTrue(result);
        assertEquals(new ObjectData((byte)0x11), object.getData(EPC.x80));
        assertEquals(1,  notifyDelegate.getCountNotify());
    }
}
