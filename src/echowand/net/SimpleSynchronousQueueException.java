package echowand.net;

/**
 * SimpleSynchronousQueueに関する例外を表す。
 * @author Yoshiki Makino
 */
public class SimpleSynchronousQueueException extends Exception {
    
    /**
     * SimpleSynchronousQueueに関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     */
    public SimpleSynchronousQueueException(String message) {
        super(message);
    }
    
    /**
     * SimpleSynchronousQueueに関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     * @param cause この例外の発生原因となった例外
     */
    public SimpleSynchronousQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}