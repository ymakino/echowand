/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.NodeProfileInfo;
import echowand.logic.MainLoop;
import echowand.logic.RequestDispatcher;
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
public class RemoteObjectSample {
    public static void main(String[] args) {
        try {
            final InetSubnet subnet = new InetSubnet();
            final TransactionManager transactionManager = new TransactionManager(subnet);
            RemoteObjectManager remoteManager = new RemoteObjectManager();
            LocalObjectManager localManager = new LocalObjectManager();
            
            final RequestDispatcher dispatcher = new RequestDispatcher();
            dispatcher.addRequestProcessor(new AnnounceRequestProcessor(localManager, remoteManager));
            
            LocalObject nodeProfileObject = new LocalObject(new NodeProfileInfo());
            nodeProfileObject.addDelegate(new NodeProfileObjectDelegate(localManager));
            nodeProfileObject.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
            localManager.add(nodeProfileObject);

            MainLoop mainLoop = new MainLoop();
            mainLoop.setSubnet(subnet);
            mainLoop.addListener(transactionManager);
            mainLoop.addListener(dispatcher);
            
            Thread mainThread = new Thread(mainLoop);
            mainThread.start();

            InstanceListRequestExecutor instanceListRequest = new InstanceListRequestExecutor(subnet, transactionManager, remoteManager);
            instanceListRequest.execute();
            instanceListRequest.join();

            for (Node node : remoteManager.getNodes()) {
                RemoteObject remoteObject = remoteManager.get(node, new EOJ("001101"));
                remoteObject.addObserver(new RemoteObjectObserver() {
                    @Override
                    public void notifyData(RemoteObject object, EPC epc, ObjectData data) {
                        System.out.println(object.getNode() + " " + object.getEOJ() + " " + epc + " " + data);
                    }
                });
                
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
