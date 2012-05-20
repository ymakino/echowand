package echowand.object;

/**
 *
 * @author Yoshiki Makino
 */
public class EchonetObjectException extends Exception {
    public Exception internalException;
    
    public EchonetObjectException(String message) {
        super(message);
    }
    
    public EchonetObjectException(String message, Exception e) {
        super(message);
        this.internalException = e;
    }
    
    public Exception getInternalException() {
        return this.internalException;
    }
}
