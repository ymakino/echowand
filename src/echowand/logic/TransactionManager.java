package echowand.logic;

import echowand.net.Frame;
import echowand.net.Subnet;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Transactionを管理するし、受信したフレームを適切なTransactionに処理させる。
 * @author Yoshiki Makino
 */
public class TransactionManager implements Listener {
    private Subnet subnet;
    private LinkedList<Transaction> transactions;
    
    /**
     * TransactinManagerを生成する。
     * @param subnet 生成するTransactionManagerが属するサブネット
     */
    public TransactionManager(Subnet subnet) {
        this.subnet = subnet;
        transactions = new LinkedList<Transaction>();
    }
    
    /**
     * Transactionを処理中として登録する。
     * @param t 登録するトランザクション
     */
    protected synchronized void addTransaction(Transaction t) {
        transactions.add(t);
    }
    
    /**
     * Transactionの処理が終了したとして登録を抹消する。
     * @param t 登録を抹消するトランザクション
     */
    protected synchronized void removeTransaction(Transaction t) {
        transactions.remove(t);
    }
    
    /**
     * 処理中のTransactionの数を返す。
     * @return 処理中のTransaction数
     */
    public synchronized int countActiveTransactions() {
        return transactions.size();
    }
    
    /**
     * 受信したフレームのTIDを確認して適切なTransactionのrecvResponseを呼び出す。
     * 既に処理済みのフレームは無視を行なう。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    @Override
    public synchronized boolean process(Subnet subnet, Frame frame, boolean processed) {
        boolean ret = false;
        
        if (processed) {
            return ret;
        }
        
        for (Transaction transaction : new ArrayList<Transaction>(transactions)) {
            if (frame.getCommonFrame().getTID() == transaction.getTID()) {
                ret |= transaction.recvResponse(frame);
            }
        }
        
        return ret;
    }

    /**
     * このTransactionManagerに所属するTransactionを生成する。
     * @param transactionConfig トランザクションの詳細設定
     * @return このTransactionManagerに所属するTransaction
     */
    public Transaction createTransaction(TransactionConfig transactionConfig) {
        return new Transaction(subnet, this, transactionConfig);
    }
}
