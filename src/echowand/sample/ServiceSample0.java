package echowand.sample;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.DeviceObjectInfo;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.InetSubnet;
import echowand.net.Node;
import echowand.net.SubnetException;
import echowand.object.EchonetObjectException;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.LocalObjectConfig;
import echowand.service.ObjectNotFoundException;
import echowand.service.PropertyDelegate;
import echowand.service.PropertyUpdater;
import echowand.service.Service;
import echowand.service.Core;
import echowand.service.result.ResultData;
import echowand.service.result.ResultDataMatcherRule;
import echowand.service.result.GetResult;
import echowand.service.result.ObserveResult;
import echowand.service.result.UpdateRemoteInfoResult;
import echowand.util.LoggerConfig;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ServiceSample0 {
    
    static class TemperatureDelegate extends PropertyDelegate {
        
        public TemperatureDelegate() {
            super(EPC.xE0, true, false, true);
        }
        
        @Override
        public ObjectData getUserData(LocalObject localObject, EPC epc) {
            //byte b = getData(EPC.x80).getData().getResultDataList(0);
            //return new ObjectData(b, b);
            return new ObjectData((byte)0x12, (byte)0x34);
        }
    }
    
    static class DummyDataDelegate extends PropertyDelegate {
        
        public DummyDataDelegate() {
            super(EPC.xE1, false, false, true);
        }
        
        @Override
        public void notifyDataChanged(LocalObject localObject, EPC epc, ObjectData curData, ObjectData oldData) {
            System.out.println(curData);
        }
    }
    
    static class DummyDataUpdater extends PropertyUpdater {
        private int num=0;
        
        public DummyDataUpdater() {
            super(1000);
        }

        private static byte b1(int num) {
            return (byte) ((num & 0xff00) >> 8);
        }

        private static byte b0(int num) {
            return (byte) (num & 0x00ff);
        }

        @Override
        public void loop(LocalObject localObject) {
            setData(EPC.xE1, new ObjectData(b1(num), b0(num)));
            num++;
        }
    }
    
    public static void main(String[] args) throws TooManyObjectsException, SocketException, UnknownHostException, InterruptedException, ObjectNotFoundException, EchonetObjectException {
        // LoggerConfig.changeLogLevelAll(ResultBase.class.getName());
        // LoggerConfig.changeLogLevelAll(ObserveResult.class.getName());
        
        try {
            // Construct a Core
            // NetworkInterface ni = NetworkInterface.getByName("eth9");
            NetworkInterface ni = NetworkInterface.getByName("en0");
            InetSubnet subnet = Inet4Subnet.startSubnet(ni);
            Core core = new Core(subnet);
            
            // Create a device object information
            DeviceObjectInfo info = new DeviceObjectInfo();
            info.setClassEOJ(new ClassEOJ("0011"));
            info.add(EPC.xE0, true, false, false, 2);
            info.add(EPC.xE1, true, false, true, new byte[]{0x00, 0x00});
            
            // Register behaviors of a device object
            LocalObjectConfig config = new LocalObjectConfig(info);
            config.addPropertyDelegate(new TemperatureDelegate());
            config.addPropertyDelegate(new DummyDataDelegate());
            config.addPropertyUpdater(new DummyDataUpdater());
            
            // Register a device object config
            core.addLocalObjectConfig(config);
            
            // Start the core service
            core.startService();
            
            // Construct a service
            Service service = new Service(core);
            
            // Test doGet
            LinkedList<EPC> epcs = new LinkedList<EPC>();
            epcs.add(EPC.xE0);
            epcs.add(EPC.x80);
            epcs.add(EPC.x82);
            epcs.add(EPC.x84);
            // GetResult getResult1 = service.doGet(subnet.getGroupNode(), new ClassEOJ("0ef0"), EPC.x80, 1000);
            GetResult getResult1 = service.doGet(subnet.getGroupNode(), new ClassEOJ("0011"), epcs, 1000);
            getResult1.join();
            
            List<ResultData> dataList1 = getResult1.getResultDataList();
            for (int i=0; i<dataList1.size(); i++) {
                System.out.println("Get1 " + i + ": " + dataList1.get(i));
            }
            
            // Update remote node information
            UpdateRemoteInfoResult updateResult = service.doUpdateRemoteInfo(1000);
            updateResult.join();
            
            for (int i=0; i<updateResult.countNodes(); i++) {
                Node node = updateResult.getNode(i);
                for (int j=0; j<updateResult.countEOJs(node); j++) {
                    EOJ eoj = updateResult.getEOJ(node, j);
                    System.out.println("ResultUpdate: " + node + " " + eoj);
                    
                    if (eoj.isMemberOf(new ClassEOJ("0011"))) {
                        System.out.println("Begin getRemoteData(" + eoj + ", " + EPC.xE0 + ")");
                        ObjectData d = service.getRemoteData(node, eoj, EPC.xE0);
                        double t = (((0xff & d.get(0)) << 8) + (0xff & d.get(1))) / 10.0;
                        System.out.println("0xE0: " + d);
                        System.out.println("0xE0(double): " + t);
                        System.out.println("End getRemoteData");
                    }
                }
            }
            
            
            // Test doGet for all nodes in the network
            LinkedList<GetResult> getResults = new LinkedList<GetResult>();
            
            for (Node node : service.getRemoteNodes()) {
                // NodeInfo nodeInfo = new InetNodeInfo(Inet4Address.getByName("192.168.1.1"));
                LinkedList<EPC> epcs2 = new LinkedList<EPC>();
                epcs2.add(EPC.xE0);
                epcs2.add(EPC.x80);
                getResults.add(service.doGet(node, new EOJ("001101"), epcs2, 1000));
            }
            
            for (GetResult getResult: getResults) {
                getResult.join();
                List<ResultData> dataList2 = getResult.getResultDataList(new ResultDataMatcherRule());
                for (int i = 0; i < dataList2.size(); i++) {
                    System.out.println("Get2 " + ": " + i + " " + dataList2.get(i));
                }
            }

            // Test doObserve
            LinkedList<EOJ> eojs = new LinkedList<EOJ>();
            eojs.add(new EOJ("013000"));
            eojs.add(new EOJ("05fd02"));
            eojs.add(new EOJ("001100"));
            LinkedList<EPC> epcs2 = new LinkedList<EPC>();
            epcs2.add(EPC.xBA);
            epcs2.add(EPC.xBB);
            epcs2.add(EPC.xE1);
            // ObserveResult observeResult = service.doObserve(new FrameMatcherRule(null, eojs, epcs2));
            ObserveResult observeResult = service.doObserve(new LinkedList<Node>(), eojs, epcs2);
            
            Thread.sleep(5000);
            
            observeResult.stopObserve();
            System.out.println("done");
            
            for (ResultData resultData: observeResult.getResultDataList()) {
                System.out.println("Observe: " + resultData);
            }
            
            Thread.sleep(5000);
            
            System.exit(0);
        } catch (SubnetException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
