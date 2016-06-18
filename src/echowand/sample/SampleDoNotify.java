package echowand.sample;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.HumanDetectionSensorInfo;
import echowand.net.Inet4Subnet;
import echowand.net.SubnetException;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.LocalObjectConfig;
import echowand.service.PropertyDelegate;
import echowand.service.Service;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SampleDoNotify {
    
    static private EOJ eoj = new EOJ("000701");
    static private EPC epc = EPC.xB1;
    static private Data found = new Data((byte)0x41);
    static private Data notFound = new Data((byte)0x42);
    static private String pinFile = "/sys/class/gpio/gpio21/value";
    
    static int getValue() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(pinFile);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();

        if (line.equals("1")) {
            return 1;
        } else if (line.equals("0")) {
            return 0;
        } else {
            System.err.println("getValue fails: " + line);
            return -1;
        }
    }

    static class SampleLocalObjectPropertyDelegate extends PropertyDelegate {

        public SampleLocalObjectPropertyDelegate() {
            super(EPC.xB1, true, false, false);
        }

        @Override
        public ObjectData getUserData(LocalObject object, EPC epc) {
            try {
                switch (getValue()) {
                    case 0: return new ObjectData(notFound);
                    case 1: return new ObjectData(found);
                    default: return null;
                }
            } catch (IOException ex) {
                Logger.getLogger(SampleDoNotify.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }
    
    public static void doNotifyLoop(Core core) throws FileNotFoundException, IOException, SubnetException, InterruptedException {
        Service service = new Service(core);
        
        int lastValue = -1;
        
        for (;;) {
            
            int value = getValue();
            
            if (lastValue != value) {
                switch (value) {
                    case 0:
                        service.doNotify(eoj, epc, notFound, 0);
                        break;
                    case 1:
                        service.doNotify(eoj, epc, found, 0);
                        break;
                    default:
                        // do nothing
                }
                
                lastValue = value;
            }
            
            Thread.sleep(100);
        }
    }

    public static void main(String[] args) throws Exception {
        
        Core core = new Core(Inet4Subnet.startSubnet());
        
        // NetworkInterface nif = NetworkInterface.getByName("eth0");
        // Core core = new Core(Inet4Subnet.startSubnet(nif));
        
        HumanDetectionSensorInfo info = new HumanDetectionSensorInfo();
        LocalObjectConfig config = new LocalObjectConfig(info);
        config.addPropertyDelegate(new SampleLocalObjectPropertyDelegate());
        core.addLocalObjectConfig(config);
        
        core.startService();
        
        doNotifyLoop(core);
    }
}
