package echowand.sample;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.SubnetException;
import echowand.service.Core;
import echowand.service.Service;
import echowand.service.result.NotifyResult;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 *
 * @author ymakino
 */
public class SampleDoNotify {
    public static void main(String[] args) throws InterruptedException, SubnetException, TooManyObjectsException, SocketException {
        
        Core core = new Core(new Inet4Subnet());
        
        // NetworkInterface nif = NetworkInterface.getByName("eth0");
        // Core core = new Core(new Inet4Subnet(nif));
        
        core.startService();
        
        Service service = new Service(core);
        
        EOJ eoj = new EOJ("0ef001");
        EPC epc = EPC.x80;
        Data data = new Data((byte)0x30);
        NotifyResult result = service.doNotify(eoj, epc, data, 1000, true);
        
        result.join();
        
        for (int i=0; i<result.countData(); i++) {
            System.out.println("Data " + i + ": " + result.getData(i));
        }
        
        core.stopService();
    }
}
