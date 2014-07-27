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
public class FrameMatcherAnd implements FrameMatcher {
    private static final Logger LOGGER = Logger.getLogger(FrameMatcherAnd.class.getName());
    private static final String CLASS_NAME = FrameMatcherAnd.class.getName();
    
    private LinkedList<FrameMatcher> matchers;
    
    public FrameMatcherAnd(FrameMatcher... matchers) {
        LOGGER.entering(CLASS_NAME, "FrameMatcherAnd", matchers);
        
        this.matchers = new LinkedList<FrameMatcher>(Arrays.asList(matchers));
        
        LOGGER.exiting(CLASS_NAME, "FrameMatcherAnd");
    }
    
    public FrameMatcherAnd(List<FrameMatcher> matchers) {
        LOGGER.entering(CLASS_NAME, "FrameMatcherAnd", matchers);
        
        this.matchers = new LinkedList<FrameMatcher>(matchers);
        
        LOGGER.exiting(CLASS_NAME, "FrameMatcherAnd");
    }
    
    @Override
    public boolean match(Frame frame) {
        LOGGER.entering(CLASS_NAME, "match", frame);
        
        boolean result = true;
        
        for (FrameMatcher matcher: matchers) {
            result &= matcher.match(frame);
        }
        
        LOGGER.exiting(CLASS_NAME, "match");
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("FrameMatcherAnd[");
        
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
