package echowand.service.result;

import echowand.common.ESV;
import echowand.net.Property;
import echowand.net.StandardPayload;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultGet extends ResultBase {
    private static final Logger LOGGER = Logger.getLogger(ResultGet.class.getName());
    private static final String CLASS_NAME = ResultGet.class.getName();
    
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
        
        boolean result = (property.getPDC() > 0);
        
        LOGGER.exiting(CLASS_NAME, "isValidProperty", result);
        return result;
    }
}
