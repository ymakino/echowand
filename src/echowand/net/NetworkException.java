package echowand.net;

/**
 * フレームの送受信関係の例外
 * @author ymakino
 */
public class NetworkException extends Exception {
    
    /**
     * フレームの送受信に関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     */
    public NetworkException(String message) {
        super(message);
    }
    
    /**
     * フレームの送受信に関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     * @param cause この例外の発生原因となった例外
     */
    public NetworkException(String message, Exception cause) {
        super(message, cause);
    }
}
