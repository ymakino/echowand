package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.AirSpeedSensorInfo;
import echowand.info.HumiditySensorInfo;
import echowand.info.IlluminanceSensorInfo;
import echowand.info.NodeProfileInfo;
import echowand.info.PropertyConstraintIlluminance;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.MainLoop;
import echowand.logic.RequestDispatcher;
import echowand.logic.TooManyObjectsException;
import echowand.logic.TransactionManager;
import echowand.net.Inet4Subnet;
import echowand.net.SubnetException;
import echowand.object.AnnounceRequestProcessor;
import echowand.object.LocalObject;
import echowand.object.LocalObjectDateTimeDelegate;
import echowand.object.LocalObjectDefaultDelegate;
import echowand.object.LocalObjectManager;
import echowand.object.LocalObjectNotifyDelegate;
import echowand.object.NodeProfileObjectDelegate;
import echowand.object.ObjectData;
import echowand.object.RemoteObjectManager;
import echowand.object.SetGetRequestProcessor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class SensorDataDelegate extends LocalObjectDefaultDelegate {
    private LocalObject object;
    private EPC epc;
    private String command;
    
    public SensorDataDelegate(LocalObject object, EPC epc, String command) {
        this.object = object;
        this.epc = epc;
        this.command = command;
    }
    
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
        if (this.epc == epc) {
            try {
                Process p = Runtime.getRuntime().exec(command);
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = br.readLine();
                    ObjectData data = stringToData(line);
                    result.setGetData(data);
                } finally {
                    p.destroy();
                }
            } catch (IOException ex) {
                Logger.getLogger(SensorDataDelegate.class.getName()).log(Level.SEVERE, null, ex);
                result.setFail();
            }
        }
    }
    
    public LocalObject getObject() {
        return object;
    }
    
    public EPC getEPC() {
        return epc;
    }
    
    public String getCommand() {
        return command;
    }

    protected abstract ObjectData stringToData(String stringData);
}

class TemperatureDataDelegate extends SensorDataDelegate {
    
    public TemperatureDataDelegate(LocalObject object, String command) {
        super(object, EPC.xE0, command);
    }

    @Override
    protected ObjectData stringToData(String stringData) {
        double temp = Double.parseDouble(stringData);
        EOJ eoj = getObject().getEOJ();
        String command = getCommand();
        System.out.println("Temp(" + eoj + "): " + temp + " \"" + command + "\"");
        long temp10 = Math.round(temp * 10);
        return new ObjectData((byte) ((temp10 & 0xff00) >> 8), (byte)(temp10 & 0xff));
    }
}

class HumidityDataDelegate extends SensorDataDelegate {
    
    public HumidityDataDelegate(LocalObject object, String command) {
        super(object, EPC.xE0, command);
    }

    @Override
    protected ObjectData stringToData(String stringData) {
        double humid = Double.parseDouble(stringData);
        EOJ eoj = getObject().getEOJ();
        String command = getCommand();
        System.out.println("Humid(" + eoj + "): " + humid + " \"" + command + "\"");
        return new ObjectData((byte)Math.round(humid));
    }
}

class Illuminance1DataDelegate extends SensorDataDelegate {
    
    public Illuminance1DataDelegate(LocalObject object, String command) {
        super(object, EPC.xE0, command);
    }

    @Override
    protected ObjectData stringToData(String stringData) {
        long lux = Long.parseLong(stringData);
        EOJ eoj = getObject().getEOJ();
        String command = getCommand();
        System.out.println("Lux1(" + eoj + "): " + lux + " \"" + command + "\"");
        return new ObjectData((byte) ((lux & 0xff00) >> 8), (byte)(lux & 0xff));
    }
}

class Illuminance2DataDelegate extends SensorDataDelegate {
    
    public Illuminance2DataDelegate(LocalObject object, String command) {
        super(object, EPC.xE1, command);
    }

    @Override
    protected ObjectData stringToData(String stringData) {
        long lux = Long.parseLong(stringData) / 1000;
        EOJ eoj = getObject().getEOJ();
        String command = getCommand();
        System.out.println("Lux2(" + eoj + "): " + lux + " \"" + command + "\"");
        return new ObjectData((byte) ((lux & 0xff00) >> 8), (byte)(lux & 0xff));
    }
}

