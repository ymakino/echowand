package echowand.service;

/**
 * LocalObjectが存在しない例外を表す。
 * @author Yoshiki Makino
 */
public class LocalObjectNotFoundException extends Exception {
    
    /**
     * LocalObjectが存在しない例外を表す例外のオブジェクトを生成する。
     * @param message 例外に関する情報
     */
    public LocalObjectNotFoundException(String message) {
        super(message);
    }
    
    /**
     * LocalObjectが存在しない例外を表す例外のオブジェクトを生成する。
     * @param message 例外に関する情報
     * @param cause この例外の発生原因となった例外
     */
    public LocalObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
