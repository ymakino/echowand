package echowand.service;

import echowand.common.EPC;
import echowand.object.LocalObject;
import echowand.object.LocalObjectDefaultDelegate;
import echowand.object.ObjectData;
import java.util.logging.Logger;

/**
 * PropertyDelegateをLocalObjectDelegateとして利用
 * @author ymakino
 */
public class LocalObjectPropertyDelegate extends LocalObjectDefaultDelegate {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectPropertyDelegate.class.getName());
    private static final String CLASS_NAME = LocalObjectPropertyDelegate.class.getName();
    
    private PropertyDelegate propertyDelegate;
    
    /**
     * 利用するPropertyDelegateを指定してLocalObjectPropertyDelegateを生成する。
     * @param propertyDelegate 利用するPropertyDelegate
     */
    public LocalObjectPropertyDelegate(PropertyDelegate propertyDelegate) {
        LOGGER.entering(CLASS_NAME, "LocalObjectPropertyDelegate", propertyDelegate);
        
        this.propertyDelegate = propertyDelegate;
        
        LOGGER.exiting(CLASS_NAME, "LocalObjectPropertyDelegate");
    }
    
    private boolean isTargetEPC(EPC epc) {
        LOGGER.entering(CLASS_NAME, "isTargetEPC", epc);
        
        boolean result = propertyDelegate.getEPC() == epc;
        
        LOGGER.exiting(CLASS_NAME, "isTargetEPC", result);
        return result;
    }

    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
        LOGGER.entering(CLASS_NAME, "getData", new Object[]{result, object, epc});
        
        if (!isTargetEPC(epc) || !propertyDelegate.isGetEnabled()) {
            return;
        }

        ObjectData data = propertyDelegate.getUserData(object, epc);
        if (data != null) {
            result.setGetData(data);
            result.setDone();
        } else {
            result.setFail();
        }
        
        LOGGER.exiting(CLASS_NAME, "getData");
    }

    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData curData) {
        LOGGER.entering(CLASS_NAME, "setData", new Object[]{result, object, epc, newData, curData});
        
        if (!isTargetEPC(epc) || !propertyDelegate.isSetEnabled()) {
            return;
        }

        boolean ret = propertyDelegate.setUserData(object, epc, newData);
        if (ret) {
            result.setDone();
        } else {
            result.setFail();
        }
        
        LOGGER.exiting(CLASS_NAME, "setData");
    }

    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData curData, ObjectData oldData) {
        LOGGER.entering(CLASS_NAME, "notifyDataChanged", new Object[]{result, object, epc, curData, oldData});
        
        if (!isTargetEPC(epc) || !propertyDelegate.isNotifyEnabled()) {
            return;
        }
        
        propertyDelegate.notifyDataChanged(object, epc, curData, oldData);
        
        LOGGER.exiting(CLASS_NAME, "notifyDataChanged");
    }
    
}
