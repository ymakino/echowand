package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.util.Collector;
import echowand.util.Selector;
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
    
    private LinkedList<ResultFrame> requestFrameList;
    private LinkedList<ResultFrame> unsentRequestFrameList;
    private LinkedList<ResultFrame> invalidRequestFrameList;
    private LinkedList<ResultData> requestDataList;
    private LinkedList<ResultData> unsentRequestDataList;
    private HashMap<ResultData, ResultFrame> requestDataFrameMap;
    
    private LinkedList<ResultFrame> frameList;
    private LinkedList<ResultFrame> errorFrameList;
    private LinkedList<ResultFrame> invalidFrameList;
    private LinkedList<ResultData> dataList;
    private LinkedList<ResultData> errorDataList;
    private HashMap<ResultData, ResultFrame> dataFrameMap;
    
    public ResultBase() {
        LOGGER.entering(CLASS_NAME, "ResultBase");
        
        done = false;
        requestFrameList = new LinkedList<ResultFrame>();
        unsentRequestFrameList = new LinkedList<ResultFrame>();
        invalidRequestFrameList = new LinkedList<ResultFrame>();
        requestDataList = new LinkedList<ResultData>();
        unsentRequestDataList = new LinkedList<ResultData>();
        requestDataFrameMap = new HashMap<ResultData, ResultFrame>();
    
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
    
    public synchronized boolean addRequestFrame(Frame frame, boolean success) {
        LOGGER.entering(CLASS_NAME, "addRequestFrame", new Object[]{frame, success});
        
        boolean result = addRequestFrame(createResultFrame(frame), success);
        
        LOGGER.exiting(CLASS_NAME, "addRequestFrame", result);
        return result;
    }
    
    public synchronized boolean addRequestFrame(ResultFrame resultFrame, boolean success) {
        LOGGER.entering(CLASS_NAME, "addRequestFrame", new Object[]{resultFrame, success});
        
        if (requestFrameList.contains(resultFrame)) {
            LOGGER.exiting(CLASS_NAME, "addRequestFrame", false);
            return false;
        }
        
        Frame frame = resultFrame.frame;
        
        if (!hasStandardPayload(frame)) {
            invalidRequestFrameList.add(resultFrame);
            LOGGER.exiting(CLASS_NAME, "addRequestFrame", false);
            return false;
        }
        
        boolean result;
        
        if (success) {
            result = requestFrameList.add(resultFrame);
        } else {
            result = unsentRequestFrameList.add(resultFrame);
        }

        StandardPayload payload = (StandardPayload) frame.getCommonFrame().getEDATA();

        int count = payload.getFirstOPC();
        for (int i = 0; i < count; i++) {
            Property property = payload.getFirstPropertyAt(i);

            Node node = frame.getSender();
            ESV esv = payload.getESV();
            EOJ eoj = payload.getSEOJ();
            EPC epc = property.getEPC();
            Data data = property.getEDT();

            ResultData resultData = new ResultData(node, esv, eoj, epc, data, resultFrame.time);
            if (success) {
                result &= requestDataList.add(resultData);
            } else {
                result &= unsentRequestDataList.add(resultData);
            }

            requestDataFrameMap.put(resultData, resultFrame);
        }
        
        LOGGER.exiting(CLASS_NAME, "addRequestFrame", result);
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
        
        if (frameList.contains(resultFrame)) {
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }
        
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
            ESV esv = payload.getESV();
            EOJ eoj = payload.getSEOJ();
            EPC epc = property.getEPC();
            Data data = property.getEDT();
            
            ResultData resultData = new ResultData(node, esv, eoj, epc, data, resultFrame.time);
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
    
    public synchronized int countRequestFrames() {
        LOGGER.entering(CLASS_NAME, "countRequestFrames");
        
        int count = requestFrameList.size();
        
        LOGGER.exiting(CLASS_NAME, "countRequestFrames", count);
        return count;
    }
    
    public synchronized int countFrames() {
        LOGGER.entering(CLASS_NAME, "countFrames");
        
        int count = frameList.size();
        
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }
    
    public synchronized ResultFrame getRequestFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getRequestFrame", index);
        
        ResultFrame frame = requestFrameList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrame", frame);
        return frame;
    }
    
    public synchronized ResultFrame getRequestFrame(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "getRequestFrame", resultData);
        
        ResultFrame frame = requestDataFrameMap.get(resultData);
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrame", frame);
        return frame;
    }
    
    public synchronized List<ResultFrame> getRequestFrameList() {
        LOGGER.entering(CLASS_NAME, "getRequestFrameList");
        
        LinkedList<ResultFrame> resultList = new LinkedList<ResultFrame>(requestFrameList);
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrameList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultFrame> getRequestFrameList(Selector<? super ResultFrame> selector) {
        LOGGER.entering(CLASS_NAME, "getRequestFrameList", selector);
        
        List<ResultFrame> resultList = new Collector<ResultFrame>(selector).collect(requestFrameList);
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrameList", resultList);
        return resultList;
    }
    
    public synchronized ResultFrame getFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getFrame", index);
        
        ResultFrame frame = frameList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getFrame", frame);
        return frame;
    }
    
    public synchronized ResultFrame getFrame(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "getFrame", resultData);
        
        ResultFrame frame = dataFrameMap.get(resultData);
        
        LOGGER.exiting(CLASS_NAME, "getFrame", frame);
        return frame;
    }
    
    public synchronized List<ResultFrame> getFrameList() {
        LOGGER.entering(CLASS_NAME, "getFrameList");
        
        LinkedList<ResultFrame> resultList = new LinkedList<ResultFrame>(frameList);
        
        LOGGER.exiting(CLASS_NAME, "getFrameList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultFrame> getFrameList(Selector<? super ResultFrame> selector) {
        LOGGER.entering(CLASS_NAME, "getFrameList", selector);
        
        List<ResultFrame> resultList = new Collector<ResultFrame>(selector).collect(frameList);
        
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
    
    public synchronized List<ResultData> getDataList(Selector<? super ResultData> selector) {
        LOGGER.entering(CLASS_NAME, "getDataList", selector);
        
        List<ResultData> resultList = new Collector<ResultData>(selector).collect(dataList);
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getDataList(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "getDataList", resultFrame);
        
        final ResultFrame selectedFrame = resultFrame;
        
        Selector<ResultData> selector = new Selector<ResultData>() {
            @Override
            public boolean match(ResultData resultData) {
                return selectedFrame.equals(dataFrameMap.get(resultData));
            }
        };
        
        List<ResultData> resultList = new Collector<ResultData>(selector).collect(dataList);
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
    
    public synchronized int countRequestData() {
        LOGGER.entering(CLASS_NAME, "countRequestData");
        
        int count = requestDataList.size();
        
        LOGGER.exiting(CLASS_NAME, "countRequestData", count);
        return count;
    }
    
    public synchronized ResultData getRequestData(int index) {
        LOGGER.entering(CLASS_NAME, "getRequestData");
        
        ResultData resultData = requestDataList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getRequestData", resultData);
        return resultData;
    }
    
    public synchronized List<ResultData> getRequestDataList() {
        LOGGER.entering(CLASS_NAME, "getRequestDataList");
        
        List<ResultData> resultList = new LinkedList<ResultData>(requestDataList);
        
        LOGGER.exiting(CLASS_NAME, "getRequestDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestDataList(Selector<? super ResultData> selector) {
        LOGGER.entering(CLASS_NAME, "getRequestDataList", selector);
        
        List<ResultData> resultList = new Collector<ResultData>(selector).collect(requestDataList);
        
        LOGGER.exiting(CLASS_NAME, "getRequestDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestDataList(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "getRequestDataList", resultFrame);
        
        final ResultFrame selectedFrame = resultFrame;
        
        Selector<ResultData> selector = new Selector<ResultData>() {
            @Override
            public boolean match(ResultData resultData) {
                return selectedFrame.equals(requestDataFrameMap.get(resultData));
            }
        };
        
        List<ResultData> resultList = new Collector<ResultData>(selector).collect(requestDataList);
        
        LOGGER.exiting(CLASS_NAME, "getRequestDataList", resultList);
        return resultList;
    }
}
