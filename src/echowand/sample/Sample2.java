package echowand.sample;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.NodeProfileInfo;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.*;
import echowand.net.Inet4Subnet;
import echowand.net.SubnetException;
import echowand.object.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yoshiki Makino
 */
public class Sample2 {
    public static LocalObject createNodeProfileObject(LocalObjectManager manager) {
        LocalObject nodeProfileObject = new LocalObject(new NodeProfileInfo());
        nodeProfileObject.addDelegate(new NodeProfileObjectDelegate(manager));
        nodeProfileObject.setData(EPC.xD5, new ObjectData((byte)0x01, (byte)0x0e, (byte)0xf0, (byte)0x01));
        return nodeProfileObject;
    }
    
    public static void main(String[] args) throws TooManyObjectsException {
        MainLoop loop = new MainLoop();
        Inet4Subnet subnet;
        
        String interfaceName = null;
        
        if (args.length == 1) {
            interfaceName = args[0];
        }

        try {
            if (interfaceName == null) {
                subnet = new Inet4Subnet();
                subnet.startService();
            } else {
                NetworkInterface networkInterface = NetworkInterface.getByName(args[0]);
                subnet = new Inet4Subnet(networkInterface);
                subnet.startService();
            }
        } catch (SubnetException ex) {
            ex.printStackTrace();
            Logger.getLogger(Sample2.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (SocketException ex) {
            Logger.getLogger(Sample2.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        loop.setSubnet(subnet);
        
        LocalObjectManager manager = new LocalObjectManager();
        LocalObject nodeProfileObject = createNodeProfileObject(manager);
        manager.add(nodeProfileObject);
        SetGetRequestProcessor processor = new SetGetRequestProcessor(manager);
        RequestDispatcher requestDispatcher = new RequestDispatcher();
        requestDispatcher.addRequestProcessor(processor);
        loop.addListener(requestDispatcher);
        
        TransactionManager transactionManager = new TransactionManager(subnet);
        loop.addListener(transactionManager);
        
        Thread loopThread = new Thread(loop);
        loopThread.setDaemon(true);
        loopThread.start();

        try {
            AnnounceTransactionConfig transactionConfig = new AnnounceTransactionConfig();
            
            transactionConfig.setSenderNode(subnet.getLocalNode());
            transactionConfig.setReceiverNode(subnet.getGroupNode());
            transactionConfig.setSourceEOJ(new EOJ("0EF001"));
            transactionConfig.setDestinationEOJ(new EOJ("0EF001"));
            transactionConfig.setResponseRequired(false);
            transactionConfig.addAnnounce(EPC.xD5, new Data((byte)0x01, (byte)0x0e, (byte)0xf0, (byte)0x01));
            Transaction transaction = transactionManager.createTransaction(transactionConfig);
            transaction.setTimeout(1000);
            transaction.execute();
        } catch (SubnetException e) {
            e.printStackTrace();
        }
        
        for (;;) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            manager.add(new LocalObject(new TemperatureSensorInfo()));
            System.out.println("A temperature sensor has been added");
        }
    }
}
