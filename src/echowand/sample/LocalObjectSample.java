package echowand.sample;

import echowand.common.EPC;
import echowand.info.NodeProfileInfo;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.MainLoop;
import echowand.logic.RequestDispatcher;
import echowand.logic.TransactionManager;
import echowand.net.Inet4Subnet;
import echowand.object.*;
import echowand.util.ConstraintSize;
import java.util.logging.Level;
import java.util.logging.Logger;



class LocalObjectSampleDelegate extends LocalObjectDefaultDelegate {
    private ObjectData data;

    LocalObjectSampleDelegate(ObjectData data) {
        this.data = data;
    }

    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
        if (epc == EPC.xF0) {
            result.setGetData(data);
        }
    }

    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData curData) {
        if (epc == EPC.xF0) {
            result.setCurrentData(data);
            result.setNewData(newData);
            data = newData;
        }
    }
}

/**
 *
 * @author Yoshiki Makino
 */
public class LocalObjectSample {
    public static void main(String[] args) {
        try {
            Inet4Subnet subnet = new Inet4Subnet();
            TransactionManager transactionManager = new TransactionManager(subnet);
            RemoteObjectManager remoteManager = new RemoteObjectManager();
            LocalObjectManager localManager = new LocalObjectManager();
            
            RequestDispatcher dispatcher = new RequestDispatcher();
            dispatcher.addRequestProcessor(new SetGetRequestProcessor(localManager));
            dispatcher.addRequestProcessor(new AnnounceRequestProcessor(localManager, remoteManager));
            
            LocalObject nodeProfileObject = new LocalObject(new NodeProfileInfo());
            nodeProfileObject.addDelegate(new NodeProfileObjectDelegate(localManager));
            localManager.add(nodeProfileObject);
            
            TemperatureSensorInfo info = new TemperatureSensorInfo();
            info.add(EPC.xF0, true, true, true, 1, new ConstraintSize(1, 10));
            LocalObject temperatureSensor = new LocalObject(info);
            temperatureSensor.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
            temperatureSensor.addDelegate(new LocalObjectSampleDelegate(new ObjectData((byte)0x00)));
            localManager.add(temperatureSensor);

            MainLoop mainLoop = new MainLoop();
            mainLoop.setSubnet(subnet);
            mainLoop.addListener(transactionManager);
            mainLoop.addListener(dispatcher);
            
            Thread mainThread = new Thread(mainLoop);
            mainThread.setDaemon(true);
            mainThread.start();
            
            for (;;) {
                Thread.sleep(1000);
                ObjectData data = temperatureSensor.getData(EPC.xE0);
                int newValue = Integer.parseInt(data.toString(), 16) + 1;
                byte upper = (byte)((0xff00 & newValue) >> 8);
                byte lower = (byte)(0xff & newValue);
                ObjectData newData = new ObjectData(upper, lower);
                temperatureSensor.forceSetData(EPC.xE0, newData);
            }

        } catch (Exception ex) {
            Logger.getLogger(RemoteObjectGetSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
