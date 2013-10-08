package echowand.net;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class Inet4SubnetTest {
    private Inet4Subnet subnet;
    
    @Before
    public void setUp() throws SubnetException {
        subnet = new Inet4Subnet();
    }
    
    @After
    public void tearDown() {
        subnet.stopService();
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    public CommonFrame createFrame() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(14);
            buffer.put((byte) 0x10);
            buffer.put((byte) 0x81);
            buffer.putShort((short) 0x01);
            buffer.put(new EOJ("001101").toBytes());
            buffer.put(new EOJ("001101").toBytes());
            buffer.put(ESV.Get.toByte());
            buffer.put((byte) 0x01);
            buffer.put(new Property(EPC.x80).toBytes());
            return new CommonFrame(buffer.array());
        } catch (InvalidDataException e) {
            e.printStackTrace();
            fail();
            return null;
        }
    }
    
    public void sendTest(Node target, boolean success) {
        Frame sendFrame = new Frame(subnet.getLocalNode(), target, createFrame());

        try {
            subnet.send(sendFrame);
        } catch (SubnetException e) {
            e.printStackTrace();
        }

        FrameReceiver receiver = new FrameReceiver(subnet);
        receiver.start();
        
        Frame recvFrame = receiver.getRecvFrame();
        if (success) {
            assertFalse(recvFrame == null);
            assertTrue(Arrays.equals(sendFrame.getCommonFrame().toBytes(), recvFrame.getCommonFrame().toBytes()));
        } else {
            assertTrue(recvFrame == null);
        }
    }

    @Test
    public void testSendAndRecv() throws SubnetException, UnknownHostException {
        subnet.startService();
        
        sendTest(subnet.getGroupNode(), true);
        sendTest(subnet.getLocalNode(), true);
        
            Node node = subnet.getRemoteNode(Inet4Address.getByName("127.0.0.1"), Inet4Subnet.DEFAULT_PORT_NUMBER);
            sendTest(node, true);
            Node invalidAddr = subnet.getRemoteNode(Inet4Address.getByName("172.21.254.254"), Inet4Subnet.DEFAULT_PORT_NUMBER);
            sendTest(invalidAddr, false);
            Node invalidPort = subnet.getRemoteNode(Inet4Address.getByName("127.0.0.1"), 4321);
            sendTest(invalidPort, false);
    }
    
    @Test
    public void testCreation() throws SubnetException {
        assertFalse(subnet.stopService());
        assertFalse(subnet.stopService());
        
        assertTrue(subnet.startService());
        assertFalse(subnet.startService());
        assertTrue(subnet.isWorking());
        
        assertTrue(subnet.stopService());
        assertFalse(subnet.stopService());
        assertFalse(subnet.isWorking());
    }
    
    private LinkedList<Inet4Address> getInet4Addresses() throws SocketException {
        LinkedList<Inet4Address> inet6addrs = new LinkedList<Inet4Address>();
        
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();
            Enumeration<InetAddress> addrs = nif.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (addr instanceof Inet4Address) {
                    inet6addrs.add((Inet4Address)addr);
                }
            }
        }
        
        return inet6addrs;
    }
    
    private LinkedList<NetworkInterface> getInet4Interfaces() throws SocketException {
        LinkedList<NetworkInterface> inet6ifs = new LinkedList<NetworkInterface>();
        
        for (Inet4Address addr : getInet4Addresses()) {
            NetworkInterface nif = NetworkInterface.getByInetAddress(addr);
            if (!inet6ifs.contains(nif)) {
                inet6ifs.add(nif);
            }
        }
        
        return inet6ifs;
    }
    
    @Test
    public void testCreationWithNetworkInterface() throws SocketException, SubnetException {
        subnet.stopService();

        for (NetworkInterface nif : getInet4Interfaces()) {
            subnet = new Inet4Subnet(nif);
            assertFalse(subnet.isWorking());
            assertEquals(subnet.getNetworkInterface(), nif);
            subnet.stopService();
        }
    }

    @Test (expected=SubnetException.class)
    public void testCreationWithNullNetworkInterface() throws SubnetException {
        subnet.stopService();
        subnet = new Inet4Subnet((NetworkInterface) null);
    }

    @Test
    public void testCreationWithAddress() throws SocketException, SubnetException {
        subnet.stopService();

        for (Inet4Address addr : getInet4Addresses()) {
            NetworkInterface nif = NetworkInterface.getByInetAddress(addr);
            subnet = new Inet4Subnet(addr);
            assertFalse(subnet.isWorking());
            assertEquals(subnet.getNetworkInterface(), nif);
            subnet.stopService();
        }
    }

    @Test(expected = SubnetException.class)
    public void testCreationWithNullAddress() throws SubnetException {
        subnet.stopService();
        subnet = new Inet4Subnet((Inet4Address)null);
    }

    @Test
    public void testStartAndStopService() throws SubnetException {
        assertFalse(subnet.isWorking());

        assertTrue(subnet.startService());
        assertTrue(subnet.isWorking());
        assertFalse(subnet.startService());
        assertTrue(subnet.isWorking());

        assertTrue(subnet.stopService());
        assertFalse(subnet.isWorking());
        assertFalse(subnet.stopService());
        assertFalse(subnet.isWorking());
    }
    
    @Test(expected=SubnetException.class)
    public void testInvalidSend() throws SubnetException {
        subnet.stopService();
        subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame()));
    }
    
    @Test(expected=SubnetException.class)
    public void testInvalidRecv() throws SubnetException {  
        subnet.stopService();
        subnet.receive();
    }
    
    @Test(expected= SubnetException.class)
    public void testSendAfterStopService() throws SubnetException {
        subnet.stopService();
        subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame()));
    }

    @Test
    public void testStartServiceAfterStopService() throws SubnetException {
        try {
            subnet.startService();
            assertTrue(subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame())));
        } catch (SubnetException e) {
            fail();
        }    
        
        subnet.stopService();

        try {
            subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame()));
        } catch (SubnetException e) {
        }

        subnet.startService();

        try {
            assertTrue(subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame())));
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /*
    @Test
    public void setBufferSize() {
        assertEquals(4096, subnet.getBufferSize());
        subnet.setBufferSize(3000);
        assertEquals(3000, subnet.getBufferSize());
    }*/
    
    @Test
    public void testNodeEquals() throws SubnetException {
        try {
            Node node1 = subnet.getRemoteNode(Inet4Address.getByName("192.168.1.1"));
            Node node2 = subnet.getRemoteNode(Inet4Address.getByName("192.168.1.1"), 3610);
            Node node3 = subnet.getRemoteNode(Inet4Address.getByName("192.168.1.1"), 3611);
            assertEquals(node1, node2);
            assertFalse(node1.equals(node3));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            fail();
        }
    }
}
