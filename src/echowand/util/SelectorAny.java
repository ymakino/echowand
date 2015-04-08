package echowand.util;

import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class SelectorAny<T> implements Selector<T> {
    private static final Logger LOGGER = Logger.getLogger(SelectorAny.class.getName());
    private static final String CLASS_NAME = SelectorAny.class.getName();
    
    @Override
    public boolean match(T target) {
        LOGGER.entering(CLASS_NAME, "match", target);
        
        LOGGER.exiting(CLASS_NAME, "match", true);
        return true;
    }
    
    @Override
    public String toString() {
        return "SelectorAny";
    }
}
