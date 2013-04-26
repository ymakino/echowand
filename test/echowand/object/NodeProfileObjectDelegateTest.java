package echowand.object;

import echowand.object.LocalObjectManager;
import echowand.object.NodeProfileObjectDelegate;
import echowand.common.ClassEOJ;
import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.DeviceObjectInfo;
import echowand.info.HumiditySensorInfo;
import echowand.info.NodeProfileInfo;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.TooManyObjectsException;
import echowand.object.ObjectData;
import echowand.object.LocalObject;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class NodeProfileObjectDelegateTest {

    LocalObjectManager manager;
    NodeProfileObjectDelegate delegate;
    LocalObject object;
    
    @Before
    public void setUp() {
        manager = new LocalObjectManager();
        delegate = new NodeProfileObjectDelegate(manager);
        object = new LocalObject(new NodeProfileInfo());
        object.addDelegate(delegate);
        try {
            manager.add(object);
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testCountObjects() {
        ObjectData data = object.getData(EPC.xD3);
        assertEquals(3, data.size());
        assertEquals((byte)0x00, data.get(0));
        assertEquals((byte)0x00, data.get(1));
        assertEquals((byte)0x00, data.get(2));
        
        data = object.getData(EPC.xD4);
        assertEquals(2, data.size());
        assertEquals((byte)0x00, data.get(0));
        assertEquals((byte)0x00, data.get(1));
        
        for (int i = 0; i < 256 * 2 + 9; i++) {
            TemperatureSensorInfo info = new TemperatureSensorInfo();
            info.setClassEOJ(new ClassEOJ((byte)(i / 255), (byte)0x11));
            LocalObject obj = new LocalObject(info);
            try {
                manager.add(obj);
            } catch (TooManyObjectsException e) {
                e.printStackTrace();
                fail();
            }
        }
        data = object.getData(EPC.xD3);
        assertEquals(3, data.size());
        assertEquals((byte)0x00, data.get(0));
        assertEquals((byte)0x02, data.get(1));
        assertEquals((byte)0x09, data.get(2));
        
        data = object.getData(EPC.xD4);
        assertEquals(2, data.size());
        assertEquals((byte)0x00, data.get(0));
        assertEquals((byte)0x03, data.get(1));
    }
    
    @Test
    public void testInstanceList() {
        ObjectData data = object.getData(EPC.xD6);
        assertEquals((byte)0x0, data.get(0));
        assertEquals(0, data.getExtraSize());
        
        for (int i = 0; i < 255; i++) {
            LocalObject obj = new LocalObject(new TemperatureSensorInfo());
            try {
                manager.add(obj);
            } catch (TooManyObjectsException e) {
                e.printStackTrace();
                fail();
            }
            assertEquals((byte) Math.min(i + 1, 0xff), object.getData(EPC.xD6).get(0));
            assertEquals(i / 84, object.getData(EPC.xD6).getExtraSize());
        }

        data = object.getData(EPC.xD6);
        assertEquals((byte) 0xff, data.get(0));
        assertEquals(3, data.getExtraSize());

        EOJ eoj = new EOJ(data.get(1), data.get(2), data.get(3));
        assertEquals(new EOJ("001101"), eoj);

        Data e1 = data.getExtraDataAt(0);
        assertEquals((byte)0xff, e1.get(0));
        assertEquals(253, e1.size());
        eoj = new EOJ(e1.get(1), e1.get(2), e1.get(3));
        assertEquals(new EOJ("001155"), eoj);
        
        Data e2 = data.getExtraDataAt(1);
        assertEquals((byte)0xff, e2.get(0));
        assertEquals(253, e2.size());
        eoj = new EOJ(e2.get(1), e2.get(2), e2.get(3));
        assertEquals(new EOJ("0011a9"), eoj);
        
        Data e3 = data.getExtraDataAt(2);
        assertEquals((byte)0xff, e3.get(0));
        assertEquals(10, e3.size());
        eoj = new EOJ(e3.get(1), e3.get(2), e3.get(3));
        assertEquals(new EOJ("0011fd"), eoj);
        eoj = new EOJ(e3.get(7), e3.get(8), e3.get(9));
        assertEquals(new EOJ("0011ff"), eoj);
    }
    
    class DummyInfo extends DeviceObjectInfo {
        public DummyInfo(int num) {
            byte groupCode = (byte)(num / 256);
            byte group = (byte)(num % 256);
            this.setClassEOJ(new ClassEOJ(groupCode, group));
        }
    }
    @Test
    public void testClassList() {
        ObjectData data = object.getData(EPC.xD7);
        for (int i = 0; i < 257; i++) {
            try {
                manager.add(new LocalObject(new DummyInfo(i)));
            } catch (TooManyObjectsException e) {
                e.printStackTrace();
                fail();
            }
            assertEquals((byte) Math.min(i + 1, 0xff), object.getData(EPC.xD7).get(0));
            assertEquals(i / 8, object.getData(EPC.xD7).getExtraSize());
        }

        data = object.getData(EPC.xD7);
        assertEquals((byte)0xff, data.get(0));
        assertEquals(32, data.getExtraSize());
        
        for (int i=0; i<7; i++) {
            assertEquals((byte) 0x00, data.get(1+(i*2)));
            assertEquals((byte) i, data.get(2+(i*2)));
        }
        for (int i = 0; i < 31; i++) {
            Data extraData = data.getExtraDataAt(i);
            assertEquals(17, extraData.size());
            assertEquals((byte)0xff, extraData.get(0));
            for (int j = 0; j < 7; j++) {
                assertEquals((byte) 0x00, extraData.get(1+(j * 2)));
                assertEquals((byte) (j+(8*(i+1))), extraData.get(2+(j * 2)));
            }
        }
        Data lastExtraData = data.getExtraDataAt(31);
        assertEquals(3, lastExtraData.size());
        assertEquals((byte)0xff, lastExtraData.get(0));
        assertEquals((byte)0x01, lastExtraData.get(1));
        assertEquals((byte)0x00, lastExtraData.get(2));
    }
    @Test
    public void testDupClassList() {
        assertEquals(0, object.getData(EPC.xD7).get(0));
        
        ObjectData data = object.getData(EPC.xD7);
        for (int i = 0; i < 100; i++) {
            try {
                manager.add(new LocalObject(new TemperatureSensorInfo()));
                manager.add(new LocalObject(new HumiditySensorInfo()));
            } catch (TooManyObjectsException e) {
                e.printStackTrace();
                fail();
            }
            assertEquals(2, object.getData(EPC.xD7).get(0));
            assertEquals(0, object.getData(EPC.xD7).getExtraSize());
        }
    }
}
