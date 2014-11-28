package echowand.logic;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.StandardPayload;
import echowand.net.Node;
import echowand.net.InternalSubnet;
import echowand.net.SubnetException;
import echowand.net.Subnet;
import echowand.common.ESV;
import echowand.logic.TransactionManager;
import echowand.logic.TransactionConfig;
import echowand.logic.Transaction;
import echowand.common.EOJ;
import echowand.object.LocalObjectManager;
import echowand.object.LocalObject;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.SetGetTransactionConfig;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class TransactionManagerTest {
    
    public Frame receiveWithoutError(Subnet subnet) {
        try {
            return subnet.receive();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
            return null;
        }
    }
    
    @Test
    public void testGetResponse() {
        InternalSubnet subnet = new InternalSubnet();
        Node local = subnet.getLocalNode();
        Node group = subnet.getGroupNode();
        CommonFrame frame = new CommonFrame();
        StandardPayload payload = new StandardPayload();
        payload.setSEOJ(new EOJ("002201"));
        payload.setDEOJ(new EOJ("002301"));
        payload.setESV(ESV.Get_Res);
        frame.setEDATA(payload);
        frame.setTID((short)10);
        TransactionManager transactionManager = new TransactionManager(subnet);
        assertFalse(transactionManager.process(subnet, new Frame(local, group, frame), false));
    }
    
    @Test
    public void testGetResponseSNA() {
        InternalSubnet subnet = new InternalSubnet();
        Node local = subnet.getLocalNode();
        Node group = subnet.getGroupNode();
        CommonFrame frame = new CommonFrame();
        StandardPayload payload = new StandardPayload();
        payload.setSEOJ(new EOJ("002201"));
        payload.setDEOJ(new EOJ("002301"));
        payload.setESV(ESV.Get_SNA);
        frame.setEDATA(payload);
        TransactionManager transactionManager = new TransactionManager(subnet);
        assertFalse(transactionManager.process(subnet, new Frame(local, group, frame), false));
    }
    
    @Test
    public void testCreateTransaction() {
        InternalSubnet subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        TransactionConfig config = new SetGetTransactionConfig();
        Transaction t = transactionManager.createTransaction(config);
        assertTrue(t != null);
        assertEquals(config, t.getTransactionConfig());
    }
}
