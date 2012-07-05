package echowand.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class InetNodeTest {
    
    InetSubnet subnet;
    
    @Before
    public void setUp() {
        subnet = new InetSubnet();
    }
    
    @After
    public void tearDown() {
        subnet.disable();
    }

    /**
     * Test of getPort method, of class InetNode.
     */
    @Test
    public void testGetPort() throws UnknownHostException {
        InetNode node1 = new InetNode(subnet, InetAddress.getByName("127.0.0.1"), 3610);
        assertEquals(3610, node1.getPort());
        
        InetNode node2 = new InetNode(subnet, InetAddress.getByName("127.0.0.1"), 1234);
        assertEquals(1234, node2.getPort());
    }

    /**
     * Test of getAddress method, of class InetNode.
     */
    @Test
    public void testGetAddress() throws UnknownHostException {
        InetNode node1 = new InetNode(subnet, InetAddress.getByName("127.0.0.1"), 3610);
        assertEquals(InetAddress.getByName("127.0.0.1"), node1.getAddress());
        
        InetNode node2 = new InetNode(subnet, InetAddress.getByName("192.168.0.1"), 3610);
        assertEquals(InetAddress.getByName("192.168.0.1"), node2.getAddress());
    }

    /**
     * Test of isMemberOf method, of class InetNode.
     */
    @Test
    public void testIsMemberOf() throws UnknownHostException {
        InetSubnet subnet2 = new InetSubnet(false);
        assertFalse(subnet2.isEnabled());
        
        InetNode node = new InetNode(subnet, InetAddress.getByName("127.0.0.1"), 3610);
        assertTrue(node.isMemberOf(subnet));
        assertFalse(node.isMemberOf(subnet2));
    }

    /**
     * Test of equals method, of class InetNode.
     */
    @Test
    public void testEquals() throws UnknownHostException {
        InetSubnet subnet2 = new InetSubnet(false);
        assertFalse(subnet2.isEnabled());
        
        InetNode node = new InetNode(subnet, InetAddress.getByName("172.16.1.1"), 3610);
        InetNode node1 = new InetNode(subnet, InetAddress.getByName("172.16.1.1"), 3610);
        InetNode node2 = new InetNode(subnet2, InetAddress.getByName("172.16.1.1"), 3610);
        InetNode node3 = new InetNode(subnet, InetAddress.getByName("192.168.0.1"), 3610);
        InetNode node4 = new InetNode(subnet, InetAddress.getByName("172.16.1.1"), 3611);
        
        assertTrue(node.equals(node));
        assertTrue(node1.equals(node));
        assertTrue(node.equals(node1));
        
        assertFalse(node1.equals(node2));
        assertFalse(node1.equals(node3));
        assertFalse(node1.equals(node4));
        
        assertFalse(node2.equals(node1));
        assertFalse(node2.equals(node3));
        assertFalse(node2.equals(node4));
        
        assertFalse(node3.equals(node1));
        assertFalse(node3.equals(node2));
        assertFalse(node3.equals(node4));
        
        assertFalse(node4.equals(node1));
        assertFalse(node4.equals(node2));
        assertFalse(node4.equals(node3));
    }
}
