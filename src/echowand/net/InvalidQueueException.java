package echowand.net;

/**
 * InetSubnetReceiverQueueに関する例外を表す。
 * @author Yoshiki Makino
 */
public class InvalidQueueException extends Exception {
    
    /**
     * InetSubnetReceiverQueueに関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     */
    public InvalidQueueException(String message) {
        super(message);
    }
    
    /**
     * InetSubnetReceiverQueueに関する例外を表すオブジェクトを生成する。
     * @param message 例外に関する情報
     * @param cause この例外の発生原因となった例外
     */
    public InvalidQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
