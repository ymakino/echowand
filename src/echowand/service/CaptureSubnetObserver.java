package echowand.service;

import echowand.net.Frame;

/**
 *
 * @author ymakino
 */
public interface CaptureSubnetObserver {
    public void notifySent(Frame frame, boolean result);
    public void notifyReceived(Frame frame);
}
