package echowand.service;

import echowand.info.ObjectInfo;
import echowand.object.LocalObject;
import echowand.object.LocalObjectDelegate;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * CoreがLocalObjectを生成するための設定
 * @author ymakino
 */
public class LocalObjectConfig {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectConfig.class.getName());
    private static final String CLASS_NAME = LocalObjectConfig.class.getName();
    
    private ObjectInfo objectInfo;
    private LinkedList<LocalObjectDelegate> delegates;
    private LinkedList<PropertyDelegate> propertyDelegates;
    private LinkedList<PropertyUpdater> propertyUpdaters;
    private LinkedList<LazyConfiguration> lazyConfigurations;
    
    public interface LazyConfiguration {
        void configure(LocalObjectConfig config, Core core);
    }
    
    /**
     * 指定されたObjectInfoを利用するLocalObjectConfigを生成する。
     * @param objectInfo 利用するObjectInfo
     */
    public LocalObjectConfig(ObjectInfo objectInfo) {
        LOGGER.entering(CLASS_NAME, "LocalObjectConfig", objectInfo);
        
        this.objectInfo = objectInfo;
        delegates = new LinkedList<LocalObjectDelegate>();
        propertyDelegates = new LinkedList<PropertyDelegate>();
        propertyUpdaters = new LinkedList<PropertyUpdater>();
        lazyConfigurations = new LinkedList<LazyConfiguration>();
        
        LOGGER.exiting(CLASS_NAME, "LocalObjectConfig");
    }
    
    /**
     * 利用するObjectInfoを返す。
     * @return 利用するObjectInfo
     */
    public ObjectInfo getObjectInfo() {
        LOGGER.entering(CLASS_NAME, "getObjectInfo");
        
        LOGGER.exiting(CLASS_NAME, "getObjectInfo", objectInfo);
        return objectInfo;
    }
    
    /**
     * 登録されているLocalObjectDelegateの個数を返す。
     * @return 登録されているLocalObjectDelegateの個数
     */
    public int countDelegates() {
        LOGGER.entering(CLASS_NAME, "countDelegates");
        
        int count = delegates.size();
        
        LOGGER.exiting(CLASS_NAME, "countDelegates", count);
        return count;
    }
    
    /**
     * 指定されたLocalObjectDelegateを追加する。
     * @param delegate 追加するLocalObjectDelegate
     * @return 追加に成功したらtrue、そうでなければfalse
     */
    public boolean addDelegate(LocalObjectDelegate delegate) {
        LOGGER.entering(CLASS_NAME, "addDelegate", delegate);
        
        boolean result = delegates.add(delegate);
        
        LOGGER.exiting(CLASS_NAME, "addDelegate", result);
        return result;
    }
    
    /**
     * 指定されたLocalObjectDelegateを抹消する。
     * @param delegate 抹消するLocalObjectDelegate
     * @return 抹消に成功したらtrue、そうでなければfalse
     */
    public boolean removeDelegate(LocalObjectDelegate delegate) {
        LOGGER.entering(CLASS_NAME, "removeDelegate", delegate);
        
        boolean result = delegates.remove(delegate);
        
        LOGGER.exiting(CLASS_NAME, "removeDelegate", result);
        return result;
    }
    
    /**
     * index番目のLocalObjectDelegateを返す。
     * @param index LocalObjectDelegateのインデックス
     * @return 指定されたLocalObjectDelegate
     */
    public LocalObjectDelegate getDelegate(int index) {
        LOGGER.entering(CLASS_NAME, "getDelegate");
        
        LocalObjectDelegate delegate = delegates.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getDelegate", delegate);
        return delegate;
    }
    
    /**
     * 登録されているPropertyDelegateの個数を返す。
     * @return 登録されているPropertyDelegateの個数
     */
    public int countPropertyDelegates() {
        LOGGER.entering(CLASS_NAME, "countPropertyDelegates");
        
        int count = propertyDelegates.size();
        
        LOGGER.exiting(CLASS_NAME, "countPropertyDelegates", count);
        return count;
    }
    
    /**
     * 指定されたPropertyDelegateを追加する。
     * @param delegate 追加するPropertyDelegate
     * @return 追加に成功したらtrue、そうでなければfalse
     */
    public boolean addPropertyDelegate(PropertyDelegate delegate) {
        LOGGER.entering(CLASS_NAME, "addPropertyDelegate", delegate);
        
        boolean result = propertyDelegates.add(delegate);
        
        LOGGER.exiting(CLASS_NAME, "addPropertyDelegate", result);
        return result;
    }
    
    /**
     * 指定されたPropertyDelegateを抹消する。
     * @param delegate 抹消するPropertyDelegate
     * @return 抹消に成功したらtrue、そうでなければfalse
     */
    public boolean removePropertyDelegate(PropertyDelegate delegate) {
        LOGGER.entering(CLASS_NAME, "removePropertyDelegate", delegate);
        
        boolean result = propertyDelegates.remove(delegate);
        
        LOGGER.exiting(CLASS_NAME, "removePropertyDelegate", result);
        return result;
    }
    
    /**
     * index番目のPropertyDelegateを返す。
     * @param index PropertyDelegateのインデックス
     * @return 指定されたPropertyDelegate
     */
    public PropertyDelegate getPropertyDelegate(int index) {
        LOGGER.entering(CLASS_NAME, "getPropertyDelegate");
        
        PropertyDelegate delegate = propertyDelegates.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getPropertyDelegate", delegate);
        return delegate;
    }
    
    /**
     * 登録されているPropertyUpdaterの個数を返す。
     * @return 登録されているPropertyUpdaterの個数
     */
    public int countPropertyUpdaters() {
        LOGGER.entering(CLASS_NAME, "countPropertyUpdaters");
        
        int count = propertyUpdaters.size();
        
        LOGGER.exiting(CLASS_NAME, "countPropertyUpdaters", count);
        return count;
    }
    
    /**
     * 指定されたPropertyUpdaterを追加する。
     * @param updater 追加するPropertyUpdater
     * @return 追加に成功したらtrue、そうでなければfalse
     */
    public boolean addPropertyUpdater(PropertyUpdater updater) {
        LOGGER.entering(CLASS_NAME, "addPropertyUpdater", updater);
        
        boolean result = propertyUpdaters.add(updater);
        
        LOGGER.exiting(CLASS_NAME, "addPropertyUpdater", result);
        return result;
    }
    
    /**
     * 指定されたPropertyUpdaterを抹消する。
     * @param updater 抹消するPropertyUpdater
     * @return 抹消に成功したらtrue、そうでなければfalse
     */
    public boolean removePropertyUpdater(PropertyUpdater updater) {
        LOGGER.entering(CLASS_NAME, "removePropertyUpdater", updater);
        
        boolean result = propertyUpdaters.remove(updater);
        
        LOGGER.exiting(CLASS_NAME, "removePropertyUpdater", result);
        return result;
    }
    
    /**
     * index番目のPropertyUpdaterを返す。
     * @param index PropertyUpdaterのインデックス
     * @return 指定されたPropertyUpdater
     */
    public PropertyUpdater getPropertyUpdater(int index) {
        LOGGER.entering(CLASS_NAME, "getPropertyUpdater");
        
        PropertyUpdater updater = propertyUpdaters.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getPropertyUpdater", updater);
        return updater;
    }
    
    /**
     * このLocalObjectConfigを利用したLocalObjectが生成された時に呼び出される。
     * 登録されているLocalObjectServiceDelegate、PropertyDelegate、PropertyUpdaterのnotifyCreationを呼び出す。
     * @param object 生成されたLocalObject
     * @param core 生成に利用したCore
     */
    public void notifyCreation(LocalObject object, Core core) {
        for (LocalObjectDelegate delegate : delegates) {
            if (delegate instanceof LocalObjectServiceDelegate) {
                ((LocalObjectServiceDelegate)delegate).notifyCreation(object, core);
            }
        }
        
        for (PropertyDelegate  propertyDelegate : propertyDelegates) {
            propertyDelegate.notifyCreation(object, core);
        }
        
        for (PropertyUpdater  propertyUpdater : propertyUpdaters) {
            propertyUpdater.notifyCreation(object, core);
        }
    }
    
    /**
     * 指定されたLazyConfigurationを追加する。
     * @param configuration 追加するLazyConfiguration
     * @return 追加に成功した場合にはtrue、失敗した場合にはfalse
     */
    public boolean addLazyConfiguration(LazyConfiguration configuration) {
        LOGGER.entering(CLASS_NAME, "addLazyConfiguration", configuration);
        
        boolean result = lazyConfigurations.add(configuration);
        
        LOGGER.exiting(CLASS_NAME, "addLazyConfiguration", configuration);
        return result;
    }
    
    /**
     * 登録されているLazyConfigurationの個数を返す。
     * @return 登録されているLazyConfigurationの個数
     */
    public int countLazyConfigurations() {
        LOGGER.entering(CLASS_NAME, "countLazyConfigurations");
        
        int count = lazyConfigurations.size();
        
        LOGGER.exiting(CLASS_NAME, "countLazyConfigurations", count);
        return count;
    }
    
    /**
     * index番目のLazyConfigurationを返す。
     * @param index LazyConfigurationのインデックス
     * @return 指定されたLazyConfiguration
     */
    public LazyConfiguration getLazyConfiguration(int index) {
        LOGGER.entering(CLASS_NAME, "getLazyConfiguration");
        
        LazyConfiguration configuration = lazyConfigurations.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getLazyConfiguration", configuration);
        return configuration;
    }
    
    /**
     * 指定されたLazyConfigurationを抹消する。
     * @param configuration 抹消するLazyConfiguration
     * @return 抹消に成功したらtrue、そうでなければfalse
     */
    public boolean removeLazyConfiguration(LazyConfiguration configuration) {
        LOGGER.entering(CLASS_NAME, "removeLazyConfiguration", configuration);
        
        boolean result = lazyConfigurations.remove(configuration);
        
        LOGGER.exiting(CLASS_NAME, "removeLazyConfiguration", configuration);
        return result;
    }
    
    /**
     * 指定されたLazyConfigurationが登録されているかどうかを返す。
     * @param configuration 登録の確認を行うLazyConfiguration
     * @return 登録されていたらtrue、そうでなければfalse
     */
    public boolean containsLazyConfiguration(LazyConfiguration configuration) {
        LOGGER.entering(CLASS_NAME, "removeLazyConfiguration", configuration);
        
        boolean result = lazyConfigurations.contains(configuration);
        
        LOGGER.exiting(CLASS_NAME, "removeLazyConfiguration", configuration);
        return result;
    }
    
    /**
     * 登録されたLazyConfigurationのconfigureメソッドを呼び出す。
     * @param core 利用されるCoreの指定
     */
    public void lazyConfigure(Core core) {
        for (LazyConfiguration lazyConfiguration : lazyConfigurations) {
            lazyConfiguration.configure(this, core);
        }
    }
}