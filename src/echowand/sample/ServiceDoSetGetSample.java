package echowand.sample;

import echowand.common.ClassEOJ;
import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.Property;
import echowand.net.SubnetException;
import echowand.service.Service;
import echowand.service.Core;
import echowand.service.result.CaptureResult;
import echowand.service.result.ResultData;
import echowand.service.result.ResultFrame;
import echowand.service.result.SetGetResult;
import echowand.util.LoggerConfig;
import echowand.util.Pair;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ServiceDoSetGetSample {
    
    public static void main(String[] args) throws SubnetException, TooManyObjectsException, InterruptedException {
        // LoggerConfig.changeLogLevelAll(ResultBase.class.getName());

        // Construct a Core
        Core core = new Core();

        // Start the core service
        core.startService();

        // Construct a service
        Service service = new Service(core);
        
        // Capture frames for debug
        CaptureResult captureResult = service.doCapture();
        
        // Test doSetGet
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(EPC.x80, new Data((byte)0x30)));
        properties.add(new Pair<EPC, Data>(EPC.xA0, new Data((byte)0x33)));

        LinkedList<EPC> epcs = new LinkedList<EPC>();
        epcs.add(EPC.x80);
        epcs.add(EPC.xA0);
        
        SetGetResult setGetResult = service.doSetGet(service.getGroupNode(), new ClassEOJ("0132"), properties, epcs, 5000);
        setGetResult.join();

        List<ResultData> dataList = setGetResult.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            System.out.println("Set " + i + ": " + dataList.get(i));
        }
        
        List<ResultData> secondDataList = setGetResult.getSecondDataList();
        for (int i = 0; i < secondDataList.size(); i++) {
            System.out.println("Get " + i + ": " + secondDataList.get(i));
        }
        
        captureResult.stopCapture();
        for (ResultFrame frame : captureResult.getFrameList()) {
            System.out.println(frame);
        }

        core.stopService();
    }
}
