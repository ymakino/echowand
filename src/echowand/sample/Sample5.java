package echowand.sample;

import echowand.common.EPC;
import echowand.info.NodeProfileInfo;
import echowand.logic.MainLoop;
import echowand.logic.RequestDispatcher;
import echowand.logic.TooManyObjectsException;
import echowand.logic.TransactionManager;
import echowand.net.Inet4Subnet;
import echowand.net.Node;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import echowand.object.*;
import echowand.util.Selector;
import java.net.BindException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yoshiki Makino
 */
public class Sample5 {
    
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
        return nodeProfileObject;
    }
    
    
    public static LinkedList<Node> getRemoteNodes(RemoteObjectManager remoteManager) {
        LinkedList<RemoteObject> objects = remoteManager.get(new Selector<RemoteObject>(){
            @Override
            public boolean select(RemoteObject obj){ return true; }
        });
        
        LinkedList<Node> nodes = new LinkedList<Node>();
        for (RemoteObject object : objects) {
            if (!nodes.contains(object.getNode())) {
                nodes.add(object.getNode());
            }
        }
        
        return nodes;
    }
    
    public static LinkedList<RemoteObject> getRemoteObjectsAt(final Node node, RemoteObjectManager remoteManager) {
        return remoteManager.get(new Selector<RemoteObject>(){
            @Override
            public boolean select(RemoteObject obj){ return obj.getNode().equals(node); }
        });
    }
    
    public static void main(String[] args) throws TooManyObjectsException, BindException {
        Inet4Subnet subnet;

        try {
            // ECHONET Liteメッセージ送受信に利用するIPのサブネットを作成
            subnet = Inet4Subnet.startSubnet();
        } catch (SubnetException e) {
            e.printStackTrace();
            return;
        }
        
        TransactionManager transactionManager = new TransactionManager(subnet);
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        LocalObjectManager localManager = new LocalObjectManager();
        LocalObject nodeProfile = createNodeProfileObject(subnet, localManager, transactionManager);
        localManager.add(nodeProfile);
        
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
        
        InstanceListRequestExecutor updater = new InstanceListRequestExecutor(subnet, transactionManager, remoteManager);
        try {
            updater.execute();
            updater.join();
        } catch (SubnetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        for (Node node: getRemoteNodes(remoteManager)) {
            System.out.println(node);
            for (RemoteObject object: getRemoteObjectsAt(node, remoteManager)) {
                try {
                    System.out.println("  " + object.getEOJ() + " " + object.getData(EPC.x9F));
                } catch (EchonetObjectException ex) {
                    Logger.getLogger(Sample5.class.getName()).log(Level.SEVERE, object.getEOJ().toString(), ex);
                }
            }
        }
    }
}
