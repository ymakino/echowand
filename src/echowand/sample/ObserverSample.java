package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.SubnetException;
import echowand.object.ObjectData;
import echowand.object.RemoteObject;
import echowand.object.RemoteObjectObserver;
import echowand.service.Core;
import echowand.service.Service;

/**
 *
 * @author ymakino
 */
public class ObserverSample {
    
    public static void main(String[] args) throws SubnetException, TooManyObjectsException, InterruptedException {
        // Construct a Core
        Core core = new Core();

        // Start the core service
        core.startService();

        // Construct a service
        Service service = new Service(core);

        service.registerRemoteEOJ(service.getRemoteNode("192.168.0.1"), new EOJ("000701"));
        RemoteObject remoteObject = service.getRemoteObject(service.getRemoteNode("192.168.0.1"), new EOJ("000701"));
        // 状変時アナウンス受信時の処理を指定
        remoteObject.addObserver(new RemoteObjectObserver() {
            public void notifyData(RemoteObject object, EPC epc, ObjectData data) {
                  System.out.println(object + " " + epc + " " + data); // 取得した状変時アナウンスのデータを表示
            }
        });
    }
}
