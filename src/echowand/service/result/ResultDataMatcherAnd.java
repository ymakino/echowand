package echowand.service.result;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultDataMatcherAnd implements ResultDataMatcher {
    private static final Logger LOGGER = Logger.getLogger(ResultDataMatcherAnd.class.getName());
    private static final String CLASS_NAME = ResultDataMatcherAnd.class.getName();
    
    private LinkedList<ResultDataMatcher> matchers;
    
    public ResultDataMatcherAnd(ResultDataMatcher... matchers) {
        LOGGER.entering(CLASS_NAME, "ResultDataMatcherAnd", matchers);
        
        this.matchers = new LinkedList<ResultDataMatcher>(Arrays.asList(matchers));
        
        LOGGER.exiting(CLASS_NAME, "ResultDataMatcherAnd");
    }
    
    public ResultDataMatcherAnd(List<ResultDataMatcher> matchers) {
        LOGGER.entering(CLASS_NAME, "ResultDataMatcherAnd", matchers);
        
        this.matchers = new LinkedList<ResultDataMatcher>(matchers);
        
        LOGGER.exiting(CLASS_NAME, "ResultDataMatcherAnd");
    }
    
    @Override
    public boolean match(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "match", resultData);
        
        boolean result = true;
        
        for (ResultDataMatcher matcher: matchers) {
            result &= matcher.match(resultData);
        }
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("ResultDataMatcherAnd[");
        
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
