package echowand.sample;

import echowand.common.ClassEOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.SubnetException;
import echowand.service.Service;
import echowand.service.Core;
import echowand.service.result.ResultData;
import echowand.service.result.GetResult;
import echowand.util.LoggerConfig;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ServiceDoGetSample {
    
    public static void main(String[] args) throws SubnetException, TooManyObjectsException, InterruptedException {
        // LoggerConfig.changeLogLevelAll(ResultBase.class.getName());

        // Construct a Core
        Core core = new Core();

        // Start the core service
        core.startService();

        // Construct a service
        Service service = new Service(core);

        // Test doGet
        LinkedList<EPC> epcs = new LinkedList<EPC>();
        epcs.add(EPC.x80);
        epcs.add(EPC.xE0);
        GetResult getResult = service.doGet(service.getGroupNode(), new ClassEOJ("0011"), epcs, 1000);
        getResult.join();

        List<ResultData> dataList = getResult.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            System.out.println("Get " + i + ": " + dataList.get(i));
        }

        core.stopService();
    }
}
