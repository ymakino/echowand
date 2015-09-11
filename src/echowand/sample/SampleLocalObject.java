package echowand.sample;

import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.net.Inet4Subnet;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.LocalObjectConfig;
import echowand.service.PropertyDelegate;
import java.io.BufferedReader;
import java.io.FileReader;


public class SampleLocalObject {

    static class SampleLocalObjectPropertyDelegate extends PropertyDelegate {

        public SampleLocalObjectPropertyDelegate() {
            super(EPC.xE0, true, false, false);
        }

        @Override
        public ObjectData getUserData(LocalObject object, EPC epc) {
            try {
                FileReader fr = new FileReader("/sys/class/thermal/thermal_zone0/temp");
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();
                int value = (int) (Double.parseDouble(line) * 10 / 1000);
                byte b1 = (byte) ((value >> 8) & 0xff);
                byte b2 = (byte) (value & 0xff);
                return new ObjectData(b1, b2);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        
        Core core = new Core(Inet4Subnet.startSubnet());
        
        // NetworkInterface nif = NetworkInterface.getByName("eth0");
        // Core core = new Core(Inet4Subnet.startSubnet(nif));
        
        TemperatureSensorInfo info = new TemperatureSensorInfo();
        LocalObjectConfig config = new LocalObjectConfig(info);
        config.addPropertyDelegate(new SampleLocalObjectPropertyDelegate());
        core.addLocalObjectConfig(config);
        
        core.startService();
    }
}
