package echowand.util;

import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class SelectorNone<T> implements Selector<T> {
    private static final Logger LOGGER = Logger.getLogger(SelectorNone.class.getName());
    private static final String CLASS_NAME = SelectorNone.class.getName();
    
    @Override
    public boolean match(T target) {
        LOGGER.entering(CLASS_NAME, "match", target);
        
        LOGGER.exiting(CLASS_NAME, "match", false);
        return false;
    }
    
    @Override
    public String toString() {
        return "SelectorNone";
    }
}
