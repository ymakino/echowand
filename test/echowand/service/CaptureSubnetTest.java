package echowand.service;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Inet4Subnet;
import echowand.net.InternalNodeInfo;
import echowand.net.InternalSubnet;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class CaptureSubnetTest {
    
    public Subnet internalSubnet;
    public CaptureSubnet subnet;
    public TestCaptureSubnetObserver observer;
    
    class TestCaptureSubnetObserver implements CaptureSubnetObserver {
        
        public Frame sentFrame;
        public boolean successSentFrame;
        public Frame receivedFrame;

        @Override
        public void notifySent(Frame frame, boolean success) {
            sentFrame = frame;
            successSentFrame = success;
        }

        @Override
        public void notifyReceived(Frame frame) {
            receivedFrame = frame;
        }
        
    }
    
    public CaptureSubnetTest() {
        internalSubnet = new InternalSubnet("CaptureSubnetTest");
    }
    
    @Before
    public void setUp() {
        subnet = new CaptureSubnet(internalSubnet);
        observer = new TestCaptureSubnetObserver();
        subnet.addObserver(observer);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testGetSubnet() {
        assertEquals(subnet, subnet.getSubnet(Subnet.class));
        assertEquals(subnet, subnet.getSubnet(ExtendedSubnet.class));
        assertEquals(subnet, subnet.getSubnet(CaptureSubnet.class));
        assertEquals(internalSubnet, subnet.getSubnet(InternalSubnet.class));
        assertNull(subnet.getSubnet(Inet4Subnet.class));
    }

    @Test
    public void testGetInternalSubnet() {
        assertEquals(internalSubnet, subnet.getInternalSubnet());
    }

    @Test
    public void testSend() throws Exception {
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), new CommonFrame());
        
        assertTrue(subnet.send(frame));
        
        assertEquals(frame, observer.sentFrame);
    }

    @Test
    public void testReceive() throws Exception {
        
        StandardPayload payload = new StandardPayload();
        payload.setESV(ESV.Get);
        payload.setSEOJ(new EOJ("0ef001"));
        payload.setDEOJ(new EOJ("0ef001"));
        payload.addFirstProperty(new Property(EPC.x80));
        CommonFrame commonFrame = new CommonFrame();
        commonFrame.setEDATA(payload);
        Frame frame = new Frame(subnet.getLocalNode(), subnet.getLocalNode(), commonFrame);
        
        assertTrue(subnet.send(frame));
        
        Frame receivedFrame = subnet.receive();
        
        assertEquals(frame.toString(), receivedFrame.toString());
    }

    @Test
    public void testGetLocalNode() {
        assertEquals(internalSubnet.getLocalNode(), subnet.getLocalNode());
    }

    @Test
    public void testGetRemoteNode_NodeInfo() throws Exception {
        InternalNodeInfo nodeInfo1 = new InternalNodeInfo("dummy1");
        InternalNodeInfo nodeInfo2 = new InternalNodeInfo("dummy2");
        assertEquals(internalSubnet.getRemoteNode(nodeInfo1), subnet.getRemoteNode(nodeInfo1));
        assertFalse(internalSubnet.getRemoteNode(nodeInfo1).equals(subnet.getRemoteNode(nodeInfo2)));
    }

    @Test
    public void testGetRemoteNode_String() throws Exception {
        assertEquals(internalSubnet.getRemoteNode("dummy1"), subnet.getRemoteNode("dummy1"));
        assertFalse(internalSubnet.getRemoteNode("dummy1").equals(subnet.getRemoteNode("dummy2")));
    }

    @Test
    public void testGetGroupNode() {
        assertEquals(internalSubnet.getGroupNode(), subnet.getGroupNode());
    }

    @Test
    public void testCountObservers() {
        CaptureSubnet subnet = new CaptureSubnet(internalSubnet);
        assertEquals(0, subnet.countObservers());
    }

    @Test
    public void testGetObserver() {
        assertEquals(observer, subnet.getObserver(0));
    }

    @Test
    public void testAddObserver() {
        CaptureSubnet subnet = new CaptureSubnet(internalSubnet);
        assertEquals(0, subnet.countObservers());
        subnet.addObserver(observer);
        assertEquals(observer, subnet.getObserver(0));
    }

    @Test
    public void testRemoveObserver() {
        subnet.removeObserver(observer);
        assertEquals(0, subnet.countObservers());
    }
    
}
