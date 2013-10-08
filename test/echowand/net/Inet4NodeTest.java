package echowand.net;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class Inet4NodeTest {
    
    Inet4Subnet subnet;
    
    @Before
    public void setUp() throws SubnetException {
        subnet = new Inet4Subnet();
    }
    
    @After
    public void tearDown() {
        subnet.stopService();
    }

    /**
     * Test of getPort method, of class InetNode.
     */
    @Test
    public void testGetPort() throws UnknownHostException {
        Inet4Node node1 = new Inet4Node(subnet, (Inet4Address)Inet4Address.getByName("127.0.0.1"), 3610);
        assertEquals(3610, node1.getPort());
        
        Inet4Node node2 = new Inet4Node(subnet, (Inet4Address)Inet4Address.getByName("127.0.0.1"), 1234);
        assertEquals(1234, node2.getPort());
    }

    /**
     * Test of getAddress method, of class InetNode.
     */
    @Test
    public void testGetAddress() throws UnknownHostException {
        Inet4Node node1 = new Inet4Node(subnet, (Inet4Address)Inet4Address.getByName("127.0.0.1"), 3610);
        assertEquals((Inet4Address)Inet4Address.getByName("127.0.0.1"), node1.getAddress());
        
        Inet4Node node2 = new Inet4Node(subnet, (Inet4Address)Inet4Address.getByName("192.168.0.1"), 3610);
        assertEquals((Inet4Address)Inet4Address.getByName("192.168.0.1"), node2.getAddress());
    }

    /**
     * Test of isMemberOf method, of class InetNode.
     */
    @Test
    public void testIsMemberOf() throws UnknownHostException, SubnetException {
        Inet4Subnet subnet2 = new Inet4Subnet();
        assertFalse(subnet2.isWorking());
        
        Inet4Node node = new Inet4Node(subnet, (Inet4Address)Inet4Address.getByName("127.0.0.1"), 3610);
        assertTrue(node.isMemberOf(subnet));
        assertFalse(node.isMemberOf(subnet2));
    }

    /**
     * Test of equals method, of class InetNode.
     */
    @Test
    public void testEquals() throws UnknownHostException, SubnetException {
        Inet4Subnet subnet2 = new Inet4Subnet();
        assertFalse(subnet2.isWorking());
        
        Inet4Node node = new Inet4Node(subnet, (Inet4Address)Inet4Address.getByName("172.16.1.1"), 3610);
        Inet4Node node1 = new Inet4Node(subnet, (Inet4Address)Inet4Address.getByName("172.16.1.1"), 3610);
        Inet4Node node2 = new Inet4Node(subnet2, (Inet4Address)Inet4Address.getByName("172.16.1.1"), 3610);
        Inet4Node node3 = new Inet4Node(subnet, (Inet4Address)Inet4Address.getByName("192.168.0.1"), 3610);
        Inet4Node node4 = new Inet4Node(subnet, (Inet4Address)Inet4Address.getByName("172.16.1.1"), 3611);
        System.out.println(node);
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
