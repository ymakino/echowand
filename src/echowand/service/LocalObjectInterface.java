package echowand.service;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class LocalObjectInterface {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectInterface.class.getName());
    private static final String CLASS_NAME = LocalObjectInterface.class.getName();
    
    private ServiceManager serviceManager;
    private LocalObject localObject;
    
    public ServiceManager setServiceManager(ServiceManager serviceManager) {
        LOGGER.entering(CLASS_NAME, "setLocalObjectManager", serviceManager);
        
        ServiceManager lastManager = serviceManager;
        this.serviceManager = serviceManager;
        
        LOGGER.exiting(CLASS_NAME, "setLocalObjectManager", lastManager);
        return lastManager;
    }
    
    public LocalObject setLocalObject(LocalObject object) {
        LOGGER.entering(CLASS_NAME, "setLocalObject", object);
        
        LocalObject lastObject = localObject;
        localObject = object;
        
        LOGGER.exiting(CLASS_NAME, "setLocalObject", localObject);
        return lastObject;
    }
    
    public ServiceManager getServiceManager() {
        return serviceManager;
    }
    
    public LocalObject getLocalObject() {
        return localObject;
    }
    
    public ObjectData getData(EPC epc) {
        LOGGER.entering(CLASS_NAME, "getData", epc);
        
        ObjectData data = localObject.getData(epc);
        
        LOGGER.exiting(CLASS_NAME, "getData", data);
        return data;
    }
    
    public ObjectData getData(EOJ eoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "getData", new Object[]{eoj, epc});
        
        LocalObject otherObject = serviceManager.getLocalObjectManager().get(eoj);
        
        ObjectData data = null;
        if (otherObject != null) {
            data = otherObject.getData(epc);
        }
        
        LOGGER.exiting(CLASS_NAME, "getData", data);
        return data;
    }
    
    public boolean setData(EPC epc, ObjectData data) {
        LOGGER.entering(CLASS_NAME, "setData", new Object[]{epc, data});
        
        boolean result = localObject.forceSetData(epc, data);
        
        LOGGER.entering(CLASS_NAME, "setData", result);
        return result;
    }
    
    public boolean setData(EOJ eoj, EPC epc, ObjectData data) {
        LOGGER.entering(CLASS_NAME, "setData", new Object[]{eoj, epc, data});
        
        LocalObject otherObject = serviceManager.getLocalObjectManager().get(eoj);
        
        boolean result = false;
        if (otherObject != null) {
            result = otherObject.forceSetData(epc, data);
        }
        
        LOGGER.entering(CLASS_NAME, "setData", result);
        return result;
    }
}
