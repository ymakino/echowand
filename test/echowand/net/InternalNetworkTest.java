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
public class InternalNetworkTest {
    
    @Test
    public void creationTest() {
        InternalNetwork network1 = new InternalNetwork();
        InternalNetwork network2 = new InternalNetwork();
        assertFalse(network1.equals(network2));
        assertFalse(network1.equals(InternalNetwork.getDefault()));
        assertFalse(network2.equals(InternalNetwork.getDefault()));
    }
    
    @Test
    public void nameTest() {
        InternalNetwork defaultNetwork = InternalNetwork.getDefault();
        assertEquals(defaultNetwork, InternalNetwork.getDefault());
        
        InternalNetwork t1 = InternalNetwork.getByName("TEST1");
        assertEquals(t1, InternalNetwork.getByName("TEST1"));
        InternalNetwork t2 = InternalNetwork.getByName("TEST2");
        assertEquals(t2, InternalNetwork.getByName("TEST2"));
        assertFalse(t2.equals(InternalNetwork.getByName("TEST1")));
    }
    
    @Test
    public void broadcastTest() throws SubnetException {
        InternalNetwork network = new InternalNetwork();
        InternalNetworkPort port1 = new InternalNetworkPort();
        InternalNetworkPort port2 = new InternalNetworkPort();
        
        network.addPort(port1);
        network.addPort(port2);
        
        Frame sendFrame = new Frame(null, null, new CommonFrame());
        network.broadcast(sendFrame);
        
        Frame receivedFrame1 = port1.receiveNoWait();
        assertEquals(sendFrame.getReceiver(), receivedFrame1.getReceiver());
        assertEquals(sendFrame.getSender(), receivedFrame1.getSender());
        assertTrue(sendFrame.getCommonFrame().toString().equals(receivedFrame1.getCommonFrame().toString()));
        assertTrue(port1.receiveNoWait() == null);
        
        Frame receivedFrame2 = port2.receiveNoWait();
        assertEquals(sendFrame.getReceiver(), receivedFrame2.getReceiver());
        assertEquals(sendFrame.getSender(), receivedFrame2.getSender());
        assertTrue(sendFrame.getCommonFrame().toString().equals(receivedFrame2.getCommonFrame().toString()));
        assertTrue(port2.receiveNoWait() == null);
    }
}
