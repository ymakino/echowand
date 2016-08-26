package echowand.service.result;

import echowand.common.ESV;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.service.TimestampManager;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class SetGetResult extends ResultBase<SetGetResult> {
    private static final Logger LOGGER = Logger.getLogger(SetGetResult.class.getName());
    private static final String CLASS_NAME = SetGetResult.class.getName();
    
    public SetGetResult(TimestampManager timestampManager) {
        super(SetGetResult.class, timestampManager);
    }
    
    public synchronized void setSetGetListener(SetGetListener listener) {
        LOGGER.entering(CLASS_NAME, "setSetGetListener", listener);
        
        setResultListener(listener);
        
        LOGGER.exiting(CLASS_NAME, "setSetGetListener");
    }
    
    @Override
    public boolean isSuccessPayload(StandardPayload payload) {
        LOGGER.entering(CLASS_NAME, "isSuccessPayload", payload);
        
        boolean result = payload.getESV() == ESV.SetGet_Res;
        
        LOGGER.exiting(CLASS_NAME, "isSuccessPayload", result);
        return result;
    }
    
    @Override
    public boolean isValidPayload(StandardPayload payload) {
        LOGGER.entering(CLASS_NAME, "isValidPayload", payload);
        
        ESV esv = payload.getESV();
        boolean result = (esv == ESV.SetGet_Res || esv == ESV.SetGet_SNA);
        
        LOGGER.exiting(CLASS_NAME, "isValidPayload", result);
        return result;
    }
    
    @Override
    public boolean isValidProperty(Property property) {
        LOGGER.entering(CLASS_NAME, "isValidProperty", property);
        
        boolean result = (property.getPDC() == 0);
        
        LOGGER.exiting(CLASS_NAME, "isValidProperty", result);
        return result;
    }
    
    @Override
    public boolean isValidSecondProperty(Property property) {
        LOGGER.entering(CLASS_NAME, "isValidProperty", property);
        
        boolean result = (property.getPDC() != 0);
        
        LOGGER.exiting(CLASS_NAME, "isValidProperty", result);
        return result;
    }
}
