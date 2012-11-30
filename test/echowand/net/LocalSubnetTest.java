package echowand.net;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.StandardPayload;
import echowand.net.Node;
import echowand.net.LocalSubnet;
import echowand.net.SubnetException;
import echowand.common.ESV;
import echowand.common.EOJ;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class LocalSubnetTest {
    
    @Test
    public void testCreation() {
        LocalSubnet subnet = new LocalSubnet();
        Node local = subnet.getLocalNode();
        Node group = subnet.getGroupNode();
        
        assertTrue(local.isMemberOf(subnet));
        assertTrue(group.isMemberOf(subnet));
        assertFalse(local.isMemberOf(new LocalSubnet()));
        assertFalse(group.isMemberOf(new LocalSubnet()));
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
        LocalSubnet subnet = new LocalSubnet();
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
        LocalSubnet subnet = new LocalSubnet();
        try {
            Frame frame = subnet.recvNoWait();
            assertEquals(null, frame);
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testLocalNetworkLocal() throws SubnetException {
        
        LocalSubnet subnet1 = new LocalSubnet();
        LocalSubnet subnet2 = new LocalSubnet();
        LocalSubnet subnet3 = new LocalSubnet();
        
        LocalSubnetNode node1 = (LocalSubnetNode) subnet1.getLocalNode();
        
        LocalNetwork network = LocalNetwork.getInstance();
        
        Frame sendFrame = new Frame(node1, node1, this.createFrame());
        network.broadcast(sendFrame);
        
        Frame recvFrame = subnet1.recvNoWait();
        assertEquals(sendFrame.getSender(), recvFrame.getSender());
        assertTrue(subnet1.recvNoWait() == null);
        
        assertTrue(subnet2.recvNoWait() == null);
        
        assertTrue(subnet3.recvNoWait() == null);
    }
    
    @Test
    public void testLocalNetworkUnicast() throws SubnetException {
        
        LocalSubnet subnet1 = new LocalSubnet();
        LocalSubnet subnet2 = new LocalSubnet();
        LocalSubnet subnet3 = new LocalSubnet();
        
        LocalSubnetNode node1 = (LocalSubnetNode) subnet1.getLocalNode();
        LocalSubnetNode node2 = (LocalSubnetNode) subnet2.getLocalNode();
        
        LocalNetwork network = LocalNetwork.getInstance();
        
        Frame sendFrame1 = new Frame(node1, node2, this.createFrame());
        network.broadcast(sendFrame1);
        
        assertTrue(subnet1.recvNoWait() == null);
        
        Frame recvFrame1_2 = subnet2.recvNoWait();
        assertEquals(sendFrame1.getSender(), recvFrame1_2.getSender());
        assertTrue(subnet2.recvNoWait() == null);
        
        assertTrue(subnet3.recvNoWait() == null);
    }
    
    @Test
    public void testLocalNetworkUnicastQueue() throws SubnetException {
        
        LocalSubnet subnet1 = new LocalSubnet();
        LocalSubnet subnet2 = new LocalSubnet();
        LocalSubnet subnet3 = new LocalSubnet();
        
        LocalSubnetNode node1 = (LocalSubnetNode) subnet1.getLocalNode();
        LocalSubnetNode node2 = (LocalSubnetNode) subnet2.getLocalNode();
        LocalSubnetNode node3 = (LocalSubnetNode) subnet3.getLocalNode();
        
        LocalNetwork network = LocalNetwork.getInstance();
        
        Frame sendFrame1 = new Frame(node1, node2, this.createFrame());
        Frame sendFrame2 = new Frame(node1, node3, this.createFrame());
        network.broadcast(sendFrame1);
        network.broadcast(sendFrame1);
        network.broadcast(sendFrame2);
        network.broadcast(sendFrame2);
        
        assertTrue(subnet1.recvNoWait() == null);
        
        Frame recvFrame1 = subnet2.recvNoWait();
        assertTrue(recvFrame1 != null);
        assertEquals(sendFrame1.getSender(), recvFrame1.getSender());
        recvFrame1 = subnet2.recvNoWait();
        assertTrue(recvFrame1 != null);
        assertEquals(sendFrame1.getSender(), recvFrame1.getSender());
        recvFrame1 = subnet2.recvNoWait();
        assertTrue(subnet2.recvNoWait() == null);
        
        Frame recvFrame2 = subnet3.recvNoWait();
        assertTrue(recvFrame2 != null);
        assertEquals(sendFrame1.getSender(), recvFrame2.getSender());
        recvFrame2 = subnet3.recvNoWait();
        assertTrue(recvFrame2 != null);
        assertEquals(sendFrame1.getSender(), recvFrame2.getSender());
        assertTrue(subnet3.recvNoWait() == null);
        
    }
    
    @Test
    public void testLocalNetworkBroadcast() throws SubnetException {
        
        LocalSubnet subnet1 = new LocalSubnet();
        LocalSubnet subnet2 = new LocalSubnet();
        LocalSubnet subnet3 = new LocalSubnet();
        
        LocalSubnetNode node1 = (LocalSubnetNode) subnet1.getLocalNode();
        
        LocalNetwork network = LocalNetwork.getInstance();
        
        Frame sendFrame = new Frame(node1, subnet1.getGroupNode(), this.createFrame());
        network.broadcast(sendFrame);
        
        Frame recvFrame1 = subnet1.recvNoWait();
        assertEquals(sendFrame.getSender(), recvFrame1.getSender());
        assertTrue(subnet1.recvNoWait() == null);
        
        Frame recvFrame2 = subnet2.recvNoWait();
        assertEquals(sendFrame.getSender(), recvFrame2.getSender());
        assertTrue(subnet2.recvNoWait() == null);
        
        Frame recvFrame3 = subnet3.recvNoWait();
        assertEquals(sendFrame.getSender(), recvFrame3.getSender());
        assertTrue(subnet3.recvNoWait() == null);
    }
}
