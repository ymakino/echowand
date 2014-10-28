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
public class LocalObjectAccessInterface {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectAccessInterface.class.getName());
    private static final String CLASS_NAME = LocalObjectAccessInterface.class.getName();
    
    private Core core = null;
    private LocalObject localObject = null;
    
    public Core setCore(Core core) {
        LOGGER.entering(CLASS_NAME, "setCore", core);
        
        Core lastCore = this.core;
        this.core = core;
        
        LOGGER.exiting(CLASS_NAME, "setCore", lastCore);
        return lastCore;
    }
    
    public LocalObject setLocalObject(LocalObject object) {
        LOGGER.entering(CLASS_NAME, "setLocalObject", object);
        
        LocalObject lastObject = localObject;
        localObject = object;
        
        LOGGER.exiting(CLASS_NAME, "setLocalObject", localObject);
        return lastObject;
    }
    
    public Core getCore() {
        return core;
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
        
        LocalObject otherObject = core.getLocalObjectManager().get(eoj);
        
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
        
        LocalObject otherObject = core.getLocalObjectManager().get(eoj);
        
        boolean result = false;
        if (otherObject != null) {
            result = otherObject.forceSetData(epc, data);
        }
        
        LOGGER.entering(CLASS_NAME, "setData", result);
        return result;
    }
}
