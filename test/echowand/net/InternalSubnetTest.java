package echowand.net;

import echowand.common.EOJ;
import echowand.common.ESV;
import java.util.Arrays;
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
    public void testSendAndRecv() {
        InternalSubnet subnet = new InternalSubnet();
        Node local = subnet.getLocalNode();
        Node group = subnet.getGroupNode();

        CommonFrame sendFrame = createFrame();
        Frame recvFrame = null;
        try {
            subnet.send(new Frame(local, group, sendFrame));
            recvFrame = subnet.recv();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(Arrays.equals(recvFrame.getCommonFrame().toBytes(), sendFrame.toBytes()));
        assertEquals(subnet.getLocalNode(), recvFrame.getSender());
        assertEquals(subnet.getGroupNode(), recvFrame.getReceiver());


        sendFrame = createFrame();
        try {
            subnet.send(new Frame(local, local, sendFrame));
            recvFrame = subnet.recv();
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(Arrays.equals(recvFrame.getCommonFrame().toBytes(), sendFrame.toBytes()));
        assertEquals(subnet.getLocalNode(), recvFrame.getSender());
        assertEquals(subnet.getLocalNode(), recvFrame.getReceiver());
    }
    
    @Test
    public void testRecvNoWait() {
        InternalSubnet subnet = new InternalSubnet();
        try {
            Frame frame = subnet.recvNoWait();
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
}