package echowand.object;

import echowand.net.CommonFrame;
import echowand.net.Property;
import echowand.net.Frame;
import echowand.net.StandardPayload;
import echowand.net.LocalSubnet;
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
        LocalSubnet subnet = new LocalSubnet();
        RemoteObjectManager manager = new RemoteObjectManager();
        TransactionManager transactionManager = new TransactionManager(subnet);
        NodeProfileObjectListener profileListener = new NodeProfileObjectListener(manager, transactionManager);

        try {
            SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
            transactionConfig.setSenderNode(subnet.getLocalNode());
            transactionConfig.setReceiverNode(subnet.getGroupNode());
            transactionConfig.setSourceEOJ(new EOJ("0ef001"));
            transactionConfig.setDestinationEOJ(new EOJ("0ef001"));
            transactionConfig.addGet(EPC.x80);
            Transaction transaction = transactionManager.createTransaction(transactionConfig);
            transaction.execute();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        CommonFrame cf = new CommonFrame(new EOJ("0ef001"), new EOJ("0ef001"), ESV.Get_Res);
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        payload.addFirstProperty(new Property(EPC.xD6,
                        new Data(new byte[]{(byte)0x02, (byte)0x0e, (byte)0xf0, (byte)0x01,
                                                        (byte)0x00, (byte)0x11, (byte)0x01})));
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), cf);
        profileListener.receive(transaction, subnet, frame);
        assertTrue(manager.get(subnet.getLocalNode(), new EOJ("0ef001")) != null);
        assertTrue(manager.get(subnet.getLocalNode(), new EOJ("001101")) != null);
    }
}
