package echowand.app;

import echowand.common.EOJ;
import echowand.logic.TransactionManager;
import echowand.net.LocalSubnet;
import echowand.net.Node;
import echowand.net.Subnet;
import echowand.object.RemoteObject;
import echowand.object.RemoteObjectManager;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Yoshiki Makino
 */
public class NodeListModelTest {
    public static LocalSubnet subnet;
    public static Node node;
    public static TransactionManager transactionManager;
    
    public NodeListModelTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        subnet = new LocalSubnet();
        node = subnet.getLocalNode();
        transactionManager = new TransactionManager(subnet);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getSize method, of class NodeListModel.
     */
    @Test
    public void testGetSize() {
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        NodeListModel nodeListModel = new NodeListModel(remoteManager);
        assertEquals(0, nodeListModel.getSize());
        nodeListModel.updateNodes();
        assertEquals(0, nodeListModel.getSize());
    }
    
    @Test
    public void testVariableGetSize() {
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        NodeListModel nodeListModel = new NodeListModel(remoteManager);
        assertEquals(0, nodeListModel.getSize());
        
        remoteManager.add(new RemoteObject(subnet, node, new EOJ("001101"), transactionManager));
        assertEquals(0, nodeListModel.getSize());
        nodeListModel.updateNodes();
        assertEquals(1, nodeListModel.getSize());
        
        remoteManager.add(new RemoteObject(subnet, node, new EOJ("001102"), transactionManager));
        assertEquals(1, nodeListModel.getSize());
    }

    /**
     * Test of getElementAt method, of class NodeListModel.
     */
    @Test
    public void testGetElementAt() {
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        NodeListModel nodeListModel = new NodeListModel(remoteManager);
        
        remoteManager.add(new RemoteObject(subnet, node, new EOJ("001101"), transactionManager));
        nodeListModel.updateNodes();
        assertEquals(node, nodeListModel.getElementAt(0));
    }
}
