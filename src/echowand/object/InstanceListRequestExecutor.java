package echowand.object;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.SetGetTransactionConfig;
import echowand.logic.Transaction;
import echowand.logic.TransactionListener;
import echowand.logic.TransactionManager;
import echowand.net.Node;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * 他のノード内にあるインスタンスを問い合わせるトランザクションを実行
 * @author Yoshiki Makino
 */
public class InstanceListRequestExecutor {
    private static final Logger logger = Logger.getLogger(InstanceListRequestExecutor.class.getName());
    private static final String className = InstanceListRequestExecutor.class.getName();
    
    private Subnet subnet;
    private TransactionManager transactionManager;
    private RemoteObjectManager remoteManager;
    private Transaction transaction;
    private Node node;
    private int timeout;
    private boolean done;
    
    private LinkedList<TransactionListener> listeners;
    
    /**
     * InstanceListRequestExecutorを生成する。
     * @param subnet Subnetの指定
     * @param transactionManager TransactionManagerの指定
     * @param remoteManager 更新するRemoteObjectManagerの指定
     */
    public InstanceListRequestExecutor(Subnet subnet, TransactionManager transactionManager, RemoteObjectManager remoteManager) {
        logger.entering(className, "InstanceListRequestExecutor", new Object[]{subnet, transactionManager, remoteManager});
        
        this.subnet = subnet;
        this.transactionManager = transactionManager;
        this.remoteManager = remoteManager;
        this.transaction = null;
        this.node = null;
        this.timeout = 2000;
        this.done = false;
        this.listeners = new LinkedList<TransactionListener>();
        
        logger.exiting(className, "InstanceListRequestExecutor");
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setNode(Node node) {
        this.node = node;
    }
    
    public Node getNode() {
        return node;
    }
    
    public boolean isDone() {
        return transaction.isDone();
    }
    
    public int countTransactionListeners() {
        return listeners.size();
    }
    
    public TransactionListener getTransactionListener(int index) {
        return listeners.get(index);
    }
    
    public boolean addTransactionListener(TransactionListener listener) {
        return listeners.add(listener);
    }
    
    public boolean removeTransactionListener(TransactionListener listener) {
        return listeners.remove(listener);
    }
    
    private Transaction createTransaction() {
        logger.entering(className, "createTransaction");
        
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        transactionConfig.setSenderNode(subnet.getLocalNode());
        
        if (node == null) {
            transactionConfig.setReceiverNode(subnet.getGroupNode());
        } else {
            transactionConfig.setReceiverNode(node);
        }
        
        transactionConfig.setSourceEOJ(new EOJ("0ef001"));
        transactionConfig.setDestinationEOJ(new EOJ("0ef001"));
        transactionConfig.addGet(EPC.xD6);
        Transaction newTransaction = transactionManager.createTransaction(transactionConfig);
        newTransaction.setTimeout(timeout);
        
        NodeProfileObjectListener profileListener = new NodeProfileObjectListener(remoteManager, transactionManager);
        newTransaction.addTransactionListener(profileListener);
        
        for (TransactionListener listener : listeners) {
            newTransaction.addTransactionListener(listener);
        }
        
        logger.exiting(className, "createTransaction", newTransaction);
        return newTransaction;
    }
    
    /**
     * 他のノードのインスタンスリストを要求するトランザクションを実行する
     * @return 成功した場合はtrue、トランザクションがすでに開始されている場合にはfalse
     * @throws SubnetException トランザクションに問題が発生した場合
     */
    public synchronized boolean execute() throws SubnetException {
        logger.entering(className, "execute");

        if (transaction != null) {
            logger.exiting(className, "execute", false);
            return false;
        }

        transaction = createTransaction();
        transaction.execute();

        logger.exiting(className, "execute", true);
        return true;
    }
    
    /**
     * トランザクションが終了するまで待機する。
     * @return 成功した場合はtrue、トランザクションの開始前、あるいは複数回joinが呼ばれた時はfalse
     * @throws InterruptedException 割り込みが発生した場合
     */
    public synchronized boolean join() throws InterruptedException {
        logger.entering(className, "join");
        
        if (done || transaction == null) {
            logger.exiting(className, "join", false);
            return false;
        }
        
        transaction.join();
        done = true;
        
        logger.exiting(className, "join", true);
        return true;
    }
}
