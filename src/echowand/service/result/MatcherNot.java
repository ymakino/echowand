package echowand.service.result;

import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class MatcherNot<T> implements Matcher<T> {
    private static final Logger LOGGER = Logger.getLogger(MatcherNot.class.getName());
    private static final String CLASS_NAME = MatcherNot.class.getName();
    
    private Matcher<T> matcher;
    
    public MatcherNot(Matcher<T> matcher) {
        LOGGER.entering(CLASS_NAME, "MatcherNot", matcher);
        
        this.matcher = matcher;
        
        LOGGER.exiting(CLASS_NAME, "MatcherNot");
    }
    
    @Override
    public boolean match(T target) {
        LOGGER.entering(CLASS_NAME, "match", target);
        
        boolean result = !matcher.match(target);
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        return "MatcherNot(" + matcher + ")";
    }
}
