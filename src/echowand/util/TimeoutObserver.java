package echowand.util;

/**
 *
 * @author ymakino
 */
public interface TimeoutObserver {
    void notifyTimeout(TimeoutTask timeoutTask);
}
