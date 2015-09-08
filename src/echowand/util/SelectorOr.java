package echowand.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class SelectorOr<T> implements Selector<T> {
    private static final Logger LOGGER = Logger.getLogger(SelectorOr.class.getName());
    private static final String CLASS_NAME = SelectorOr.class.getName();
    
    private LinkedList<Selector<? super T>> selectors;
    
    public SelectorOr(Selector<? super T>... selectors) {
        LOGGER.entering(CLASS_NAME, "SelectorOr", selectors);
        
        this.selectors = new LinkedList<Selector<? super T>>(Arrays.asList(selectors));
        
        LOGGER.exiting(CLASS_NAME, "SelectorOr");
    }
    
    public SelectorOr(List<Selector<? super T>> selectors) {
        LOGGER.entering(CLASS_NAME, "SelectorOr", selectors);
        
        this.selectors = new LinkedList<Selector<? super T>>(selectors);
        
        LOGGER.exiting(CLASS_NAME, "SelectorOr");
    }
    
    @Override
    public boolean match(T target) {
        LOGGER.entering(CLASS_NAME, "match", target);
        
        boolean result = true;
        
        for (Selector<? super T> selector: selectors) {
            result |= selector.match(target);
        }
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("SelectorOr[");
        
        for (int i=0; i<selectors.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            
            builder.append(selectors.get(i));
        }
        
        builder.append("]");
        
        return builder.toString();
    }
}
