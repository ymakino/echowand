package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.service.ObserveResultProcessor;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ObserveResult {
    private static final Logger LOGGER = Logger.getLogger(ObserveResult.class.getName());
    private static final String CLASS_NAME = ObserveResult.class.getName();
    
    private FrameMatcher matcher;
    private ObserveResultProcessor processor;
    private LinkedList<ResultData> dataList;
    private LinkedList<ResultFrame> frameList;
    private boolean done;
    
    public ObserveResult(FrameMatcher matcher, ObserveResultProcessor processor) {
        LOGGER.entering(CLASS_NAME, "ObserveResult", new Object[]{matcher, processor});
        
        this.matcher = matcher;
        this.processor = processor;
        this.dataList = new LinkedList<ResultData>();
        this.frameList = new LinkedList<ResultFrame>();
        done = false;
        
        LOGGER.exiting(CLASS_NAME, "ObserveResult");
    }
    
    public synchronized void stopObserve() {
        LOGGER.entering(CLASS_NAME, "stopObserve");
        
        if (!done) {
            processor.removeObserveResult(this);
            done = true;
        }
        
        LOGGER.exiting(CLASS_NAME, "stopObserve");
    }
    
    public synchronized boolean isDone() {
        LOGGER.entering(CLASS_NAME, "isDone");
        
        LOGGER.exiting(CLASS_NAME, "isDone", done);
        return done;
    }
    
    public boolean shouldReceive(Frame frame) {
        LOGGER.entering(CLASS_NAME, "shouldReceive", frame);
        
        boolean result = matcher.match(frame);
        
        LOGGER.exiting(CLASS_NAME, "shouldReceive", result);
        return result;
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
        
        boolean result = frameList.add(new ResultFrame(frame, time));
        LOGGER.exiting(CLASS_NAME, "addFrame", result);
        return result;
    }
    
    public synchronized int countFrames() {
        LOGGER.entering(CLASS_NAME, "countFrames");
        
        int count = frameList.size();
        
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }
    
    public synchronized Frame getFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getFrame", index);
        
        Frame frame = frameList.get(index).frame;
        
        LOGGER.exiting(CLASS_NAME, "getFrame", frame);
        return frame;
    }
    
    public synchronized ResultFrame getResultFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getResultFrame", index);
        
        ResultFrame resultFrame = frameList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getResultFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized List<ResultFrame> getResultFrameList() {
        LOGGER.entering(CLASS_NAME, "getResultFrameList");
        
        List<ResultFrame> resultList = new LinkedList<ResultFrame>(frameList);
        
        LOGGER.exiting(CLASS_NAME, "getResultFrameList", resultList);
        return resultList;
    }
    
    public synchronized int countResultData() {
        LOGGER.entering(CLASS_NAME, "countResultData");
        
        int count = dataList.size();
        
        LOGGER.exiting(CLASS_NAME, "countResultData", count);
        return count;
    }
    
    public synchronized ResultData getResultData(int index) {
        LOGGER.entering(CLASS_NAME, "getResultData");
        
        ResultData resultData = dataList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getResultData", resultData);
        return resultData;
    }
    
    public synchronized List<ResultData> getResultDataList() {
        LOGGER.entering(CLASS_NAME, "getResultDataList");
        
        List<ResultData> resultList = new LinkedList<ResultData>(dataList);
        
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
    
    public synchronized int removeResultData(int size) {
        LOGGER.entering(CLASS_NAME, "removeResultDataList", size);
        
        if (dataList.size() < size) {
            size = dataList.size();
        }
        
        dataList.subList(0, size).clear();
        
        LOGGER.exiting(CLASS_NAME, "removeResultDataList", size);
        return size;
    }
    
    public synchronized int removeAllResultData() {
        LOGGER.entering(CLASS_NAME, "removeAllResultDataList");
        
        int size = dataList.size();
        dataList.clear();
        
        LOGGER.exiting(CLASS_NAME, "removeAllResultDataList", size);
        return size;
    }
    
    public synchronized int removeFrames(int size) {
        LOGGER.entering(CLASS_NAME, "removeFrames", size);
        
        if (frameList.size() < size) {
            size = frameList.size();
        }
        
        frameList.subList(0, size).clear();
        
        LOGGER.exiting(CLASS_NAME, "removeFrames", size);
        return size;
    }
    
    public synchronized int removeAllFrames() {
        LOGGER.entering(CLASS_NAME, "removeAllFrames");
        
        int size = frameList.size();
        frameList.clear();
        
        LOGGER.exiting(CLASS_NAME, "removeAllFrames", size);
        return size;
    }
}
