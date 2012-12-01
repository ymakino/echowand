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
}