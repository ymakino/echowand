package echowand.app;

import echowand.common.EOJ;
import echowand.logic.TransactionManager;
import echowand.net.InternalSubnet;
import echowand.net.Node;
import echowand.object.RemoteObject;
import echowand.object.RemoteObjectManager;
import java.util.LinkedList;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectListModelTest {
    public static InternalSubnet subnet;
    public static Node node;
    public static TransactionManager transactionManager;
    
    public ObjectListModelTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        subnet = new InternalSubnet();
        node = subnet.getLocalNode();
        transactionManager = new TransactionManager(subnet);
    }

    /**
     * Test of getSize method, of class ObjectListModel.
     */
    @Test
    public void testGetSize() {
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        ObjectListModel objectListModel = new ObjectListModel(remoteManager);
        assertEquals(0, objectListModel.getSize());
        objectListModel.updateObjects();
        assertEquals(0, objectListModel.getSize());
        
        remoteManager.add(new  RemoteObject(subnet, node, new EOJ("001101"), transactionManager));
        assertEquals(0, objectListModel.getSize());
        objectListModel.updateObjects();
        assertEquals(0, objectListModel.getSize());
    }
    
    @Test
    public void testSelectNodes() {
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        ObjectListModel objectListModel = new ObjectListModel(remoteManager);
        objectListModel.selectNodes(new LinkedList<Node>());
        assertEquals(0, objectListModel.getSize());
        
        remoteManager.add(new  RemoteObject(subnet, node, new EOJ("001101"), transactionManager));
        assertEquals(0, objectListModel.getSize());
        
        LinkedList<Node> nodes = new LinkedList<Node>();
        nodes.add(node);
        objectListModel.selectNodes(nodes);
        assertEquals(1, objectListModel.getSize());
        
        remoteManager.add(new  RemoteObject(subnet, node, new EOJ("001102"), transactionManager));
        assertEquals(1, objectListModel.getSize());
        objectListModel.updateObjects();
        assertEquals(2, objectListModel.getSize());
        
        objectListModel.selectNodes(new LinkedList<Node>());
        assertEquals(0, objectListModel.getSize());
    }

    /**
     * Test of getElementAt method, of class ObjectListModel.
     */
    @Test
    public void testGetElementAt() {
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        ObjectListModel objectListModel = new ObjectListModel(remoteManager);
        
        RemoteObject object1 = new  RemoteObject(subnet, node, new EOJ("001101"), transactionManager);
        remoteManager.add(object1);
        objectListModel.updateObjects();
        
        LinkedList<Node> nodes = new LinkedList<Node>();
        nodes.add(node);
        objectListModel.selectNodes(nodes);
        assertEquals(object1, objectListModel.getElementAt(0));
        
        RemoteObject object2 = new  RemoteObject(subnet, node, new EOJ("001102"), transactionManager);
        remoteManager.add(object2);
        objectListModel.updateObjects();
        
        objectListModel.selectNodes(nodes);
        if (objectListModel.getElementAt(0).equals(object1)) {
            assertEquals(object2, objectListModel.getElementAt(1));
        } else {
            assertEquals(object2, objectListModel.getElementAt(0));
        }
    }
}
