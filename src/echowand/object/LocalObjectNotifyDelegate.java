package echowand.object;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.AnnounceTransactionConfig;
import echowand.logic.Transaction;
import echowand.logic.TransactionManager;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.logging.Logger;

/**
 * プロパティ値変更時に通知を行うDelegate
 * @author Yoshiki Makino
 */
public class LocalObjectNotifyDelegate extends LocalObjectDefaultDelegate {
    private static final Logger logger = Logger.getLogger(LocalObjectNotifyDelegate.class.getName());
    private static final String className = LocalObjectNotifyDelegate.class.getName();
    
    private Subnet subnet;
    private TransactionManager transactionManager;
    
    /**
     * LocalObjectNotifyDelegateを生成する。
     * @param subnet 通知を送信するサブネット
     * @param transactionManager 通知トランザクションの生成に利用するTransactionManager
     */
    public LocalObjectNotifyDelegate(Subnet subnet, TransactionManager transactionManager) {
        logger.entering(className, "LocalObjectNotifyDelegate", new Object[]{subnet, transactionManager});
        
        this.subnet = subnet;
        this.transactionManager = transactionManager;
        
        logger.exiting(className, "LocalObjectNotifyDelegate");
    }
    
    /**
     * 指定されたEPCのプロパティが指定されたデータで更新されたことをサブネットに通知する。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータの変更通知を行っているオブジェクト
     * @param epc プロパティデータに変更のあったEPC
     * @param curData 現在のプロパティデータ
     * @param oldData 以前のプロパティデータ
     */
    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData curData, ObjectData oldData) {
        logger.entering(className, "notifyDataChanged", new Object[]{result, object, epc, curData, oldData});
        
        if (object.isObservable(epc) && !curData.equals(oldData)) {
            try {
                AnnounceTransactionConfig transactionConfig = new AnnounceTransactionConfig();
                transactionConfig.addAnnounce(epc, curData.getData());
                transactionConfig.setResponseRequired(false);
                transactionConfig.setSenderNode(subnet.getLocalNode());
                transactionConfig.setReceiverNode(subnet.getGroupNode());
                transactionConfig.setSourceEOJ(object.getEOJ());
                transactionConfig.setDestinationEOJ(new EOJ("0EF001"));
                Transaction transaction = transactionManager.createTransaction(transactionConfig);
                transaction.execute();
            } catch (SubnetException e) {
                e.printStackTrace();
                result.setFail();
            }
        }
        
        result.setDone();
        
        logger.exiting(className, "notifyDataChanged");
    }
}
