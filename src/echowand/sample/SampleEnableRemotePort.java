package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.InetNodeInfo;
import echowand.net.SubnetException;
import echowand.service.Core;
import echowand.service.Service;
import echowand.service.result.GetResult;
import echowand.service.result.ResultFrame;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author ymakino
 */
public class SampleEnableRemotePort {
    
    public static Service createService(int portNumber) throws SubnetException, TooManyObjectsException {
        Inet4Subnet subnet = new Inet4Subnet();
        subnet.setPortNumber(3610);
        subnet.enableRemotePortNumber();
        subnet.startService();
        Core core = new Core(subnet);
        core.startService();
        return new Service(core);
    }
    
    public static void main(String[] args) throws SubnetException, UnknownHostException, TooManyObjectsException, InterruptedException {
        Service service1 = createService(3610);
        Service service2 = createService(3611);
        
        InetNodeInfo info1 = new InetNodeInfo(InetAddress.getByName("127.0.0.1"), 3611);
        GetResult getResult1 = service1.doGet(info1, new EOJ("0ef001"), EPC.x9D, 1000);
        getResult1.join();
        
        for (ResultFrame frame : getResult1.getFrameList()) {
            System.out.println(frame);
        }
        
        InetNodeInfo info2 = new InetNodeInfo(InetAddress.getByName("127.0.0.1"), 3610);
        GetResult getResult2 = service2.doGet(info2, new EOJ("0ef001"), EPC.x9E, 1000);
        getResult2.join();
        
        for (ResultFrame frame : getResult2.getFrameList()) {
            System.out.println(frame);
        }
        
        System.exit(0);
    }
}
