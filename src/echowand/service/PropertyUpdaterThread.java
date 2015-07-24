package echowand.service;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class PropertyUpdaterThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(PropertyUpdaterThread.class.getName());
    private static final String CLASS_NAME = PropertyUpdaterThread.class.getName();
    
    private PropertyUpdater updater;
    
    public PropertyUpdaterThread(PropertyUpdater updater) {
        LOGGER.entering(CLASS_NAME, "PropertyUpdaterThread", updater);
        
        this.updater = updater;
        
        LOGGER.exiting(CLASS_NAME, "PropertyUpdaterThread");
    }
    
    @Override
    public void run() {
        LOGGER.entering(CLASS_NAME, "run");
        
        int delay = updater.getDelay();
        
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                LOGGER.logp(Level.INFO, CLASS_NAME, "run", "interrupted", ex);
            }
        }
        
        while (!updater.isDone()) {
            
            updater.doLoopOnce();

            int interval = updater.getIntervalPeriod();

            if (interval > 0) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException ex) {
                    LOGGER.logp(Level.INFO, CLASS_NAME, "run", "interrupted", ex);
                }
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "run");
    }
}
