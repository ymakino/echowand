package echowand.net;

/**
 * コネクション関係の例外
 * @author ymakino
 */
public class NetworkException extends Exception {

    private Exception internalException;
    
    /**
     * Connectionに関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     */
    public NetworkException(String message) {
        super(message);
    }
    
    /**
     * Connectionに関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     * @param exception この例外の発生原因となった例外
     */
    public NetworkException(String message, Exception exception) {
        super(message);
        this.internalException = exception;
    }
    
    /**
     * この例外の発生原因となった例外を返す。
     * @return この例外の発生原因となった例外
     */
    public Exception getInternalException() {
        return this.internalException;
    }
}
