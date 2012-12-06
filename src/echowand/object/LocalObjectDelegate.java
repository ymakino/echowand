package echowand.object;

import echowand.common.EPC;

/**
 * LocalObjectの代理処理
 * @author Yoshiki Makino
 */
public interface LocalObjectDelegate {
    /**
     * 指定されたEPCのプロパティデータを返す。
     * 特に代理処理を行わない場合にはnullを返す。
     * @param object プロパティデータが要求されているオブジェクト
     * @param epc 要求プロパティデータのEPC
     * @return EPCで指定されたプロパティデータ
     */
    public ObjectData getData(LocalObject object, EPC epc);
    
    /**
     * 指定されたEPCのデータを指定されたデータの内容に変更する。
     * @param object プロパティデータの変更を要求されているオブジェクト
     * @param epc 変更するプロパティデータのEPC
     * @param newData 設定するプロパティデータ
     * @param curData 現在のプロパティデータ
     * @return 変更に成功したらtrue、そうでなければfalse
     */
    public boolean setData(LocalObject object, EPC epc, ObjectData newData, ObjectData curData);
    
    /**
     * 指定されたEPCのプロパティが指定されたデータで更新された際の処理を行う。
     * @param object プロパティデータの変更通知を行っているオブジェクト
     * @param epc プロパティデータに変更のあったEPC
     * @param curData 現在のプロパティデータ
     * @param oldData 以前のプロパティデータ
     */
    public void notifyDataChanged(LocalObject object, EPC epc, ObjectData curData, ObjectData oldData);
}
