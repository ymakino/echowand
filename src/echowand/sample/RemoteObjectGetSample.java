package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.NodeProfileInfo;
import echowand.logic.MainLoop;
import echowand.logic.TransactionManager;
import echowand.net.InetSubnet;
import echowand.net.Node;
import echowand.object.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yoshiki Makino
 */
public class RemoteObjectGetSample {
    public static void main(String[] args) {
        try {
            final InetSubnet subnet = new InetSubnet();
            final TransactionManager transactionManager = new TransactionManager(subnet);
            RemoteObjectManager remoteManager = new RemoteObjectManager();
            LocalObjectManager localManager = new LocalObjectManager();
            
            LocalObject nodeProfileObject = new LocalObject(new NodeProfileInfo());
            nodeProfileObject.addDelegate(new NodeProfileObjectDelegate(localManager));
            localManager.add(nodeProfileObject);

            MainLoop mainLoop = new MainLoop();
            mainLoop.setSubnet(subnet);
            mainLoop.addListener(transactionManager);
            
            Thread mainThread = new Thread(mainLoop);
            mainThread.setDaemon(true);
            mainThread.start();

            InstanceListRequestExecutor instanceListRequest = new InstanceListRequestExecutor(subnet, transactionManager, remoteManager);
            instanceListRequest.execute();
            instanceListRequest.join();

            for (Node node : remoteManager.getNodes()) {
                RemoteObject remoteObject = remoteManager.get(node, new EOJ("001101"));
                
                System.out.print(node + "\n");
                for (int i = 0x80; i <= 0xff; i++) {
                    EPC epc = EPC.fromByte((byte) i);
                    if (remoteObject.isGettable(epc)) {
                        System.out.println("\t" + epc + " " + remoteObject.getData(epc));
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(RemoteObjectGetSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}