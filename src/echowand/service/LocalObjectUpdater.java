package echowand.service;

import echowand.object.LocalObject;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class LocalObjectUpdater implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectUpdater.class.getName());
    private static final String CLASS_NAME = LocalObjectUpdater.class.getName();
    
    private LocalObject localObject;
    private ServiceManager serviceManager;
    private LinkedList<PropertyUpdater> propertyUpdaters;
    
    public LocalObjectUpdater(LocalObject localObject, ServiceManager serviceManager) {
        LOGGER.entering(CLASS_NAME, "LocalObjectUpdater", new Object[]{localObject, serviceManager});
        
        this.localObject = localObject;
        this.serviceManager = serviceManager;
        propertyUpdaters = new LinkedList<PropertyUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "LocalObjectUpdater");
    }
    
    public LocalObject getLocalObject() {
        LOGGER.entering(CLASS_NAME, "getLocalObject");
        
        LOGGER.exiting(CLASS_NAME, "getLocalObject", localObject);
        return localObject;
    }
    
    public ServiceManager getServiceManager() {
        LOGGER.entering(CLASS_NAME, "getServiceManager");
        
        LOGGER.exiting(CLASS_NAME, "getServiceManager", serviceManager);
        return serviceManager;
    }
    
    public int countPropertyUpdaters() {
        LOGGER.entering(CLASS_NAME, "countPropertyUpdaters");
        
        int size = propertyUpdaters.size();
        LOGGER.exiting(CLASS_NAME, "countPropertyUpdaters", size);
        return size;
    }
    
    public boolean addPropertyUpdater(PropertyUpdater delegate) {
        LOGGER.entering(CLASS_NAME, "addPropertyUpdater", delegate);
        
        boolean result = propertyUpdaters.add(delegate);
        LOGGER.exiting(CLASS_NAME, "addPropertyUpdater", result);
        return result;
    }
    
    public boolean removePropertyUpdater(PropertyUpdater delegate) {
        LOGGER.entering(CLASS_NAME, "addPropertyUpdater", delegate);
        
        boolean result = propertyUpdaters.remove(delegate);
        LOGGER.exiting(CLASS_NAME, "addPropertyUpdater", result);
        return result;
    }
    
    public PropertyUpdater getPropertyUpdater(int index) {
        LOGGER.entering(CLASS_NAME, "getPropertyUpdater", index);
        
        PropertyUpdater updater = propertyUpdaters.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getPropertyUpdater", updater);
        return updater;
    }
    
    @Override
    public void run() {
        LOGGER.entering(CLASS_NAME, "run");
        
        LinkedList<PropertyUpdaterThread> threads = new LinkedList<PropertyUpdaterThread>();
        
        for (PropertyUpdater propertyUpdater : propertyUpdaters) {
            propertyUpdater.setServiceManager(serviceManager);
            propertyUpdater.setLocalObject(localObject);
            threads.add(new PropertyUpdaterThread(propertyUpdater));
        }

        for (PropertyUpdaterThread thread : threads) {
            thread.start();
        }

        for (PropertyUpdaterThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(LocalObjectUpdater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "run");
    }
}
