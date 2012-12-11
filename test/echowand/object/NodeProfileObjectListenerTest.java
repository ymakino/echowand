package echowand.object;

import echowand.net.CommonFrame;
import echowand.net.Property;
import echowand.net.Frame;
import echowand.net.StandardPayload;
import echowand.net.InternalSubnet;
import echowand.net.SubnetException;
import echowand.common.Data;
import echowand.common.ESV;
import echowand.common.EPC;
import echowand.common.EOJ;
import echowand.logic.SetGetTransactionConfig;
import echowand.logic.Transaction;
import echowand.logic.TransactionManager;
import echowand.object.NodeProfileObjectListener;
import echowand.object.RemoteObjectManager;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class NodeProfileObjectListenerTest {

    /**
     * Test of begin method, of class NodeProfileObjectListener.
     */
    @Test
    public void testTransaction() {
        InternalSubnet subnet = new InternalSubnet();
        RemoteObjectManager manager = new RemoteObjectManager();
        TransactionManager transactionManager = new TransactionManager(subnet);
        NodeProfileObjectListener profileListener = new NodeProfileObjectListener(manager, transactionManager);
        
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        CommonFrame cf = new CommonFrame(new EOJ("0ef001"), new EOJ("0ef001"), ESV.Get_Res);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.xD6,
                        new Data(new byte[]{(byte)0x02, (byte)0x00, (byte)0x12, (byte)0x01,
                                                        (byte)0x00, (byte)0x11, (byte)0x01})));
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        profileListener.receive(transaction, subnet, frame);
        assertTrue(manager.get(subnet.getLocalNode(), new EOJ("0ef001")) != null);
        assertTrue(manager.get(subnet.getLocalNode(), new EOJ("001101")) != null);
        assertTrue(manager.get(subnet.getLocalNode(), new EOJ("001201")) != null);
    }
    
    @Test
    public void testINF_SNA() {
        InternalSubnet subnet = new InternalSubnet();
        RemoteObjectManager manager = new RemoteObjectManager();
        TransactionManager transactionManager = new TransactionManager(subnet);
        NodeProfileObjectListener profileListener = new NodeProfileObjectListener(manager, transactionManager);
        
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        CommonFrame cf = new CommonFrame(new EOJ("0ef001"), new EOJ("0ef001"), ESV.Get_SNA);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.xD6));
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        profileListener.receive(transaction, subnet, frame);
        assertTrue(manager.get(subnet.getLocalNode(), new EOJ("0ef001")) != null);
        assertEquals(1, manager.getAtNode(subnet.getLocalNode()).size());
    }
    
    @Test
    public void testAdditionOfSameEOJs() {
        InternalSubnet subnet = new InternalSubnet();
        RemoteObjectManager manager = new RemoteObjectManager();
        TransactionManager transactionManager = new TransactionManager(subnet);
        NodeProfileObjectListener profileListener = new NodeProfileObjectListener(manager, transactionManager);
        
        SetGetTransactionConfig transactionConfig1 = new SetGetTransactionConfig();
        Transaction transaction1 = transactionManager.createTransaction(transactionConfig1);
        CommonFrame cf1 = new CommonFrame(new EOJ("0ef001"), new EOJ("0ef001"), ESV.Get_Res);
        StandardPayload payload1 = (StandardPayload)cf1.getEDATA();
        payload1.addFirstProperty(new Property(EPC.xD6,
                        new Data(new byte[]{(byte)0x02, (byte)0x00, (byte)0x12, (byte)0x01,
                                                        (byte)0x00, (byte)0x11, (byte)0x01})));
        Frame frame1 = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf1);
        profileListener.receive(transaction1, subnet, frame1);
        
        RemoteObject object1 = manager.get(subnet.getLocalNode(), new EOJ("0ef001"));
        RemoteObject object2 = manager.get(subnet.getLocalNode(), new EOJ("001101"));
        RemoteObject object3 = manager.get(subnet.getLocalNode(), new EOJ("001201"));
        
        
        SetGetTransactionConfig transactionConfig2 = new SetGetTransactionConfig();
        Transaction transaction2 = transactionManager.createTransaction(transactionConfig2);
        CommonFrame cf2 = new CommonFrame(new EOJ("0ef001"), new EOJ("0ef001"), ESV.Get_Res);
        StandardPayload payload2 = (StandardPayload)cf2.getEDATA();
        payload2.addFirstProperty(new Property(EPC.xD6,
                        new Data(new byte[]{(byte)0x02, (byte)0x00, (byte)0x12, (byte)0x01,
                                                        (byte)0x00, (byte)0x11, (byte)0x01})));
        Frame frame2 = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf2);
        profileListener.receive(transaction2, subnet, frame2);
        
        assertTrue(object1 == manager.get(subnet.getLocalNode(), new EOJ("0ef001")));
        assertTrue(object2 == manager.get(subnet.getLocalNode(), new EOJ("001101")));
        assertTrue(object3 == manager.get(subnet.getLocalNode(), new EOJ("001201")));
    }
}