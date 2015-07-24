package echowand.service;

import echowand.object.LocalObject;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public abstract class PropertyUpdater extends LocalObjectAccessInterface {
    private static final Logger LOGGER = Logger.getLogger(PropertyUpdater.class.getName());
    private static final String CLASS_NAME = PropertyUpdater.class.getName();
    
    private int delay;
    private int intervalPeriod;
    private boolean done;
    
    public PropertyUpdater() {
        LOGGER.entering(CLASS_NAME, "PropertyUpdater");
        
        delay = 0;
        intervalPeriod = 0;
        done = false;
        
        LOGGER.exiting(CLASS_NAME, "PropertyUpdater");
    }
    
    public PropertyUpdater(int delay, int intervalPeriod) {
        LOGGER.entering(CLASS_NAME, "PropertyUpdater", new Object[]{delay, intervalPeriod});
        
        this.delay = delay;
        this.intervalPeriod = intervalPeriod;
        done = false;
        
        LOGGER.exiting(CLASS_NAME, "PropertyUpdater");
    }
    
    public int getDelay() {
        LOGGER.entering(CLASS_NAME, "getDelay");
        
        LOGGER.exiting(CLASS_NAME, "getDelay");
        return delay;
    }
    
    public void setDelay(int delay) {
        LOGGER.entering(CLASS_NAME, "setDelay", delay);
        
        this.delay = delay;
        
        LOGGER.exiting(CLASS_NAME, "setDelay");
    }
    
    public int getIntervalPeriod() {
        LOGGER.entering(CLASS_NAME, "getIntervalPeriod");
        
        LOGGER.exiting(CLASS_NAME, "getIntervalPeriod");
        return intervalPeriod;
    }
    
    public void setIntervalPeriod(int intervalPeriod) {
        LOGGER.entering(CLASS_NAME, "setIntervalPeriod", intervalPeriod);
        
        this.intervalPeriod = intervalPeriod;
        
        LOGGER.exiting(CLASS_NAME, "setIntervalPeriod");
    }
    
    public void done() {
        done = true;
    }
    
    public boolean isDone() {
        return done;
    }

    public void doLoopOnce() {
        LOGGER.entering(CLASS_NAME, "doLoopOnce");
        
        loop(getLocalObject());
        
        LOGGER.exiting(CLASS_NAME, "doLoopOnce");
    }
    
    public abstract void loop(LocalObject localObject);
    
    public void notifyCreated(LocalObject object) {
    }
}
