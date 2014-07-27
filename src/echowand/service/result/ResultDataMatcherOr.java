package echowand.service.result;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultDataMatcherOr implements ResultDataMatcher {
    private static final Logger LOGGER = Logger.getLogger(ResultDataMatcherOr.class.getName());
    private static final String CLASS_NAME = ResultDataMatcherOr.class.getName();
    
    private LinkedList<ResultDataMatcher> matchers;
    
    public ResultDataMatcherOr(ResultDataMatcher... matchers) {
        LOGGER.entering(CLASS_NAME, "ResultDataMatcherOr", matchers);
        
        this.matchers = new LinkedList<ResultDataMatcher>(Arrays.asList(matchers));
        
        LOGGER.exiting(CLASS_NAME, "ResultDataMatcherOr");
    }
    
    public ResultDataMatcherOr(List<ResultDataMatcher> matchers) {
        LOGGER.entering(CLASS_NAME, "ResultDataMatcherOr", matchers);
        
        this.matchers = new LinkedList<ResultDataMatcher>(matchers);
        
        LOGGER.exiting(CLASS_NAME, "ResultDataMatcherOr");
    }
    
    @Override
    public boolean match(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "match", resultData);
        
        boolean result = false;
        
        for (ResultDataMatcher matcher: matchers) {
            result |= matcher.match(resultData);
        }
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("ResultDataMatcherOr[");
        
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
