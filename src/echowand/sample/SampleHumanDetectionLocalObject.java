package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.HumanDetectionSensorInfo;
import echowand.net.Inet4Subnet;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.LocalObjectConfig;
import echowand.service.Service;
import java.io.BufferedReader;
import java.io.FileReader;


public class SampleHumanDetectionLocalObject {
    
    static private EOJ eoj = new EOJ("000701");
    static private EPC epc = EPC.xB1;
    static private ObjectData found = new ObjectData((byte)0x41);
    static private ObjectData notFound = new ObjectData((byte)0x42);
    static private String pinFile = "/sys/class/gpio/gpio21/value";
    
    public static void main(String[] args) throws Exception {
        
        Core core = new Core(Inet4Subnet.startSubnet());
        
        // NetworkInterface nif = NetworkInterface.getByName("eth0");
        // Core core = new Core(Inet4Subnet.startSubnet(nif));
        
        HumanDetectionSensorInfo info = new HumanDetectionSensorInfo();
        LocalObjectConfig config = new LocalObjectConfig(info);
        core.addLocalObjectConfig(config);
        
        core.startService();
        
        Service service = new Service(core);
        LocalObject object = service.getLocalObject(new EOJ("000701"));
        
        while (true) {
            FileReader fr = new FileReader(pinFile);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            fr.close();
            
            if (line.equals("1")) {
                object.forceSetData(epc, found);
            } else if (line.equals("0")) {
                object.forceSetData(epc, notFound);
            } else {
                System.err.println("invalid value: " + line);
            }
            
            Thread.sleep(100);
        }
    }
}
