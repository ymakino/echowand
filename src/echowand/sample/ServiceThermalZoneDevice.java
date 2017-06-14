package echowand.sample;

import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.SubnetException;
import echowand.object.LocalObject;
import echowand.object.LocalObjectDateTimeDelegate;
import echowand.object.LocalObjectDefaultDelegate;
import echowand.object.LocalObjectDelegate;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.LocalObjectConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An ECHONET Lite Device supporting thermal zone sensors.
 * @author ymakino
 */
public class ServiceThermalZoneDevice {
    public static final String SENSOR_FILENAME_TEMPLATE = "/sys/class/thermal/thermal_zone%d/temp";
    
    public static class ThermalZoneDelegate extends LocalObjectDefaultDelegate {

        private File file;
        private int scale;

        public ThermalZoneDelegate(File file, int scale) {
            this.file = file;
            this.scale = scale;
        }

        @Override
        public void getData(LocalObjectDelegate.GetState result, LocalObject object, EPC epc) {
            if (epc == EPC.xE0) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line = br.readLine();
                    int value = (int) (Double.parseDouble(line) * 10 / scale);
                    System.out.println(file  + ": " + line + "(" + (value/10.0) + ")");
                    byte b1 = (byte) ((value >> 8) & 0xff);
                    byte b2 = (byte) (value & 0xff);
                    result.setGetData(new ObjectData(b1, b2));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    object.setInternalData(EPC.x88, new ObjectData((byte)0x41));
                    result.setFail();
                }
            }
        }
    }
    
    public static File getSensorFileAt(int index) {
        return new File(String.format(SENSOR_FILENAME_TEMPLATE, index));
    }
    
    public static int countSensors() {
        int index;
        
        for (index = 0; index < 128; index++) {
            File file = getSensorFileAt(index);
            if (!file.exists()) {
                break;
            }
        }
        
        return index;
    }
    
    public static LocalObjectConfig createThermalZoneConfig(int index) {
        TemperatureSensorInfo info = new TemperatureSensorInfo();
        info.add(EPC.x97, true, false, false, 2);
        info.add(EPC.x98, true, false, false, 4);
        
        LocalObjectConfig config = new LocalObjectConfig(info);
        config.addDelegate(new LocalObjectDateTimeDelegate());
        config.addDelegate(new ThermalZoneDelegate(getSensorFileAt(index), 1000));

        return config;
    }
    
    public static void main(String[] args) throws InterruptedException, SocketException, SubnetException, TooManyObjectsException {
        if (args.length > 1) {
            System.err.println("Usage: ServiceThermalZoneDevice [ifname]");
            return;
        }
        
        Core core;
        
        if (args.length == 0) {
            core = new Core(new Inet4Subnet());
        } else {
            NetworkInterface nif = NetworkInterface.getByName(args[0]);
            core = new Core(new Inet4Subnet(nif));
        }
        
        int count = countSensors();
        for (int index = 0; index < count; index++) {
            core.addLocalObjectConfig(createThermalZoneConfig(index));
        }
        
        core.startService();
    }
}
