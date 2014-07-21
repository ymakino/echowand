package echowand.service.result;

import echowand.object.InstanceListRequestExecutor;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultUpdate {
    private static final Logger LOGGER = Logger.getLogger(ResultUpdate.class.getName());
    private static final String CLASS_NAME = ResultUpdate.class.getName();
    
    private InstanceListRequestExecutor executor;
    
    public ResultUpdate(InstanceListRequestExecutor executor) {
        LOGGER.entering(CLASS_NAME, "ResultUpdate", executor);
        
        this.executor = executor;
        
        LOGGER.exiting(CLASS_NAME, "ResultUpdate");
    }
    
    public boolean isDone() {
        LOGGER.entering(CLASS_NAME, "isDone");
        
        boolean result = executor.isDone();
        
        LOGGER.exiting(CLASS_NAME, "isDone", result);
        return result;
    }
    
    public synchronized void join() throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "join");
        
        executor.join();
        
        LOGGER.exiting(CLASS_NAME, "join");
    }
}
