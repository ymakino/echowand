package echowand.util;

import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class SelectorNot<T> implements Selector<T> {
    private static final Logger LOGGER = Logger.getLogger(SelectorNot.class.getName());
    private static final String CLASS_NAME = SelectorNot.class.getName();
    
    private Selector<? super T> selector;
    
    public SelectorNot(Selector<? super T> selector) {
        LOGGER.entering(CLASS_NAME, "SelectorNot", selector);
        
        this.selector = selector;
        
        LOGGER.exiting(CLASS_NAME, "SelectorNot");
    }
    
    @Override
    public boolean match(T target) {
        LOGGER.entering(CLASS_NAME, "match", target);
        
        boolean result = !selector.match(target);
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        return "SelectorNot(" + selector + ")";
    }
}
