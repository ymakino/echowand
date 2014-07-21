package echowand.service.result;

import echowand.net.Frame;

/**
 *
 * @author ymakino
 */
public interface FrameMatcher {
    public boolean match(Frame frame);
}
