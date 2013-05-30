package echowand.object;

/**
 * EchonetObjectに関する例外を表す。
 * @author Yoshiki Makino
 */
public class EchonetObjectException extends Exception {

    private Exception internalException;
    
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
     * @param exception この例外の発生原因となった例外
     */
    public EchonetObjectException(String message, Exception exception) {
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
