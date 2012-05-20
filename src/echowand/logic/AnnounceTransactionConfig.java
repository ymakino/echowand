package echowand.logic;

import echowand.common.Data;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.util.Pair;
import java.util.LinkedList;

/**
 * INF、INFCトランザクションの詳細設定。
 * @author Yoshiki Makino
 */
public class AnnounceTransactionConfig extends TransactionConfig {
    private LinkedList<Pair<EPC, Data>> annoProperties;
    private boolean responseRequired;
    
    /**
     * AnnounceTransactionConfigを生成する。
     */
    public AnnounceTransactionConfig() {
        this.annoProperties = new LinkedList<Pair<EPC, Data>>();
        responseRequired = true;
    }

    /**
     * リクエストのESVを返す。
     * @return リクエストのESV
     */
    @Override
    public ESV getESV() {
        if (responseRequired) {
            return ESV.INFC;
        } else {
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
        for (Pair<EPC, Data> prop : annoProperties) {
            payload.addFirstProperty(new Property(prop.first, prop.second));
        }
    }
    
    /**
     * INF、INFCで通知を行うプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param data 追加するプロパティのデータ
     */
    public void addAnnounce(EPC epc, Data data) {
        annoProperties.add(new Pair<EPC, Data>(epc, data));
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
}
