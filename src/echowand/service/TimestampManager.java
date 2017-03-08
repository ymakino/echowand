package echowand.service;

import echowand.net.Frame;
import java.util.WeakHashMap;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class TimestampManager {
    private static final Logger LOGGER = Logger.getLogger(TimestampObserver.class.getName());
    private static final String CLASS_NAME = TimestampObserver.class.getName();
    
    private WeakHashMap<Frame, Long> timestampMap;
    
    public TimestampManager() {
        LOGGER.entering(CLASS_NAME, "TimestampManager");
        
        timestampMap = new WeakHashMap<Frame, Long>();
        
        LOGGER.exiting(CLASS_NAME, "TimestampManager");
    }
    
    public synchronized long put(Frame frame, long timestamp) {
        LOGGER.entering(CLASS_NAME, "put", new Object[]{frame, timestamp});
        
        Long prev = timestampMap.put(frame, timestamp);
        
        if (prev == null) {
            return -1;
        }
        
        LOGGER.exiting(CLASS_NAME, "put", prev);
        return prev;
    }
    
    public synchronized long get(Frame frame) {
        LOGGER.entering(CLASS_NAME, "get",  frame);
        
        Long timestamp = timestampMap.get(frame);
        
        if (timestamp == null) {
            return -1;
        }
        
        LOGGER.exiting(CLASS_NAME, "put", timestamp);
        return timestamp;
    }
    
    public synchronized long get(Frame frame, long defaultValue) {
        LOGGER.entering(CLASS_NAME, "get",  new Object[]{frame, defaultValue});
        
        Long timestamp = timestampMap.get(frame);
        
        if (timestamp == null) {
            timestamp = defaultValue;
        }
        
        LOGGER.exiting(CLASS_NAME, "get", timestamp);
        return timestamp;
    }
    
    public synchronized long remove(Frame frame) {
        LOGGER.entering(CLASS_NAME, "remove",  frame);
        
        Long timestamp = timestampMap.remove(frame);
        
        if (timestamp == null) {
            timestamp = (long)-1;
        }
        
        LOGGER.exiting(CLASS_NAME, "remove", timestamp);
        return timestamp;
    }
}
