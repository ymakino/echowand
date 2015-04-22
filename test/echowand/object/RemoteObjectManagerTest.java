package echowand.object;

import echowand.object.EchonetObjectException;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.InternalSubnet;
import echowand.net.SubnetException;
import echowand.net.Subnet;
import echowand.common.EOJ;
import echowand.common.ClassEOJ;
import echowand.common.ESV;
import echowand.common.EPC;
import echowand.common.Data;
import echowand.object.RemoteObjectManager;
import echowand.logic.Listener;
import echowand.logic.TransactionManager;
import echowand.object.RemoteObject;
import echowand.util.Collector;
import echowand.util.Selector;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class RemoteObjectManagerTest {
    
    class ResponseThread extends Thread {

        Subnet subnet;
        Listener listener;

        public ResponseThread(Subnet subnet, Listener listener) {
            this.subnet = subnet;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                Frame receivedFrame = subnet.receive();
                CommonFrame receivedCommonFrame = receivedFrame.getCommonFrame();
                StandardPayload receivedPayload = (StandardPayload) receivedCommonFrame.getEDATA();

                CommonFrame sendCommonFrame = new CommonFrame(receivedPayload.getDEOJ(), receivedPayload.getSEOJ(), ESV.Get_Res);
                sendCommonFrame.setTID(receivedCommonFrame.getTID());
                StandardPayload sendPayload = (StandardPayload) sendCommonFrame.getEDATA();
                sendPayload.addFirstProperty(new Property(EPC.xE0, new Data((byte) 0x12, (byte) 0x34)));
                Frame sendFrame = new Frame(subnet.getLocalNode(), receivedFrame.getSender(), sendCommonFrame);
                
                this.listener.process(subnet, sendFrame, false);
            } catch (SubnetException e) {
                e.printStackTrace();
                fail();
            }
        }
    }

    @Test
    public void testCreateRemoteObject() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObjectManager manager = new RemoteObjectManager();
        RemoteObject object = manager.get(subnet.getLocalNode(), new EOJ("001101"));
        assertTrue(object == null);
    }
    
    @Test
    public void testCreationAndGetRemoteObject() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObjectManager manager = new RemoteObjectManager();
        RemoteObject object = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        manager.add(object);
        RemoteObject o = manager.get(subnet.getLocalNode(), new EOJ("001101"));;
        assertEquals(object, o);
        try {
            new ResponseThread(subnet, transactionManager).start();
            o.getData(EPC.xE0);
        } catch (EchonetObjectException e) {
            e.printStackTrace();
            fail();
        }
        
        RemoteObject object2 = manager.get(subnet.getLocalNode(), new EOJ("001101"));
        assertEquals(object, object2);
        
        manager.remove(object);
        object2 = manager.get(subnet.getLocalNode(), new EOJ("001101"));
        assertTrue(object2 == null);
    }
    
    public void testAdd() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObjectManager manager = new RemoteObjectManager();
        RemoteObject object1 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        assertTrue(manager.add(object1));
        assertFalse(manager.add(object1));
        RemoteObject object2 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001102"), transactionManager);
        assertTrue(manager.add(object2));
    }
    
    @Test
    public void testUpdateObjects() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObjectManager manager = new RemoteObjectManager();
        
        RemoteObject object1 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        assertTrue(manager.add(object1));
        RemoteObject object2 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        assertFalse(manager.add(object2));
        assertTrue(manager.remove(object1));
        assertFalse(manager.remove(object2));
        RemoteObject object3 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        assertTrue(manager.add(object3));
        
        RemoteObject object4 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001201"), transactionManager);
        assertTrue(manager.add(object4));
        RemoteObject object5 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001201"), transactionManager);
        assertFalse(manager.add(object5));
        
        assertEquals(object3, manager.get(subnet.getLocalNode(), new EOJ("001101")));
        assertEquals(null, manager.get(subnet.getLocalNode(), new EOJ("001102")));
        assertEquals(object4, manager.get(subnet.getLocalNode(), new EOJ("001201")));
        assertEquals(null, manager.get(subnet.getLocalNode(), new EOJ("001202")));
    }

    @Test
    public void testGetWithSelector() {
        final InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObjectManager manager = new RemoteObjectManager();
        RemoteObject object1 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        manager.add(object1);
        RemoteObject object2 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001102"), transactionManager);
        manager.add(object2);
        RemoteObject object3 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001103"), transactionManager);
        manager.add(object3);
        RemoteObject object4 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001201"), transactionManager);
        manager.add(object4);
        RemoteObject object5 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001202"), transactionManager);
        manager.add(object5);

        List<RemoteObject> list1 = manager.get(new Selector<RemoteObject>() {
            @Override
            public boolean match(RemoteObject object) {
                return object.getEOJ().getClassEOJ().equals(new ClassEOJ("0011"));
            }
        });
        assertEquals(3, list1.size());
        
        List<RemoteObject> list2 = manager.get(new Selector<RemoteObject>() {
            @Override
            public boolean match(RemoteObject object) {
                return object.getEOJ().getClassEOJ().equals(new ClassEOJ("0012"));
            }
        });
        assertEquals(2, list2.size());
        
        List<RemoteObject> list3 = manager.get(new Selector<RemoteObject>() {
            @Override
            public boolean match(RemoteObject object) {
                return object.getNode().equals(subnet.getLocalNode());
            }
        });
        assertEquals(5, list3.size());
    }
    
    @Test
    public void testGetAtNode() {
        final InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObjectManager manager = new RemoteObjectManager();
        
        RemoteObject object1 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        manager.add(object1);
        RemoteObject object2 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001102"), transactionManager);
        manager.add(object2);
        
        assertEquals(0, manager.getAtNode(subnet.getGroupNode()).size());
        assertEquals(2, manager.getAtNode(subnet.getLocalNode()).size());
    }
    
    @Test
    public void testGetNodes() {
        final InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObjectManager manager = new RemoteObjectManager();
        
        assertTrue(manager.getNodes().isEmpty());
        
        RemoteObject object1 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
        manager.add(object1);
        
        assertEquals(1, manager.getNodes().size());
        
        RemoteObject object2 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001102"), transactionManager);
        manager.add(object2);
        
        assertEquals(1, manager.getNodes().size());
        
        assertEquals(subnet.getLocalNode(), manager.getNodes().get(0));
    }
}
