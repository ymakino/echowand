package echowand.net;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
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
    private InetSubnet subnet;
    
    public String localAddress4 = "127.0.0.1";
    public String remoteAddress4 = "172.21.254.254";
    public String localAddress6 = "::1";
    public String remoteAddress6 = "FD00::FFFE";
    
    public InetSubnet newInetSubnet() throws SubnetException {
        return new Inet4Subnet();
    }
    
    public InetSubnet newInetSubnet(InetAddress addr) throws SubnetException {
        return new Inet4Subnet((Inet4Address)addr);
    }
    
    public InetSubnet newInetSubnet(NetworkInterface nif) throws SubnetException {
        return new Inet4Subnet(nif);
    }
    
    public boolean isValidAddress(InetAddress addr) {
        return addr instanceof Inet4Address;
    }
    
    public InetAddress getLocalAddress() throws UnknownHostException {
        return InetAddress.getByName(localAddress4);
    }
    
    public InetAddress getRemoteAddress() throws UnknownHostException {
        return InetAddress.getByName(remoteAddress4);
    }
    
    public InetAddress getInvalidAddress() throws UnknownHostException {
        return InetAddress.getByName(localAddress6);
    }
    
    @Before
    public void setUp() throws SubnetException {
        subnet = newInetSubnet();
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
        
        Frame receivedFrame = receiver.getReceivedFrame();
        if (success) {
            assertFalse(receivedFrame == null);
            assertTrue(Arrays.equals(sendFrame.getCommonFrame().toBytes(), receivedFrame.getCommonFrame().toBytes()));
        } else {
            assertTrue(receivedFrame == null);
        }
    }

    @Test
    public void testSendAndReceive() throws SubnetException, UnknownHostException {
        subnet.startService();

        sendTest(subnet.getGroupNode(), true);
        sendTest(subnet.getLocalNode(), true);

        Node node = subnet.getRemoteNode(getLocalAddress());
        sendTest(node, true);
        Node invalidAddr = subnet.getRemoteNode(getRemoteAddress());
        sendTest(invalidAddr, false);
    }

    @Test
    public void testCreation() throws SubnetException {
        assertFalse(subnet.stopService());
        assertFalse(subnet.stopService());
        
        assertTrue(subnet.startService());
        assertFalse(subnet.startService());
        assertTrue(subnet.isInService());
        
        assertTrue(subnet.stopService());
        assertFalse(subnet.stopService());
        assertFalse(subnet.isInService());
    }
    
    @Test
    public void testIsValidAddress() throws UnknownHostException {
        assertTrue(subnet.isValidAddress(getLocalAddress()));
        assertFalse(subnet.isValidAddress(getInvalidAddress()));
    }
    
    private LinkedList<InetAddress> getInetAddresses() throws SocketException {
        LinkedList<InetAddress> inetAddrs = new LinkedList<InetAddress>();
        
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();
            Enumeration<InetAddress> addrs = nif.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (isValidAddress(addr)) {
                    inetAddrs.add((InetAddress)addr);
                }
            }
        }
        
        return inetAddrs;
    }
    
    private LinkedList<NetworkInterface> getInetInterfaces() throws SocketException {
        LinkedList<NetworkInterface> inetIfs = new LinkedList<NetworkInterface>();
        
        for (InetAddress addr : getInetAddresses()) {
            NetworkInterface nif = NetworkInterface.getByInetAddress(addr);
            if (!inetIfs.contains(nif)) {
                inetIfs.add(nif);
            }
        }
        
        return inetIfs;
    }
    
    @Test
    public void testCreationWithNetworkInterface() throws SocketException, SubnetException {
        subnet.stopService();

        for (NetworkInterface nif : getInetInterfaces()) {
            subnet = newInetSubnet(nif);
            assertFalse(subnet.isInService());
            assertEquals(subnet.getNetworkInterface(), nif);
            subnet.stopService();
        }
    }

    @Test (expected=SubnetException.class)
    public void testCreationWithNullNetworkInterface() throws SubnetException {
        subnet.stopService();
        subnet = newInetSubnet((NetworkInterface)null);
    }

    @Test
    public void testCreationWithAddress() throws SocketException, SubnetException {
        subnet.stopService();

        for (InetAddress addr : getInetAddresses()) {
            NetworkInterface nif = NetworkInterface.getByInetAddress(addr);
            subnet = newInetSubnet(addr);
            assertFalse(subnet.isInService());
            assertEquals(nif, subnet.getNetworkInterface());
            subnet.stopService();
        }
    }

    @Test(expected = SubnetException.class)
    public void testCreationWithNullAddress() throws SubnetException {
        subnet.stopService();
        subnet = newInetSubnet((InetAddress)null);
    }

    @Test
    public void testStartAndStopService() throws SubnetException {
        assertFalse(subnet.isInService());

        assertTrue(subnet.startService());
        assertTrue(subnet.isInService());
        assertFalse(subnet.startService());
        assertTrue(subnet.isInService());

        assertTrue(subnet.stopService());
        assertFalse(subnet.isInService());
        assertFalse(subnet.stopService());
        assertFalse(subnet.isInService());
    }
    
    @Test(expected=SubnetException.class)
    public void testInvalidSend() throws SubnetException {
        subnet.stopService();
        subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame()));
    }
    
    @Test(expected=SubnetException.class)
    public void testInvalidReceive() throws SubnetException {  
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
            Node node1 = subnet.getRemoteNode(getRemoteAddress());
            Node node2 = subnet.getRemoteNode(getRemoteAddress());
            assertEquals(node1, node2);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testEnableTCPAcceptor() throws SubnetException {
        assertFalse(subnet.isTCPAcceptorEnabled());
        assertTrue(subnet.enableTCPAcceptor());
        assertTrue(subnet.isTCPAcceptorEnabled());
        
        subnet.startService();
        
        assertFalse(subnet.enableTCPAcceptor());
        assertTrue(subnet.isTCPAcceptorEnabled());
    }
    
    @Test
    public void testDisableTCPAcceptor() throws SubnetException {
        subnet.enableTCPAcceptor();
        assertTrue(subnet.isTCPAcceptorEnabled());
        assertTrue(subnet.disableTCPAcceptor());
        assertFalse(subnet.isTCPAcceptorEnabled());
        
        subnet.startService();
        
        assertFalse(subnet.disableTCPAcceptor());
        assertFalse(subnet.isTCPAcceptorEnabled());
    }
    
    @Test(expected = SubnetException.class)
    public void testNewTCPConnectionFailure() throws SubnetException, UnknownHostException {
        subnet.newTCPConnection(subnet.getRemoteNode(getLocalAddress()));
    }
    
    @Test
    public void testNewTCPConnection() throws SubnetException, UnknownHostException, IOException, NetworkException {
        ServerSocket ss = new ServerSocket(3610);
        ss.setReuseAddress(true);
        try {
            Node localNode = subnet.getLocalNode();
            Node remoteNode = subnet.getRemoteNode(getLocalAddress());
            TCPConnection conn1 = subnet.newTCPConnection(remoteNode);
            Socket socket = ss.accept();
            TCPConnection conn2 = new TCPConnection(socket, localNode.getNodeInfo(), remoteNode.getNodeInfo());
            
            CommonFrame commonFrame1 = new CommonFrame();
            StandardPayload payload = new StandardPayload();
            payload.setSEOJ(new EOJ("0ef001"));
            payload.setDEOJ(new EOJ("0ef001"));
            payload.setESV(ESV.Get);
            payload.addFirstProperty(new Property(EPC.x80));
            commonFrame1.setEDATA(payload);
            
            conn1.send(commonFrame1);
            CommonFrame commonFrame2 = conn2.receive();
            
            assertArrayEquals(commonFrame1.toBytes(), commonFrame2.toBytes());
        } finally {
            ss.close();
        }
    }
}
