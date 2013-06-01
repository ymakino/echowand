package echowand.logic;

/**
 * オブジェクト数が多すぎる場合の例外を表す。
 * @author Yoshiki Makino
 */
public class TooManyObjectsException extends Exception {
    
    private Exception internalException;
    
    /**
     * オブジェクト数が多すぎる場合の例外のオブジェクトを生成する。
     * @param message 例外に関する情報
     */
    public TooManyObjectsException(String message) {
        super(message);
    }
    
    /**
     * オブジェクト数が多すぎる場合の例外のオブジェクトを生成する。
     * @param message 例外に関する情報
     * @param exception この例外の発生原因となった例外
     */
    public TooManyObjectsException(String message, Exception exception) {
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
