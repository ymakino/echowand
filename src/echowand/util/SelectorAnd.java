package echowand.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class SelectorAnd<T> implements Selector<T> {
    private static final Logger LOGGER = Logger.getLogger(SelectorAnd.class.getName());
    private static final String CLASS_NAME = SelectorAnd.class.getName();
    
    private LinkedList<Selector<T>> selectors;
    
    public SelectorAnd(Selector<T>... selectors) {
        LOGGER.entering(CLASS_NAME, "SelectorAnd", selectors);
        
        this.selectors = new LinkedList<Selector<T>>(Arrays.asList(selectors));
        
        LOGGER.exiting(CLASS_NAME, "SelectorAnd");
    }
    
    public SelectorAnd(List<Selector<T>> selectors) {
        LOGGER.entering(CLASS_NAME, "SelectorAnd", selectors);
        
        this.selectors = new LinkedList<Selector<T>>(selectors);
        
        LOGGER.exiting(CLASS_NAME, "SelectorAnd");
    }
    
    @Override
    public boolean match(T target) {
        LOGGER.entering(CLASS_NAME, "match", target);
        
        boolean result = true;
        
        for (Selector<T> selector: selectors) {
            result &= selector.match(target);
        }
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("SelectorAnd[");
        
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
