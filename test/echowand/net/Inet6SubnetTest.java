package echowand.net;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import java.net.Inet6Address;
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
class FrameReceiver6 extends Thread {
    public Subnet subnet;
    public Frame recvFrame;

    public FrameReceiver6(Subnet subnet) {
        this.subnet = subnet;
    }
    
    public Frame getRecvFrame() {
        try {
            for (int i=0; i<5; i++) {
                Thread.sleep(100);
                if (!this.isAlive()) {
                    break;
                }
            }
            this.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return recvFrame;
    }

    @Override
    public void run() {
        try {
            recvFrame = subnet.recv();
        } catch (SubnetException e) {
            e.printStackTrace();
        }
    }
}

public class Inet6SubnetTest {
    private Inet6Subnet subnet;
    
    @Before
    public void setUp() throws SubnetException {
        subnet = new Inet6Subnet();
    }
    
    @After
    public void tearDown() {
        subnet.disable();
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
    public void testSendAndRecv() {
        sendTest(subnet.getGroupNode(), true);
        sendTest(subnet.getLocalNode(), true);
        try {
            Node node = subnet.getRemoteNode((Inet6Address)Inet6Address.getByName("::1"), Inet6Subnet.DEFAULT_PORT);
            sendTest(node, true);
            Node invalidAddr = subnet.getRemoteNode((Inet6Address)Inet6Address.getByName("FD00::fe"), Inet6Subnet.DEFAULT_PORT);
            sendTest(invalidAddr, false);
            Node invalidPort = subnet.getRemoteNode((Inet6Address)Inet6Address.getByName("::1"), 4321);
            sendTest(invalidPort, false);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testCreation() throws SubnetException {
        assertFalse(subnet.enable());
        subnet.disable();
        subnet = new Inet6Subnet(true);
        assertTrue(subnet.isEnabled());
        subnet.disable();
        subnet = new Inet6Subnet(false);
        assertFalse(subnet.isEnabled());
        subnet.disable();
    }
    
    private LinkedList<Inet6Address> getInet6Addresses() throws SocketException {
        LinkedList<Inet6Address> inet6addrs = new LinkedList<Inet6Address>();
        
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();
            Enumeration<InetAddress> addrs = nif.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (addr instanceof Inet6Address) {
                    inet6addrs.add((Inet6Address)addr);
                }
            }
        }
        
        return inet6addrs;
    }
    
    private LinkedList<NetworkInterface> getInet6Interfaces() throws SocketException {
        LinkedList<NetworkInterface> inet6ifs = new LinkedList<NetworkInterface>();
        
        for (Inet6Address addr : getInet6Addresses()) {
            NetworkInterface nif = NetworkInterface.getByInetAddress(addr);
            if (!inet6ifs.contains(nif)) {
                inet6ifs.add(nif);
            }
        }
        
        return inet6ifs;
    }
    
    @Test
    public void testCreationWithNetworkInterface() throws SocketException, SubnetException {
        subnet.disable();

        for (NetworkInterface nif : getInet6Interfaces()) {
            subnet = new Inet6Subnet(nif, true);
            assertTrue(subnet.isEnabled());
            assertEquals(subnet.getNetworkInterface(), nif);
            subnet.disable();

            subnet = new Inet6Subnet(nif, false);
            assertFalse(subnet.isEnabled());
            assertEquals(subnet.getNetworkInterface(), nif);
            subnet.disable();
        }
    }

    @Test(expected = SubnetException.class)
    public void testCreationWithNullNetworkInterface() throws SubnetException {
        subnet.disable();
        subnet = new Inet6Subnet((NetworkInterface) null, true);
    }

    @Test
    public void testCreationWithAddress() throws SocketException, SubnetException {
        subnet.disable();

        for (Inet6Address addr : getInet6Addresses()) {
            NetworkInterface nif = NetworkInterface.getByInetAddress(addr);
            
            subnet = new Inet6Subnet(addr, true);
            assertTrue(subnet.isEnabled());
            assertEquals(subnet.getNetworkInterface(), nif);
            subnet.disable();

            subnet = new Inet6Subnet(addr, false);
            assertFalse(subnet.isEnabled());
            assertEquals(subnet.getNetworkInterface(), nif);
            subnet.disable();
        }
    }
    
    @Test(expected=SubnetException.class)
    public void testCreationWithNullAddress() throws SubnetException {
        subnet.disable();
        subnet = new Inet6Subnet((Inet6Address)null, true);
    }
    
    @Test
    public void testEnable() throws SubnetException {
        assertTrue(subnet.isEnabled());

        assertTrue(subnet.disable());
        assertFalse(subnet.isEnabled());
        assertFalse(subnet.disable());
        assertFalse(subnet.isEnabled());


        assertTrue(subnet.enable());
        assertTrue(subnet.isEnabled());
        assertFalse(subnet.enable());
        assertTrue(subnet.isEnabled());
    }
    
    @Test(expected=SubnetException.class)
    public void testInvalidSend() throws SubnetException {
        subnet.disable();
        subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame()));
    }
    
    @Test(expected=SubnetException.class)
    public void testInvalidRecv() throws SubnetException {  
        subnet.disable();
        subnet.recv();
    }
    
    @Test(expected= SubnetException.class)
    public void testSendAfterDisable() throws SubnetException {
        subnet.disable();
        subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame()));
    }

    @Test
    public void testEnableAfterDisable() throws SubnetException {
        try {
            assertTrue(subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame())));
        } catch (SubnetException e) {
            fail();
        }    
        
        subnet.disable();

        try {
            subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame()));
        } catch (SubnetException e) {
        }

        subnet.enable();

        try {
            assertTrue(subnet.send(new Frame(subnet.getLocalNode(), subnet.getLocalNode(), createFrame())));
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void setBufferSize() {
        assertEquals(1500, subnet.getBufferSize());
        subnet.setBufferSize(3000);
        assertEquals(3000, subnet.getBufferSize());
    }
    
    @Test
    public void testNodeEquals() {
        try {
            Node node1 = subnet.getRemoteNode((Inet6Address)Inet6Address.getByName("FD00::1"));
            Node node2 = subnet.getRemoteNode((Inet6Address)Inet6Address.getByName("FD00::1"), 3610);
            Node node3 = subnet.getRemoteNode((Inet6Address)Inet6Address.getByName("FD00::1"), 3611);
            assertEquals(node1, node2);
            assertFalse(node1.equals(node3));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            fail();
        }
    }
}
