package echowand.sample;

import echowand.common.EPC;
import echowand.info.HumanDetectionSensorInfo;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.SubnetException;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.LocalObjectConfig;
import echowand.service.PropertyUpdater;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SampleHumanDetectionPropertyUpdater {

    static class HumanDetectionPropertyUpdater extends PropertyUpdater {
        
        private String pinFile;
        private EPC epc = EPC.xB1;
        private ObjectData found = new ObjectData((byte)0x41);
        private ObjectData notFound = new ObjectData((byte)0x42);
        
        public HumanDetectionPropertyUpdater(String pinFile) {
            super(0, 100);
            this.pinFile = pinFile;
        }
        
        @Override
        public void loop(LocalObject localObject) {
            FileReader fr = null;
            try {
                fr = new FileReader(pinFile);
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();

                if (line.equals("1")) {
                    setData(epc, found);
                } else if (line.equals("0")) {
                    setData(epc, notFound);
                } else {
                    System.err.println("Invalid value: " + line);
                }
            } catch (FileNotFoundException ex) {
                    System.err.println("No such file: " + pinFile);
            } catch (IOException ex) {
                Logger.getLogger(SampleHumanDetectionLocalObject.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SampleHumanDetectionLocalObject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        @Override
        public void notifyCreation(LocalObject localObject, Core core) {
            localObject.forceSetData(epc, notFound);
        }
    }

    public static void main(String[] args) throws SubnetException, TooManyObjectsException {
        
        Core core = new Core(new Inet4Subnet());
        
        // NetworkInterface nif = NetworkInterface.getByName("eth0");
        // Core core = new Core(new Inet4Subnet(nif));
        
        HumanDetectionSensorInfo info = new HumanDetectionSensorInfo();
        LocalObjectConfig config = new LocalObjectConfig(info);
        config.addPropertyUpdater(new HumanDetectionPropertyUpdater("/sys/class/gpio/gpio21/value"));
        core.addLocalObjectConfig(config);
        
        core.startService();
    }
}
