package echowand.service;

import echowand.logic.TooManyObjectsException;
import echowand.logic.TransactionManager;
import echowand.net.Subnet;
import echowand.object.LocalObject;
import echowand.object.LocalObjectNotifyDelegate;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class LocalObjectCreator {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectCreator.class.getName());
    private static final String CLASS_NAME = LocalObjectCreator.class.getName();
    
    private LocalObjectConfig config;
    
    public LocalObjectCreator(LocalObjectConfig config) {
        LOGGER.entering(CLASS_NAME, "LocalObjectCreator", config);
        
        this.config = config;
        
        LOGGER.exiting(CLASS_NAME, "LocalObjectCreator");
    }
    
    private LocalObject createLocalObject(ServiceManager service) throws TooManyObjectsException {
        LOGGER.entering(CLASS_NAME, "createLocalObject", service);
        
        LocalObject object = new LocalObject(config.getObjectInfo());

        int propertyDelegateSize = config.countPropertyDelegates();
        for (int i=0; i<propertyDelegateSize; i++) {
            PropertyDelegate propertyDelegate = config.getPropertyDelegate(i);
            propertyDelegate.setLocalObject(object);
            propertyDelegate.setServiceManager(service);
            LocalObjectPropertyDelegate delegate = new LocalObjectPropertyDelegate(propertyDelegate);
            object.addDelegate(delegate);
        }

        int delegateSize = config.countDelegates();
        for (int i=0; i<delegateSize; i++) {
            object.addDelegate(config.getDelegate(i));
        }
        
        Subnet subnet = service.getSubnet();
        TransactionManager transactionManager = service.getTransactionManager();
        object.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
        
        service.getLocalObjectManager().add(object);
        
        LOGGER.exiting(CLASS_NAME, "createLocalObject", object);
        return object;
    }
    
    private LocalObjectUpdater createUpdater(LocalObject localObject, ServiceManager serviceManager) {
        LOGGER.entering(CLASS_NAME, "createUpdater", new Object[]{localObject, serviceManager});
        
        int propertyUpdaterSize = config.countPropertyUpdaters();
        
        if (propertyUpdaterSize == 0) {
            LOGGER.exiting(CLASS_NAME, "createUpdater", null);
            return null;
        }
        
        LocalObjectUpdater updater = new LocalObjectUpdater(localObject, serviceManager);
        
        for (int i=0; i<propertyUpdaterSize; i++) {
            updater.addPropertyUpdater(config.getPropertyUpdater(i));
        }
        
        LOGGER.exiting(CLASS_NAME, "createUpdater", updater);
        return updater;
    }
    
    public LocalObjectCreatorResult create(ServiceManager service) throws TooManyObjectsException {
        LOGGER.entering(CLASS_NAME, "create", service);
        
        LocalObject object = createLocalObject(service);
        LocalObjectUpdater updater = createUpdater(object, service);
        
        LocalObjectCreatorResult result = new LocalObjectCreatorResult(object, updater);
        LOGGER.exiting(CLASS_NAME, "create", result);
        return result;
    }
}
