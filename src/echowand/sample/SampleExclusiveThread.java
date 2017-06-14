package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.SubnetException;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.Service;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class SampleExclusiveThread {
    public static void main(String[] args) throws SubnetException, SocketException, TooManyObjectsException, InterruptedException {
        Core core = new Core(new Inet4Subnet());
        core.startService();
        
        final Service service = new Service(core);
        
        service.runExclusive(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<=0xff; i++) {
                    System.out.printf("0x%02x\n", i);
                    service.getLocalObject(new EOJ("0ef001")).forceSetData(EPC.x80, new ObjectData((byte)i));
                }
            }
        });
        
        Thread.sleep(1000);
        
        System.out.println(service.getLocalObject(new EOJ("0ef001")).getData(EPC.x80));
        
        core.stopService();
    }
}
