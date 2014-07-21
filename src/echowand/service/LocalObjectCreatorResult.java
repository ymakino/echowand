package echowand.service;

import echowand.object.LocalObject;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class LocalObjectCreatorResult {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectCreatorResult.class.getName());
    private static final String CLASS_NAME = LocalObjectCreatorResult.class.getName();
    
    public final LocalObject object;
    public final LocalObjectUpdater updater;
    
    public LocalObjectCreatorResult(LocalObject object, LocalObjectUpdater updater) {
        LOGGER.entering(CLASS_NAME, "LocalObjectCreatorResult", new Object[]{object, updater});
        
        this.object = object;
        this.updater = updater;
        
        LOGGER.exiting(CLASS_NAME, "LocalObjectCreatorResult");
    }
}
