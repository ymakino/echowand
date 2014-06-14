package echowand.sample;

import echowand.common.EPC;
import echowand.info.NodeProfileInfo;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.MainLoop;
import echowand.logic.RequestDispatcher;
import echowand.logic.TooManyObjectsException;
import echowand.logic.TransactionManager;
import echowand.net.Inet4Subnet;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import echowand.object.AnnounceRequestProcessor;
import echowand.object.LocalObject;
import echowand.object.LocalObjectDateTimeDelegate;
import echowand.object.LocalObjectDefaultDelegate;
import echowand.object.LocalObjectDelegate;
import echowand.object.LocalObjectManager;
import echowand.object.LocalObjectNotifyDelegate;
import echowand.object.NodeProfileObjectDelegate;
import echowand.object.ObjectData;
import echowand.object.RemoteObjectManager;
import echowand.object.SetGetRequestProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

class FileTemperatureSensorDelegate extends LocalObjectDefaultDelegate {

    private File file;
    private int scale;

    public FileTemperatureSensorDelegate(File file, int scale) {
        this.file = file;
        this.scale = scale;
    }

    @Override
    public void getData(LocalObjectDelegate.GetState result, LocalObject object, EPC epc) {
        if (epc == EPC.xE0) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                int value = (int) (Double.parseDouble(line) * 10 / scale);
                byte b1 = (byte) ((value >> 8) & 0xff);
                byte b2 = (byte) (value & 0xff);
                result.setGetData(new ObjectData(b1, b2));
            } catch (Exception ex) {
                ex.printStackTrace();
                object.setInternalData(EPC.x88, new ObjectData((byte)0x41));
                result.setFail();
            }
        }
    }
}

/**
 * An ECHONET Lite Device supporting thermal zone sensors.
 * @author ymakino
 */
public class ThermalZoneDevice implements Runnable {
    public static final String SENSOR_FILENAME_TEMPLATE = "/sys/class/thermal/thermal_zone%d/temp";
    
    private Subnet subnet;
    private TransactionManager transactionManager;
    private RemoteObjectManager remoteManager;
    private LocalObjectManager localManager;
    private RequestDispatcher dispatcher;
    
    public ThermalZoneDevice(Subnet subnet) {
        this.subnet = subnet;
    }
    
    public static File getSensorFileAt(int index) {
        return new File(String.format(SENSOR_FILENAME_TEMPLATE, index));
    }
    
    public static int countSensors() {
        int index;
        
        for (index = 0; index < 128; index++) {
            File file = getSensorFileAt(index);
            if (!file.exists()) {
                break;
            }
        }
        
        return index;
    }

    public NodeProfileInfo getNodeProfileInfo() {
        NodeProfileInfo info = new NodeProfileInfo();
        info.add(EPC.x97, true, false, false, 2);
        info.add(EPC.x98, true, false, false, 4);
        return info;
    }

    private void initialize() throws SubnetException, SocketException {
        transactionManager = new TransactionManager(subnet);
        remoteManager = new RemoteObjectManager();
        localManager = new LocalObjectManager();

        dispatcher = new RequestDispatcher();
        dispatcher.addRequestProcessor(new SetGetRequestProcessor(localManager));
        dispatcher.addRequestProcessor(new AnnounceRequestProcessor(localManager, remoteManager));
    }

    private void createNodeProfileObject() throws TooManyObjectsException {
        NodeProfileInfo nodeProfileInfo = getNodeProfileInfo();
        LocalObject nodeProfileObject = new LocalObject(nodeProfileInfo);
        nodeProfileObject.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
        nodeProfileObject.addDelegate(new NodeProfileObjectDelegate(localManager));
        nodeProfileObject.addDelegate(new LocalObjectDateTimeDelegate());
        localManager.add(nodeProfileObject);
    }

    private void createThermalZoneSensors() throws TooManyObjectsException {
        int count = countSensors();
        for (int index = 0; index < count; index++) {
            TemperatureSensorInfo tempSensorInfo = new TemperatureSensorInfo();
            LocalObject sensor = new LocalObject(tempSensorInfo);
            sensor.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
            sensor.addDelegate(new FileTemperatureSensorDelegate(getSensorFileAt(index), 1000));
            localManager.add(sensor);
        }
    }
    
    private void startMainThread() {
        MainLoop mainLoop = new MainLoop();
        mainLoop.setSubnet(subnet);
        mainLoop.addListener(transactionManager);
        mainLoop.addListener(dispatcher);
        
        mainLoop.run();
    }
    
    @Override
    public void run() {
        try {
            initialize();
            
            createNodeProfileObject();
            createThermalZoneSensors();
            
            startMainThread();
        } catch (Exception ex) {
            Logger.getLogger(RemoteObjectGetSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) throws InterruptedException, SocketException, SubnetException {
        if (args.length > 1) {
            System.err.println("Usage: ThermalZoneDevice [ifname]");
            return;
        }

        Inet4Subnet subnet;
        
        if (args.length == 0) {
            subnet = new Inet4Subnet();
        } else {
            NetworkInterface nif = NetworkInterface.getByName(args[0]);
            subnet = new Inet4Subnet(nif);
        }
        
        subnet.startService();
        ThermalZoneDevice thermalZoneDevice = new ThermalZoneDevice(subnet);

        Thread mainThread = new Thread(thermalZoneDevice);
        mainThread.start();
        mainThread.join();
    }
}
