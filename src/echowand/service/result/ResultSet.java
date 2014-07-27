package echowand.service.result;

import echowand.common.ESV;
import echowand.net.Property;
import echowand.net.StandardPayload;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultSet extends ResultBase {
    private static final Logger LOGGER = Logger.getLogger(ResultSet.class.getName());
    private static final String CLASS_NAME = ResultSet.class.getName();
    
    private boolean responseRequired = false;
    
    public ResultSet(boolean responseRequired) {
        this.responseRequired = responseRequired;
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