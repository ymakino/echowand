package echowand.net;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class LocalNetworkTest {
    
    @Test
    public void creationTest() {
        LocalNetwork network1 = new LocalNetwork();
        LocalNetwork network2 = new LocalNetwork();
        assertFalse(network1.equals(network2));
        assertFalse(network1.equals(LocalNetwork.getDefault()));
        assertFalse(network2.equals(LocalNetwork.getDefault()));
    }
    
    @Test
    public void nameTest() {
        LocalNetwork defaultNetwork = LocalNetwork.getDefault();
        assertEquals(defaultNetwork, LocalNetwork.getDefault());
        
        LocalNetwork t1 = LocalNetwork.getByName("TEST1");
        assertEquals(t1, LocalNetwork.getByName("TEST1"));
        LocalNetwork t2 = LocalNetwork.getByName("TEST2");
        assertEquals(t2, LocalNetwork.getByName("TEST2"));
        assertFalse(t2.equals(LocalNetwork.getByName("TEST1")));
    }
    
    @Test
    public void broadcastTest() throws SubnetException {
        LocalNetwork network = new LocalNetwork();
        LocalNetworkPort port1 = new LocalNetworkPort();
        LocalNetworkPort port2 = new LocalNetworkPort();
        
        network.addPort(port1);
        network.addPort(port2);
        
        Frame sendFrame = new Frame(null, null, new CommonFrame());
        network.broadcast(sendFrame);
        
        Frame recvFrame1 = port1.recvNoWait();
        assertEquals(sendFrame.getReceiver(), recvFrame1.getReceiver());
        assertEquals(sendFrame.getSender(), recvFrame1.getSender());
        assertTrue(sendFrame.getCommonFrame().toString().equals(recvFrame1.getCommonFrame().toString()));
        assertTrue(port1.recvNoWait() == null);
        
        Frame recvFrame2 = port2.recvNoWait();
        assertEquals(sendFrame.getReceiver(), recvFrame2.getReceiver());
        assertEquals(sendFrame.getSender(), recvFrame2.getSender());
        assertTrue(sendFrame.getCommonFrame().toString().equals(recvFrame2.getCommonFrame().toString()));
        assertTrue(port2.recvNoWait() == null);
    }
}
