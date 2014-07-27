package echowand.service.result;

import echowand.net.Frame;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class FrameMatcherOr implements FrameMatcher {
    private static final Logger LOGGER = Logger.getLogger(FrameMatcherOr.class.getName());
    private static final String CLASS_NAME = FrameMatcherOr.class.getName();
    
    private LinkedList<FrameMatcher> matchers;
    
    public FrameMatcherOr(FrameMatcher... matchers) {
        LOGGER.entering(CLASS_NAME, "FrameMatcherOr", matchers);
        
        this.matchers = new LinkedList<FrameMatcher>(Arrays.asList(matchers));
        
        LOGGER.exiting(CLASS_NAME, "FrameMatcherOr");
    }
    
    public FrameMatcherOr(List<FrameMatcher> matchers) {
        LOGGER.entering(CLASS_NAME, "FrameMatcherOr", matchers);
        
        this.matchers = new LinkedList<FrameMatcher>(matchers);
        
        LOGGER.exiting(CLASS_NAME, "FrameMatcherOr");
    }
    
    @Override
    public boolean match(Frame frame) {
        LOGGER.entering(CLASS_NAME, "match", frame);
        
        boolean result = false;
        
        for (FrameMatcher matcher: matchers) {
            result |= matcher.match(frame);
        }
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("FrameMatcherOr[");
        
        for (int i=0; i<matchers.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            
            builder.append(matchers.get(i));
        }
        
        builder.append("]");
        
        return builder.toString();
    }
}
