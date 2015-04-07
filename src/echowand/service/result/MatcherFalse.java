package echowand.service.result;

import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class MatcherFalse<T> implements Matcher<T> {
    private static final Logger LOGGER = Logger.getLogger(MatcherFalse.class.getName());
    private static final String CLASS_NAME = MatcherFalse.class.getName();
    
    @Override
    public boolean match(T target) {
        LOGGER.entering(CLASS_NAME, "match", target);
        
        LOGGER.exiting(CLASS_NAME, "match", false);
        return false;
    }
    
    @Override
    public String toString() {
        return "MatcherTrue";
    }
}
