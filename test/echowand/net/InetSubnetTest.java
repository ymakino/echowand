package echowand.net;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Property;
import echowand.net.InetSubnet;
import echowand.net.Node;
import echowand.net.InvalidDataException;
import echowand.net.SubnetException;
import echowand.net.Subnet;
import echowand.common.ESV;
import echowand.common.EPC;
import echowand.common.EOJ;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
class FrameReceiver extends Thread {
    public Subnet subnet;
    public Frame recvFrame;

    public FrameReceiver(Subnet subnet) {
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

public class InetSubnetTest {
    private InetSubnet subnet;
    
    @Before
    public void setUp() {
        subnet = new InetSubnet();
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
            Node node = subnet.getRemoteNode(InetAddress.getByName("127.0.0.1"), InetSubnet.ECHONET_PORT);
            sendTest(node, true);
            Node invalidAddr = subnet.getRemoteNode(InetAddress.getByName("172.21.254.254"), InetSubnet.ECHONET_PORT);
            sendTest(invalidAddr, false);
            Node invalidPort = subnet.getRemoteNode(InetAddress.getByName("127.0.0.1"), 4321);
            sendTest(invalidPort, false);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testCreation() {
        assertFalse(subnet.enable());
        subnet.disable();
        subnet = new InetSubnet(true);
        assertTrue(subnet.isEnabled());
        subnet.disable();
        subnet = new InetSubnet(false);
        assertFalse(subnet.isEnabled());
        subnet.disable();
    }
    
    @Test
    public void testEnable() {
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
    public void testEnableAfterDisable() {
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
            Node node1 = subnet.getRemoteNode(InetAddress.getByName("192.168.1.1"));
            Node node2 = subnet.getRemoteNode(InetAddress.getByName("192.168.1.1"), 3610);
            Node node3 = subnet.getRemoteNode(InetAddress.getByName("192.168.1.1"), 3611);
            assertEquals(node1, node2);
            assertFalse(node1.equals(node3));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            fail();
        }
    }
}
