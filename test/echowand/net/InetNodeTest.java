package echowand.net;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class InetNodeTest {
    
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
     * Test of getAddress method, of class InetNode.
     */
    @Test
    public void testGetAddress() throws UnknownHostException {
        InetNode node1 = new InetNode(subnet, Inet4Address.getByName("127.0.0.1"));
        assertEquals(Inet4Address.getByName("127.0.0.1"), node1.getAddress());
        
        InetNode node2 = new InetNode(subnet, Inet4Address.getByName("192.168.0.1"));
        assertEquals(Inet4Address.getByName("192.168.0.1"), node2.getAddress());
    }
    
    @Test
    public void testGetNodeInfo() throws UnknownHostException {
        InetNodeInfo nodeInfo = new InetNodeInfo(Inet4Address.getByName("127.0.0.1"));
        InetNode node1 = new InetNode(subnet, nodeInfo);
        assertEquals(Inet4Address.getByName("127.0.0.1"), node1.getAddress());
        assertEquals(nodeInfo, node1.getNodeInfo());
    }

    /**
     * Test of isMemberOf method, of class InetNode.
     */
    @Test
    public void testIsMemberOf() throws UnknownHostException, SubnetException {
        Inet4Subnet subnet2 = new Inet4Subnet();
        assertFalse(subnet2.isWorking());
        
        InetNode node = new InetNode(subnet, Inet4Address.getByName("127.0.0.1"));
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
        
        InetNode node = new InetNode(subnet, Inet4Address.getByName("172.16.1.1"));
        InetNode node1 = new InetNode(subnet, Inet4Address.getByName("172.16.1.1"));
        InetNode node2 = new InetNode(subnet2, Inet4Address.getByName("172.16.1.1"));
        InetNode node3 = new InetNode(subnet, Inet4Address.getByName("192.168.0.1"));

        assertTrue(node.equals(node));
        assertTrue(node1.equals(node));
        assertTrue(node.equals(node1));
        
        assertFalse(node1.equals(node2));
        assertFalse(node1.equals(node3));
        
        assertFalse(node2.equals(node1));
        assertFalse(node2.equals(node3));
        
        assertFalse(node3.equals(node1));
        assertFalse(node3.equals(node2));
    }
}
