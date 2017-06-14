package echowand.sample;

import echowand.common.EPC;
import echowand.info.GeneralLightingInfo;
import echowand.net.Inet4Subnet;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.LocalObjectConfig;
import echowand.service.PropertyDelegate;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SampleLEDLocalObject {

    static private class LED {

        static public boolean write(String filename, String str) throws Exception {
            String cmd = String.format("echo \"%s\" > %s", str, filename);
            Process p = Runtime.getRuntime().exec(new String[]{"sudo", "/bin/sh", "-c", cmd});
            return p.waitFor() == 0;
        }
        
        static public String read(String filename) throws Exception {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            try {
                return br.readLine();
            } finally {
                br.close();
            }
        }

        static public boolean setModeNONE() throws Exception {
            return write("/sys/class/leds/led0/trigger", "none");
        }

        static public boolean setModeMMC0() throws Exception {
            return write("/sys/class/leds/led0/trigger", "mmc0");
        }

        static public boolean on() throws Exception {
            return write("/sys/class/leds/led0/brightness", "1");
        }

        static public boolean off() throws Exception {
            return write("/sys/class/leds/led0/brightness", "0");
        }

        static public boolean isOn() throws Exception {
            return !read("/sys/class/leds/led0/brightness").startsWith("0");
        }

        static public boolean isOff() throws Exception {
            return read("/sys/class/leds/led0/brightness").startsWith("0");
        }
    }

    static class SampleLEDLocalObjectPropertyDelegate extends PropertyDelegate {

        public SampleLEDLocalObjectPropertyDelegate() throws IOException {
            super(EPC.x80, true, true, true);
        }

        @Override
        public ObjectData getUserData(LocalObject object, EPC epc) {
            try {
                if (LED.isOn()) {
                    System.out.println("Get: On");
                    return new ObjectData((byte)0x30);
                } else {
                    System.out.println("Get: Off");
                    return new ObjectData((byte)0x31);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        
        @Override
        public boolean setUserData(LocalObject object, EPC epc, ObjectData data) {
            try {
                if (data.equals(new ObjectData((byte) 0x30))) {
                    System.out.println("Set: On");
                    LED.on();
                } else {
                    System.out.println("Set: Off");
                    LED.off();
                }
                
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
    
    public static void initializeLED() throws Exception {
        boolean result = LED.setModeNONE();
        System.out.println("Change the LED mode: " + result);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    boolean result = LED.setModeMMC0();
                    System.out.println("Reset the LED mode: " + result);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Initializing");
        
        initializeLED();
        
        Core core = new Core(new Inet4Subnet());
        
        // NetworkInterface nif = NetworkInterface.getByName("eth0");
        // Core core = new Core(new Inet4Subnet(nif));
        
        GeneralLightingInfo info = new GeneralLightingInfo();
        LocalObjectConfig config = new LocalObjectConfig(info);
        config.addPropertyDelegate(new SampleLEDLocalObjectPropertyDelegate());
        core.addLocalObjectConfig(config);
        
        core.startService();
        System.out.println("Initialized");
    }
}
