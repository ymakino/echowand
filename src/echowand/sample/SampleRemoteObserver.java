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
import echowand.object.RemoteObjectObserver;
import echowand.service.Core;
import echowand.service.Service;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 *
 * @author ymakino
 */
public class SampleRemoteObserver {
    public static void main(String[] args) throws InterruptedException, SubnetException, TooManyObjectsException, SocketException, EchonetObjectException {
        
        Core core = new Core(new Inet4Subnet());
        
        // NetworkInterface nif = NetworkInterface.getByName("eth0");
        // Core core = new Core(new Inet4Subnet(nif));
        
        core.startService();
        
        Service service = new Service(core);
        
        Node node = service.getRemoteNode("192.168.0.1");
        EOJ eoj = new EOJ("013001");
        service.registerRemoteEOJ(node, eoj);
        
        RemoteObject remoteObject = service.getRemoteObject(node, eoj);
        
        remoteObject.addObserver(new RemoteObjectObserver() {
            @Override
            public void notifyData(RemoteObject object, EPC epc, ObjectData data) {
                System.out.println(object.getNode() + " " + object.getEOJ() + " " + epc + " " + data);
            }
        });
    }
}
