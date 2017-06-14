package echowand.service;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ProperyUpdaterクラスを利用し定期実行を行うスレッド
 * @author ymakino
 */
public class PropertyUpdaterThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(PropertyUpdaterThread.class.getName());
    private static final String CLASS_NAME = PropertyUpdaterThread.class.getName();
    
    private PropertyUpdater updater;
    
    /**
     * PropertyUpdaterThreadを生成する。
     * @param updater 利用するPropertyUpdater
     */
    public PropertyUpdaterThread(PropertyUpdater updater) {
        LOGGER.entering(CLASS_NAME, "PropertyUpdaterThread", updater);
        
        this.updater = updater;
        
        LOGGER.exiting(CLASS_NAME, "PropertyUpdaterThread");
    }
    
    private long getNextTime(long baseTime, int interval) {
        long nextTime = baseTime + interval;
        long currentTime = System.currentTimeMillis();
        
        if (currentTime < baseTime) {
            return currentTime + interval;
        }
        
        if (nextTime < currentTime) {
            long diffTime = currentTime - nextTime;
            long remains = diffTime % interval;
            
            if (remains > 0) {
                diffTime = diffTime - remains + interval;
            }
            
            nextTime += diffTime;
        }
        
        return nextTime;
    }
    
    private long sleepUntil(long nextTime) throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "sleepUntil", nextTime);
        
        long sleepTime = Math.max(0, nextTime - System.currentTimeMillis());
        
        if (sleepTime > 0) {
            LOGGER.logp(Level.FINE, CLASS_NAME, "sleepUntil", "sleep: " + sleepTime);
            Thread.sleep(sleepTime);
        }
        
        LOGGER.exiting(CLASS_NAME, "sleepUntil", sleepTime);
        return sleepTime;
    }
    
    /**
     * 定期実行を行う。
     * 利用するPropertyUpdaterのdoLoopOnce()がtrueを返す間繰り返す。
     */
    @Override
    public void run() {
        LOGGER.entering(CLASS_NAME, "run");
        
        try {
            long baseTime = System.currentTimeMillis();
            LOGGER.logp(Level.FINE, CLASS_NAME, "doWork", "initial baseTime: " + baseTime);

            int delay = updater.getDelay();

            if (delay > 0) {
                LOGGER.logp(Level.FINE, CLASS_NAME, "doWork", "delay: " + delay);
                Thread.sleep(delay);
                baseTime += delay;
            }

            while (!isInterrupted()) {
                if (!updater.doLoopOnce()) {
                    LOGGER.exiting(CLASS_NAME, "doWork", false);
                    break;
                }

                int interval = updater.getIntervalPeriod();

                if (interval > 0) {
                    baseTime = getNextTime(baseTime, interval);
                    LOGGER.logp(Level.FINE, CLASS_NAME, "doWork", "next baseTime: " + baseTime);
                    sleepUntil(baseTime);
                } else {
                    baseTime = System.currentTimeMillis();
                    LOGGER.logp(Level.FINE, CLASS_NAME, "doWork", "next baseTime(no sleep): " + baseTime);
                }
            }
        } catch (InterruptedException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "run", "interrupted", ex);
        }
        
        LOGGER.exiting(CLASS_NAME, "run");
    }
}
