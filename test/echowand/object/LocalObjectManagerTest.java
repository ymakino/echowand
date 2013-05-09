package echowand.object;

import echowand.object.LocalObjectManager;
import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.info.HomeAirConditionerInfo;
import echowand.info.HumiditySensorInfo;
import echowand.info.NodeProfileInfo;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.TooManyObjectsException;
import echowand.object.LocalObject;
import echowand.util.Selector;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.*;
/**
 *
 * @author Yoshiki Makino
 */
public class LocalObjectManagerTest {
    
    @Test
    public void testCreation() {
        try {
            LocalObjectManager manager = new LocalObjectManager();
            assertEquals(0, manager.size());

            LocalObject object = new LocalObject(new TemperatureSensorInfo());
            manager.add(object);
            assertEquals(1, manager.size());
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test(expected=TooManyObjectsException.class)
    public void testTooManyCreation() throws TooManyObjectsException {
        LocalObjectManager manager = new LocalObjectManager();
        LocalObject object = null;
        try {
            for (int i = 0x01; i <= 0x7f; i++) {
                object = new LocalObject(new TemperatureSensorInfo());
                manager.add(object);
            }
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
        manager.add(object);
    }

    @Test
    public void testAdditionAndGet() {
        try {
            LocalObjectManager manager = new LocalObjectManager();
            LocalObject object1 = new LocalObject(new TemperatureSensorInfo());
            assertEquals(new EOJ("001101"), object1.getEOJ());
            manager.add(object1);
            assertEquals(new EOJ("001101"), object1.getEOJ());

            LocalObject object2 = new LocalObject(new TemperatureSensorInfo());
            assertEquals(new EOJ("001101"), object1.getEOJ());
            manager.add(object2);
            assertEquals(new EOJ("001102"), object2.getEOJ());

            assertEquals(object1, manager.get(new EOJ("001101")));
            assertEquals(object2, manager.get(new EOJ("001102")));
            assertEquals(null, manager.get(new EOJ("001103")));
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetAtIndex() {
        try {
            LocalObjectManager manager = new LocalObjectManager();
            assertEquals(0, manager.size());
            LocalObject object1 = new LocalObject(new TemperatureSensorInfo());
            manager.add(object1);
            assertEquals(1, manager.size());
            assertEquals(object1, manager.getAtIndex(0));
            LocalObject object2 = new LocalObject(new TemperatureSensorInfo());
            manager.add(object2);
            assertEquals(2, manager.size());
            assertEquals(object1, manager.getAtIndex(0));
            assertEquals(object2, manager.getAtIndex(1));
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testGetWithSelector() {
        try {
            LocalObjectManager manager = new LocalObjectManager();
            assertEquals(0, manager.size());
            LocalObject object1 = new LocalObject(new TemperatureSensorInfo());
            manager.add(object1);
            LocalObject object3 = new LocalObject(new HumiditySensorInfo());
            manager.add(object3);
            LocalObject object2 = new LocalObject(new TemperatureSensorInfo());
            manager.add(object2);
            LinkedList<LocalObject> list1 = manager.get(new Selector<LocalObject>() {
                @Override
                public boolean select(LocalObject object) {
                    return object.getEOJ().getClassEOJ().equals(new ClassEOJ("0011"));
                }
            });
            assertEquals(2, list1.size());
            assertEquals(object1, list1.get(0));
            assertEquals(object2, list1.get(1));
            LinkedList<LocalObject> list2 = manager.get(new Selector<LocalObject>() {
                @Override
                public boolean select(LocalObject object) {
                    return object.getEOJ().getClassEOJ().equals(new ClassEOJ("0012"));
                }
            });
            assertEquals(1, list2.size());
            assertEquals(object3, list2.get(0));
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetClassEOJObject() {
        try {
            LocalObjectManager manager = new LocalObjectManager();
            assertEquals(0, manager.size());

            manager.add(new LocalObject(new TemperatureSensorInfo()));
            manager.add(new LocalObject(new TemperatureSensorInfo()));
            manager.add(new LocalObject(new HomeAirConditionerInfo()));
            assertEquals(3, manager.size());
            List<LocalObject> list = manager.getWithClassEOJ(new ClassEOJ("0011"));
            assertEquals(2, list.size());
            assertTrue(list.get(0).getEOJ().isMemberOf(new ClassEOJ("0011")));
            assertTrue(list.get(1).getEOJ().isMemberOf(new ClassEOJ("0011")));
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetDeviceObjects() {
        try {
            LocalObjectManager manager = new LocalObjectManager();
            assertEquals(0, manager.size());

            manager.add(new LocalObject(new TemperatureSensorInfo()));
            manager.add(new LocalObject(new NodeProfileInfo()));
            manager.add(new LocalObject(new TemperatureSensorInfo()));
            manager.add(new LocalObject(new HomeAirConditionerInfo()));
            assertEquals(4, manager.size());
            List<LocalObject> list = manager.getDeviceObjects();
            assertEquals(3, list.size());
            assertTrue(list.get(0).getEOJ().isMemberOf(new ClassEOJ("0011")));
            assertTrue(list.get(1).getEOJ().isMemberOf(new ClassEOJ("0011")));
            assertTrue(list.get(2).getEOJ().isMemberOf(new ClassEOJ("0130")));
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
    }
}
