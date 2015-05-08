package echowand.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

public class SelectorMember<T> implements Selector<T> {
    private static final Logger LOGGER = Logger.getLogger(SelectorMember.class.getName());
    private static final String CLASS_NAME = SelectorMember.class.getName();

    private ArrayList<T> targetList;

    public SelectorMember(Collection<? extends T> targets) {
        LOGGER.entering(CLASS_NAME, "SelectorMember", targets);
        
        targetList = new ArrayList<T>(targets);
        
        LOGGER.exiting(CLASS_NAME, "SelectorMember");
    }

    public SelectorMember(T... targets) {
        LOGGER.entering(CLASS_NAME, "SelectorMember", targets);
        
        targetList = new ArrayList<T>(Arrays.asList(targets));
        
        LOGGER.exiting(CLASS_NAME, "SelectorMember");
    }

    @Override
    public boolean match(T target) {
        LOGGER.entering(CLASS_NAME, "match", target);
        
        boolean result = targetList.contains(target);
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("SelectorMember[");
        
        for (int i=0; i<targetList.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            
            builder.append(targetList.get(i));
        }
        
        builder.append("]");
        
        return builder.toString();
    }
}
