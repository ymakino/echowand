package echowand.service;

import echowand.info.TemperatureSensorInfo;
import echowand.net.InternalSubnet;
import echowand.object.LocalObject;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ymakino
 */
public class LocalObjectCreatorResultTest {
    
    @Test
    public void testCreation() {
        LocalObject localObject = new LocalObject(new TemperatureSensorInfo());
        ServiceManager serviceManager = new ServiceManager(new InternalSubnet("LocalObjectCreatorResultTest"));
        LocalObjectUpdater updater = new LocalObjectUpdater(localObject, serviceManager);
        LocalObjectCreatorResult result = new LocalObjectCreatorResult(localObject, updater);
        
        assertEquals(localObject, result.object);
        assertEquals(updater, result.updater);
    }
    
}
