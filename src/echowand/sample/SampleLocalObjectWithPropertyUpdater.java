package echowand.sample;

import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.net.Inet4Subnet;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.LocalObjectConfig;
import echowand.service.PropertyDelegate;
import echowand.service.PropertyUpdater;
import java.io.BufferedReader;
import java.io.FileReader;


public class SampleLocalObjectWithPropertyUpdater {

    static class SampleLocalObjectPropertyUpdater extends PropertyUpdater {

        public SampleLocalObjectPropertyUpdater() {
            super(1000,10000);
        }

        @Override
        public void loop(LocalObject localObject) {
            try {
                FileReader fr = new FileReader("/sys/class/thermal/thermal_zone0/temp");
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();
                int value = (int) (Double.parseDouble(line) * 10 / 1000);
                byte b1 = (byte) ((value >> 8) & 0xff);
                byte b2 = (byte) (value & 0xff);
                localObject.forceSetData(EPC.xE0, new ObjectData(b1, b2));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        
        Core core = new Core(new Inet4Subnet());
        
        // NetworkInterface nif = NetworkInterface.getByName("eth0");
        // Core core = new Core(new Inet4Subnet(nif));
        
        TemperatureSensorInfo info = new TemperatureSensorInfo();
        LocalObjectConfig config = new LocalObjectConfig(info);
        config.addPropertyUpdater(new SampleLocalObjectPropertyUpdater());
        core.addLocalObjectConfig(config);
        
        core.startService();
    }
}
