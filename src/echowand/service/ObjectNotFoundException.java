package echowand.service;

/**
 * Objectが存在しない例外を表す。
 * @author Yoshiki Makino
 */
public class ObjectNotFoundException extends Exception {
    
    /**
     * Objectが存在しない例外を表す例外のオブジェクトを生成する。
     * @param message 例外に関する情報
     */
    public ObjectNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Objectが存在しない例外を表す例外のオブジェクトを生成する。
     * @param message 例外に関する情報
     * @param cause この例外の発生原因となった例外
     */
    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
