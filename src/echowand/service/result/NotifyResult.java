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
public class NotifyResult extends ResultBase<NotifyResult> {
    private static final Logger LOGGER = Logger.getLogger(NotifyResult.class.getName());
    private static final String CLASS_NAME = NotifyResult.class.getName();
    
    private boolean responseRequired = false;
    
    public NotifyResult(boolean responseRequired, TimestampManager timestampManager) {
        super(NotifyResult.class, timestampManager);
        this.responseRequired = responseRequired;
    }
    
    public synchronized void setNotifyListener(NotifyListener notifyListener) {
        LOGGER.entering(CLASS_NAME, "setNotifyListener", notifyListener);
        
        setResultListener(notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "setNotifyListener");
    }
    
    @Override
    public boolean isSuccessPayload(StandardPayload payload) {
        LOGGER.entering(CLASS_NAME, "isSuccessPayload", payload);
        
        boolean result = false;
        if (responseRequired) {
            result = (payload.getESV() == ESV.INFC_Res);
        }
        
        LOGGER.exiting(CLASS_NAME, "isSuccessPayload", result);
        return result;
    }
    
    @Override
    public boolean isValidPayload(StandardPayload payload) {
        LOGGER.entering(CLASS_NAME, "isValidPayload", payload);
        
        boolean result = false;
        if (responseRequired) {
            result = (payload.getESV() == ESV.INFC_Res);
        }
        
        LOGGER.exiting(CLASS_NAME, "isValidPayload", result);
        return result;
    }
    
    @Override
    public boolean isValidProperty(Property property) {
        LOGGER.entering(CLASS_NAME, "isValidProperty", property);
        
        boolean result = false;
        if (responseRequired) {
            result = (property.getPDC() == 0);
        }
        
        LOGGER.exiting(CLASS_NAME, "isValidProperty", result);
        return result;
    }
}
