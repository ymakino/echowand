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
public class GetResult extends ResultBase<GetResult> {
    private static final Logger LOGGER = Logger.getLogger(GetResult.class.getName());
    private static final String CLASS_NAME = GetResult.class.getName();
    
    public GetResult(TimestampManager timestampManager) {
        super(GetResult.class, timestampManager);
    }
    
    public synchronized void setGetListener(GetListener getListener) {
        LOGGER.entering(CLASS_NAME, "setGetListener", getListener);
        
        setResultListener(getListener);
        
        LOGGER.exiting(CLASS_NAME, "setGetListener");
    }
    
    @Override
    public boolean isSuccessPayload(StandardPayload payload) {
        LOGGER.entering(CLASS_NAME, "isSuccessPayload", payload);
        
        boolean result = payload.getESV() == ESV.Get_Res;
        
        LOGGER.exiting(CLASS_NAME, "isSuccessPayload", result);
        return result;
    }
    
    @Override
    public boolean isValidPayload(StandardPayload payload) {
        LOGGER.entering(CLASS_NAME, "isValidPayload", payload);
        
        ESV esv = payload.getESV();
        boolean result = (esv == ESV.Get_Res || esv == ESV.Get_SNA);
        
        LOGGER.exiting(CLASS_NAME, "isValidPayload", result);
        return result;
    }
    
    @Override
    public boolean isValidProperty(Property property) {
        LOGGER.entering(CLASS_NAME, "isValidProperty", property);
        
        boolean result = (property.getPDC() != 0);
        
        LOGGER.exiting(CLASS_NAME, "isValidProperty", result);
        return result;
    }
}
