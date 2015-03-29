package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.Property;
import echowand.net.StandardPayload;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public abstract class ResultBase {
    private static final Logger LOGGER = Logger.getLogger(ResultBase.class.getName());
    private static final String CLASS_NAME = ResultBase.class.getName();
    
    private boolean done;
    
    private LinkedList<ResultFrame> frameList;
    private LinkedList<ResultFrame> errorFrameList;
    private LinkedList<ResultFrame> invalidFrameList;
    private LinkedList<ResultData> dataList;
    private LinkedList<ResultData> errorDataList;
    private HashMap<ResultData, ResultFrame> dataFrameMap;
    
    public ResultBase() {
        LOGGER.entering(CLASS_NAME, "ResultBase");
        
        done = false;
        frameList = new LinkedList<ResultFrame>();
        errorFrameList = new LinkedList<ResultFrame>();
        invalidFrameList = new LinkedList<ResultFrame>();
        dataList = new LinkedList<ResultData>();
        errorDataList = new LinkedList<ResultData>();
        dataFrameMap = new HashMap<ResultData, ResultFrame>();
        
        LOGGER.exiting(CLASS_NAME, "ResultBase");
    }
    
    public synchronized void done() {
        LOGGER.entering(CLASS_NAME, "done");
        
        if (!done) {
            done = true;
            notifyAll();
        }
        
        LOGGER.exiting(CLASS_NAME, "done");
    }
    
    public synchronized boolean isDone() {
        LOGGER.entering(CLASS_NAME, "isDone");
        
        LOGGER.exiting(CLASS_NAME, "isDone", done);
        return done;
    }
    
    public synchronized void join() throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "join");
        
        for (;;) {
            if (done) {
                LOGGER.exiting(CLASS_NAME, "join");
                return;
            }
            wait();
        }
    }
    
    public abstract boolean isSuccessPayload(StandardPayload payload);
    
    public abstract boolean isValidPayload(StandardPayload payload);
    
    public abstract boolean isValidProperty(Property property);
    
    public boolean hasStandardPayload(Frame frame) {
        LOGGER.entering(CLASS_NAME, "hasStandardPayload", frame);
        
        CommonFrame commonFrame = frame.getCommonFrame();
        
        boolean result = false;
        
        if (commonFrame.isStandardPayload()) {
            result = commonFrame.getEDATA() instanceof StandardPayload;
        }
        
        LOGGER.exiting(CLASS_NAME, "hasStandardPayload", result);
        return result;
    }
    
    public synchronized boolean addFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "addFrame", frame);
        
        boolean result = addFrame(createResultFrame(frame));
        
        LOGGER.exiting(CLASS_NAME, "addFrame", result);
        return result;
    }
    
    public synchronized boolean addFrame(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "addFrame", resultFrame);
        
        Frame frame = resultFrame.frame;
        
        if (!hasStandardPayload(frame)) {
            invalidFrameList.add(resultFrame);
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }

        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        
        if (!isValidPayload(payload)) {
            invalidFrameList.add(resultFrame);
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }
        
        boolean result = frameList.add(resultFrame);
        
        int count = payload.getFirstOPC();
        for (int i=0; i<count; i++) {
            Property property = payload.getFirstPropertyAt(i);
            
            Node node = frame.getSender();
            EOJ eoj = payload.getSEOJ();
            EPC epc = property.getEPC();
            Data data = property.getEDT();
            
            ResultData resultData = new ResultData(node, eoj, epc, data, resultFrame.time);
            if (isValidProperty(property)) {
                result &= dataList.add(resultData);
            } else {
                errorDataList.add(resultData);
                result = false;
            }
            
            dataFrameMap.put(resultData, resultFrame);
        }
        
        if (!isSuccessPayload(payload)) {
            errorFrameList.add(resultFrame);
        }
        
        LOGGER.exiting(CLASS_NAME, "addFrame", result);
        return result;
    }
    
    private synchronized ResultFrame createResultFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "createResultFrame", frame);
        
        long time = System.currentTimeMillis();
        ResultFrame resultFrame = new ResultFrame(frame, time);
        
        LOGGER.exiting(CLASS_NAME, "createResultFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized int countFrames() {
        LOGGER.entering(CLASS_NAME, "countFrames");
        
        int count = frameList.size();
        
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }
    
    public synchronized ResultFrame getFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getFrame", index);
        
        ResultFrame frame = frameList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getFrame", frame);
        return frame;
    }
    
    public synchronized ResultFrame getFrame(ResultData resultData) {
        return dataFrameMap.get(resultData);
    }
    
    public synchronized List<ResultFrame> getFrameList() {
        LOGGER.entering(CLASS_NAME, "getFrameList");
        
        LinkedList<ResultFrame> resultList = new LinkedList<ResultFrame>(frameList);
        
        LOGGER.exiting(CLASS_NAME, "getFrameList", resultList);
        return resultList;
    }
    
    public synchronized int countData() {
        LOGGER.entering(CLASS_NAME, "countData");
        
        int count = dataList.size();
        
        LOGGER.exiting(CLASS_NAME, "countData", count);
        return count;
    }
    
    public synchronized ResultData getData(int index) {
        LOGGER.entering(CLASS_NAME, "getData");
        
        ResultData resultData = dataList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getData", resultData);
        return resultData;
    }
    
    public synchronized List<ResultData> getDataList() {
        LOGGER.entering(CLASS_NAME, "getDataList");
        
        List<ResultData> resultList = new LinkedList<ResultData>(dataList);
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getDataList(ResultDataMatcher matcher) {
        LOGGER.entering(CLASS_NAME, "getDataList", matcher);
        
        LinkedList<ResultData> resultList = new LinkedList<ResultData>();
        
        for (ResultData resultData: dataList) {
            if (matcher.match(resultData)) {
                resultList.add(resultData);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getDataList(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "getDataList", resultFrame);
        
        final ResultFrame matcherFrame = resultFrame;
        
        ResultDataMatcher matcher = new ResultDataMatcher() {
            @Override
            public boolean match(ResultData resultData) {
                return matcherFrame.equals(dataFrameMap.get(resultData));
            }
        };
        
        List<ResultData> resultList = getDataList(matcher);
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
}
