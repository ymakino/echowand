package echowand.service;

import echowand.info.ObjectInfo;
import echowand.object.LocalObject;
import echowand.object.LocalObjectDelegate;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class LocalObjectConfig {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectConfig.class.getName());
    private static final String CLASS_NAME = LocalObjectConfig.class.getName();
    
    private ObjectInfo objectInfo;
    private LinkedList<LocalObjectDelegate> delegates;
    private LinkedList<PropertyDelegate> propertyDelegates;
    private LinkedList<PropertyUpdater> propertyUpdaters;
    
    public LocalObjectConfig(ObjectInfo objectInfo) {
        LOGGER.entering(CLASS_NAME, "LocalObjectConfig", objectInfo);
        
        this.objectInfo = objectInfo;
        delegates = new LinkedList<LocalObjectDelegate>();
        propertyDelegates = new LinkedList<PropertyDelegate>();
        propertyUpdaters = new LinkedList<PropertyUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "LocalObjectConfig");
    }
    
    public ObjectInfo getObjectInfo() {
        LOGGER.entering(CLASS_NAME, "getObjectInfo");
        
        LOGGER.exiting(CLASS_NAME, "getObjectInfo", objectInfo);
        return objectInfo;
    }
    
    public int countDelegates() {
        LOGGER.entering(CLASS_NAME, "countDelegates");
        
        int count = delegates.size();
        
        LOGGER.exiting(CLASS_NAME, "countDelegates", count);
        return count;
    }
    
    public boolean addDelegate(LocalObjectDelegate delegate) {
        LOGGER.entering(CLASS_NAME, "addDelegate", delegate);
        
        boolean result = delegates.add(delegate);
        
        LOGGER.exiting(CLASS_NAME, "addDelegate", result);
        return result;
    }
    
    public boolean removeDelegate(LocalObjectDelegate delegate) {
        LOGGER.entering(CLASS_NAME, "removeDelegate", delegate);
        
        boolean result = delegates.remove(delegate);
        
        LOGGER.exiting(CLASS_NAME, "removeDelegate", result);
        return result;
    }
    
    public LocalObjectDelegate getDelegate(int index) {
        LOGGER.entering(CLASS_NAME, "getDelegate");
        
        LocalObjectDelegate delegate = delegates.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getDelegate", delegate);
        return delegate;
    }
    
    public int countPropertyDelegates() {
        LOGGER.entering(CLASS_NAME, "countPropertyDelegates");
        
        int count = propertyDelegates.size();
        
        LOGGER.exiting(CLASS_NAME, "countPropertyDelegates", count);
        return count;
    }
    
    public boolean addPropertyDelegate(PropertyDelegate delegate) {
        LOGGER.entering(CLASS_NAME, "addPropertyDelegate", delegate);
        
        boolean result = propertyDelegates.add(delegate);
        
        LOGGER.exiting(CLASS_NAME, "addPropertyDelegate", result);
        return result;
    }
    
    public boolean removePropertyDelegate(PropertyDelegate delegate) {
        LOGGER.entering(CLASS_NAME, "removePropertyDelegate", delegate);
        
        boolean result = propertyDelegates.remove(delegate);
        
        LOGGER.exiting(CLASS_NAME, "removePropertyDelegate", result);
        return result;
    }
    
    public PropertyDelegate getPropertyDelegate(int index) {
        LOGGER.entering(CLASS_NAME, "getPropertyDelegate");
        
        PropertyDelegate delegate = propertyDelegates.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getPropertyDelegate", delegate);
        return delegate;
    }
    
    public int countPropertyUpdaters() {
        LOGGER.entering(CLASS_NAME, "countPropertyUpdaters");
        
        int count = propertyUpdaters.size();
        
        LOGGER.exiting(CLASS_NAME, "countPropertyUpdaters", count);
        return count;
    }
    
    public boolean addPropertyUpdater(PropertyUpdater updater) {
        LOGGER.entering(CLASS_NAME, "addPropertyUpdater", updater);
        
        boolean result = propertyUpdaters.add(updater);
        
        LOGGER.exiting(CLASS_NAME, "addPropertyUpdater", result);
        return result;
    }
    
    public boolean removePropertyUpdater(PropertyUpdater updater) {
        LOGGER.entering(CLASS_NAME, "removePropertyUpdater", updater);
        
        boolean result = propertyUpdaters.remove(updater);
        
        LOGGER.exiting(CLASS_NAME, "removePropertyUpdater", result);
        return result;
    }
    
    public PropertyUpdater getPropertyUpdater(int index) {
        LOGGER.entering(CLASS_NAME, "getPropertyUpdater");
        
        PropertyUpdater updater = propertyUpdaters.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getPropertyUpdater", updater);
        return updater;
    }
    
    public void notifyCreation(LocalObject object) {
        for (LocalObjectDelegate delegate : delegates) {
            if (delegate instanceof LocalObjectServiceDelegate) {
                ((LocalObjectServiceDelegate)delegate).notifyCreation(object);
            }
        }
        
        for (PropertyDelegate  propertyDelegate : propertyDelegates) {
            propertyDelegate.notifyCreation(object);
        }
        
        for (PropertyUpdater  propertyUpdater : propertyUpdaters) {
            propertyUpdater.notifyCreation(object);
        }
    }
}