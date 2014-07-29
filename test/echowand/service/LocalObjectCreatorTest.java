package echowand.service;

import echowand.info.TemperatureSensorInfo;
import echowand.net.InternalSubnet;
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
public class LocalObjectCreatorTest {
    private ServiceManager serviceManager;
    private TemperatureSensorInfo info;
    private LocalObjectConfig config;
    private LocalObjectCreator creator;
    
    @Before
    public void setUp() {
        serviceManager = new ServiceManager(new InternalSubnet("LocalObjectCreatorTest"));
        info = new TemperatureSensorInfo();
        config = new LocalObjectConfig(info);
        creator = new LocalObjectCreator(config);
    }

    /**
     * Test of create method, of class LocalObjectCreator.
     */
    @Test
    public void testCreate() throws Exception {
        serviceManager.startService();
        
        LocalObjectCreatorResult result1 = creator.create(serviceManager);
        assertEquals(info.getClassEOJ().getEOJWithInstanceCode((byte)0x01), result1.object.getEOJ());
        
        LocalObjectCreatorResult result2 = creator.create(serviceManager);
        assertEquals(info.getClassEOJ().getEOJWithInstanceCode((byte)0x02), result2.object.getEOJ());
    }
    
}
