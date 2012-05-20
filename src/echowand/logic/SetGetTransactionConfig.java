package echowand.logic;

import echowand.common.Data;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.util.Pair;
import java.util.LinkedList;

/**
 * Set、Get、SetGet、INF_REQトランザクションの詳細設定。
 * @author Yoshiki Makino
 */
public class SetGetTransactionConfig extends TransactionConfig {
    private LinkedList<Pair<EPC, Data>> setProperties;
    private LinkedList<EPC> getProperties;
    private boolean responseRequired;
    private boolean announcePreffered;
    
    /**
     * リクエストのESVを返す。
     * @return リクエストのESV
     */
    @Override
    public ESV getESV() {
        if (setProperties.isEmpty()) {
            if (announcePreffered) {
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
    }
    
    /**
     * SetGetTransactionConfigを生成する。
     */
    public SetGetTransactionConfig() {
        this.setProperties = new LinkedList<Pair<EPC, Data>>();
        this.getProperties = new LinkedList<EPC>();
        responseRequired = true;
    }
    
    /**
     * レスポンスが必須であるかどうかの設定を行う。
     * @param responseRequired 応答が必須であればtrue、必須でなければfalse
     */
    public void setResponseRequired(boolean responseRequired) {
        this.responseRequired = responseRequired;
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
        return announcePreffered;
    }
    
    /**
     * Getの代わりにINF_REQを使うかどうかの設定を行う。
     * @param announcePreffered Getの代わりにINF_REQを使うのであればtrue、そうでなければfalse
     */
    public void setAnnouncePreferred(boolean announcePreffered) {
        this.announcePreffered = announcePreffered;
    }
    
    /**
     * Setのためのプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param data Setを行うデータ
     */
    public void addSet(EPC epc, Data data) {
        setProperties.add(new Pair<EPC, Data>(epc, data));
    }
    
    /**
     * Getのためにプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     */
    public void addGet(EPC epc) {
        getProperties.add(epc);
    }
}
