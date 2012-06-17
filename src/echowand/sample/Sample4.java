package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.HomeAirConditionerInfo;
import echowand.info.NodeProfileInfo;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.*;
import echowand.net.InetSubnet;
import echowand.net.Node;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import echowand.object.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Yoshiki Makino
 */
class CountUpThread extends Thread {
    private LocalObject object;
    public CountUpThread (LocalObject object) { this.object = object; }
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CountUpThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            ObjectData d = object.getData(EPC.x88);
            object.forceSetData(EPC.x88, new ObjectData((byte)(d.get(0)+1)));
            object.forceSetData(EPC.x80, new ObjectData((byte)(d.get(0)+1)));
        }
        
    }
}
class SetRandomThread extends Thread {
    private LocalObject object;
    private EPC epc;
    private int len;
    private int interval;
    private static Random random = new Random();
    
    public SetRandomThread(LocalObject object, EPC epc, int len, int interval) {
        this.object = object;
        this.epc = epc;
        this.len = len;
        this.interval = interval;
    }
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
                Logger.getLogger(CountUpThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            byte[] bytes = new byte[len];
            random.nextBytes(bytes);
            object.forceSetData(epc, new ObjectData(bytes));
        }
    }
}
class PrintNotifiedDataObserver implements RemoteObjectObserver {
    @Override
    public void notifyData(RemoteObject object, EPC epc, ObjectData data) {
        System.out.println(object.getNode() + ", " + object.getEOJ() + ", " + epc + ", " + data);
    }
}
public class Sample4 {
    
    public static String peerAddress1 = "192.168.1.1";
    public static String peerAddress2 = "192.168.1.2";
    
    public static NodeProfileInfo createNodeProfileInfo() {
        NodeProfileInfo nodeProfileInfo = new NodeProfileInfo();
        nodeProfileInfo.add(EPC.x97, true, false, false, 2);
        nodeProfileInfo.add(EPC.x98, true, false, false, 4);
        return nodeProfileInfo;
    }
    public static LocalObject createNodeProfileObject(Subnet subnet, LocalObjectManager manager, TransactionManager transactionManager) {
        LocalObject nodeProfileObject = new LocalObject(createNodeProfileInfo());
        nodeProfileObject.addDelegate(new LocalObjectDateTimeDelegate());
        nodeProfileObject.addDelegate(new NodeProfileObjectDelegate(manager));
        nodeProfileObject.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
        new CountUpThread(nodeProfileObject).start();
        return nodeProfileObject;
    }
    
    public static Transaction createAnnounceTransaction(Subnet subnet, TransactionManager manager, LocalObject object, EPC epc) {
        AnnounceTransactionConfig transactionConfig = new AnnounceTransactionConfig();
        transactionConfig.setSenderNode(subnet.getLocalNode());
        transactionConfig.setReceiverNode(subnet.getGroupNode());
        transactionConfig.setSourceEOJ(new EOJ("0EF001"));
        transactionConfig.setDestinationEOJ(new EOJ("0EF001"));
        transactionConfig.setResponseRequired(false);
        ObjectData data = object.forceGetData(epc);
        transactionConfig.addAnnounce(epc, data.getData());
        int extraLen = data.getExtraSize();
        for (int i = 0; i < extraLen; i++) {
            transactionConfig.addAnnounce(epc, data.getExtraDataAt(i));
        }
        
        Transaction transaction = manager.createTransaction(transactionConfig);
        transaction.setTimeout(1000);
        
        return transaction;
    }
    
    static class NodeProfileAnnounceThread extends Thread {
        private Subnet subnet;
        private LocalObject nodeProfile;
        private TransactionManager transactionManager;
        
        public NodeProfileAnnounceThread(Subnet subnet, LocalObject nodeProfile, TransactionManager transactionManager) {
            this.subnet = subnet;
            this.nodeProfile = nodeProfile;
            this.transactionManager = transactionManager;
        }
        
