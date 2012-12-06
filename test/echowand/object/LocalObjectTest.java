package echowand.object;

import echowand.object.LocalObject;
import echowand.object.LocalObjectDelegate;
import echowand.object.ObjectData;
import echowand.info.BaseObjectInfo;
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
    public ObjectData getData(LocalObject object, EPC epc) {
        return data;
    }
    
    public LocalObject lastObject;
    public EPC lastEPC;
    public ObjectData lastData;
    
    @Override
    public boolean setData(LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
        lastObject = object;
        lastEPC = epc;
        lastData = newData;
        
        if (data != null) {
            assertEquals(data, oldData);
        }
        
        assertEquals(object.getData(epc), oldData);
        
        data = newData;
        
        object.notifyDataChanged(epc, newData, oldData);
        return true;
    }

    @Override
    public void notifyDataChanged(LocalObject object, EPC epc, ObjectData newData, ObjectData oldData) {
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
        BaseObjectInfo info = new HomeAirConditionerInfo();
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
        BaseObjectInfo info = new HomeAirConditionerInfo();
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
        assertFalse(object.isSettable(EPC.x80));
        
        assertTrue(object.isObservable(EPC.xB0));
        assertFalse(object.isObservable(EPC.xB1));
        assertTrue(object.isObservable(EPC.x80));
        
        assertTrue(object.contains(EPC.x80));
        assertFalse(object.contains(EPC.xFF));
    }
    
    @Test
    public void testDelegateSetData() {
        BaseObjectInfo info = new HomeAirConditionerInfo();
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
    public void testSetAndGet2() {
        BaseObjectInfo info = new HomeAirConditionerInfo();
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
}
