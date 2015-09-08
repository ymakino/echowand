package echowand.service;

import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.net.SubnetException;
import echowand.object.LocalObject;
import echowand.object.LocalObjectDelegate.GetState;
import echowand.object.LocalObjectDelegate.NotifyState;
import echowand.object.LocalObjectDelegate.SetState;
import echowand.object.ObjectData;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class LocalObjectPropertyDelegateTest {
    private LocalObject localObject;
    private DummyPropertyDelegate propertyDelegate;
    private DummyPropertyDelegate propertyDelegateGet;
    private DummyPropertyDelegate propertyDelegateSet;
    private DummyPropertyDelegate propertyDelegateNotify;
    private LocalObjectPropertyDelegate delegate;
    private LocalObjectPropertyDelegate delegateGet;
    private LocalObjectPropertyDelegate delegateSet;
    private LocalObjectPropertyDelegate delegateNotify;
    
    private class DummyPropertyDelegate extends PropertyDelegate {
        public LinkedList<EPC> getList = new LinkedList<EPC>();
        public HashMap<EPC, ObjectData> dataMap = new HashMap<EPC, ObjectData>();
        public HashMap<EPC, ObjectData> notifyMap = new HashMap<EPC, ObjectData>();

        public DummyPropertyDelegate(EPC epc, boolean getEnabled, boolean setEnabled, boolean notifyEnabled) {
            super(epc, getEnabled, setEnabled, notifyEnabled);
        }

        @Override
        public ObjectData getUserData(LocalObject object, EPC epc) {
            getList.add(epc);
            return dataMap.get(epc);
        }

        @Override
        public boolean setUserData(LocalObject object, EPC epc, ObjectData data) {
            dataMap.put(epc, data);
            return true;
        }

        @Override
        public void notifyDataChanged(LocalObject object, EPC epc, ObjectData curData, ObjectData oldData) {
            notifyMap.put(epc, curData);
        }
    }
    
    public LocalObjectPropertyDelegateTest() {
    }
    
    @Before
    public void setUp() throws SubnetException {
        TemperatureSensorInfo info = new TemperatureSensorInfo();
        info.add(EPC.x80, true, true, true, new byte[]{(byte)0x41});
        
        localObject = new LocalObject(info);
        
        propertyDelegate = new DummyPropertyDelegate(EPC.x80, true, true, true);
        propertyDelegateGet = new DummyPropertyDelegate(EPC.x80, true, false, false);
        propertyDelegateSet = new DummyPropertyDelegate(EPC.x80, false, true, false);
        propertyDelegateNotify = new DummyPropertyDelegate(EPC.x80, false, false, true);
        
        delegate = new LocalObjectPropertyDelegate(propertyDelegate);
        delegateGet = new LocalObjectPropertyDelegate(propertyDelegateGet);
        delegateSet = new LocalObjectPropertyDelegate(propertyDelegateSet);
        delegateNotify = new LocalObjectPropertyDelegate(propertyDelegateNotify);
        
        propertyDelegate.setLocalObject(localObject);
    }
    
    @After
    public void tearDown() {
    }
    
    private void testGetDataOne(LocalObjectPropertyDelegate delegate, DummyPropertyDelegate propertyDelegate, boolean done, boolean fail, boolean enabled) {
        ObjectData data0 = new ObjectData((byte)0x00);
        
        assertEquals(done, propertyDelegate.getList.contains(EPC.x80));
        GetState state = new GetState(data0);
        delegate.getData(state, localObject, EPC.x80);
        assertEquals(data0, state.getGetData());
        assertEquals(done, state.isDone());
        assertEquals(fail, state.isFail());
        assertEquals(enabled, propertyDelegate.getList.contains(EPC.x80));
    }

    @Test
    public void testGetData() {
        ObjectData data0 = new ObjectData((byte)0x00);
        
        testGetDataOne(delegate, propertyDelegate, false, true, true);
        testGetDataOne(delegateGet, propertyDelegateGet, false, true, true);
        testGetDataOne(delegateSet, propertyDelegateSet, false, false, false);
        testGetDataOne(delegateNotify, propertyDelegateNotify, false, false, false);
        
        propertyDelegate.setUserData(localObject, EPC.x80, data0);
        propertyDelegateGet.setUserData(localObject, EPC.x80, data0);
        propertyDelegateSet.setUserData(localObject, EPC.x80, data0);
        propertyDelegateNotify.setUserData(localObject, EPC.x80, data0);
        
        testGetDataOne(delegate, propertyDelegate, true, false, true);
        testGetDataOne(delegateGet, propertyDelegateGet, true, false, true);
        testGetDataOne(delegateSet, propertyDelegateSet, false, false, false);
        testGetDataOne(delegateNotify, propertyDelegateNotify, false, false, false);
    }

    private void testSetDataOne(LocalObjectPropertyDelegate delegate, DummyPropertyDelegate propertyDelegate, boolean done, boolean fail, boolean enabled) {
        ObjectData data0 = new ObjectData((byte)0x00);
        ObjectData data1 = new ObjectData((byte)0x01);
        
        assertFalse(propertyDelegate.dataMap.containsKey(EPC.x80));
        SetState state0 = new SetState(data1, data0);
        delegate.setData(state0, localObject, EPC.x80, data1, data0);
        assertEquals(data1, state0.getNewData());
        assertEquals(done, state0.isDone());
        assertEquals(false, state0.isFail());
        assertEquals(enabled, propertyDelegate.dataMap.containsKey(EPC.x80));
    }
    
    @Test
    public void testSetData() {
        ObjectData data0 = new ObjectData((byte)0x00);
        ObjectData data1 = new ObjectData((byte)0x01);
        
        testSetDataOne(delegate, propertyDelegate, true, false, true);
        testSetDataOne(delegateGet, propertyDelegateGet, false, false, false);
        testSetDataOne(delegateSet, propertyDelegateSet, true, false, true);
        testSetDataOne(delegateNotify, propertyDelegateNotify, false, false, false);
    }

    private void testNotifyDataChangedOne(LocalObjectPropertyDelegate delegate, DummyPropertyDelegate propertyDelegate, boolean done, boolean fail, boolean enabled) {
        ObjectData data0 = new ObjectData((byte)0x00);
        ObjectData data1 = new ObjectData((byte)0x01);
        
        assertFalse(propertyDelegate.notifyMap.containsKey(EPC.x80));
        NotifyState state0 = new NotifyState();
        delegate.notifyDataChanged(state0, localObject, EPC.x80, data1, data0);
        assertEquals(done, state0.isDone());
        assertEquals(fail, state0.isFail());
        assertEquals(enabled, propertyDelegate.notifyMap.containsKey(EPC.x80));
    }

    @Test
    public void testNotifyDataChanged() {
        ObjectData data0 = new ObjectData((byte)0x00);
        ObjectData data1 = new ObjectData((byte)0x01);
        
        testNotifyDataChangedOne(delegate, propertyDelegate, false, false, true);
        testNotifyDataChangedOne(delegateGet, propertyDelegateGet, false, false, false);
        testNotifyDataChangedOne(delegateSet, propertyDelegateSet, false, false, false);
        testNotifyDataChangedOne(delegateNotify, propertyDelegateNotify, false, false, true);
    }
    
}
