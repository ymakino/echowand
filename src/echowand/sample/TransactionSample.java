package echowand.sample;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.MainLoop;
import echowand.logic.SetGetTransactionConfig;
import echowand.logic.Transaction;
import echowand.logic.TransactionManager;
import echowand.net.InetSubnet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Yoshiki Makino
 */
public class TransactionSample {
    public static void main(String[] args) {
        try {
            final InetSubnet subnet = new InetSubnet();
            final TransactionManager transactionManager = new TransactionManager(subnet);

            MainLoop mainLoop = new MainLoop();
            mainLoop.setSubnet(subnet);
            mainLoop.addListener(transactionManager);
            
            Thread mainThread = new Thread(mainLoop);
            mainThread.setDaemon(true);
            mainThread.start();

            SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
            transactionConfig.setSenderNode(subnet.getLocalNode());
            transactionConfig.setReceiverNode(subnet.getGroupNode());
            transactionConfig.setSourceEOJ(new EOJ("0ef001"));
            transactionConfig.setDestinationEOJ(new EOJ("013001"));
            transactionConfig.addSet(EPC.x80, new Data((byte) 0x30));
            Transaction transaction = transactionManager.createTransaction(transactionConfig);
            
            transaction.execute();
            transaction.join();
        } catch (Exception ex) {
            Logger.getLogger(TransactionSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

