package echowand.logic;

import echowand.net.CommonFrame;
import echowand.net.Property;
import echowand.net.Frame;
import echowand.net.StandardPayload;
import echowand.net.InternalSubnet;
import echowand.net.SubnetException;
import echowand.net.Subnet;
import echowand.common.Data;
import echowand.common.ESV;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Inet4Subnet;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
class DummyTransactionConfig extends TransactionConfig {
    public ESV esv;
    public int count;
    
    public DummyTransactionConfig(ESV esv, int count) {
        this.esv = esv;
        this.count = count;
    }

    @Override
    public ESV getESV() {
        return esv;
    }

    @Override
    public int getCountPayloads() {
        return count;
    }

    @Override
    public void addPayloadProperties(int index, StandardPayload payload) {
        payload.addFirstProperty(new Property(EPC.x80));
    }
    
}
public class TransactionTest {
    public InternalSubnet subnet;
    public TransactionManager transactionManager;
    public TransactionManager failSendTransactionManager;
    public DummyTransactionConfig transactionConfig1;
    public DummyTransactionConfig transactionConfig2;
    public DummyTransactionConfig failSendTransactionConfig;
    public Inet4Subnet inet4Subnet;
    
    
    public InternalSubnet failSendSubnet;
    
    @Before
    public void setUp() throws SubnetException {
        subnet = new InternalSubnet();
        transactionManager = new TransactionManager(subnet);
        transactionConfig1 = new DummyTransactionConfig(ESV.Get, 1);
        transactionConfig1.setSenderNode(subnet.getLocalNode());
        transactionConfig1.setReceiverNode(subnet.getGroupNode());
        transactionConfig1.setSourceEOJ(new EOJ("001101"));
        transactionConfig1.setDestinationEOJ(new EOJ("0ef001"));
        
        transactionConfig2 = new DummyTransactionConfig(ESV.Get, 2);
        transactionConfig2.setSenderNode(subnet.getLocalNode());
        transactionConfig2.setReceiverNode(subnet.getGroupNode());
        transactionConfig2.setSourceEOJ(new EOJ("001101"));
        transactionConfig2.setDestinationEOJ(new EOJ("0ef001"));
        inet4Subnet = new Inet4Subnet();
        
        failSendSubnet = new InternalSubnet("failSendSubnet") {
            @Override
            public boolean send(Frame frame) throws SubnetException {
                super.send(frame);
                return false;
            }
        };
        failSendTransactionManager = new TransactionManager(failSendSubnet);
        failSendTransactionConfig = new DummyTransactionConfig(ESV.Get, 1);
        failSendTransactionConfig.setSenderNode(failSendSubnet.getLocalNode());
        failSendTransactionConfig.setReceiverNode(failSendSubnet.getGroupNode());
        failSendTransactionConfig.setSourceEOJ(new EOJ("001101"));
        failSendTransactionConfig.setDestinationEOJ(new EOJ("0ef001"));
    }
    
    @After
    public void tearDown() {
        inet4Subnet.stopService();
    }
    
