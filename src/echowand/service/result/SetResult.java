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
public class SetResult extends ResultBase<SetResult> {
    private static final Logger LOGGER = Logger.getLogger(SetResult.class.getName());
    private static final String CLASS_NAME = SetResult.class.getName();
    
    private boolean responseRequired = false;
    
    public SetResult(boolean responseRequired, TimestampManager timestampManager) {
        super(SetResult.class, timestampManager);
        this.responseRequired = responseRequired;
    }
    
    public synchronized void setSetListener(SetListener setListener) {
        LOGGER.entering(CLASS_NAME, "setSetListener", setListener);
        
        setResultListener(setListener);
        
        LOGGER.exiting(CLASS_NAME, "setSetListener");
    }
    
    @Override
    public boolean isSuccessPayload(StandardPayload payload) {
        LOGGER.entering(CLASS_NAME, "isSuccessPayload", payload);
        
        boolean result = false;
        if (responseRequired) {
            result = (payload.getESV() == ESV.Set_Res);
        }
        
        LOGGER.exiting(CLASS_NAME, "isSuccessPayload", result);
        return result;
    }
    
    @Override
    public boolean isValidPayload(StandardPayload payload) {
        LOGGER.entering(CLASS_NAME, "isValidPayload", payload);
        
        ESV esv = payload.getESV();
        boolean result;
        
        if (responseRequired) {
            result = (esv == ESV.Set_Res ||  esv == ESV.SetC_SNA);
        } else {
            result = (esv == ESV.SetI_SNA);
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