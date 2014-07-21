package echowand.logic;

/**
 * オブジェクト数が多すぎる場合の例外を表す。
 * @author Yoshiki Makino
 */
public class TooManyObjectsException extends Exception {
    
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
     * @param cause この例外の発生原因となった例外
     */
    public TooManyObjectsException(String message, Throwable cause) {
        super(message, cause);
    }
}
