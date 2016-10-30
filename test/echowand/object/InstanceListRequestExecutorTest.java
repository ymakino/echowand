package echowand.object;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.logic.TransactionManager;
import echowand.net.*;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author ymakino
 */
public class InstanceListRequestExecutorTest {
    Subnet subnet;
    private InstanceListRequestExecutor executor;
    
    @Before
    public void setUp() throws Exception {
        subnet = new InternalSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        executor = new InstanceListRequestExecutor(subnet, transactionManager, remoteManager);
    }
    
    @Test
    public void testUpdate() {
        try {
            assertFalse(executor.join());
            assertTrue(executor.execute());
            assertFalse(executor.execute());
        } catch (SubnetException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
        
        try {
            checkFrame(subnet);
        } catch (SubnetException ex) {
            fail();
        }
        
        try {
            assertTrue(executor.join());
            assertFalse(executor.join());
        } catch (InterruptedException e) {
            fail();
        }
    }
    
    @Test
    public void testUpdateAndJoin() {
        try {
            assertFalse(executor.join());
            
            assertTrue(executor.execute());
            assertTrue(executor.join());
            
            assertFalse(executor.execute());
            assertFalse(executor.join());
        } catch (SubnetException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
        
        try {
            checkFrame(subnet);
        } catch (SubnetException ex) {
            fail();
        }
        
        try {
            assertFalse(executor.join());
        } catch (InterruptedException e) {
            fail();
        }
    }
    
    public void checkFrame(Subnet subnet) throws SubnetException {
        Frame frame = subnet.receive();
        assertEquals(frame.getReceiver(), subnet.getGroupNode());
        CommonFrame commonFrame = frame.getCommonFrame();
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
        assertEquals(ESV.Get, payload.getESV());
        assertEquals(new EOJ("0ef001"), payload.getDEOJ());
        assertEquals((byte) 0x01, payload.getFirstOPC());
        assertEquals(EPC.xD6, payload.getFirstPropertyAt(0).getEPC());
        assertEquals((byte) 0x00, payload.getFirstPropertyAt(0).getPDC());
    }
    
    public void doTestExecutorReceive(boolean shouldReceive) throws SubnetException, InterruptedException {
        assertTrue(executor.execute());
        
        StandardPayload payload = new StandardPayload(new EOJ("0ef001"), new EOJ("0ef001"), ESV.Get);
        payload.addFirstProperty(new Property(EPC.x80));
        CommonFrame commonFrame = new CommonFrame();
        commonFrame.setEDATA(payload);
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getGroupNode(), commonFrame);
        
        subnet.send(frame);
        
        Thread.sleep(200);
        
        assertTrue(executor.join());
        
        if (shouldReceive) {
            assertNotEquals(frame.toString(), subnet.receive().toString());
        } else {
            assertEquals(frame.toString(), subnet.receive().toString());
        }
    }
    
    @Test
    public void testExecutorReceiveWithDummyNode() throws SubnetException, InterruptedException {
        executor.setTimeout(500);
        executor.setNode(subnet.getRemoteNode("dummy"));
        doTestExecutorReceive(false);
    }
    
    @Test
    public void testExecutorReceiveWithLocalNode() throws SubnetException, InterruptedException {
        executor.setTimeout(500);
        executor.setNode(subnet.getLocalNode());
        doTestExecutorReceive(true);
    }
    
    @Test
    public void testExecutorReceiveWithGroupNode() throws SubnetException, InterruptedException {
        executor.setTimeout(500);
        executor.setNode(subnet.getGroupNode());
        doTestExecutorReceive(true);
    }
    
    @Test
    public void testExecutorReceiveWithNullNode() throws SubnetException, InterruptedException {
        executor.setTimeout(500);
        executor.setNode(null);
        doTestExecutorReceive(true);
    }
    
    @Test
    public void testExecutorReceiveWithDefaultNode() throws SubnetException, InterruptedException {
        executor.setTimeout(500);
        doTestExecutorReceive(true);
    }
    
    @Test
    public void testTimeoutDefault() {
        doTestTimeout(2000, true);
    }
    
    @Test
    public void testTimeout500() {
        doTestTimeout(500, false);
    }
    
    @Test
    public void testTimeout2000() {
        doTestTimeout(2000, false);
    }
    
    @Test
    public void testTimeout3000() {
        doTestTimeout(3000, false);
    }
    
    private long time() {
        return GregorianCalendar.getInstance().getTimeInMillis();
    }
    
    public void doTestTimeout(int timeout, boolean useDefault) {
        
        try {
            System.out.println("doTestTimeout timeout: " + timeout + " default: " + useDefault);
            if (!useDefault) {
                executor.setTimeout(timeout);
            }
            
            assertTrue(executor.execute());
            long t1 = time();
            
            for (;;) {
                long t2 = time();
                if (executor.isDone()) {
                    break;
                } else {
                    assertTrue(timeout > (t2 - t1));
                }
                Thread.sleep(100);
            }
            
            long t3 = time();
            
            assertTrue(timeout < (t3 - t1));
            
            
            assertTrue(executor.join());
            
            assertFalse(executor.execute());
            assertFalse(executor.join());
        } catch (SubnetException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }
}
