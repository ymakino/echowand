package echowand.net;

/**
 * Subnetに関する例外を表す。
 * @author Yoshiki Makino
 */
public class SubnetException extends Exception {
    
    /**
     * Subnetに関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     */
    public SubnetException(String message) {
        super(message);
    }
    
    /**
     * Subnetに関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     * @param cause この例外の発生原因となった例外
     */
    public SubnetException(String message, Throwable cause) {
        super(message, cause);
    }
}
