package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.Node;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultData {
    private static final Logger LOGGER = Logger.getLogger(ResultData.class.getName());
    private static final String CLASS_NAME = ResultData.class.getName();
    
    public final Node node;
    public final ESV esv;
    public final EOJ eoj;
    public final EPC epc;
    public final Data data;
    public final long time;
    
    public ResultData(Node node, ESV esv, EOJ eoj, EPC epc, Data data, long time) {
        LOGGER.entering(CLASS_NAME, "ResultData", new Object[]{node, eoj, epc, data, time});
        
        this.node = node;
        this.esv = esv;
        this.eoj = eoj;
        this.epc = epc;
        this.data = data;
        this.time = time;
        
        LOGGER.exiting(CLASS_NAME, "ResultData");
    }
    
    public ResultData(Node node, ESV esv, EOJ eoj, EPC epc, Data data) {
        LOGGER.entering(CLASS_NAME, "ResultData", new Object[]{node, eoj, epc, data});
        
        this.node = node;
        this.esv = esv;
        this.eoj = eoj;
        this.epc = epc;
        this.data = data;
        this.time = System.currentTimeMillis();
        
        LOGGER.exiting(CLASS_NAME, "ResultData");
    }
    
    @Override
    public String toString() {
        return "ResultData{Node: " + node + ", ESV: " + esv + ", EOJ: " + eoj + ", EPC: " + epc + ", Data: " + data + ", Time: " + time + "}";
    }
}
