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
 * INF、INFCトランザクションの詳細設定。
 * @author Yoshiki Makino
 */
public class AnnounceTransactionConfig extends TransactionConfig {
    private static final Logger logger = Logger.getLogger(AnnounceTransactionConfig.class.getName());
    private static final String className = AnnounceTransactionConfig.class.getName();
    
    private LinkedList<Pair<EPC, Data>> annoProperties;
    private boolean responseRequired;
    
    /**
     * AnnounceTransactionConfigを生成する。
     */
    public AnnounceTransactionConfig() {
        logger.entering(className, "AnnounceTransactionConfig");
        
        this.annoProperties = new LinkedList<Pair<EPC, Data>>();
        responseRequired = true;
        
        logger.exiting(className, "AnnounceTransactionConfig");
    }

    /**
     * リクエストのESVを返す。
     * @return リクエストのESV
     */
    @Override
    public ESV getESV() {
        logger.entering(className, "getESV");
        
        if (responseRequired) {
            logger.exiting(className, "getESV", ESV.INFC);
            return ESV.INFC;
        } else {
            logger.exiting(className, "getESV", ESV.INF);
            return ESV.INF;
        }
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
        
        for (Pair<EPC, Data> prop : annoProperties) {
            payload.addFirstProperty(new Property(prop.first, prop.second));
        }
        
        logger.exiting(className, "addPayloadProperties");
    }
    
    /**
     * INF、INFCで通知を行うプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param data 追加するプロパティのデータ
     */
    public void addAnnounce(EPC epc, Data data) {
        logger.entering(className, "addAnnounce", new Object[]{epc, data});
        
        annoProperties.add(new Pair<EPC, Data>(epc, data));
        
        logger.exiting(className, "addAnnounce");
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
}
