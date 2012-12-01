/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class LocalNetworkPortTest {
    
    @Test
    public void setGetNetworkTest() {
        LocalNetworkPort port = new LocalNetworkPort();
        
        LocalNetwork network = new LocalNetwork();
        port.setNetwork(network);
        assertEquals(network, port.getNetwork());
    }
    
    @Test
    public void enqueueAndRecvTest() throws SubnetException {
        LocalNetworkPort port = new LocalNetworkPort();
        
        Frame frame = new Frame(null, null, new CommonFrame());
        port.enqueue(frame);
        assertEquals(frame.getCommonFrame().toString(), port.recv().getCommonFrame().toString());
        assertTrue(port.recvNoWait() == null);
    }
    
    @Test
    public void recvNoWaitTest() throws SubnetException {
        LocalNetworkPort port = new LocalNetworkPort();
        
        Frame frame = new Frame(null, null, new CommonFrame());
        port.enqueue(frame);
        assertEquals(frame.getCommonFrame().toString(), port.recvNoWait().getCommonFrame().toString());
        assertTrue(port.recvNoWait() == null);
    }
    
    @Test
    public void sendTest() throws SubnetException {
        LocalNetworkPort port1 = new LocalNetworkPort();
        LocalNetworkPort port2 = new LocalNetworkPort();
        
        Frame frame = new Frame(null, null, new CommonFrame());
        port1.send(frame);
        assertTrue(port1.recvNoWait() == null);
        
        LocalNetwork network = new LocalNetwork();
        network.addPort(port1);
        network.addPort(port2);
        
        port1.send(frame);
        assertEquals(frame.getCommonFrame().toString(), port1.recv().getCommonFrame().toString());
        assertTrue(port1.recvNoWait() == null);
        assertEquals(frame.getCommonFrame().toString(), port2.recv().getCommonFrame().toString());
        assertTrue(port2.recvNoWait() == null);
    }
}
