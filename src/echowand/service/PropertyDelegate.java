package echowand.service;

import echowand.common.EPC;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import java.util.logging.Logger;

/**
 * プロパティを対象としたデリゲート
 * 必要に応じてnotifyCreation, getUserData, setUserData, notifyDataChangedをオーバーライドしたクラスを定義して利用する。
 * @author ymakino
 */
public class PropertyDelegate extends LocalObjectAccessInterface {
    private static final Logger LOGGER = Logger.getLogger(PropertyDelegate.class.getName());
    private static final String CLASS_NAME = PropertyDelegate.class.getName();
    
    private EPC epc;
    private boolean getEnabled;
    private boolean setEnabled;
    private boolean notifyEnabled;
    
    /**
     * PropertyDelegateを生成する。
     * @param epc 操作の対象とするEPCの設定
     * @param getEnabled データを取得する際に本デリゲートを利用するか
     * @param setEnabled データを設定する際に本デリゲートを利用するか
     * @param notifyEnabled データが変更された際に本デリゲートを利用するか
     */
    public PropertyDelegate(EPC epc, boolean getEnabled, boolean setEnabled, boolean notifyEnabled) {
        LOGGER.entering(CLASS_NAME, "PropertyDelegate", new Object[]{epc, getEnabled, setEnabled, notifyEnabled});
        
        this.epc = epc;
        this.getEnabled = getEnabled;
        this.setEnabled = setEnabled;
        this.notifyEnabled = notifyEnabled;
        
        LOGGER.exiting(CLASS_NAME, "PropertyDelegate");
    }

    /**
     * 操作の対象とするEPCを返す。
     * @return 操作の対象とするEPC
     */
    public EPC getEPC() {
        return epc;
    }
    
    /**
     * データを取得する際に本デリゲートを利用するかを返す。
     * @return 本デリゲートを利用するのであればtrue、そうでなければfalse
     */
    public boolean isGetEnabled() {
        return getEnabled;
    }
    
    /**
     * データを設定する際に本デリゲートを利用するかを返す。
     * @return 本デリゲートを利用するのであればtrue、そうでなければfalse
     */
    public boolean isSetEnabled() {
        return setEnabled;
    }
    
    /**
     * データが変更された際に本デリゲートを利用するかを返す。
     * @return 本デリゲートを利用するのであればtrue、そうでなければfalse
     */
    public boolean isNotifyEnabled() {
        return notifyEnabled;
    }
    
    /**
     * ローカルオブジェクトが生成された時に呼び出される。
     * @param object 生成されたローカルオブジェクト
     * @param core 利用するCoreの指定
     */
    public void notifyCreation(LocalObject object, Core core) {
    }
    
    /**
     * 指定されたローカルオブジェクトとEPCに対してデータ取得処理を実行する。
     * @param object ローカルオブジェクトの指定
     * @param epc EPCの指定
     * @return 取得したObjectData
     */
    public ObjectData getUserData(LocalObject object, EPC epc) {
        return null;
    }

    /**
     * 指定されたローカルオブジェクトとEPCに対してデータ設定処理を実行する。
     * @param object ローカルオブジェクトの指定
     * @param epc EPCの指定
     * @param data 設定するObjectData
     * @return 設定に成功した場合にはtrue、そうでなければfalse
     */
    public boolean setUserData(LocalObject object, EPC epc, ObjectData data) {
        return false;
    }
    
    /**
     * 指定されたローカルオブジェクトとEPCのデータが設定された時の処理を実行する。
     * @param object ローカルオブジェクトの指定
     * @param epc EPCの指定
     * @param curData 現在のObjectData
     * @param oldData 以前のObjectData
     */
    public void notifyDataChanged(LocalObject object, EPC epc, ObjectData curData, ObjectData oldData) {
    }
}