class AirSpeedDataDelegate extends SensorDataDelegate {
    
    public AirSpeedDataDelegate(LocalObject object, String command) {
        super(object, EPC.xE0, command);
    }

    @Override
    protected ObjectData stringToData(String stringData) {
        double speed = Double.parseDouble(stringData);
        EOJ eoj = getObject().getEOJ();
        String command = getCommand();
        System.out.println("AirSpeed(" + eoj + "): " + speed + " \"" + command + "\"");
        long temp10 = Math.round(speed * 100);
        return new ObjectData((byte) ((temp10 & 0xff00) >> 8), (byte)(temp10 & 0xff));
    }
}

/**
 *
 * @author ymakino
 */
public class ExternalSensors implements Runnable {

    private List<String> temperatureCommands = new LinkedList<String>();
    private List<String> humidityCommands = new LinkedList<String>();
    private List<String> illuminance1Commands = new LinkedList<String>();
    private List<String> illuminance2Commands = new LinkedList<String>();
    private List<String> airSpeedCommands = new LinkedList<String>();
    
    private Inet4Subnet subnet;
    private TransactionManager transactionManager;
    private RemoteObjectManager remoteManager;
    private LocalObjectManager localManager;
    private RequestDispatcher dispatcher;
    
    private List<LocalObject> temperatureSensors = new LinkedList<LocalObject>();
    private List<LocalObject> humiditySensors = new LinkedList<LocalObject>();
    private List<LocalObject> illuminance1Sensors = new LinkedList<LocalObject>();
    private List<LocalObject> illuminance2Sensors = new LinkedList<LocalObject>();
    private List<LocalObject> airSpeedSensors = new LinkedList<LocalObject>();

    public ExternalSensors() {
    }
    
    public void addTemperatureCommand(String temperatureCommand) {
        temperatureCommands.add(temperatureCommand);
    }
    
    public void addHumidityCommand(String humidityCommand) {
        humidityCommands.add(humidityCommand);
    }
    
    public void addIlluminance1Command(String illuminance1Command) {
        illuminance1Commands.add(illuminance1Command);
    }
    
    public void addIlluminance2Command(String illuminance2Command) {
        illuminance2Commands.add(illuminance2Command);
    }
    
    public void addAirSpeedCommand(String airSpeedCommand) {
        airSpeedCommands.add(airSpeedCommand);
    }

    public NodeProfileInfo getNodeProfileInfo() {
        NodeProfileInfo info = new NodeProfileInfo();
        info.add(EPC.x97, true, false, false, 2);
        info.add(EPC.x98, true, false, false, 4);
        return info;
    }

    public TemperatureSensorInfo getTemperatureSensorInfo() {
        TemperatureSensorInfo info = new TemperatureSensorInfo();
        return info;
    }

    public HumiditySensorInfo getHumiditySensorInfo() {
        HumiditySensorInfo info = new HumiditySensorInfo();
        return info;
    }

    public IlluminanceSensorInfo getIlluminance1SensorInfo() {
        IlluminanceSensorInfo info = new IlluminanceSensorInfo();
        info.add(EPC.xE0, true, false, false, 2, new PropertyConstraintIlluminance());
        return info;
    }

    public IlluminanceSensorInfo getIlluminance2SensorInfo() {
        IlluminanceSensorInfo info = new IlluminanceSensorInfo();
        info.add(EPC.xE1, true, false, false, 2, new PropertyConstraintIlluminance());
        return info;
    }

    public AirSpeedSensorInfo getAirSpeedSensorInfo() {
        AirSpeedSensorInfo info = new AirSpeedSensorInfo();
        return info;
    }

