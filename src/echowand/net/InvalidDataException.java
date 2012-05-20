package echowand.net;

/**
 *
 * @author Yoshiki Makino
 */
public class InvalidDataException extends Exception {
    public Exception internalException;
    
    public InvalidDataException(String message) {
        super(message);
    }
    
    public InvalidDataException(String message, Exception e) {
        super(message);
        this.internalException = e;
    }
    
    public Exception getInternalException() {
        return this.internalException;
    }
}
