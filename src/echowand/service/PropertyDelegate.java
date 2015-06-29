package echowand.service;

import echowand.common.EPC;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class PropertyDelegate extends LocalObjectAccessInterface {
    private static final Logger LOGGER = Logger.getLogger(PropertyDelegate.class.getName());
    private static final String CLASS_NAME = PropertyDelegate.class.getName();
    
    private EPC epc;
    private boolean getEnabled;
    private boolean setEnabled;
    private boolean notifyEnabled;
    
    public PropertyDelegate(EPC epc, boolean getEnabled, boolean setEnabled, boolean notifyEnabled) {
        LOGGER.entering(CLASS_NAME, "PropertyDelegate", new Object[]{epc, getEnabled, setEnabled, notifyEnabled});
        
        this.epc = epc;
        this.getEnabled = getEnabled;
        this.setEnabled = setEnabled;
        this.notifyEnabled = notifyEnabled;
        
        LOGGER.exiting(CLASS_NAME, "PropertyDelegate");
    }

    public EPC getEPC() {
        return epc;
    }
    
    public boolean isGetEnabled() {
        return getEnabled;
    }
    
    public boolean isSetEnabled() {
        return setEnabled;
    }
    
    public boolean isNotifyEnabled() {
        return notifyEnabled;
    }
    
    
    public void notifyCreated(LocalObject object) {
    }
    
    public ObjectData getUserData(LocalObject object, EPC epc) {
        return null;
    }

    public boolean setUserData(LocalObject object, EPC epc, ObjectData data) {
        return false;
    }
    
    public void notifyDataChanged(LocalObject object, EPC epc, ObjectData curData, ObjectData oldData) {
    }
}
