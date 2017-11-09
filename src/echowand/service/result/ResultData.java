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
    
    private final Node node;
    private final ESV esv;
    private final EOJ eoj;
    private final EPC epc;
    private final Data data;
    private final long timestamp;
    
    public ResultData(Node node, ESV esv, EOJ eoj, EPC epc, Data data, long timestamp) {
        LOGGER.entering(CLASS_NAME, "ResultData", new Object[]{node, eoj, epc, data, timestamp});
        
        this.node = node;
        this.esv = esv;
        this.eoj = eoj;
        this.epc = epc;
        this.data = data;
        this.timestamp = timestamp;
        
        LOGGER.exiting(CLASS_NAME, "ResultData");
    }
    
    public ResultData(Node node, ESV esv, EOJ eoj, EPC epc, Data data) {
        LOGGER.entering(CLASS_NAME, "ResultData", new Object[]{node, eoj, epc, data});
        
        this.node = node;
        this.esv = esv;
        this.eoj = eoj;
        this.epc = epc;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        
        LOGGER.exiting(CLASS_NAME, "ResultData");
    }
    
    public Node getNode() {
        return node;
    }
    
    public ESV getESV() {
        return esv;
    }
    
    public EOJ getEOJ() {
        return eoj;
    }
    
    public EPC getEPC() {
        return epc;
    }
    
    public Data getActualData() {
        return data;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public byte get(int index) {
        return data.get(index);
    }
    
    public int size() {
        return data.size();
    }
    
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    public byte[] toBytes() {
        return data.toBytes();
    }
    
    public byte[] toBytes(int offset, int length) {
        return data.toBytes(offset, length);
    }
    
    public void copyBytes(int srcOffset, byte[] destData, int destOffset, int length) {
        data.copyBytes(srcOffset, destData, destOffset, length);
    }
    
    @Override
    public String toString() {
        return "ResultData{Node: " + node + ", ESV: " + esv + ", EOJ: " + eoj + ", EPC: " + epc + ", Data: " + data + ", Time: " + timestamp + "}";
    }
}
