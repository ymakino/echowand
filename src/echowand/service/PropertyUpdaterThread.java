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
    
    /**
     * 定期実行を行う。
     * 利用するPropertyUpdaterのisDone()がtrueを返す間繰り返す。
     */
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
        
        while (updater.doLoopOnce()) {

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
