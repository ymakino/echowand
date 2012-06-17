package echowand.object;

import echowand.common.EPC;

/**
 * RemoteObjectのプロパティデータ更新通知監視
 * @author Yoshiki Makino
 */
public interface RemoteObjectObserver {
    /**
     * 更新通知の処理を行う
     * @param object 更新されたオブジェクト
     * @param epc 更新されたEPC
     * @param data 更新された新しいデータ
     */
    public void notifyData(RemoteObject object, EPC epc, ObjectData data);
}
