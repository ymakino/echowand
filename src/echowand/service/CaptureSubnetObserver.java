package echowand.service;

import echowand.net.Frame;

/**
 *
 * @author ymakino
 */
public interface CaptureSubnetObserver {
    public void notifySent(Frame frame, boolean success);
    public void notifyReceived(Frame frame);
}
