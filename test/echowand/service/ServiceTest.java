package echowand.service;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.TooManyObjectsException;
import echowand.logic.TransactionManager;
import echowand.net.Frame;
import echowand.net.InternalNodeInfo;
import echowand.net.InternalSubnet;
import echowand.net.Node;
import echowand.net.NodeInfo;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import echowand.object.EchonetObjectException;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.object.RemoteObject;
import echowand.service.result.CaptureResult;
import echowand.service.result.GetResult;
import echowand.service.result.NotifyResult;
import echowand.service.result.ObserveResult;
import echowand.service.result.ResultFrame;
import echowand.service.result.SetGetResult;
import echowand.service.result.SetResult;
import echowand.service.result.UpdateRemoteInfoResult;
import echowand.util.Pair;
import echowand.util.Selector;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class ServiceTest {
    public InternalSubnet subnet;
    public Core core;
    public Service service;
    
    public InternalSubnet peerSubnet;
    public Core peerCore;
    public Service peerService;
    
    public ServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws TooManyObjectsException, SubnetException {
        Random r = new Random();
        String networkName = Integer.toString(r.nextInt());
        
        subnet = new InternalSubnet(networkName);
        core = new Core(new CaptureSubnet(subnet));
        service = null;
        
        LocalObjectConfig config = new LocalObjectConfig(new TemperatureSensorInfo());
        core.addLocalObjectConfig(config);
        
        peerSubnet = new InternalSubnet(networkName);
        peerCore = new Core(new CaptureSubnet(peerSubnet));
        peerCore.startService();
        peerService = new Service(peerCore);
    }
    
    @After
    public void tearDown() {
    }
    
    public void startService() {
        try {
            core.startService();
            service = new Service(core);
        } catch (TooManyObjectsException ex) {
            Logger.getLogger(ServiceTest.class.getName()).log(Level.SEVERE, null, ex);
            org.junit.Assert.fail(ex.getMessage());
        } catch (SubnetException ex) {
            Logger.getLogger(ServiceTest.class.getName()).log(Level.SEVERE, null, ex);
            org.junit.Assert.fail(ex.getMessage());
        }
    }
    
    public static class TestRemoteObject extends RemoteObject {
        public List<Pair<EPC, ObjectData>> setDataList;
        
        public TestRemoteObject(Subnet subnet, Node node, EOJ eoj, TransactionManager transactionManager) {
            super(subnet, node, eoj, transactionManager);
            setDataList = new  LinkedList<Pair<EPC, ObjectData>>();
        }
        
        @Override
        public boolean setData(EPC epc, ObjectData data) {
            setDataList.add(new Pair<EPC, ObjectData>(epc, data));
            return true;
        }
        
        @Override
        public ObjectData getData(EPC epc) throws EchonetObjectException {
            ObjectData data = null;
            for (Pair<EPC, ObjectData> pair : setDataList) {
                if (pair.first == epc) {
                    data = pair.second;
                }
            }
            
            if (data != null) {
                return data;
            }
            
            return super.getData(epc);
        }
        
        @Override
        public boolean isGettable(EPC epc) {
            return true;
        }
        
        @Override
        public boolean isSettable(EPC epc) {
            return true;
        }
        
        @Override
        public boolean isObservable(EPC epc) {
            return true;
        }
    }

    /**
     * Test of getCore method, of class Service.
     */
    @Test
    public void testGetCore() {
        System.out.println("getCore");
        startService();
        
        assertNotNull(service.getCore());
        assertTrue(core == service.getCore());
    }

    /**
     * Test of getSubnet method, of class Service.
     */
    @Test
    public void testGetSubnet() {
        System.out.println("getSubnet");
        startService();
        
        assertNotNull(service.getSubnet());
        CaptureSubnet captureSubnet = (CaptureSubnet)service.getSubnet();
        assertEquals(subnet, captureSubnet.getInternalSubnet());
    }

    /**
     * Test of getLocalObjectManager method, of class Service.
     */
    @Test
    public void testGetLocalObjectManager() {
        System.out.println("getLocalObjectManager");
        startService();
        
        assertNotNull(service.getLocalObjectManager());
        assertTrue(core.getLocalObjectManager() == service.getLocalObjectManager());
    }

    /**
     * Test of getRemoteObjectManager method, of class Service.
     */
    @Test
    public void testGetRemoteObjectManager() {
        System.out.println("getRemoteObjectManager");
        startService();
        
        assertNotNull(service.getRemoteObjectManager());
        assertTrue(core.getRemoteObjectManager() == service.getRemoteObjectManager());
    }

    /**
     * Test of getTransactionManager method, of class Service.
     */
    @Test
    public void testGetTransactionManager() {
        System.out.println("getTransactionManager");
        startService();
        
        assertNotNull(service.getTransactionManager());
        assertTrue(core.getTransactionManager() == service.getTransactionManager());
    }

    /**
     * Test of doGet method, of class Service.
     */
    @Test
    public void testDoGet() throws Exception {
        System.out.println("doGet");
        startService();
        
        service.getLocalObject(new EOJ("001101")).setInternalData(EPC.x81, new ObjectData((byte)0x11));
        
        Node node = subnet.getLocalNode();
        EOJ eoj = new EOJ("001101");
        List<EPC> epcs = new LinkedList<EPC>();
        epcs.add(EPC.x81);
        
        GetResult result = service.doGet(node, eoj, epcs, 1000);
        Thread.sleep(500);
        
        assertEquals(1, result.countFrames());
        assertEquals(1, result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstOPC());
        assertEquals(EPC.x81, result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstPropertyAt(0).getEPC());
        assertEquals(1, result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstPropertyAt(0).getPDC());
        assertEquals(new Data((byte)0x11), result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstPropertyAt(0).getEDT());
    }

    /**
     * Test of doSet method, of class Service.
     */
    @Test
    public void testDoSet() throws Exception {
        System.out.println("doSet");
        startService();
        
        Node node = subnet.getLocalNode();
        EOJ eoj = new EOJ("001101");
        List<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(EPC.x81, new Data((byte)0x11)));
        
        SetResult result1 = service.doSet(node, eoj, properties, 1000, false);
        SetResult result2 = service.doSet(node, eoj, properties, 1000, true);
        Thread.sleep(500);
        
        assertEquals(0, result1.countFrames());
        assertEquals(1, result2.countFrames());
        assertEquals(1, result2.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstOPC());
        assertEquals(EPC.x81, result2.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstPropertyAt(0).getEPC());
        assertEquals(0, result2.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstPropertyAt(0).getPDC());
    }

    /**
     * Test of doSetGet method, of class Service.
     */
    @Test
    public void testDoSetGet() throws Exception {
        System.out.println("doSetGet");
        startService();
        
        Node node = subnet.getLocalNode();
        EOJ eoj = new EOJ("001101");
        List<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(EPC.x81, new Data((byte)0x11)));
        List<EPC> epcs = new LinkedList<EPC>();
        epcs.add(EPC.x81);
        
        SetGetResult result = service.doSetGet(node, eoj, properties, epcs, 1000);
        Thread.sleep(500);
        
        assertEquals(1, result.countFrames());
        assertEquals(1, result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstOPC());
        assertEquals(EPC.x81, result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstPropertyAt(0).getEPC());
        assertEquals(0, result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstPropertyAt(0).getPDC());
        
        assertEquals(1, result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getSecondOPC());
        assertEquals(EPC.x81, result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getSecondPropertyAt(0).getEPC());
        assertEquals(1, result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getSecondPropertyAt(0).getPDC());
        assertEquals(new Data((byte)0x11), result.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getSecondPropertyAt(0).getEDT());
    }

    /**
     * Test of doUpdateRemoteInfo method, of class Service.
     */
    @Test
    public void testDoUpdateRemoteInfo() throws Exception {
        System.out.println("doUpdateRemoteInfo");
        startService();
        
        UpdateRemoteInfoResult result = service.doUpdateRemoteInfo(1000);
        Thread.sleep(500);
        
        assertEquals(2, result.countNodes());
        assertEquals(2, result.countEOJs(subnet.getLocalNode()));
        assertEquals(1, result.countEOJs(peerSubnet.getLocalNode()));
        assertEquals(new EOJ("0ef001"), result.getEOJ(subnet.getLocalNode(), 0));
        assertEquals(new EOJ("001101"), result.getEOJ(subnet.getLocalNode(), 1));
        assertEquals(new EOJ("0ef001"), result.getEOJ(peerSubnet.getLocalNode(), 0));
    }

    /**
     * Test of doObserve method, of class Service.
     */
    @Test
    public void testDoObserve_0args() throws SubnetException, InterruptedException {
        System.out.println("doObserve");
        startService();
        
        ObserveResult observeResult = service.doObserve();
        
        peerService.doNotify(new EOJ("001101"), EPC.x80, new Data((byte)0x00), 1000);
        peerService.doNotify(new EOJ("001101"), EPC.xE0, new Data((byte)0x00), 1000);
        peerService.doNotify(new EOJ("001201"), EPC.x80, new Data((byte)0x00), 1000);
        Thread.sleep(500);
        
        assertEquals(3, observeResult.countFrames());
    }

    /**
     * Test of doObserve method, of class Service.
     */
    @Test
    public void testDoObserve_3args_9() throws Exception {
        System.out.println("doObserve");
        startService();
        
        List nodes = new LinkedList();
        List<EOJ> eojs = new LinkedList<EOJ>();
        List<EPC> epcs = new LinkedList<EPC>();
        nodes.add(peerSubnet.getLocalNode());
        eojs.add(new EOJ("001101"));
        epcs.add(EPC.xE0);
        
        ObserveResult observeResult = service.doObserve(nodes, eojs, epcs);
        
        peerService.doNotify(new EOJ("001101"), EPC.x80, new Data((byte)0x00), 1000);
        peerService.doNotify(new EOJ("001101"), EPC.xE0, new Data((byte)0x00), 1000);
        peerService.doNotify(new EOJ("001201"), EPC.x80, new Data((byte)0x00), 1000);
        Thread.sleep(500);
        
        assertEquals(1, observeResult.countFrames());
        
        assertEquals(new EOJ("001101"), observeResult.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getSEOJ());
    }

    /**
     * Test of doObserve method, of class Service.
     */
    @Test
    public void testDoObserve_Selector() throws SubnetException, InterruptedException {
        System.out.println("doObserve");
        startService();
        
        Selector<Frame> selector = new Selector<Frame>() {
            @Override
            public boolean match(Frame frame) {
                return frame.getCommonFrame().getEDATA(StandardPayload.class).getSEOJ().equals(new EOJ("001101"));
            }
        };
        
        ObserveResult observeResult = service.doObserve(selector);
        
        peerService.doNotify(new EOJ("001101"), EPC.x80, new Data((byte)0x00), 1000);
        peerService.doNotify(new EOJ("001201"), EPC.x80, new Data((byte)0x00), 1000);
        Thread.sleep(500);
        
        assertEquals(1, observeResult.countFrames());
        
        assertEquals(new EOJ("001101"), observeResult.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getSEOJ());
    }

    /**
     * Test of isCaptureEnabled method, of class Service.
     */
    @Test
    public void testIsCaptureEnabled() throws TooManyObjectsException, SubnetException {
        System.out.println("isCaptureEnabled");
        startService();
        
        assertTrue(service.isCaptureEnabled());
        
        Core core2 = new Core(new InternalSubnet("isCaptureEnabled"));
        core2.startService();
        Service service2 = new Service(core2);
        assertFalse(service2.isCaptureEnabled());
    }

    /**
     * Test of doCapture method, of class Service.
     */
    @Test
    public void testDoCapture() throws SubnetException, InterruptedException {
        System.out.println("doCapture");
        startService();
        
        CaptureResult result = service.doCapture();
        assertEquals(0, result.countFrames());
        
        service.doNotify(new EOJ("001101"), EPC.x80, new Data((byte)0x12), 1000, true);
        Thread.sleep(5000);
        assertEquals(5, result.countFrames());
        assertEquals(2, result.countSentFrames());
        assertEquals(3, result.countReceivedFrames());
        
        result.stopCapture();
    }

    /**
     * Test of getLocalData method, of class Service.
     */
    @Test
    public void testGetLocalData() throws Exception {
        System.out.println("getLocalData");
        startService();
        
        EOJ eoj = new EOJ("001101");
        EPC epc = EPC.x81;
        ObjectData data = new ObjectData((byte)0x34);
        assertTrue(service.setLocalData(eoj, epc, data));
        assertEquals(data, service.getLocalData(eoj, epc));
    }

    /**
     * Test of setLocalData method, of class Service.
     */
    @Test
    public void testSetLocalData() throws Exception {
        System.out.println("setLocalData");
        startService();
        
        EOJ eoj = new EOJ("001101");
        EPC epc = EPC.x81;
        ObjectData data = new ObjectData((byte)0x34);
        assertTrue(service.setLocalData(eoj, epc, data));
    }

    /**
     * Test of getRemoteData method, of class Service.
     */
    @Test
    public void testGetRemoteData_3args_1() throws Exception {
        System.out.println("getRemoteData");
        startService();
        
        Node node = peerSubnet.getLocalNode();
        NodeInfo nodeInfo = node.getNodeInfo();
        EOJ eoj = new EOJ("001101");
        TransactionManager transactionManager = service.getTransactionManager();
        TestRemoteObject remoteObject = new TestRemoteObject(subnet, node, eoj, transactionManager);
        
        service.getRemoteObjectManager().add(remoteObject);
        service.setRemoteData(node, eoj, EPC.x81, new ObjectData((byte)0x11));
        
        assertEquals(new ObjectData((byte)0x11), service.getRemoteData(nodeInfo, eoj, EPC.x81));
    }

    /**
     * Test of getRemoteData method, of class Service.
     */
    @Test
    public void testGetRemoteData_3args_2() throws Exception {
        System.out.println("setRemoteData");
        startService();
        
        Node node = peerSubnet.getLocalNode();
        EOJ eoj = new EOJ("001101");
        TransactionManager transactionManager = service.getTransactionManager();
        TestRemoteObject remoteObject = new TestRemoteObject(subnet, node, eoj, transactionManager);
        
        service.getRemoteObjectManager().add(remoteObject);
        service.setRemoteData(node, eoj, EPC.x81, new ObjectData((byte)0x11));
        
        assertEquals(new ObjectData((byte)0x11), service.getRemoteData(node, eoj, EPC.x81));
    }

    /**
     * Test of setRemoteData method, of class Service.
     */
    @Test
    public void testSetRemoteData_4args_1() throws Exception {
        System.out.println("setRemoteData");
        startService();
        
        Node node = peerSubnet.getLocalNode();
        NodeInfo nodeInfo = node.getNodeInfo();
        EOJ eoj = new EOJ("001101");
        TransactionManager transactionManager = service.getTransactionManager();
        TestRemoteObject remoteObject = new TestRemoteObject(subnet, node, eoj, transactionManager);
        
        service.getRemoteObjectManager().add(remoteObject);
        
        assertTrue(service.setRemoteData(nodeInfo, eoj, EPC.x81, new ObjectData((byte)0x11)));
        
        assertEquals(EPC.x81, remoteObject.setDataList.get(0).first);
        assertEquals(new ObjectData((byte)0x11), remoteObject.setDataList.get(0).second);
    }

    /**
     * Test of setRemoteData method, of class Service.
     */
    @Test
    public void testSetRemoteData_4args_2() throws Exception {
        System.out.println("setRemoteData");
        startService();
        
        Node node = peerSubnet.getLocalNode();
        EOJ eoj = new EOJ("001101");
        TransactionManager transactionManager = service.getTransactionManager();
        TestRemoteObject remoteObject = new TestRemoteObject(subnet, node, eoj, transactionManager);
        
        service.getRemoteObjectManager().add(remoteObject);
        
        assertTrue(service.setRemoteData(node, eoj, EPC.x81, new ObjectData((byte)0x11)));
        
        assertEquals(EPC.x81, remoteObject.setDataList.get(0).first);
        assertEquals(new ObjectData((byte)0x11), remoteObject.setDataList.get(0).second);
    }

    /**
     * Test of getRemoteNodes method, of class Service.
     */
    @Test
    public void testGetRemoteNodes() {
        System.out.println("getRemoteNodes");
        startService();
        
        Node node = peerSubnet.getLocalNode();
        
        assertTrue(service.registerRemoteEOJ(node, new EOJ("001101")));
        List<Node> result = service.getRemoteNodes();
        assertEquals(1, result.size());
        assertEquals(node, result.get(0));
    }

    /**
     * Test of countLocalEOJs method, of class Service.
     */
    @Test
    public void testCountLocalEOJs() {
        System.out.println("countLocalEOJs");
        startService();
        
        assertEquals(2, service.countLocalEOJs());
    }

    /**
     * Test of getLocalEOJ method, of class Service.
     */
    @Test
    public void testGetLocalEOJ() {
        System.out.println("getLocalEOJ");
        startService();
        
        assertEquals(new EOJ("0ef001"), service.getLocalEOJ(0));
        assertEquals(new EOJ("001101"), service.getLocalEOJ(1));
    }

    /**
     * Test of getLocalEOJs method, of class Service.
     */
    @Test
    public void testGetLocalEOJs() {
        System.out.println("getLocalEOJs");
        startService();
        
        List<EOJ> result = service.getLocalEOJs();
        assertEquals(2, result.size());
        assertEquals(new EOJ("0ef001"), result.get(0));
        assertEquals(new EOJ("001101"), result.get(1));
    }

    /**
     * Test of countRemoteEOJs method, of class Service.
     */
    @Test
    public void testCountRemoteEOJs_Node() {
        System.out.println("countRemoteEOJs");
        startService();
        
        Node node = peerSubnet.getLocalNode();
        
        assertEquals(0, service.countRemoteEOJs(node));
        service.registerRemoteEOJ(node, new EOJ("001101"));
        assertEquals(1, service.countRemoteEOJs(node));
        service.registerRemoteEOJ(node, new EOJ("001201"));
        assertEquals(2, service.countRemoteEOJs(node));
    }

    /**
     * Test of countRemoteEOJs method, of class Service.
     */
    @Test
    public void testCountRemoteEOJs_NodeInfo() throws Exception {
        System.out.println("countRemoteEOJs");
        startService();
        
        NodeInfo nodeInfo = peerSubnet.getLocalNode().getNodeInfo();
        
        assertEquals(0, service.countRemoteEOJs(nodeInfo));
        service.registerRemoteEOJ(nodeInfo, new EOJ("001101"));
        assertEquals(1, service.countRemoteEOJs(nodeInfo));
        service.registerRemoteEOJ(nodeInfo, new EOJ("001201"));
        assertEquals(2, service.countRemoteEOJs(nodeInfo));
    }

    /**
     * Test of getRemoteEOJ method, of class Service.
     */
    @Test
    public void testGetRemoteEOJ_Node_int() {
        System.out.println("getRemoteEOJ");
        startService();
        
        Node node = peerSubnet.getLocalNode();
        
        assertEquals(0, service.getRemoteEOJs(node).size());
        service.registerRemoteEOJ(node, new EOJ("001101"));
        service.registerRemoteEOJ(node, new EOJ("001201"));
        assertEquals(2, service.getRemoteEOJs(node).size());
        
        assertEquals(new EOJ("001101"), service.getRemoteEOJ(node, 0));
        assertEquals(new EOJ("001201"), service.getRemoteEOJ(node, 1));
    }

    /**
     * Test of getRemoteEOJ method, of class Service.
     */
    @Test
    public void testGetRemoteEOJ_NodeInfo_int() throws Exception {
        System.out.println("getRemoteEOJ");
        startService();
        
        NodeInfo nodeInfo = peerSubnet.getLocalNode().getNodeInfo();
        
        assertEquals(0, service.getRemoteEOJs(nodeInfo).size());
        service.registerRemoteEOJ(peerSubnet.getLocalNode(), new EOJ("001101"));
        service.registerRemoteEOJ(peerSubnet.getLocalNode(), new EOJ("001201"));
        assertEquals(2, service.getRemoteEOJs(nodeInfo).size());
        
        assertEquals(new EOJ("001101"), service.getRemoteEOJ(nodeInfo, 0));
        assertEquals(new EOJ("001201"), service.getRemoteEOJ(nodeInfo, 1));
    }

    /**
     * Test of getRemoteEOJs method, of class Service.
     */
    @Test
    public void testGetRemoteEOJs_Node() {
        System.out.println("getRemoteEOJs");
        startService();
        
        Node node = peerSubnet.getLocalNode();
        
        assertEquals(0, service.getRemoteEOJs(node).size());
        service.registerRemoteEOJ(node, new EOJ("001101"));
        assertEquals(1, service.getRemoteEOJs(node).size());
    }

    /**
     * Test of getRemoteEOJs method, of class Service.
     */
    @Test
    public void testGetRemoteEOJs_NodeInfo() throws Exception {
        System.out.println("getRemoteEOJs");
        startService();
        
        NodeInfo nodeInfo = peerSubnet.getLocalNode().getNodeInfo();
        
        assertEquals(0, service.getRemoteEOJs(nodeInfo).size());
        service.registerRemoteEOJ(peerSubnet.getLocalNode(), new EOJ("001101"));
        assertEquals(1, service.getRemoteEOJs(nodeInfo).size());
    }

    /**
     * Test of getLocalNode method, of class Service.
     */
    @Test
    public void testGetLocalNode() {
        System.out.println("getLocalNode");
        startService();
        
        assertEquals(subnet.getLocalNode(), service.getLocalNode());
    }

    /**
     * Test of getRemoteNode method, of class Service.
     */
    @Test
    public void testGetRemoteNode_String() throws Exception {
        System.out.println("getRemoteNode");
        startService();
        
        String nodeName1 = "aaa";
        String nodeName2 = "bbb";
        
        assertNotNull(service.getRemoteNode(nodeName1));
        assertEquals(subnet.getRemoteNode(nodeName1), service.getRemoteNode(nodeName1));
        assertFalse(subnet.getRemoteNode(nodeName1).equals(service.getRemoteNode(nodeName2)));
    }

    /**
     * Test of getRemoteNode method, of class Service.
     */
    @Test
    public void testGetRemoteNode_NodeInfo() throws Exception {
        System.out.println("getRemoteNode");
        startService();
        
        NodeInfo nodeInfo1 = new InternalNodeInfo("aaa");
        NodeInfo nodeInfo2 = new InternalNodeInfo("bbb");
        
        assertNotNull(service.getRemoteNode(nodeInfo1));
        assertEquals(subnet.getRemoteNode(nodeInfo1), service.getRemoteNode(nodeInfo1));
        assertFalse(subnet.getRemoteNode(nodeInfo1).equals(service.getRemoteNode(nodeInfo2)));
    }

    /**
     * Test of getGroupNode method, of class Service.
     */
    @Test
    public void testGetGroupNode() {
        System.out.println("getGroupNode");
        startService();
        
        assertEquals(subnet.getGroupNode(), service.getGroupNode());
    }

    /**
     * Test of getLocalObjects method, of class Service.
     */
    @Test
    public void testGetLocalObjects() {
        System.out.println("getLocalObjects");
        startService();
        
        List<LocalObject> result = service.getLocalObjects();
        
        assertEquals(1, result.size());
        assertEquals(new EOJ("001101"), result.get(0).getEOJ());
    }

    /**
     * Test of getLocalObject method, of class Service.
     */
    @Test
    public void testGetLocalObject() {
        System.out.println("getLocalObject");
        startService();
        
        assertNotNull(service.getLocalObject(new EOJ("0ef001")));
        assertNotNull(service.getLocalObject(new EOJ("001101")));
        assertNull(service.getLocalObject(new EOJ("001201")));
    }

    /**
     * Test of getRemoteObject method, of class Service.
     */
    @Test
    public void testGetRemoteObject_NodeInfo_EOJ() throws Exception {
        System.out.println("getRemoteObject");
        startService();
        
        Node node = service.getRemoteNode("dummy");
        NodeInfo nodeInfo = node.getNodeInfo();
        EOJ eoj = new EOJ("001101");
        
        assertNull(service.getRemoteObject(nodeInfo, eoj));
        service.registerRemoteEOJ(node, eoj);
        assertNotNull(service.getRemoteObject(nodeInfo, eoj));
    }

    /**
     * Test of getRemoteObject method, of class Service.
     */
    @Test
    public void testGetRemoteObject_Node_EOJ() throws SubnetException {
        System.out.println("getRemoteObject");
        startService();
        
        Node node = service.getRemoteNode("dummy");
        EOJ eoj = new EOJ("001101");
        
        assertNull(service.getRemoteObject(node, eoj));
        service.registerRemoteEOJ(node, eoj);
        assertNotNull(service.getRemoteObject(node, eoj));
    }

    /**
     * Test of registerRemoteEOJ method, of class Service.
     */
    @Test
    public void testRegisterRemoteEOJ_NodeInfo_EOJ() throws Exception {
        System.out.println("registerRemoteEOJ");
        startService();
        
        Node node = service.getRemoteNode("dummy");
        NodeInfo nodeInfo = node.getNodeInfo();
        EOJ eoj = new EOJ("001101");
        
        assertTrue(service.registerRemoteEOJ(nodeInfo, eoj));
        assertNotNull(service.getRemoteObject(node, eoj));
    }

    /**
     * Test of registerRemoteEOJ method, of class Service.
     */
    @Test
    public void testRegisterRemoteEOJ_Node_EOJ() throws SubnetException {
        System.out.println("registerRemoteEOJ");
        startService();
        
        Node node = service.getRemoteNode("dummy");
        EOJ eoj = new EOJ("001101");
        
        assertTrue(service.registerRemoteEOJ(node, eoj));
        assertNotNull(service.getRemoteObject(node, eoj));
    }
    /**
     * Test of doNotifyInstanceList method, of class Service.
     */
    @Test
    public void testDoNotifyInstanceList() throws Exception {
        System.out.println("doNotifyInstanceList");
        
        startService();
        
        CaptureResult captureResult = peerService.doCapture();
        
        NotifyResult notifyResult = service.doNotifyInstanceList(subnet.getGroupNode(), 0, false);
        
        assertEquals(0, notifyResult.countFrames());
        assertEquals(1, notifyResult.countRequestFrames());
        assertEquals(ESV.INF, notifyResult.getRequestFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getESV());
        assertEquals(1, notifyResult.getRequestFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstOPC());
        assertEquals(EPC.xD5, notifyResult.getRequestFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getFirstPropertyAt(0).getEPC());
        assertEquals(subnet.getGroupNode(), notifyResult.getRequestFrame(0).frame.getReceiver());
        Thread.sleep(100);
        
        assertEquals(1, captureResult.countFrames());
        ResultFrame frame = captureResult.getFrame(0);
        StandardPayload payload = frame.frame.getCommonFrame().getEDATA(StandardPayload.class);
        assertEquals(ESV.INF, payload.getESV());
        assertEquals(new EOJ("0ef001"), payload.getDEOJ());
        assertEquals(new EOJ("0ef001"), payload.getSEOJ());
        assertEquals(1, payload.getFirstOPC());
        assertEquals(EPC.xD5, payload.getFirstPropertyAt(0).getEPC());
        assertEquals(new Data((byte)0x01, (byte)0x00, (byte)0x11, (byte)0x01), payload.getFirstPropertyAt(0).getEDT());
        assertEquals(0, payload.getSecondOPC());
        
        captureResult.stopCapture();
    }

    /**
     * Test of doNotify method, of class Service.
     */
    @Test
    public void testDoNotify() throws Exception {
        System.out.println("testDoNotify");
        startService();
        
        EOJ eoj = new EOJ("001301");
        EPC epc1 = EPC.xA1;
        EPC epc2 = EPC.xA2;
        Data data1 = new Data((byte)0x31);
        Data data2 = new Data((byte)0x32);
        Data data3 = new Data((byte)0x33);
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc1, data1));
        properties.add(new Pair<EPC, Data>(epc1, data2));
        properties.add(new Pair<EPC, Data>(epc2, data3));
        
        NotifyResult notifyResult1 = service.doNotify(service.getGroupNode(), eoj, properties, 1000, true);
        
        Thread.sleep(500);
        assertFalse(notifyResult1.isDone());
        Thread.sleep(600);
        assertTrue(notifyResult1.isDone());
        
        boolean self = false;
        boolean peer = false;
        
        assertEquals(1, notifyResult1.countRequestFrames());
        assertEquals(service.getGroupNode(), notifyResult1.getRequestFrame(0).frame.getReceiver());
        assertEquals(ESV.INFC, notifyResult1.getRequestFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getESV());
        
        assertEquals(3, notifyResult1.countRequestData());
        
        assertEquals(eoj, notifyResult1.getRequestData(0).eoj);
        assertEquals(epc1, notifyResult1.getRequestData(0).epc);
        assertEquals(data1, notifyResult1.getRequestData(0).data);
        
        assertEquals(eoj, notifyResult1.getRequestData(1).eoj);
        assertEquals(epc1, notifyResult1.getRequestData(1).epc);
        assertEquals(data2, notifyResult1.getRequestData(1).data);
        
        assertEquals(eoj, notifyResult1.getRequestData(2).eoj);
        assertEquals(epc2, notifyResult1.getRequestData(2).epc);
        assertEquals(data3, notifyResult1.getRequestData(2).data);
        
        assertEquals(2, notifyResult1.countFrames());
        for (ResultFrame frame: notifyResult1.getFrameList()) {
            if (frame.frame.getSender().equals(subnet.getLocalNode())) {
                self = true;
            }
            if (frame.frame.getSender().equals(peerSubnet.getLocalNode())) {
                peer = true;
            }
        }
        
        assertTrue(self);
        assertTrue(peer);
        
        assertEquals(ESV.INFC_Res, notifyResult1.getFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getESV());
        assertEquals(ESV.INFC_Res, notifyResult1.getFrame(1).frame.getCommonFrame().getEDATA(StandardPayload.class).getESV());
        
        
        CaptureResult captureResult = peerService.doCapture();
        
        NotifyResult notifyResult2 = service.doNotify(service.getGroupNode(), eoj, properties, 1000, false);
        
        assertEquals(1, notifyResult2.countRequestFrames());
        assertEquals(service.getGroupNode(), notifyResult2.getRequestFrame(0).frame.getReceiver());
        assertEquals(ESV.INF, notifyResult2.getRequestFrame(0).frame.getCommonFrame().getEDATA(StandardPayload.class).getESV());
                
        assertEquals(3, notifyResult2.countRequestData());
        
        assertEquals(eoj, notifyResult2.getRequestData(0).eoj);
        assertEquals(epc1, notifyResult2.getRequestData(0).epc);
        assertEquals(data1, notifyResult2.getRequestData(0).data);
        
        assertEquals(eoj, notifyResult2.getRequestData(1).eoj);
        assertEquals(epc1, notifyResult2.getRequestData(1).epc);
        assertEquals(data2, notifyResult2.getRequestData(1).data);
        
        assertEquals(eoj, notifyResult2.getRequestData(2).eoj);
        assertEquals(epc2, notifyResult2.getRequestData(2).epc);
        assertEquals(data3, notifyResult2.getRequestData(2).data);
        
        Thread.sleep(500);
        assertFalse(notifyResult2.isDone());
        Thread.sleep(600);
        assertTrue(notifyResult2.isDone());
        
        assertEquals(0, notifyResult2.countFrames());
        
        assertEquals(1, captureResult.countFrames());
        ResultFrame frame = captureResult.getFrame(0);
        StandardPayload payload = frame.frame.getCommonFrame().getEDATA(StandardPayload.class);
        assertEquals(ESV.INF, payload.getESV());
        assertEquals(new EOJ("0ef001"), payload.getDEOJ());
        assertEquals(eoj, payload.getSEOJ());
        assertEquals(3, payload.getFirstOPC());
        assertEquals(epc1, payload.getFirstPropertyAt(0).getEPC());
        assertEquals(data1, payload.getFirstPropertyAt(0).getEDT());
        assertEquals(epc1, payload.getFirstPropertyAt(1).getEPC());
        assertEquals(data2, payload.getFirstPropertyAt(1).getEDT());
        assertEquals(epc2, payload.getFirstPropertyAt(2).getEPC());
        assertEquals(data3, payload.getFirstPropertyAt(2).getEDT());
        assertEquals(0, payload.getSecondOPC());
        
        captureResult.stopCapture();
    }
    
}
