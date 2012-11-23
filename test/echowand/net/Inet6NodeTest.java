package echowand.net;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class Inet6NodeTest {
    
    Inet6Subnet subnet;
    
    @Before
    public void setUp() throws SubnetException {
        subnet = new Inet6Subnet();
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
        Inet6Node node1 = new Inet6Node(subnet, (Inet6Address)Inet6Address.getByName("::1"), 3610);
        assertEquals(3610, node1.getPort());
        
        Inet6Node node2 = new Inet6Node(subnet, (Inet6Address)Inet6Address.getByName("::1"), 1234);
        assertEquals(1234, node2.getPort());
    }

    /**
     * Test of getAddress method, of class InetNode.
     */
    @Test
    public void testGetAddress() throws UnknownHostException {
        Inet6Node node1 = new Inet6Node(subnet, (Inet6Address)Inet6Address.getByName("::1"), 3610);
        assertEquals((Inet6Address)Inet6Address.getByName("::1"), node1.getAddress());
        
        Inet6Node node2 = new Inet6Node(subnet, (Inet6Address)Inet6Address.getByName("FD00::1"), 3610);
        assertEquals((Inet6Address)Inet6Address.getByName("FD00::1"), node2.getAddress());
    }

    /**
     * Test of isMemberOf method, of class InetNode.
     */
    @Test
    public void testIsMemberOf() throws UnknownHostException, SubnetException {
        Inet6Subnet subnet2 = new Inet6Subnet(false);
        assertFalse(subnet2.isEnabled());
        
        Inet6Node node = new Inet6Node(subnet, (Inet6Address)Inet6Address.getByName("::1"), 3610);
        assertTrue(node.isMemberOf(subnet));
        assertFalse(node.isMemberOf(subnet2));
    }

    /**
     * Test of equals method, of class InetNode.
     */
    @Test
    public void testEquals() throws UnknownHostException, SubnetException {
        Inet6Subnet subnet2 = new Inet6Subnet(false);
        assertFalse(subnet2.isEnabled());
        
        Inet6Node node = new Inet6Node(subnet, (Inet6Address)Inet6Address.getByName("FD00::1:1"), 3610);
        Inet6Node node1 = new Inet6Node(subnet, (Inet6Address)Inet6Address.getByName("FD00::1:1"), 3610);
        Inet6Node node2 = new Inet6Node(subnet2, (Inet6Address)Inet6Address.getByName("FD00::1:1"), 3610);
        Inet6Node node3 = new Inet6Node(subnet, (Inet6Address)Inet6Address.getByName("FD00::1"), 3610);
        Inet6Node node4 = new Inet6Node(subnet, (Inet6Address)Inet6Address.getByName("FD00::1:1"), 3611);
        
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
