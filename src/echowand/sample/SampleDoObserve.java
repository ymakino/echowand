package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.Node;
import echowand.net.SubnetException;
import echowand.service.Core;
import echowand.service.Service;
import echowand.service.result.ObserveResult;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 *
 * @author ymakino
 */
public class SampleDoObserve {
    public static void main(String[] args) throws InterruptedException, SubnetException, TooManyObjectsException, SocketException {
        
        Core core = new Core(new Inet4Subnet());
        
        // NetworkInterface nif = NetworkInterface.getByName("eth0");
        // Core core = new Core(new Inet4Subnet(nif));
        
        core.startService();
        
        Service service = new Service(core);
        
        Node node = service.getRemoteNode("192.168.0.1");
        EOJ eoj = new EOJ("013001");
        EPC epc = EPC.x80;
        ObserveResult result = service.doObserve(node, eoj, epc);
        
        Thread.sleep(10000);
        
        for (int i=0; i<result.countData(); i++) {
            System.out.println("Data " + i + ": " + result.getData(i));
        }
        
        result.stopObserve();
        
        core.stopService();
    }
}
