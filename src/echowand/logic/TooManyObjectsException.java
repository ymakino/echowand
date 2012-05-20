package echowand.logic;

/**
 *
 * @author Yoshiki Makino
 */
public class TooManyObjectsException extends Exception {
    public Exception internalException;
    
    public TooManyObjectsException(String message) {
        super(message);
    }
    
    public TooManyObjectsException(String message, Exception e) {
        super(message);
        this.internalException = e;
    }
    
    public Exception getInternalException() {
        return this.internalException;
    }
}