        @Override
        public void run() {
            for (;;) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                Transaction t1 = createAnnounceTransaction(subnet, transactionManager, nodeProfile, EPC.xD5);

                try {
                    t1.execute();
                    t1.join();
                } catch (SubnetException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void main(String[] args) throws TooManyObjectsException {
        InetSubnet subnet = new InetSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        LocalObjectManager localManager = new LocalObjectManager();
        LocalObject nodeProfile = createNodeProfileObject(subnet, localManager, transactionManager);
        localManager.add(nodeProfile);
        
        for (int i = 0; i < 20; i++) {
            HomeAirConditionerInfo info = new HomeAirConditionerInfo();
            info.add(EPC.x80, true, true, true, 1);
            info.add(EPC.x97, true, false, false, 4);
            info.add(EPC.x98, true, false, false, 8);
            LocalObject object = new LocalObject(info);
            object.addDelegate(new LocalObjectDateTimeDelegate());
            object.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
            new SetRandomThread(object, EPC.xB0, 1, 1000).start();
            localManager.add(object);
        }
        
        for (int i = 0; i < 20; i++) {
            LocalObject object = new LocalObject(new TemperatureSensorInfo());
            // object.addDelegate(new LocalObjectRandomDelegate(EPC.xE0, 2));
            object.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
            new SetRandomThread(object, EPC.xE0, 2, 1000).start();
            localManager.add(object);
        }
        
        RequestDispatcher requestDispatcher = new RequestDispatcher();
        requestDispatcher.addRequestProcessor(new SetGetRequestProcessor(localManager));
        requestDispatcher.addRequestProcessor(new AnnounceRequestProcessor(localManager, remoteManager));
        
        MainLoop loop = new MainLoop();
        loop.setSubnet(subnet);
        loop.addListener(requestDispatcher);
        loop.addListener(transactionManager);
        
        Thread loopThread = new Thread(loop);
        loopThread.setDaemon(true);
        loopThread.start();
        
        Transaction t1 = createAnnounceTransaction(subnet, transactionManager, nodeProfile, EPC.xD5);
        
        try {
            t1.execute();
            t1.join();
        } catch (SubnetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        new NodeProfileAnnounceThread(subnet, nodeProfile, transactionManager).start();

        Transaction t2 = createTransaction(subnet, transactionManager, remoteManager, transactionManager);
        try {
            t2.execute();
            t2.join();
        } catch (SubnetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        try {
            Node node = subnet.getRemoteNode(InetAddress.getByName(peerAddress1), 3610);
            EPC epc = EPC.x9E;
            System.out.println(remoteManager.get(node, new EOJ("0ef001")).getData(epc));
            System.out.println(remoteManager.get(node, new EOJ("013001")).getData(epc));
            System.out.println(remoteManager.get(node, new EOJ("001101")).getData(epc));
            System.out.println(remoteManager.get(node, new EOJ("000301")).getData(epc));
            RemoteObject remoteObject;
            remoteObject = remoteManager.get(node, new EOJ("013003"));
            remoteObject.addObserver(new PrintNotifiedDataObserver());
            remoteObject = remoteManager.get(node, new EOJ("001101"));
            remoteObject.addObserver(new PrintNotifiedDataObserver());
            
            node = subnet.getRemoteNode(InetAddress.getByName(peerAddress2), 3610);
            epc = EPC.x9E;
            System.out.println(remoteManager.get(node, new EOJ("0ef001")).getData(epc));
            System.out.println(remoteManager.get(node, new EOJ("013001")).getData(epc));
            System.out.println(remoteManager.get(node, new EOJ("001101")).getData(epc));
            System.out.println(remoteManager.get(node, new EOJ("000301")).getData(epc));
            remoteObject = remoteManager.get(node, new EOJ("013001"));
            remoteObject.addObserver(new PrintNotifiedDataObserver());
            remoteObject = remoteManager.get(node, new EOJ("001101"));
            remoteObject.addObserver(new PrintNotifiedDataObserver());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (EchonetObjectException e) {
            e.printStackTrace();
        }
        
        for (;;) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static Transaction createTransaction(Subnet subnet, TransactionManager transactionManager, RemoteObjectManager remoteManager, TransactionManager responseListener) {
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        transactionConfig.setSenderNode(subnet.getLocalNode());
        transactionConfig.setReceiverNode(subnet.getGroupNode());
        transactionConfig.setSourceEOJ(new EOJ("0ef001"));
        transactionConfig.setDestinationEOJ(new EOJ("0EF001"));
        transactionConfig.addGet(EPC.xD6);
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        transaction.setTimeout(1000);
        NodeProfileObjectListener profileListener = new NodeProfileObjectListener(remoteManager, responseListener);
        transaction.addTransactionListener(profileListener);
        return transaction;
    }
}
