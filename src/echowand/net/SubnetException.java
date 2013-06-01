package echowand.net;

/**
 * Subnetに関する例外を表す。
 * @author Yoshiki Makino
 */
public class SubnetException extends Exception {

    private Exception internalException;
    
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
     * @param exception この例外の発生原因となった例外
     */
    public SubnetException(String message, Exception exception) {
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
