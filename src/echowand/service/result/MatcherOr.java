package echowand.service.result;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class MatcherOr<T> implements Matcher<T> {
    private static final Logger LOGGER = Logger.getLogger(MatcherOr.class.getName());
    private static final String CLASS_NAME = MatcherOr.class.getName();
    
    private LinkedList<Matcher<T>> matchers;
    
    public MatcherOr(Matcher<T>... matchers) {
        LOGGER.entering(CLASS_NAME, "MatcherOr", matchers);
        
        this.matchers = new LinkedList<Matcher<T>>(Arrays.asList(matchers));
        
        LOGGER.exiting(CLASS_NAME, "MatcherOr");
    }
    
    public MatcherOr(List<Matcher<T>> matchers) {
        LOGGER.entering(CLASS_NAME, "MatcherOr", matchers);
        
        this.matchers = new LinkedList<Matcher<T>>(matchers);
        
        LOGGER.exiting(CLASS_NAME, "MatcherOr");
    }
    
    @Override
    public boolean match(T target) {
        LOGGER.entering(CLASS_NAME, "match", target);
        
        boolean result = true;
        
        for (Matcher<T> matcher: matchers) {
            result |= matcher.match(target);
        }
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("MatcherOr[");
        
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
