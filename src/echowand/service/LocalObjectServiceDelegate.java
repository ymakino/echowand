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
     */
    public void notifyCreation(LocalObject object);
}
