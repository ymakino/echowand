package echowand.service.result;

import echowand.net.Frame;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class FrameMatcherNot implements FrameMatcher {
    private static final Logger LOGGER = Logger.getLogger(FrameMatcherNot.class.getName());
    private static final String CLASS_NAME = FrameMatcherNot.class.getName();
    
    private FrameMatcher matcher;
    
    public FrameMatcherNot(FrameMatcher matcher) {
        LOGGER.entering(CLASS_NAME, "FrameMatcherNot", matcher);
        
        this.matcher = matcher;
        
        LOGGER.exiting(CLASS_NAME, "FrameMatcherNot");
    }
    
    @Override
    public boolean match(Frame frame) {
        LOGGER.entering(CLASS_NAME, "match", frame);
        
        boolean result = !matcher.match(frame);
        
        LOGGER.exiting(CLASS_NAME, "match");
        return result;
    }
    
    @Override
    public String toString() {
        return "FrameMatcherNot(" + matcher + ")";
    }
}
