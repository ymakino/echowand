package echowand.service;

import echowand.object.LocalObject;
import echowand.object.LocalObjectDelegate;

/**
 * LocalObjectConfigからローカルオブジェクトが作成された時に呼び出されるメソッドを定義
 * @author ymakino
 */
public interface LocalObjectServiceDelegate extends LocalObjectDelegate {
    /**
     * LocalObjectConfigからローカルオブジェクトが作成された時に呼び出される。
     * @param object 生成されたローカルオブジェクト
     * @param core 利用するCoreの指定
     */
    public void notifyCreation(LocalObject object, Core core);
}
