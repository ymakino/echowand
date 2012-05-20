package echowand.net;

/**
 *
 * @author Yoshiki Makino
 */
public class SubnetException extends Exception {
    public Exception internalException;
    
    public SubnetException(String message) {
        super(message);
    }
    
    public SubnetException(String message, Exception e) {
        super(message);
        this.internalException = e;
    }
    
    public Exception getInternalException() {
        return this.internalException;
    }
}
