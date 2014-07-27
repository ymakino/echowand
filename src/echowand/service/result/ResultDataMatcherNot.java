package echowand.service.result;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultDataMatcherNot implements ResultDataMatcher {
    private static final Logger LOGGER = Logger.getLogger(ResultDataMatcherNot.class.getName());
    private static final String CLASS_NAME = ResultDataMatcherNot.class.getName();
    
    private ResultDataMatcher matcher;
    
    public ResultDataMatcherNot(ResultDataMatcher matcher) {
        LOGGER.entering(CLASS_NAME, "ResultDataMatcherNot", matcher);
        
        this.matcher = matcher;
        
        LOGGER.exiting(CLASS_NAME, "ResultDataMatcherNot");
    }
    
    @Override
    public boolean match(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "match", resultData);
        
        boolean result = !matcher.match(resultData);
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        return "ResultDataMatcherNot(" + matcher + ")";
    }
}
