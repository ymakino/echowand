package echowand.service;

import echowand.logic.TooManyObjectsException;
import echowand.logic.TransactionManager;
import echowand.net.Subnet;
import echowand.object.LocalObject;
import echowand.object.LocalObjectNotifyDelegate;
import java.util.logging.Logger;

/**
 * LocalObjectConfigからローカルオブジェクトを生成
 * @author ymakino
 */
public class LocalObjectCreator {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectCreator.class.getName());
    private static final String CLASS_NAME = LocalObjectCreator.class.getName();
    
    private LocalObjectConfig config;
    
    /**
     * 利用するLocalObjectConfigを指定してLocalObjectCreatorを生成する。
     * @param config 利用するLocalObjectConfig
     */
    public LocalObjectCreator(LocalObjectConfig config) {
        LOGGER.entering(CLASS_NAME, "LocalObjectCreator", config);
        
        this.config = config;
        
        LOGGER.exiting(CLASS_NAME, "LocalObjectCreator");
    }
    
    private LocalObject createLocalObject(Core core) throws TooManyObjectsException {
        LOGGER.entering(CLASS_NAME, "createLocalObject", core);
        
        LocalObject object = new LocalObject(config.getObjectInfo());

        int propertyDelegateSize = config.countPropertyDelegates();
        for (int i=0; i<propertyDelegateSize; i++) {
            PropertyDelegate propertyDelegate = config.getPropertyDelegate(i);
            propertyDelegate.setLocalObject(object);
            propertyDelegate.setCore(core);
            LocalObjectPropertyDelegate delegate = new LocalObjectPropertyDelegate(propertyDelegate);
            object.addDelegate(delegate);
        }

        int delegateSize = config.countDelegates();
        for (int i=0; i<delegateSize; i++) {
            object.addDelegate(config.getDelegate(i));
        }
        
        Subnet subnet = core.getSubnet();
        TransactionManager transactionManager = core.getTransactionManager();
        object.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
        
        core.getLocalObjectManager().add(object);
        
        LOGGER.exiting(CLASS_NAME, "createLocalObject", object);
        return object;
    }
    
    private LocalObjectUpdater createUpdater(LocalObject localObject, Core core) {
        LOGGER.entering(CLASS_NAME, "createUpdater", new Object[]{localObject, core});
        
        int propertyUpdaterSize = config.countPropertyUpdaters();
        
        if (propertyUpdaterSize == 0) {
            LOGGER.exiting(CLASS_NAME, "createUpdater", null);
            return null;
        }
        
        LocalObjectUpdater updater = new LocalObjectUpdater(localObject, core);
        
        for (int i=0; i<propertyUpdaterSize; i++) {
            updater.addPropertyUpdater(config.getPropertyUpdater(i));
        }
        
        LOGGER.exiting(CLASS_NAME, "createUpdater", updater);
        return updater;
    }
    
    /**
     * 利用するCoreを指定してローカルオブジェクトを生成する。
     * @param core 利用するCoreの指定
     * @return 生成したローカルオブジェクト
     * @throws TooManyObjectsException ローカルオブジェクトの数が多すぎる場合
     */
    public LocalObjectCreatorResult create(Core core) throws TooManyObjectsException {
        LOGGER.entering(CLASS_NAME, "create", core);
        
        config.lazyConfigure(core);
        LocalObject object = createLocalObject(core);
        LocalObjectUpdater updater = createUpdater(object, core);
        config.notifyCreation(object, core);
        
        LocalObjectCreatorResult result = new LocalObjectCreatorResult(object, updater);
        LOGGER.exiting(CLASS_NAME, "create", result);
        return result;
    }
}
