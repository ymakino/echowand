package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.TooManyObjectsException;
import echowand.net.CommonFrame;
import echowand.net.InternalSubnet;
import echowand.net.StandardPayload;
import echowand.net.SubnetException;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.LocalObjectConfig;
import echowand.service.PropertyDelegate;
import echowand.service.Service;
import echowand.service.result.GetResult;

/**
 *
 * @author ymakino
 */
public class SampleLazyConfiguration {
    
    public static class SampleLocalObjectConfig extends LocalObjectConfig {
        public SampleLocalObjectConfig() {
            super(new TemperatureSensorInfo());
        }
    }
    
    public static class SamplePropertyDelegate extends PropertyDelegate {
        public SamplePropertyDelegate() {
            super(EPC.xF0, true, false, false);
        }
    }
    
    public static class SampleConfiguration implements LocalObjectConfig.LazyConfiguration {
        @Override
        public void configure(final LocalObjectConfig config, final Core core) {
            config.addPropertyDelegate(new SamplePropertyDelegate() {
                public ObjectData getUserData(LocalObject object, EPC epc) {
                    return new ObjectData((byte) core.getLocalObjectManager().size());
                }
            });
        }
    }
    
    public static void main(String[] args) throws TooManyObjectsException, SubnetException, InterruptedException {
        Core core = new Core(new InternalSubnet());
        
        for (int i=0; i<10; i++) {
            core.addLocalObjectConfig(new SampleLocalObjectConfig());
        }
        
        core.getNodeProfileObjectConfig().addProperty(EPC.xF0, true, false, false, 1);
        core.getNodeProfileObjectConfig().addLazyConfiguration(new SampleConfiguration());
        
        core.startService();
        
        Service service = new Service(core);
        
        GetResult getResult = service.doGet(service.getLocalNode(), new EOJ("0ef001"), EPC.xF0, 500);
        
        getResult.join();
        
        System.out.println(getResult.countFrames());
        System.out.println(getResult.getFrame(0));
        
        CommonFrame commonFrame = getResult.getFrame(0).frame.getCommonFrame();
        StandardPayload standardPayload = commonFrame.getEDATA(StandardPayload.class);
        System.out.println(standardPayload.getFirstPropertyAt(0));
        
        System.exit(0);
    }
}
