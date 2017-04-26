package echowand.logic;

import echowand.common.Data;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.util.Pair;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Set、Get、SetGet、INF_REQトランザクションの詳細設定。
 * @author Yoshiki Makino
 */
public class SetGetTransactionConfig extends TransactionConfig {
    private static final Logger logger = Logger.getLogger(SetGetTransactionConfig.class.getName());
    private static final String className = SetGetTransactionConfig.class.getName();
    
    private LinkedList<Pair<EPC, Data>> setProperties;
    private LinkedList<EPC> getProperties;
    private boolean responseRequired;
    private boolean announcePreferred;
    
    /**
     * リクエストのESVを返す。
     * @return リクエストのESV
     */
    @Override
    public ESV getESV() {
        if (setProperties.isEmpty()) {
            if (announcePreferred) {
                return ESV.INF_REQ;
            } else {
                return ESV.Get;
            }
        }
        
        if (getProperties.isEmpty()) {
            if (responseRequired) {
                return ESV.SetC;
            } else {
                return ESV.SetI;
            }
        }
        
        return ESV.SetGet;
    }
    
    /**
     * トランザクションのリクエストで送信を行なうフレーム数を返す。
     * ここでは常に1を返す。
     * @return 常に1
     */
    @Override
    public int getCountPayloads() {
        return 1;
    }
    
    /**
     * リクエストフレームのために、指定されたStandardPayloadのプロパティ部分の設定を行う。
     * @param index フレームの番号
     * @param payload  プロパティを追加するStandardPayload
     */
    @Override
    public void addPayloadProperties(int index, StandardPayload payload) {
        logger.entering(className, "addPayloadProperties", new Object[]{index, payload});
        
        if (setProperties.isEmpty()) {
            for (EPC epc : getProperties) {
                payload.addFirstProperty(new Property(epc));
            }
        } else {
            for (Pair<EPC, Data> prop : setProperties) {
                payload.addFirstProperty(new Property(prop.first, prop.second));
            }
            for (EPC epc : getProperties) {
                payload.addSecondProperty(new Property(epc));
            }
        }
        
        logger.exiting(className, "addPayloadProperties");
    }
    
    /**
     * SetGetTransactionConfigを生成する。
     */
    public SetGetTransactionConfig() {
        logger.entering(className, "SetGetTransactionConfig");
        
        this.setProperties = new LinkedList<Pair<EPC, Data>>();
        this.getProperties = new LinkedList<EPC>();
        responseRequired = true;
        
        logger.exiting(className, "SetGetTransactionConfig");
    }
    
    /**
     * レスポンスが必須であるかどうかの設定を行う。
     * @param responseRequired 応答が必須であればtrue、必須でなければfalse
     */
    public void setResponseRequired(boolean responseRequired) {
        logger.entering(className, "setResponseRequired", responseRequired);
        
        this.responseRequired = responseRequired;
        
        logger.exiting(className, "setResponseRequired");
    }
    
    /**
     * レスポンスが必須であるかどうか返す。
     * @return レスポンスが必須であればtrue、必須でなければfalse
     */
    public boolean isResponseRequired() {
        return responseRequired;
    }
    
    /**
     * Getの代わりにINF_REQを使うかどうかを返す。
     * @return Getの代わりにINF_REQを使うのであればtrue、そうでなければfalse
     */
    public boolean isAnnouncePreferred() {
        return announcePreferred;
    }
    
    /**
     * Getの代わりにINF_REQを使うかどうかの設定を行う。
     * @param announcePreferred Getの代わりにINF_REQを使うのであればtrue、そうでなければfalse
     */
    public void setAnnouncePreferred(boolean announcePreferred) {
        logger.entering(className, "setAnnouncePreferred", announcePreferred);
        
        this.announcePreferred = announcePreferred;
        
        logger.exiting(className, "setAnnouncePreferred");
    }
    
    /**
     * Setのためのプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param data Setを行うデータ
     */
    public void addSet(EPC epc, Data data) {
        logger.entering(className, "addSet", new Object[]{epc, data});
        
        setProperties.add(new Pair<EPC, Data>(epc, data));
        
        logger.exiting(className, "addSet");
    }
    
    /**
     * Getのためにプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     */
    public void addGet(EPC epc) {
        logger.entering(className, "addGet", epc);
        
        getProperties.add(epc);
        
        logger.exiting(className, "addGet");
    }
}
