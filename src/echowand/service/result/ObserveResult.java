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
import echowand.service.ObserveResultProcessor;
import java.util.Collection;
import java.util.HashMap;
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
    private HashMap<ResultData, ResultFrame> dataFrameMap;
    private boolean dataListEnabled = true;
    private boolean frameListEnabled = true;
    private boolean done;

    public synchronized void enableDataList() {
        dataListEnabled = true;
    }

    public synchronized void disableDataList() {
        dataList.clear();
        dataFrameMap.clear();
        dataListEnabled = false;
    }

    public boolean isDataListEnabled() {
        return dataListEnabled;
    }

    public synchronized void enableFrameList() {
        frameListEnabled = true;
    }

    public synchronized void disableFrameList() {
        frameList.clear();
        dataFrameMap.clear();
        frameListEnabled = false;
    }

    public boolean isFrameListEnabled() {
        return frameListEnabled;
    }

    public ObserveResult(FrameMatcher matcher, ObserveResultProcessor processor) {
        LOGGER.entering(CLASS_NAME, "ObserveResult", new Object[]{matcher, processor});

        this.matcher = matcher;
        this.processor = processor;
        dataList = new LinkedList<ResultData>();
        frameList = new LinkedList<ResultFrame>();
        dataFrameMap = new HashMap<ResultData, ResultFrame>();
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
        
        if (frameList.contains(resultFrame)) {
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }
        
        Frame frame = resultFrame.frame;
        
        if (!hasStandardPayload(frame)) {
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }
        
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        
        if (payload.getESV() != ESV.INF && payload.getESV() != ESV.INFC) {
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }
        
        boolean result = true;
        
        if (frameListEnabled) {
            result &= frameList.add(resultFrame);
        }
        
        int count = payload.getFirstOPC();
        for (int i=0; i<count; i++) {
            Property property = payload.getFirstPropertyAt(i);
            
            Node node = frame.getSender();
            EOJ eoj = payload.getSEOJ();
            EPC epc = property.getEPC();
            Data data = property.getEDT();
            
            ResultData resultData = new ResultData(node, eoj, epc, data, resultFrame.time);
            
            if (dataListEnabled) {
                result &= dataList.add(resultData);
            }
            
            if (frameListEnabled && dataListEnabled) {
                dataFrameMap.put(resultData, resultFrame);
            }
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
        
        ResultFrame resultFrame = frameList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized ResultFrame getFrame(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "getFrame", resultData);
        
        ResultFrame resultFrame = dataFrameMap.get(resultData);
        
        LOGGER.exiting(CLASS_NAME, "getFrame", resultFrame);
        return resultFrame;
    }
    
    
    public synchronized List<ResultFrame> getFrameList() {
        LOGGER.entering(CLASS_NAME, "getFrameList");
        
        List<ResultFrame> resultList = new LinkedList<ResultFrame>(frameList);
        
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
    
    public synchronized int truncateData(int size) {
        LOGGER.entering(CLASS_NAME, "truncateData", size);
        
        if (dataList.size() < size) {
            size = dataList.size();
        }
        
        dataList.subList(0, size).clear();
        updateDataFrameMap();
        
        LOGGER.exiting(CLASS_NAME, "truncateData", size);
        return size;
    }
    
    public synchronized boolean removeData(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "removeData", resultData);
        
        boolean result = dataList.remove(resultData);
        dataFrameMap.remove(resultData);
        
        LOGGER.exiting(CLASS_NAME, "removeData", result);
        return result;
    }
    
    public synchronized int removeData(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "removeData", resultFrame);
        
        List<ResultData> removingDataList = getDataList(resultFrame);
        int count = 0;
        
        for (ResultData resultData : removingDataList) {
            if (dataList.remove(resultData)) {
                count++;
            }
            dataFrameMap.remove(resultData);
        }
        
        LOGGER.exiting(CLASS_NAME, "removeData", count);
        return count;
    }
    
    public synchronized void removeData(int index) {
        LOGGER.entering(CLASS_NAME, "removeData", index);
        
        ResultData resultData = dataList.get(index);
        dataList.remove(index);
        dataFrameMap.remove(resultData);
        
        LOGGER.exiting(CLASS_NAME, "removeData");
    }
    
    public synchronized void removeAll() {
        LOGGER.entering(CLASS_NAME, "removeAll");
        
        frameList.clear();
        dataList.clear();
        dataFrameMap.clear();
    }
    
    public synchronized int removeAllData() {
        LOGGER.entering(CLASS_NAME, "removeAllData");
        
        int size = dataList.size();
        dataList.clear();
        dataFrameMap.clear();
        
        LOGGER.exiting(CLASS_NAME, "removeAllData", size);
        return size;
    }
    
    public synchronized int truncateFrames(int size) {
        LOGGER.entering(CLASS_NAME, "truncateFrames", size);
        
        if (frameList.size() < size) {
            size = frameList.size();
        }
        
        frameList.subList(0, size).clear();
        updateDataFrameMap();
        
        LOGGER.exiting(CLASS_NAME, "truncateFrames", size);
        return size;
    }
    
    public synchronized boolean removeFrame(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "removeFrame", resultFrame);
        
        boolean result = frameList.remove(resultFrame);
        updateDataFrameMap();
        
        LOGGER.exiting(CLASS_NAME, "removeFrame", result);
        return result;
    }
    
    public synchronized boolean removeFrame(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "removeFrame", resultData);
        
        ResultFrame resultFrame = getFrame(resultData);
        boolean result = false;
        
        if (resultFrame != null) {
            result = frameList.remove(resultFrame);
            updateDataFrameMap();
        }
        
        LOGGER.exiting(CLASS_NAME, "removeFrame", result);
        return result;
    }
    
    public synchronized void removeFrame(int index) {
        LOGGER.entering(CLASS_NAME, "removeFrame", index);
        
        frameList.remove(index);
        updateDataFrameMap();
        
        LOGGER.exiting(CLASS_NAME, "removeFrame");
    }
    
    public synchronized int removeAllFrames() {
        LOGGER.entering(CLASS_NAME, "removeAllFrames");
        
        int size = frameList.size();
        frameList.clear();
        dataFrameMap.clear();
        
        LOGGER.exiting(CLASS_NAME, "removeAllFrames", size);
        return size;
    }
    
    private void updateDataFrameMap() {
        LOGGER.entering(CLASS_NAME, "updateDataFrameMap");
        
        LinkedList<ResultData> keyDataList = new LinkedList<ResultData>(dataFrameMap.keySet());
        
        for (ResultData resultData : keyDataList) {
            ResultFrame resultFrame = dataFrameMap.get(resultData);
            
            if (!dataList.contains(resultData) || !frameList.contains(resultFrame)) {
                dataFrameMap.remove(resultData);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "updateDataFrameMap");
    }
}
