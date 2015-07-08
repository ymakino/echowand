package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.Service;
import echowand.service.LocalObjectConfig;
import echowand.service.PropertyDelegate;
import echowand.service.result.GetResult;
import echowand.service.result.ResultData;
import java.io.BufferedReader;
import java.io.FileReader;

class ThermalZoneSampleDelegate extends PropertyDelegate {

    public ThermalZoneSampleDelegate() {
        super(EPC.xE0, true, false, false);
    }

    @Override
    public ObjectData getUserData(LocalObject object, EPC epc) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/sys/class/thermal/thermal_zone0/temp"));
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


public class ThermalZoneSample {
    public static void main(String[] args) throws Exception {
        Core core = new Core();

        TemperatureSensorInfo info = new TemperatureSensorInfo();
        LocalObjectConfig config = new LocalObjectConfig(info);
        config.addPropertyDelegate(new ThermalZoneSampleDelegate());
        core.addLocalObjectConfig(config);

        core.startService();
        
        for (int i=0; i<100; i++) {
            Service service = new Service(core);
            GetResult result = service.doGet(service.getGroupNode(), new EOJ("001101"), EPC.xE0, 1000);
            result.join();
            for (ResultData resultData : result.getDataList()) {
                System.out.println(i + ": " + resultData);
            }
        }
    }
}
