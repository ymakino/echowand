package echowand.service;

import echowand.object.LocalObject;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 登録されたPropertyUpdaterを定期的に実行
 * @author ymakino
 */
public class LocalObjectUpdater {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectUpdater.class.getName());
    private static final String CLASS_NAME = LocalObjectUpdater.class.getName();
    
    private LocalObject localObject;
    private Core core;
    private LinkedList<PropertyUpdater> propertyUpdaters;
    LinkedList<PropertyUpdaterThread> threads;
    
    /**
     * 利用するローカルオブジェクトとCoreを指定してLocalObjectUpdaterを生成する。
     * @param localObject 利用するローカルオブジェクト
     * @param core 利用するCore
     */
    public LocalObjectUpdater(LocalObject localObject, Core core) {
        LOGGER.entering(CLASS_NAME, "LocalObjectUpdater", new Object[]{localObject, core});
        
        this.localObject = localObject;
        this.core = core;
        propertyUpdaters = new LinkedList<PropertyUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "LocalObjectUpdater");
    }
    
    /**
     * 利用するローカルオブジェクトを返す。
     * @return 利用するローカルオブジェクト
     */
    public LocalObject getLocalObject() {
        LOGGER.entering(CLASS_NAME, "getLocalObject");
        
        LOGGER.exiting(CLASS_NAME, "getLocalObject", localObject);
        return localObject;
    }
    
    /**
     * 利用するCoreを返す。
     * @return 利用するCore
     */
    public Core getCore() {
        LOGGER.entering(CLASS_NAME, "getCore");
        
        LOGGER.exiting(CLASS_NAME, "getCore", core);
        return core;
    }
    
    /**
     * 登録されたPropertyUpdaterの個数を返す。
     * @return 登録されたPropertyUpdaterの個数
     */
    public int countPropertyUpdaters() {
        LOGGER.entering(CLASS_NAME, "countPropertyUpdaters");
        
        int size = propertyUpdaters.size();
        LOGGER.exiting(CLASS_NAME, "countPropertyUpdaters", size);
        return size;
    }
    
    /**
     * 指定されたPropertyUpdaterの登録を追加する。
     * @param delegate 追加するPropertyUpdaterの指定
     * @return 追加に成功した場合にはtrue、そうでなければfalse
     */
    public boolean addPropertyUpdater(PropertyUpdater delegate) {
        LOGGER.entering(CLASS_NAME, "addPropertyUpdater", delegate);
        
        boolean result = propertyUpdaters.add(delegate);
        LOGGER.exiting(CLASS_NAME, "addPropertyUpdater", result);
        return result;
    }
    
    /**
     * 指定されたPropertyUpdaterの登録を抹消する。
     * @param delegate 抹消するPropertyUpdaterの指定
     * @return 抹消に成功した場合にはtrue、そうでなければfalse
     */
    public boolean removePropertyUpdater(PropertyUpdater delegate) {
        LOGGER.entering(CLASS_NAME, "removePropertyUpdater", delegate);
        
        boolean result = propertyUpdaters.remove(delegate);
        LOGGER.exiting(CLASS_NAME, "removePropertyUpdater", result);
        return result;
    }
    
    /**
     * このLocalObjectUpdaterに登録されているindex番目のPropertyUpdaterを返す。
     * @param index インデックス番号
     * @return 指定されたPropertyUpdater
     */
    public PropertyUpdater getPropertyUpdater(int index) {
        LOGGER.entering(CLASS_NAME, "getPropertyUpdater", index);
        
        PropertyUpdater updater = propertyUpdaters.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getPropertyUpdater", updater);
        return updater;
    }
    
    public void terminate() {
        for (PropertyUpdaterThread thread : threads) {
            thread.interrupt();
        }
    }
    
    /**
     * アップデート処理を開始する。
     * 登録されたPropertyUpdaterについてPropertyUpdaterThreadを生成して実行を行う。
     */
    public void start() {
        LOGGER.entering(CLASS_NAME, "run");
        
        threads = new LinkedList<PropertyUpdaterThread>();
        
        for (PropertyUpdater propertyUpdater : propertyUpdaters) {
            propertyUpdater.setCore(core);
            propertyUpdater.setLocalObject(localObject);
            threads.add(new PropertyUpdaterThread(propertyUpdater));
        }

        for (PropertyUpdaterThread thread : threads) {
            thread.start();
        }
        
        LOGGER.exiting(CLASS_NAME, "run");
    }
}
