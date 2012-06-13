package echowand.object;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.SetGetTransactionConfig;
import echowand.logic.Transaction;
import echowand.logic.TransactionManager;
import echowand.net.Subnet;
import echowand.net.SubnetException;

/**
 * 他のノード内にあるインスタンスを問い合わせるトランザクションを実行
 * @author Yoshiki Makino
 */
public class InstanceListRequestExecutor {
    private Subnet subnet;
    private TransactionManager transactionManager;
    private RemoteObjectManager remoteManager;
    private Transaction transaction;
    private int timeout;
    private boolean done;
    
    /**
     * InstanceListRequestExecutorを生成する。
     * @param subnet Subnetの指定
     * @param transactionManager TransactionManagerの指定
     * @param remoteManager 更新するRemoteObjectManagerの指定
     */
    public InstanceListRequestExecutor(Subnet subnet, TransactionManager transactionManager, RemoteObjectManager remoteManager) {
        this.subnet = subnet;
        this.transactionManager = transactionManager;
        this.remoteManager = remoteManager;
        this.transaction = null;
        this.timeout = 2000;
        this.done = false;
    }
    
    private Transaction createTransaction() {
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        transactionConfig.setSenderNode(subnet.getLocalNode());
        transactionConfig.setReceiverNode(subnet.getGroupNode());
        transactionConfig.setSourceEOJ(new EOJ("0ef001"));
        transactionConfig.setDestinationEOJ(new EOJ("0ef001"));
        transactionConfig.addGet(EPC.xD6);
        Transaction newTransaction = transactionManager.createTransaction(transactionConfig);
        newTransaction.setTimeout(timeout);
        NodeProfileObjectListener profileListener = new NodeProfileObjectListener(remoteManager, transactionManager);
        newTransaction.addTransactionListener(profileListener);
        return newTransaction;
    }
    
    /**
     * 他のノードのインスタンスリストを要求するトランザクションを実行する
     * @return 成功した場合はtrue、トランザクションがすでに開始されている場合にはfalse
     * @throws SubnetException トランザクションに問題が発生した場合
     */
    public synchronized boolean execute() throws SubnetException {
        if (transaction != null) {
            return false;
        }
        
        transaction = createTransaction();
        transaction.execute();
        
        return true;
    }
    
    /**
     * トランザクションが終了するまで待機する。
     * @return 成功した場合はtrue、トランザクションの開始前、あるいは複数回joinが呼ばれた時はfalse
     * @throws InterruptedException 割り込みが発生した場合
     */
    public synchronized boolean join() throws InterruptedException {
        if (done || transaction == null) {
            return false;
        }
        
        transaction.join();
        done = true;
        
        return true;
    }
    
    /**
     * 他のノードのインスタンスリストを要求するトランザクションを実行する
     * トランザクションが終了するまで待機する。
     * @return 成功した場合はtrue、トランザクションがすでに開始されている場合にはfalse
     * @throws SubnetException トランザクションに問題が発生した場合
     * @throws InterruptedException 割り込みが発生した場合
     */
    public boolean executeAndJoin() throws SubnetException, InterruptedException {
        return execute() && join();
    }
}
