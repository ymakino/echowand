package echowand.service;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.TooManyObjectsException;
import echowand.net.InternalSubnet;
import echowand.net.SubnetException;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import java.util.HashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class LocalObjectAccessInterfaceTest {
    private InternalSubnet subnet;
    private ServiceManager serviceManager1;
    private ServiceManager serviceManager2;
    private DummyLocalObject localObject1 = new DummyLocalObject();
    private DummyLocalObject localObject2 = new DummyLocalObject();
    
    private HashMap<EPC, ObjectData> dataMap = new HashMap<EPC, ObjectData>();

    private class DummyLocalObject extends LocalObject {
        public DummyLocalObject() {
            super(new TemperatureSensorInfo());
        }
        
        public ObjectData getReturnData(EPC epc) {
            return new ObjectData(epc.toByte(), epc.toByte());
        }
        
        @Override
        public ObjectData getData(EPC epc) {
            if (dataMap.get(epc) != null) {
                return dataMap.get(epc);
            } else {
                return getReturnData(epc);
            }
        }
        
        @Override
        public boolean forceSetData(EPC epc, ObjectData data) {
            dataMap.put(epc, data);
            return true;
        }
    }
    
    @Before
    public void setUp() throws SubnetException {
        subnet = new InternalSubnet("LocalObjectAccessInterfaceTest");
        serviceManager1 =  new ServiceManager(subnet);
        serviceManager2 =  new ServiceManager(subnet);
        localObject1 = new DummyLocalObject();
        localObject2 = new DummyLocalObject();
    }

    @Test
    public void testSetServiceManager() {
        LocalObjectAccessInterface instance = new LocalObjectAccessInterface();
        assertNull(instance.setServiceManager(serviceManager1));
        assertEquals(serviceManager1, instance.setServiceManager(serviceManager2));
    }

    @Test
    public void testSetLocalObject() {
        LocalObjectAccessInterface instance = new LocalObjectAccessInterface();
        assertNull(instance.setLocalObject(localObject1));
        assertEquals(localObject1, instance.setLocalObject(localObject2));
    }

    @Test
    public void testGetServiceManager() {
        LocalObjectAccessInterface instance = new LocalObjectAccessInterface();
        assertNull(instance.getServiceManager());
        
        instance.setServiceManager(serviceManager1);
        assertEquals(serviceManager1, instance.getServiceManager());
        
        instance.setServiceManager(serviceManager2);
        assertEquals(serviceManager2, instance.getServiceManager());
    }

    @Test
    public void testGetLocalObject() {
        LocalObjectAccessInterface instance = new LocalObjectAccessInterface();
        assertNull(instance.getLocalObject());
        
        instance.setLocalObject(localObject1);
        assertEquals(localObject1, instance.getLocalObject());
        
        instance.setLocalObject(localObject2);
        assertEquals(localObject2, instance.getLocalObject());
    }

    @Test
    public void testGetData_EPC() {
        LocalObjectAccessInterface instance = new LocalObjectAccessInterface();
        instance.setLocalObject(localObject1);
        
        assertEquals(localObject1.getReturnData(EPC.x80), instance.getData(EPC.x80));
    }

    @Test
    public void testGetData_EOJ_EPC() throws TooManyObjectsException {
        LocalObjectAccessInterface instance = new LocalObjectAccessInterface();
        instance.setLocalObject(localObject1);
        
        EOJ eoj1 = new EOJ("001101");
        EOJ eoj2 = new EOJ("001102");
        instance.setServiceManager(serviceManager1);
        serviceManager1.startService();
        serviceManager1.getLocalObjectManager().add(localObject1);
        
        assertEquals(localObject1.getReturnData(EPC.x80), instance.getData(eoj1, EPC.x80));
        assertNull(instance.getData(eoj2, EPC.x80));
    }

    @Test
    public void testSetData_EPC_ObjectData() throws TooManyObjectsException {
        LocalObjectAccessInterface instance = new LocalObjectAccessInterface();
        instance.setLocalObject(localObject1);
        
        instance.setServiceManager(serviceManager1);
        serviceManager1.startService();
        serviceManager1.getLocalObjectManager().add(localObject1);
        
        ObjectData objectData = new ObjectData((byte)0x12);
        assertTrue(instance.setData(EPC.x80, objectData));
        assertEquals(objectData, localObject1.getData(EPC.x80));
        
        assertEquals(objectData, instance.getData(EPC.x80));
    }

    @Test
    public void testSetData_3args() throws TooManyObjectsException {
        LocalObjectAccessInterface instance = new LocalObjectAccessInterface();
        instance.setLocalObject(localObject1);
        
        EOJ eoj1 = new EOJ("001101");
        EOJ eoj2 = new EOJ("001102");
        instance.setServiceManager(serviceManager1);
        serviceManager1.startService();
        serviceManager1.getLocalObjectManager().add(localObject1);
        
        ObjectData objectData = new ObjectData((byte)0x12);
        
        assertTrue(instance.setData(eoj1, EPC.x80, objectData));
        assertEquals(objectData, localObject1.getData(EPC.x80));
        assertEquals(objectData, instance.getData(eoj1, EPC.x80));
        
        assertFalse(instance.setData(eoj2, EPC.x80, objectData));
    }
    
}