    @Test
    public void testCreation() {
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);
        assertEquals(0, t.countResponses());
        assertEquals(0, t.countTransactionListeners());
        assertEquals(transactionConfig1, t.getTransactionConfig());
    }
    
    @Test
    public void testExecute() {
        try {
            Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);

            t.execute();
            
            Frame frame = subnet.receiveNoWait();
            assertTrue(frame != null);
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testExecuteMulti() {
        try {
            Transaction t = new Transaction(subnet, transactionManager, transactionConfig2);

            t.execute();
            
            Frame frame = subnet.receiveNoWait();
            assertTrue(frame != null);
            frame = subnet.receiveNoWait();
            assertTrue(frame != null);
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test(expected = SubnetException.class)
    public void testSetInvalidSender() throws SubnetException {
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);
        transactionConfig1.setSenderNode(inet4Subnet.getLocalNode());
        t.execute();
    }

    @Test(expected = SubnetException.class)
    public void testSetInvalidReceiver() throws SubnetException {
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);
        transactionConfig1.setReceiverNode(inet4Subnet.getLocalNode());
        t.execute();
    }

    public Frame createReplyFrame(Frame frame) {
        CommonFrame commonFrame = frame.getCommonFrame();
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
        
        CommonFrame replyCommonFrame = new CommonFrame(payload.getDEOJ(), payload.getSEOJ(), ESV.Get_Res);
        replyCommonFrame.setTID(commonFrame.getTID());
        StandardPayload replyPayload = replyCommonFrame.getEDATA(StandardPayload.class);
        int len = payload.getFirstOPC();
        for (int i=0; i<len; i++) {
            Property p = payload.getFirstPropertyAt(i);
            replyPayload.addFirstProperty(new Property(p.getEPC(),new Data((byte)0x30)));
        }
        
        return new Frame(subnet.getLocalNode(), frame.getSender(), replyCommonFrame);
    }
    
    @Test
    public void testSimpleTransaction() {
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);

        try {
            t.execute();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        assertTrue(t.isWaitingResponse());
        assertFalse(t.isDone());

        try {
            Frame reqFrame = subnet.receive();
            assertTrue(reqFrame != null);
            t.receiveResponse(createReplyFrame(reqFrame));
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        
        assertEquals(1, t.countResponses());
        assertFalse(t.isWaitingResponse());
        assertTrue(t.isDone());
    }
    
    @Test
    public void testInterrupt() {
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);

        try {
            t.execute();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        assertTrue(t.isWaitingResponse());
        assertFalse(t.isDone());

        try {
            Frame reqFrame = subnet.receive();
            assertTrue(reqFrame != null);
            t.finish();
            t.receiveResponse(createReplyFrame(reqFrame));
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        
        assertEquals(0, t.countResponses());
        assertFalse(t.isWaitingResponse());
        assertTrue(t.isDone());
    }
    
    @Test
    public void testAddAndRemoveListeners() {
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);
        
        TransactionListener tl1 = new TransactionListener(){
            @Override
            public void begin(Transaction t){}
            @Override
            public void send(Transaction t, Subnet subnet, Frame frame, boolean success){}
            @Override
            public void receive(Transaction t, Subnet subnet, Frame frame){}
            @Override
            public void finish(Transaction t){}
        };
        TransactionListener tl2 = new TransactionListener(){
            @Override
            public void begin(Transaction t){}
            @Override
            public void send(Transaction t, Subnet subnet, Frame frame, boolean success){}
            @Override
            public void receive(Transaction t, Subnet subnet, Frame frame){}
            @Override
            public void finish(Transaction t){}
        };
        assertEquals(0, t.countTransactionListeners());
        t.addTransactionListener(tl1);
        assertEquals(1, t.countTransactionListeners());
        t.addTransactionListener(tl2);
        assertEquals(2, t.countTransactionListeners());
        t.removeTransactionListener(tl1);
        assertEquals(1, t.countTransactionListeners());
        t.removeTransactionListener(tl2);
        assertEquals(0, t.countTransactionListeners());
    }
    
    public boolean testCallbackBegan = false;
    public int testCallbackSentSuccess = 0;
    public int testCallbackSentCount = 0;
    public int testCallbackReceiveCount = 0;
    public boolean testCallbackFinished = false;
    
    @Test
    public void testCallback() {
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);
        
        testCallbackBegan = false;
        testCallbackSentCount = 0;
        testCallbackReceiveCount = 0;
        testCallbackFinished = false;
        TransactionListener tl = new TransactionListener(){
            @Override
            public void begin(Transaction t){testCallbackBegan = true;}
            @Override
            public void send(Transaction t, Subnet subnet, Frame frame, boolean success){
                if (success) { testCallbackSentSuccess++; }
                testCallbackSentCount++;
            }
            @Override
            public void receive(Transaction t, Subnet subnet, Frame frame){testCallbackReceiveCount++;}
            @Override
            public void finish(Transaction t){testCallbackFinished = true;}
        };
        t.addTransactionListener(tl);

        try {
            assertFalse(testCallbackBegan);
            t.execute();
            assertTrue(testCallbackBegan);
            assertFalse(testCallbackFinished);
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        try {
            Frame reqFrame = subnet.receive();
            Frame resFrame1 = createReplyFrame(reqFrame);
            t.receiveResponse(resFrame1);
            Frame resFrame2 = createReplyFrame(reqFrame);
            t.receiveResponse(resFrame2);
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        
        assertEquals(1, testCallbackSentSuccess);
        assertEquals(1, testCallbackSentCount);
        assertEquals(2, testCallbackReceiveCount);
        assertTrue(testCallbackFinished);
    }
    
    @Test
    public void testFailSendCallback() {
        Transaction t = new Transaction(failSendSubnet, failSendTransactionManager, failSendTransactionConfig);
        
        testCallbackBegan = false;
        testCallbackSentSuccess = 0;
        testCallbackSentCount = 0;
        testCallbackReceiveCount = 0;
        testCallbackFinished = false;
        TransactionListener tl = new TransactionListener(){
            @Override
            public void begin(Transaction t){testCallbackBegan = true;}
            @Override
            public void send(Transaction t, Subnet subnet, Frame frame, boolean success){
                if (success) { testCallbackSentSuccess++; }
                testCallbackSentCount++;
            }
            @Override
            public void receive(Transaction t, Subnet subnet, Frame frame){testCallbackReceiveCount++;}
            @Override
            public void finish(Transaction t){testCallbackFinished = true;}
        };
        t.addTransactionListener(tl);

        try {
            assertFalse(testCallbackBegan);
            t.execute();
            assertTrue(testCallbackBegan);
            assertFalse(testCallbackFinished);
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        try {
            Frame reqFrame = failSendSubnet.receive();
            Frame resFrame1 = createReplyFrame(reqFrame);
            t.receiveResponse(resFrame1);
            Frame resFrame2 = createReplyFrame(reqFrame);
            t.receiveResponse(resFrame2);
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        
        assertEquals(0, testCallbackSentSuccess);
        assertEquals(1, testCallbackSentCount);
        assertEquals(2, testCallbackReceiveCount);
        assertTrue(testCallbackFinished);
    }
    
    @Test
    public void testTID() {
        Transaction t1 = new Transaction(subnet, transactionManager, transactionConfig1);
        Transaction t2 = new Transaction(subnet, transactionManager, transactionConfig1);
        assertTrue(t1.getTID() != t2.getTID());
    }
    
    @Test
    public void testTimeout() {
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);
        t.setTimeout(1000);
        try {
            t.execute();
            Thread.sleep(200);
            assertFalse(t.isDone());
            assertTrue(t.isWaitingResponse());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testTimeoutExpired() {
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);
        t.setTimeout(100);
        try {
            t.execute();
            Thread.sleep(200);
            assertTrue(t.isDone());
            assertFalse(t.isWaitingResponse());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testAllInstance() {
        transactionConfig1.setDestinationEOJ(new EOJ("001100"));
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);
        
        try {
            t.execute();
            Frame reqFrame = subnet.receive();
            assertTrue(reqFrame != null);
            Frame repFrame = createReplyFrame(reqFrame);
            CommonFrame cf = repFrame.getCommonFrame();
            StandardPayload payload = cf.getEDATA(StandardPayload.class);
            payload.setSEOJ(new EOJ("001101"));
            t.receiveResponse(repFrame);
            payload.setSEOJ(new EOJ("001102"));
            t.receiveResponse(repFrame);
            payload.setSEOJ(new EOJ("001103"));
            t.receiveResponse(repFrame);
            t.finish();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        assertEquals(3, t.countResponses());
    }
    
    @Test
    public void testManyInvalidResponse() {
        transactionConfig1.setDestinationEOJ(new EOJ("001102"));
        Transaction t = new Transaction(subnet, transactionManager, transactionConfig1);
        
        try {
            t.execute();
            Frame reqFrame = subnet.receive();
            assertTrue(reqFrame != null);
            Frame repFrame = createReplyFrame(reqFrame);
            CommonFrame cf = repFrame.getCommonFrame();
            StandardPayload payload = cf.getEDATA(StandardPayload.class);
            payload.setSEOJ(new EOJ("001101"));
            t.receiveResponse(repFrame);
            payload.setSEOJ(new EOJ("001102"));
            t.receiveResponse(repFrame);
            payload.setSEOJ(new EOJ("001103"));
            t.receiveResponse(repFrame);
            t.finish();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        assertEquals(1, t.countResponses());
    }
}
