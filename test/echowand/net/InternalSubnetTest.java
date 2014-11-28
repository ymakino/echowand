package echowand.net;

import echowand.common.EOJ;
import echowand.common.ESV;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class InternalSubnetTest {
    
    @Test
    public void testCreation() {
        InternalSubnet subnet = new InternalSubnet();
        Node local = subnet.getLocalNode();
        Node group = subnet.getGroupNode();
        
        assertTrue(local.isMemberOf(subnet));
        assertTrue(group.isMemberOf(subnet));
        assertFalse(local.isMemberOf(new InternalSubnet()));
        assertFalse(group.isMemberOf(new InternalSubnet()));
    }
    
    public CommonFrame createFrame() {
        CommonFrame frame = new CommonFrame();
        
        StandardPayload payload = new StandardPayload();
        payload.setDEOJ(new EOJ("000000"));
        payload.setSEOJ(new EOJ("000000"));
        payload.setESV(ESV.Get);
        frame.setEDATA(payload);
        
        return frame;
    }
    
    @Test
    public void testSendAndReceiveSameNode() {
        InternalSubnet subnet = new InternalSubnet();
        Node local = subnet.getLocalNode();
        Node group = subnet.getGroupNode();

        CommonFrame sendFrame = createFrame();
        Frame receivedFrame = null;
        try {
            subnet.send(new Frame(local, group, sendFrame));
            receivedFrame = subnet.receive();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(Arrays.equals(receivedFrame.getCommonFrame().toBytes(), sendFrame.toBytes()));
        assertEquals(subnet.getLocalNode(), receivedFrame.getSender());
        assertEquals(subnet.getGroupNode(), receivedFrame.getReceiver());


        sendFrame = createFrame();
        try {
            subnet.send(new Frame(local, local, sendFrame));
            receivedFrame = subnet.receive();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(Arrays.equals(receivedFrame.getCommonFrame().toBytes(), sendFrame.toBytes()));
        assertEquals(subnet.getLocalNode(), receivedFrame.getSender());
        assertEquals(subnet.getLocalNode(), receivedFrame.getReceiver());
    }
    
    @Test
    public void testSendAndReceive() {
        InternalSubnet subnet1 = new InternalSubnet();
        InternalSubnet subnet2 = new InternalSubnet();
        InternalSubnet subnet3 = new InternalSubnet();
        Node local1 = subnet1.getLocalNode();
        Node group1 = subnet1.getGroupNode();

        CommonFrame sendFrame = createFrame();
        Frame receivedFrame = null;
        try {
            subnet1.send(new Frame(local1, group1, sendFrame));
            subnet1.receive();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        
        try {
            receivedFrame = subnet2.receive();
            assertTrue(Arrays.equals(receivedFrame.getCommonFrame().toBytes(), sendFrame.toBytes()));
            assertEquals(subnet1.getLocalNode(), receivedFrame.getSender());
            assertEquals(subnet2.getGroupNode(), receivedFrame.getReceiver());
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }

        try {
            receivedFrame = subnet3.receive();
            assertTrue(Arrays.equals(receivedFrame.getCommonFrame().toBytes(), sendFrame.toBytes()));
            assertEquals(subnet1.getLocalNode(), receivedFrame.getSender());
            assertEquals(subnet3.getGroupNode(), receivedFrame.getReceiver());
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }

        Node local2 = subnet2.getLocalNode();
        sendFrame = createFrame();
        try {
            subnet1.send(new Frame(local1, local2, sendFrame));
            receivedFrame = subnet2.receive();
            assertNull(subnet1.receiveNoWait());
            assertNull(subnet3.receiveNoWait());
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(Arrays.equals(receivedFrame.getCommonFrame().toBytes(), sendFrame.toBytes()));
        assertEquals(subnet1.getLocalNode(), receivedFrame.getSender());
        assertEquals(subnet2.getLocalNode(), receivedFrame.getReceiver());

        Node local3 = subnet3.getLocalNode();
        sendFrame = createFrame();
        try {
            subnet1.send(new Frame(local1, local3, sendFrame));
            receivedFrame = subnet3.receive();
            assertNull(subnet1.receiveNoWait());
            assertNull(subnet2.receiveNoWait());
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(Arrays.equals(receivedFrame.getCommonFrame().toBytes(), sendFrame.toBytes()));
        assertEquals(subnet1.getLocalNode(), receivedFrame.getSender());
        assertEquals(subnet3.getLocalNode(), receivedFrame.getReceiver());
    }
    
    @Test
    public void testReceiveNoWait() {
        InternalSubnet subnet = new InternalSubnet();
        try {
            Frame frame = subnet.receiveNoWait();
            assertEquals(null, frame);
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testGetRemoteNode() {
        InternalSubnet subnet1 = new InternalSubnet();
        InternalSubnet subnet2 = new InternalSubnet();
        InternalSubnet subnet3 = new InternalSubnet("OTHER");
        
        Node node1local = subnet1.getLocalNode();
        String node1name = ((InternalNode)node1local).getName();
        Node node1remote = subnet2.getRemoteNode(node1name);
        
        assertEquals(node1local, node1remote);
        
        Node node1other = subnet3.getRemoteNode(node1name);
        
        assertFalse(node1local.equals(node1other));
    }
    
    @Test
    public void testGroupNodeEquallity() {
        InternalSubnet subnet1 = new InternalSubnet();
        InternalSubnet subnet2 = new InternalSubnet();
        InternalSubnet subnet3 = new InternalSubnet("OTHER");
        
        Node node1 = subnet1.getGroupNode();
        assertTrue(subnet2.getGroupNode().equals(node1));
        assertFalse(subnet3.getGroupNode().equals(node1));
    }
}