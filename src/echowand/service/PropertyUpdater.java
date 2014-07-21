package echowand.service;

import echowand.object.LocalObject;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public abstract class PropertyUpdater extends LocalObjectInterface {
    private static final Logger LOGGER = Logger.getLogger(PropertyUpdater.class.getName());
    private static final String CLASS_NAME = PropertyUpdater.class.getName();
    
    private LocalObject localObject;
    private int intervalPeriod;
    private boolean done;
    
    public PropertyUpdater() {
        LOGGER.entering(CLASS_NAME, "PropertyUpdater");
        
        intervalPeriod = 0;
        done = false;
        
        LOGGER.exiting(CLASS_NAME, "PropertyUpdater");
    }
    
    public PropertyUpdater(int intervalPeriod) {
        LOGGER.entering(CLASS_NAME, "PropertyUpdater");
        
        this.intervalPeriod = intervalPeriod;
        done = false;
        
        LOGGER.exiting(CLASS_NAME, "PropertyUpdater");
    }
    
    public int getIntervalPeriod() {
        LOGGER.entering(CLASS_NAME, "getIntervalPeriod");
        
        LOGGER.exiting(CLASS_NAME, "getIntervalPeriod");
        return intervalPeriod;
    }
    
    public void setIntervalPeriod(int intervalPeriod) {
        LOGGER.entering(CLASS_NAME, "setInterval", intervalPeriod);
        
        this.intervalPeriod = intervalPeriod;
        
        LOGGER.exiting(CLASS_NAME, "setInterval");
    }
    
    public void done() {
        done = true;
    }
    
    public boolean isDone() {
        return done;
    }

    public void doLoopOnce() {
        LOGGER.entering(CLASS_NAME, "doLoopOnce");
        
        loop(localObject);
        
        LOGGER.exiting(CLASS_NAME, "doLoopOnce");
    }
    
    public abstract void loop(LocalObject localObject);
}
