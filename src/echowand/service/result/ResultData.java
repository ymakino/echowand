package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
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
    public final EOJ eoj;
    public final EPC epc;
    public final Data data;
    public final long time;
    
    public ResultData(Node node, EOJ eoj, EPC epc, Data data, long time) {
        LOGGER.entering(CLASS_NAME, "ResultData", new Object[]{node, eoj, epc, data});
        
        this.node = node;
        this.eoj = eoj;
        this.epc = epc;
        this.data = data;
        this.time = time;
        
        LOGGER.exiting(CLASS_NAME, "ResultData");
    }
    
    public ResultData(Node node, EOJ eoj, EPC epc, Data data) {
        LOGGER.entering(CLASS_NAME, "ResultData", new Object[]{node, eoj, epc, data});
        
        this.node = node;
        this.eoj = eoj;
        this.epc = epc;
        this.data = data;
        this.time = System.currentTimeMillis();
        
        LOGGER.exiting(CLASS_NAME, "ResultData");
    }
    
    @Override
    public String toString() {
        return "ResultData{Node: " + node + ", EOJ: " + eoj + ", EPC: " + epc + ", Data: " + data + ", Time: " + time + "}";
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResultData)) {
            return false;
        }
        
        ResultData other = (ResultData)o;
        
        if (other.epc != epc) {
            return false;
        }
        
        if (other.time != time) {
            return false;
        }
        
        if (!other.eoj.equals(eoj)) {
            return false;
        }
        
        if (!other.node.equals(node)) {
            return false;
        }
        
        return other.data.equals(data);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.eoj != null ? this.eoj.hashCode() : 0);
        hash = 41 * hash + (this.epc != null ? this.epc.hashCode() : 0);
        hash = 41 * hash + (this.data != null ? this.data.hashCode() : 0);
        hash = 41 * hash + (int) (this.time ^ (this.time >>> 32));
        return hash;
    }
}
