package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.service.CaptureResultListener;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class CaptureResult {
    private static final Logger LOGGER = Logger.getLogger(CaptureResult.class.getName());
    private static final String CLASS_NAME = CaptureResult.class.getName();
    
    private CaptureResultListener listener;
    private LinkedList<ResultData> dataList;
    private LinkedList<Frame> frames;
    private boolean done;
    
    public CaptureResult(CaptureResultListener listener) {
        LOGGER.entering(CLASS_NAME, "CaptureResult", new Object[]{listener});
        
        this.listener = listener;
        this.dataList = new LinkedList<ResultData>();
        this.frames = new LinkedList<Frame>();
        done = false;
        
        LOGGER.exiting(CLASS_NAME, "CaptureResult");
    }
    
    public synchronized void stopCapture() {
        LOGGER.entering(CLASS_NAME, "stopObserve");
        
        if (!done) {
            listener.removeCaptureResult(this);
            done = true;
        }
        
        LOGGER.exiting(CLASS_NAME, "stopObserve");
    }
    
    public synchronized boolean isDone() {
        LOGGER.entering(CLASS_NAME, "isDone");
        
        LOGGER.exiting(CLASS_NAME, "isDone", done);
        return done;
    }
    
    public synchronized boolean addFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "addFrame", frame);
        
        long time = System.currentTimeMillis();
        
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        
        int count = payload.getFirstOPC();
        for (int i=0; i<count; i++) {
            Property property = payload.getFirstPropertyAt(i);
            
            Node node = frame.getSender();
            EOJ eoj = payload.getSEOJ();
            EPC epc = property.getEPC();
            Data data = property.getEDT();
            
            dataList.add(new ResultData(node, eoj, epc, data, time));
        }
        
        boolean result = frames.add(frame);
        LOGGER.exiting(CLASS_NAME, "addFrame", result);
        return result;
    }
    
    public synchronized int countFrames() {
        LOGGER.entering(CLASS_NAME, "countFrames");
        
        int count = frames.size();
        
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }
    
    public synchronized Frame getFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getFrame", index);
        
        Frame frame = frames.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getFrame", frame);
        return frame;
    }
    
    public synchronized List<ResultData> getResultDataList() {
        LOGGER.entering(CLASS_NAME, "getResultDataList");
        
        List<ResultData> resultList = getResultDataList(new ResultDataMatcherRule());
        
        LOGGER.exiting(CLASS_NAME, "getResultDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getResultDataList(ResultDataMatcher matcher) {
        LOGGER.entering(CLASS_NAME, "getResultDataList", matcher);
        
        LinkedList<ResultData> resultList = new LinkedList<ResultData>();
        
        for (ResultData resultData: dataList) {
            if (matcher.match(resultData)) {
                resultList.add(resultData);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "getResultDataList", resultList);
        return resultList;
    }
}
