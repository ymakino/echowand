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
    private Core core;
    private TemperatureSensorInfo info;
    private LocalObjectConfig config;
    private LocalObjectCreator creator;
    private DummyLazyConfiguration configuration;
    
    public class DummyLazyConfiguration implements LocalObjectConfig.LazyConfiguration {
        public int count = 0;
        public LocalObjectConfig config;
        public Core core;
        
        @Override
        public void configure(LocalObjectConfig config, Core core) {
            count++;
            this.config = config;
            this.core = core;
        }
    }
    
    @Before
    public void setUp() {
        core = new Core(new InternalSubnet("LocalObjectCreatorTest"));
        info = new TemperatureSensorInfo();
        config = new LocalObjectConfig(info);
        creator = new LocalObjectCreator(config);
        configuration = new DummyLazyConfiguration();
        config.addLazyConfiguration(configuration);
    }

    /**
     * Test of create method, of class LocalObjectCreator.
     */
    @Test
    public void testCreate() throws Exception {
        core.startService();
        
        LocalObjectCreatorResult result1 = creator.create(core);
        assertEquals(info.getClassEOJ().getEOJWithInstanceCode((byte)0x01), result1.object.getEOJ());
        assertEquals(config, configuration.config);
        assertEquals(core, configuration.core);
        assertEquals(1, configuration.count);
        
        LocalObjectCreatorResult result2 = creator.create(core);
        assertEquals(info.getClassEOJ().getEOJWithInstanceCode((byte)0x02), result2.object.getEOJ());
        assertEquals(config, configuration.config);
        assertEquals(core, configuration.core);
        assertEquals(2, configuration.count);
        
        Core core2 = new Core(new InternalSubnet("LocalObjectCreatorTest2"));
        core2.startService();
        LocalObjectCreatorResult result3 = creator.create(core2);
        assertEquals(info.getClassEOJ().getEOJWithInstanceCode((byte)0x01), result3.object.getEOJ());
        assertEquals(config, configuration.config);
        assertEquals(core2, configuration.core);
        assertEquals(3, configuration.count);
    }
    
}
