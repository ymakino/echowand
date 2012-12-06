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
public class LocalObjectNotifyDelegate implements LocalObjectDelegate {
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
        logger.entering(className, "addObject", new Object[]{subnet, transactionManager});
        
        this.subnet = subnet;
        this.transactionManager = transactionManager;
        
        logger.exiting(className, "addObject");
    }
    
    /**
     * 特に何も行わずにnullを返す。
     * @param object プロパティデータが要求されているオブジェクト
     * @param epc 要求プロパティデータのEPC
     * @return 常にnull
     */
    @Override
    public ObjectData getData(LocalObject object, EPC epc) {
        return null;
    }

    /**
     * 特に何も処理を行わずにfalseを返す。
     * @param object プロパティデータの変更を要求されているオブジェクト
     * @param epc 変更するプロパティデータのEPC
     * @param newData 設定するプロパティデータ
     * @param curData 現在のプロパティデータ
     * @return 常にfalse
     */
    @Override
    public boolean setData(LocalObject object, EPC epc, ObjectData newData, ObjectData curData) {
        return false;
    }
    
    /**
     * 指定されたEPCのプロパティが指定されたデータで更新されたことをサブネットに通知する。
     * @param object プロパティデータの変更通知を行っているオブジェクト
     * @param epc プロパティデータに変更のあったEPC
     * @param curData 現在のプロパティデータ
     * @param oldData 以前のプロパティデータ
     */
    @Override
    public void notifyDataChanged(LocalObject object, EPC epc, ObjectData curData, ObjectData oldData) {
        logger.entering(className, "notifyDataChanged", new Object[]{object, epc, curData, oldData});
        
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
            }
        }
        
        logger.exiting(className, "notifyDataChanged");
    }
}
