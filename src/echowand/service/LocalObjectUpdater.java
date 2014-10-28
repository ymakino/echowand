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
    private Core core;
    private LinkedList<PropertyUpdater> propertyUpdaters;
    
    public LocalObjectUpdater(LocalObject localObject, Core core) {
        LOGGER.entering(CLASS_NAME, "LocalObjectUpdater", new Object[]{localObject, core});
        
        this.localObject = localObject;
        this.core = core;
        propertyUpdaters = new LinkedList<PropertyUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "LocalObjectUpdater");
    }
    
    public LocalObject getLocalObject() {
        LOGGER.entering(CLASS_NAME, "getLocalObject");
        
        LOGGER.exiting(CLASS_NAME, "getLocalObject", localObject);
        return localObject;
    }
    
    public Core getCore() {
        LOGGER.entering(CLASS_NAME, "getCore");
        
        LOGGER.exiting(CLASS_NAME, "getCore", core);
        return core;
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
        LOGGER.entering(CLASS_NAME, "removePropertyUpdater", delegate);
        
        boolean result = propertyUpdaters.remove(delegate);
        LOGGER.exiting(CLASS_NAME, "removePropertyUpdater", result);
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
            propertyUpdater.setCore(core);
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
