package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.MainLoop;
import echowand.logic.TransactionManager;
import echowand.net.InetSubnet;
import echowand.net.Node;
import echowand.object.EchonetObjectException;
import echowand.object.ObjectData;
import echowand.object.RemoteObject;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Yoshiki Makino
 */
public class Sample3 {
    public static String peerAddress = "192.168.1.1";
    
    public static void main(String[] args) {
        InetSubnet subnet = new InetSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        
        MainLoop loop = new MainLoop();
        loop.setSubnet(subnet);
        loop.addListener(transactionManager);
        
        Thread mainLoop = new Thread(loop);
        mainLoop.setDaemon(true);
        mainLoop.start();
        
        Node node = null;
        
        try {
            node = subnet.getRemoteNode(InetAddress.getByName(peerAddress), 3610);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        RemoteObject object = new RemoteObject(subnet, node, new EOJ("013001"), transactionManager);
        
        try {
            ObjectData data;
            data = object.getData(EPC.x9F);
            System.out.println(data);
            data = object.getData(EPC.x9E);
            System.out.println(data);
            data = object.getData(EPC.x9D);
            System.out.println(data);
            object.setData(EPC.xB0, new ObjectData((byte)0x11));
        } catch (EchonetObjectException e) {
            e.printStackTrace();
        }
    }
}
