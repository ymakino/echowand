package echowand.logic;

import echowand.net.Frame;
import echowand.net.Subnet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Transactionを管理するし、受信したフレームを適切なTransactionに処理させる。
 * @author Yoshiki Makino
 */
public class TransactionManager implements Listener {
    private static final Logger logger = Logger.getLogger(TransactionManager.class.getName());
    private static final String className = TransactionManager.class.getName();
    
    private Subnet subnet;
    private LinkedList<Transaction> transactions;
    
    /**
     * TransactinManagerを生成する。
     * @param subnet 生成するTransactionManagerが属するサブネット
     */
    public TransactionManager(Subnet subnet) {
        logger.entering(className, "TransactionManager", subnet);
        
        this.subnet = subnet;
        transactions = new LinkedList<Transaction>();
        
        logger.exiting(className, "TransactionManager");
    }
    
    private synchronized LinkedList<Transaction> cloneTransactions() {
        return new LinkedList<Transaction>(transactions);
    }
    
    /**
     * Transactionを処理中として登録する。
     * @param t 登録するトランザクション
     */
    protected synchronized void addTransaction(Transaction t) {
        logger.entering(className, "addTransaction", t);
        
        transactions.add(t);
        
        logger.exiting(className, "addTransaction");
    }
    
    /**
     * Transactionの処理が終了したとして登録を抹消する。
     * @param t 登録を抹消するトランザクション
     */
    protected synchronized void removeTransaction(Transaction t) {
        logger.entering(className, "removeTransaction", t);
        
        transactions.remove(t);
        
        logger.exiting(className, "removeTransaction");
    }
    
    /**
     * 処理中のTransactionの数を返す。
     * @return 処理中のTransaction数
     */
    public synchronized int countActiveTransactions() {
        return transactions.size();
    }
    
    /**
     * 受信したフレームのTIDを確認して適切なTransactionのreceiveResponseを呼び出す。
     * 既に処理済みのフレームは無視を行なう。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean process(Subnet subnet, Frame frame, boolean processed) {
        logger.entering(className, "process", new Object[]{subnet, frame, processed});
        
        boolean ret = false;
        
        if (processed) {
            logger.exiting(className, "process", ret);
            return ret;
        }
        
        for (Transaction transaction : cloneTransactions()) {
            if (frame.getCommonFrame().getTID() == transaction.getTID()) {
                ret |= transaction.receiveResponse(frame);
            }
        }
        
        logger.exiting(className, "process", ret);
        return ret;
    }

    /**
     * このTransactionManagerに所属するTransactionを生成する。
     * @param transactionConfig トランザクションの詳細設定
     * @return このTransactionManagerに所属するTransaction
     */
    public Transaction createTransaction(TransactionConfig transactionConfig) {
        logger.entering(className, "createTransaction", transactionConfig);
        
        Transaction newTransaction = new Transaction(subnet, this, transactionConfig);
        
        logger.exiting(className, "createTransaction");
        
        return newTransaction;
    }
}
