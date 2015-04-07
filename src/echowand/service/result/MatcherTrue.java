package echowand.service.result;

import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class MatcherTrue<T> implements Matcher<T> {
    private static final Logger LOGGER = Logger.getLogger(MatcherTrue.class.getName());
    private static final String CLASS_NAME = MatcherTrue.class.getName();
    
    @Override
    public boolean match(T target) {
        LOGGER.entering(CLASS_NAME, "match", target);
        
        LOGGER.exiting(CLASS_NAME, "match", true);
        return true;
    }
    
    @Override
    public String toString() {
        return "MatcherTrue";
    }
}
