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
import echowand.service.TimestampManager;
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
public class ObserveResult {
    private static final Logger LOGGER = Logger.getLogger(ObserveResult.class.getName());
    private static final String CLASS_NAME = ObserveResult.class.getName();
    
    private Selector<? super Frame> frameSelector;
    private ObserveResultProcessor processor;
    private LinkedList<ResultData> dataList;
    private LinkedList<ResultFrame> frameList;
    private HashMap<ResultData, ResultFrame> dataFrameMap;
    private boolean dataListEnabled = true;
    private boolean frameListEnabled = true;
    private boolean done;
    
    private ObserveListener observeListener;
    private TimestampManager timestampManager;

    public ObserveResult(Selector<? super Frame> selector, ObserveResultProcessor processor, TimestampManager timestampManager) {
        LOGGER.entering(CLASS_NAME, "ObserveResult", new Object[]{selector, processor});

        frameSelector = selector;
        this.processor = processor;
        this.timestampManager = timestampManager;
        
        dataList = new LinkedList<ResultData>();
        frameList = new LinkedList<ResultFrame>();
        dataFrameMap = new HashMap<ResultData, ResultFrame>();
        done = false;
        
        observeListener = null;
        
        LOGGER.exiting(CLASS_NAME, "ObserveResult");
    }
    
    public synchronized void setObserveListener(ObserveListener observeListener) {
        LOGGER.entering(CLASS_NAME, "setObserveListener", observeListener);
        
        this.observeListener = observeListener;
        
        if (observeListener != null && !done) {
            observeListener.begin(this);
        }
        
        LOGGER.exiting(CLASS_NAME, "setObserveListener");
    }

    public synchronized void enableDataList() {
        LOGGER.entering(CLASS_NAME, "enableDataList");
        
        dataListEnabled = true;
        
        LOGGER.exiting(CLASS_NAME, "enableDataList");
    }

    public synchronized void disableDataList() {
        LOGGER.entering(CLASS_NAME, "disableDataList");
        
        dataList.clear();
        dataFrameMap.clear();
        dataListEnabled = false;
        
        LOGGER.exiting(CLASS_NAME, "disableDataList");
    }

    public synchronized boolean isDataListEnabled() {
        LOGGER.entering(CLASS_NAME, "isDataListEnabled");
        
        LOGGER.exiting(CLASS_NAME, "isDataListEnabled", dataListEnabled);
        return dataListEnabled;
    }

    public synchronized void enableFrameList() {
        LOGGER.entering(CLASS_NAME, "enableFrameList");
        
        frameListEnabled = true;
        
        LOGGER.exiting(CLASS_NAME, "enableFrameList");
    }

    public synchronized void disableFrameList() {
        LOGGER.entering(CLASS_NAME, "disableFrameList");
        
        frameList.clear();
        dataFrameMap.clear();
        frameListEnabled = false;
        
        LOGGER.exiting(CLASS_NAME, "disableFrameList");
    }

    public synchronized boolean isFrameListEnabled() {
        LOGGER.entering(CLASS_NAME, "isFrameListEnabled");
        
        LOGGER.exiting(CLASS_NAME, "isFrameListEnabled", frameListEnabled);
        return frameListEnabled;
    }
    
    public synchronized void stopObserve() {
        LOGGER.entering(CLASS_NAME, "stopObserve");
        
        if (!done) {
            processor.removeObserveResult(this);
            done = true;
            
            if (observeListener != null) {
                observeListener.finish(this);
            }
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
        
        boolean result = frameSelector.match(frame);
        
        LOGGER.exiting(CLASS_NAME, "shouldReceive", result);
        return result;
    }
    
    public boolean hasStandardPayload(Frame frame) {
        LOGGER.entering(CLASS_NAME, "hasStandardPayload", frame);
        
        CommonFrame commonFrame = frame.getCommonFrame();
        
        boolean result = false;
        
        if (commonFrame.isStandardPayload()) {
            result = commonFrame.getEDATA(StandardPayload.class) != null;
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
        
        if (!hasStandardPayload(resultFrame.getActualFrame())) {
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }
        
        StandardPayload payload = resultFrame.getCommonFrame().getEDATA(StandardPayload.class);
        
        if (payload.getESV() != ESV.INF && payload.getESV() != ESV.INFC) {
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }
        
        boolean result = true;
        int count = payload.getFirstOPC();
        LinkedList<ResultData> frameDataList = new LinkedList<ResultData>();
        
        if (frameListEnabled) {
            result &= frameList.add(resultFrame);
        }
        
        for (int i=0; i<count; i++) {
            Property property = payload.getFirstPropertyAt(i);
            
            Node node = resultFrame.getSender();
            ESV esv = payload.getESV();
            EOJ eoj = payload.getSEOJ();
            EPC epc = property.getEPC();
            Data data = property.getEDT();
            
            ResultData resultData = new ResultData(node, esv, eoj, epc, data, resultFrame.getTimestamp());
            
            frameDataList.add(resultData);
            
            if (dataListEnabled) {
                result &= dataList.add(resultData);
            }
            
            if (frameListEnabled && dataListEnabled) {
                dataFrameMap.put(resultData, resultFrame);
            }
        }
        
        if (observeListener != null) {
            observeListener.receive(this, resultFrame);
            
            for (ResultData resultData : frameDataList) {
                observeListener.receive(this, resultFrame, resultData);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "addFrame", result);
        return result;
    }
    
    private synchronized ResultFrame createResultFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "createResultFrame", frame);
        
        long timestamp = timestampManager.get(frame, System.currentTimeMillis());
        ResultFrame resultFrame = new ResultFrame(frame, timestamp);
        
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
        
        Selector<ResultData> selector = new  Selector<ResultData>() {
            @Override
            public boolean match(ResultData resultData) {
                return selectedFrame.equals(dataFrameMap.get(resultData));
            }
        };
        
        List<ResultData> resultList = new Collector<ResultData>(selector).collect(dataList);
        
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
    
    public synchronized void removeData(Selector<? super ResultData> selector) {
        LOGGER.entering(CLASS_NAME, "removeData", selector);
        
        for (ResultData resultData : getDataList(selector)) {
            removeData(resultData);
        }
        
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
    
    public synchronized void removeFrames(Selector<? super ResultFrame> selector) {
        LOGGER.entering(CLASS_NAME, "removeFrames", selector);
        
        for (ResultFrame resultFrame : getFrameList(selector)) {
            removeFrame(resultFrame);
        }
        
        LOGGER.exiting(CLASS_NAME, "removeFrames");
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
