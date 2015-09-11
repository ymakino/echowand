package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.Node;
import echowand.net.SubnetException;
import echowand.object.EchonetObjectException;
import echowand.object.ObjectData;
import echowand.object.RemoteObject;
import echowand.service.Core;
import echowand.service.Service;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 *
 * @author ymakino
 */
public class SampleRemoteSet {
    public static void main(String[] args) throws InterruptedException, SubnetException, TooManyObjectsException, SocketException, EchonetObjectException {
        
        Core core = new Core(Inet4Subnet.startSubnet());
        
        // NetworkInterface nif = NetworkInterface.getByName("eth0");
        // Core core = new Core(Inet4Subnet.startSubnet(nif));
        
        core.startService();
        
        Service service = new Service(core);
        
        Node node = service.getRemoteNode("192.168.0.1");
        EOJ eoj = new EOJ("013001");
        service.registerRemoteEOJ(node, eoj);
        
        RemoteObject remoteObject = service.getRemoteObject(node, eoj);
        
        EPC epc = EPC.x80;
        ObjectData data = new ObjectData((byte)0x30);
        boolean result = remoteObject.setData(epc, data);
    
        System.out.println(result);
        System.exit(0);
    }
}
