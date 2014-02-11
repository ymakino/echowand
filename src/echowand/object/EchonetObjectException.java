package echowand.object;

/**
 * EchonetObjectに関する例外を表す。
 * @author Yoshiki Makino
 */
public class EchonetObjectException extends Exception {
    
    /**
     * EchonetObjectに関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     */
    public EchonetObjectException(String message) {
        super(message);
    }
    
    /**
     * EchonetObjectに関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     * @param cause この例外の発生原因となった例外
     */
    public EchonetObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
