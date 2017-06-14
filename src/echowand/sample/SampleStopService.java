package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.MainLoop;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.InetSubnet;
import echowand.net.InetSubnetTCPAcceptorThread;
import echowand.net.InetSubnetTCPReceiverThread;
import echowand.net.InetSubnetUDPReceiverThread;
import echowand.net.SubnetException;
import echowand.net.UDPNetwork;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.LocalObjectConfig;
import echowand.service.PropertyUpdater;
import echowand.service.Service;
import echowand.service.result.GetListener;
import echowand.service.result.GetResult;
import echowand.service.result.ResultFrame;
import echowand.util.LoggerConfig;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;

/**
 *
 * @author ymakino
 */
public class SampleStopService {
    public static void main(String[] args) throws SocketException, SubnetException, TooManyObjectsException, InterruptedException {
        LoggerConfig.changeLogLevel(Level.ALL, Core.class.getName());
        LoggerConfig.changeLogLevel(Level.ALL, Inet4Subnet.class.getName());
        LoggerConfig.changeLogLevel(Level.ALL, InetSubnet.class.getName());
        LoggerConfig.changeLogLevel(Level.ALL, MainLoop.class.getName());
        LoggerConfig.changeLogLevel(Level.ALL, UDPNetwork.class.getName());
        LoggerConfig.changeLogLevel(Level.ALL, InetSubnetUDPReceiverThread.class.getName());
        LoggerConfig.changeLogLevel(Level.ALL, InetSubnetTCPReceiverThread.class.getName());
        LoggerConfig.changeLogLevel(Level.ALL, InetSubnetTCPAcceptorThread.class.getName());
        
        for (int i=0; i<10; i++) {
            
            Core core = new Core(new Inet4Subnet());

            LocalObjectConfig config = new LocalObjectConfig(new TemperatureSensorInfo());
            config.addPropertyUpdater(new PropertyUpdater(100, 100) {
                Random rand = new Random();
                @Override
                public void loop(LocalObject localObject) {
                    int value = rand.nextInt();
                    ObjectData data = new ObjectData((byte)((0xff00 & value) >> 8), (byte)(0x00ff & value));
                    System.out.println(data);
                    localObject.forceSetData(EPC.xE0, data);
                }
            });

            core.addLocalObjectConfig(config);

            core.startService();

            Thread.sleep(1000);

            Service service = new Service(core);
            service.doGet(service.getRemoteNode("127.0.0.1"), new EOJ("001101"), EPC.xE0, 1000, new GetListener() {
                @Override
                public void receive(GetResult result, ResultFrame resultFrame) {
                    System.out.println(resultFrame);
                }
            }).join();

            core.stopService();

            //Thread.sleep(1000);
        }
    }
}
