package echowand.util;

/**
 *
 * @author ymakino
 */
public interface TimeoutObserver {
    public void notifyTimeout(TimeoutTask timeoutThread);
}
