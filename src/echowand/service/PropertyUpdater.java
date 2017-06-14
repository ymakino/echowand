package echowand.service;

import echowand.object.LocalObject;
import java.util.logging.Logger;

/**
 * 定期的に実行を行い、必要に応じてプロパティのアップデートを行う
 * @author ymakino
 */
public abstract class PropertyUpdater extends LocalObjectAccessInterface {
    private static final Logger LOGGER = Logger.getLogger(PropertyUpdater.class.getName());
    private static final String CLASS_NAME = PropertyUpdater.class.getName();
    
    private int delay;
    private int intervalPeriod;
    private boolean done;
    
    /**
     * PropertyUpdaterを生成する。
     */
    public PropertyUpdater() {
        LOGGER.entering(CLASS_NAME, "PropertyUpdater");
        
        delay = 0;
        intervalPeriod = 0;
        done = false;
        
        LOGGER.exiting(CLASS_NAME, "PropertyUpdater");
    }
    
    /**
     * PropertyUpdaterを生成する。
     * @param delay 定期実行開始までの遅延時間(ミリ秒)
     * @param intervalPeriod 定期実行のインターバル時間(ミリ秒)
     */
    public PropertyUpdater(int delay, int intervalPeriod) {
        LOGGER.entering(CLASS_NAME, "PropertyUpdater", new Object[]{delay, intervalPeriod});
        
        this.delay = delay;
        this.intervalPeriod = intervalPeriod;
        done = false;
        
        LOGGER.exiting(CLASS_NAME, "PropertyUpdater");
    }
    
    /**
     * 定期実行開始までの遅延時間を返す。
     * @return 定期実行開始までの遅延時間(ミリ秒)
     */
    public int getDelay() {
        LOGGER.entering(CLASS_NAME, "getDelay");
        
        int result = delay;
        
        LOGGER.exiting(CLASS_NAME, "getDelay", result);
        return result;
    }
    
    /**
     * 定期実行開始までの遅延時間を設定する。
     * @param delay 定期実行開始までの遅延時間(ミリ秒)
     */
    public void setDelay(int delay) {
        LOGGER.entering(CLASS_NAME, "setDelay", delay);
        
        this.delay = delay;
        
        LOGGER.exiting(CLASS_NAME, "setDelay");
    }
    
    /**
     * 定期実行のインターバル時間を返す。
     * @return 定期実行のインターバル時間(ミリ秒)
     */
    public int getIntervalPeriod() {
        LOGGER.entering(CLASS_NAME, "getIntervalPeriod");
        
        int result = intervalPeriod;
        
        LOGGER.exiting(CLASS_NAME, "getIntervalPeriod", result);
        return result;
    }
    
    /**
     * 定期実行のインターバル時間を設定する。
     * @param intervalPeriod 定期実行のインターバル時間(ミリ秒)
     */
    public void setIntervalPeriod(int intervalPeriod) {
        LOGGER.entering(CLASS_NAME, "setIntervalPeriod", intervalPeriod);
        
        this.intervalPeriod = intervalPeriod;
        
        LOGGER.exiting(CLASS_NAME, "setIntervalPeriod");
    }
    
    /**
     * 定期的実行を終了する。
     */
    public synchronized void finish() {
        LOGGER.entering(CLASS_NAME, "finish");
        
        done = true;
        
        LOGGER.exiting(CLASS_NAME, "finish");
    }
    
    /**
     * 定期的実行が終了しているかどうかを返す。
     * @return 定期的実行が終了している場合にはtrue、そうでなければfalse
     */
    public synchronized boolean isDone() {
        LOGGER.entering(CLASS_NAME, "isDone");
        
        boolean result = done;
        
        LOGGER.exiting(CLASS_NAME, "isDone", result);
        return result;
    }

    /**
     * 設定したローカルオブジェクトを利用してloopメソッドを呼び出す。
     * すでに定期的実行が終了している場合には、loopメソッドを呼び出さずfalseを返す。
     * loopメソッド実行後、再度定期的実行が終了しているか確認を行い、
     * 定期的実行が終了していればfalseを返す。
     * @return 定期的実行が終了していない場合にはtrue、そうでなければfalse
     */
    public synchronized boolean doLoopOnce() {
        LOGGER.entering(CLASS_NAME, "doLoopOnce");
        
        boolean result = !isDone();
        
        if (result) {
            loop(getLocalObject());
            result = !isDone();
        }
        
        LOGGER.exiting(CLASS_NAME, "doLoopOnce", result);
        return result;
    }
    
    /**
     * 定期的に呼び出される。
     * @param localObject 設定されているローカルオブジェクト
     */
    public abstract void loop(LocalObject localObject);
    
    /**
     * ローカルオブジェクトが生成された時に呼び出される。
     * @param object 生成されたローカルオブジェクト
     * @param core 利用するCoreの指定
     */
    public void notifyCreation(LocalObject object, Core core) {
        LOGGER.entering(CLASS_NAME, "notifyCreation", new Object[]{object, core});
        
        LOGGER.exiting(CLASS_NAME, "notifyCreation");
    }
}
