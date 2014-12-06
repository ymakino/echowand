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
public class InternalNetworkPortTest {
    
    @Test
    public void setGetNetworkTest() {
        InternalNetworkPort port = new InternalNetworkPort();
        
        InternalNetwork network = new InternalNetwork();
        port.setNetwork(network);
        assertEquals(network, port.getNetwork());
    }
    
    @Test
    public void enqueueAndReceiveTest() throws SubnetException {
        InternalNetworkPort port = new InternalNetworkPort();
        
        Frame frame = new Frame(null, null, new CommonFrame());
        port.enqueue(frame);
        assertEquals(frame.getCommonFrame().toString(), port.receive().getCommonFrame().toString());
        assertTrue(port.receiveNoWait() == null);
    }
    
    @Test
    public void receiveNoWaitTest() throws SubnetException {
        InternalNetworkPort port = new InternalNetworkPort();
        
        Frame frame = new Frame(null, null, new CommonFrame());
        port.enqueue(frame);
        assertEquals(frame.getCommonFrame().toString(), port.receiveNoWait().getCommonFrame().toString());
        assertTrue(port.receiveNoWait() == null);
    }
    
    @Test
    public void sendTest() throws SubnetException {
        InternalNetworkPort port1 = new InternalNetworkPort();
        InternalNetworkPort port2 = new InternalNetworkPort();
        
        Frame frame = new Frame(null, null, new CommonFrame());
        port1.send(frame);
        assertTrue(port1.receiveNoWait() == null);
        
        InternalNetwork network = new InternalNetwork();
        network.addPort(port1);
        network.addPort(port2);
        
        port1.send(frame);
        assertEquals(frame.getCommonFrame().toString(), port1.receive().getCommonFrame().toString());
        assertTrue(port1.receiveNoWait() == null);
        assertEquals(frame.getCommonFrame().toString(), port2.receive().getCommonFrame().toString());
        assertTrue(port2.receiveNoWait() == null);
    }
}
