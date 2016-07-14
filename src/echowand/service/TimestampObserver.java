package echowand.service;

import echowand.net.Frame;
import java.util.logging.Logger;

/**
 * CaptureSubnetを経由する全フレームにタイムスタンプを付与
 * 
 * @author ymakino
 */
public class TimestampObserver implements CaptureSubnetObserver {
    private static final Logger LOGGER = Logger.getLogger(TimestampObserver.class.getName());
    private static final String CLASS_NAME = TimestampObserver.class.getName();
    
    private TimestampManager timestampManager;
    
    public TimestampObserver(TimestampManager timestampManager) {
        LOGGER.entering(CLASS_NAME, "TimestampObserver", timestampManager);
        
        this.timestampManager = timestampManager;
        
        LOGGER.exiting(CLASS_NAME, "TimestampObserver");
    }
    
    public TimestampObserver() {
        LOGGER.entering(CLASS_NAME, "TimestampObserver");
        
        timestampManager = new TimestampManager();
        
        LOGGER.exiting(CLASS_NAME, "TimestampObserver");
    }
    
    public TimestampManager setTimestampManager(TimestampManager newManager) {
        LOGGER.entering(CLASS_NAME, "setTimestampManager", newManager);
        
        TimestampManager lastManager = this.timestampManager;
        
        this.timestampManager = newManager;
        
        LOGGER.exiting(CLASS_NAME, "setTimestampManager", lastManager);
        return lastManager;
    }
    
    public TimestampManager getTimestampManager() {
        LOGGER.entering(CLASS_NAME, "getTimestampManager");
        
        LOGGER.exiting(CLASS_NAME, "setTimestampManager", timestampManager);
        return timestampManager;
    }

    @Override
    public void notifySent(Frame frame, boolean success) {
        LOGGER.entering(CLASS_NAME, "notifySent", new Object[]{frame, success});
        
        long timestamp = System.currentTimeMillis();
        timestampManager.put(frame, timestamp);
        
        LOGGER.exiting(CLASS_NAME, "notifySent");
    }

    @Override
    public void notifyReceived(Frame frame) {
        LOGGER.entering(CLASS_NAME, "notifyReceived", frame);
        
        long timestamp = System.currentTimeMillis();
        timestampManager.put(frame, timestamp);
        
        LOGGER.exiting(CLASS_NAME, "notifyReceived");
    }
}