    private void initialize() throws SubnetException {
        subnet = new Inet4Subnet();
        subnet.startService();
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

    private void createTemperatureSensors() throws TooManyObjectsException {
        TemperatureSensorInfo tempSensorInfo = getTemperatureSensorInfo();
        for (int i=0; i<temperatureCommands.size(); i++) {
            LocalObject sensor = new LocalObject(tempSensorInfo);
            sensor.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
            sensor.addDelegate(new TemperatureDataDelegate(sensor, temperatureCommands.get(i)));
            localManager.add(sensor);
            temperatureSensors.add(sensor);
        }
    }

    private void createHumiditySensors() throws TooManyObjectsException {
        HumiditySensorInfo humidSensorInfo = getHumiditySensorInfo();
        for (int i = 0; i < humidityCommands.size(); i++) {
            LocalObject sensor = new LocalObject(humidSensorInfo);
            sensor.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
            sensor.addDelegate(new HumidityDataDelegate(sensor, humidityCommands.get(i)));
            localManager.add(sensor);
            humiditySensors.add(sensor);
        }
    }

    private void createIlluminance1Sensors() throws TooManyObjectsException {
        IlluminanceSensorInfo illumSensorInfo = getIlluminance1SensorInfo();
        for (int i = 0; i < illuminance1Commands.size(); i++) {
            LocalObject sensor = new LocalObject(illumSensorInfo);
            sensor.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
            sensor.addDelegate(new Illuminance1DataDelegate(sensor, illuminance1Commands.get(i)));
            localManager.add(sensor);
            illuminance1Sensors.add(sensor);
        }
    }

    private void createIlluminance2Sensors() throws TooManyObjectsException {
        IlluminanceSensorInfo illumSensorInfo = getIlluminance2SensorInfo();
        for (int i = 0; i < illuminance2Commands.size(); i++) {
            LocalObject sensor = new LocalObject(illumSensorInfo);
            sensor.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
            sensor.addDelegate(new Illuminance2DataDelegate(sensor, illuminance2Commands.get(i)));
            localManager.add(sensor);
            illuminance2Sensors.add(sensor);
        }
    }

    private void createAirSpeedSensors() throws TooManyObjectsException {
        AirSpeedSensorInfo airSpeedSensorInfo = getAirSpeedSensorInfo();
        for (int i = 0; i < airSpeedCommands.size(); i++) {
            LocalObject sensor = new LocalObject(airSpeedSensorInfo);
            sensor.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
            sensor.addDelegate(new AirSpeedDataDelegate(sensor, airSpeedCommands.get(i)));
            localManager.add(sensor);
            airSpeedSensors.add(sensor);
        }
    }
    
    private void startMainThread() {
        MainLoop mainLoop = new MainLoop();
        mainLoop.setSubnet(subnet);
        mainLoop.addListener(transactionManager);
        mainLoop.addListener(dispatcher);

        Thread mainThread = new Thread(mainLoop);
        // mainThread.setDaemon(true);
        mainThread.start();
    }

    @Override
    public void run() {
        try {
            initialize();
            
            createNodeProfileObject();
            createTemperatureSensors();
            createHumiditySensors();
            createIlluminance1Sensors();
            createIlluminance2Sensors();
            createAirSpeedSensors();
            
            startMainThread();
        } catch (Exception ex) {
            Logger.getLogger(RemoteObjectGetSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String usage() {
        return "Usage: ExternalSensors [ -t TemperatureCommand | -h HumidityCommand | -i1 IlluminanceCommand | -i2 IlluminanceCommand | -a airSpeedCommand ]";
    }
    
    public static void main(String[] args) throws InterruptedException {
        if (args.length > 0 && args[0].equals("-h")) {
            System.out.println(usage());
            System.exit(0);
        }
        
        if (args.length == 0 || args.length % 2 == 1) {
            System.err.println(usage());
            System.exit(-1);
        }
        
        ExternalSensors sensors = new ExternalSensors();
        for (int i=0; i<args.length; i+=2) {
            if (args[i].equals("-t")) {
                sensors.addTemperatureCommand(args[i+1]);;
            } else if (args[i].equals("-h")) {
                sensors.addHumidityCommand(args[i+1]);;
            } else if (args[i].equals("-i1")) {
                sensors.addIlluminance1Command(args[i+1]);;
            } else if (args[i].equals("-i2")) {
                sensors.addIlluminance2Command(args[i+1]);;
            } else if (args[i].equals("-a")) {
                sensors.addAirSpeedCommand(args[i+1]);;
            } else {
                System.err.println(usage());
                System.exit(-1);
            }
        }
        
        Thread mainThread = new Thread(sensors);
        mainThread.start();
        // mainThread.join();
    }
}
