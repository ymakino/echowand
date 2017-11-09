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
import echowand.service.TimestampManager;
import echowand.util.Collector;
import echowand.util.Selector;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public abstract class ResultBase<ResultType extends ResultBase> {
    private static final Logger LOGGER = Logger.getLogger(ResultBase.class.getName());
    private static final String CLASS_NAME = ResultBase.class.getName();
    
    private static class ResultListManager<T> {
        private LinkedList<T> list = new LinkedList<T>();
        private LinkedList<T> successList = new LinkedList<T>();
        private LinkedList<T> unsuccessList = new LinkedList<T>();
        
        private List<T> getList() {
            return list;
        }
        
        private List<T> getList(boolean success) {
            if (success) {
                return successList;
            } else {
                return unsuccessList;
            }
        }
        
        public List<T> cloneList() {
            return new LinkedList<T>(getList());
        }
        
        public List<T> cloneList(boolean success) {
            return new LinkedList<T>(getList(success));
        }
        
        public List<T> collectList(Selector<? super T> selector) {
            return new Collector<T>(selector).collect(getList());
        }
        
        public List<T> collectList(Selector<? super T> selector, boolean success) {
            return new Collector<T>(selector).collect(getList(success));
        }
        
        public T get(int index) {
            return getList().get(index);
        }
        
        public T get(int index, boolean success) {
            return getList(success).get(index);
        }
        
        public boolean add(T value, boolean success) {
            boolean result = true;
            
            result &= getList().add(value);
            result &= getList(success).add(value);
            
            return result;
        }
        
        public boolean contains(T value) {
            return getList().contains(value);
        }
        
        public boolean contains(T value, boolean success) {
            return getList(success).contains(value);
        }
        
        public int size() {
            return getList().size();
        }
        
        public int size(boolean success) {
            return getList(success).size();
        }
    
        public <V> List<T> getKeyList(final HashMap<T, V> targetValueMap, final V value) {
            return ResultBase.getKeyList(targetValueMap, value, getList());
        }
    
        public <V> List<T> getKeyList(final HashMap<T, V> targetValueMap, final V value, boolean success) {
            return ResultBase.getKeyList(targetValueMap, value, getList(success));
        }
    }
    
    private boolean done;
    
    private ResultListManager<ResultFrame> requestFrameManager;
    private LinkedList<ResultFrame> invalidRequestFrameList;
    
    private ResultListManager<ResultData> requestDataManager;
    private ResultListManager<ResultData> requestSecondDataManager;
    private HashMap<ResultData, ResultFrame> requestDataFrameMap;
    
    private ResultListManager<ResultFrame> responseFrameManager;
    private LinkedList<ResultFrame> invalidFrameList;
    
    private ResultListManager<ResultData> responseDataManager;
    private ResultListManager<ResultData> responseSecondDataManager;
    
    private HashMap<ResultData, ResultFrame> dataFrameMap;
    
    private Class<ResultType> cls;
    private TimestampManager timestampManager;
    private ResultListener<ResultType> listener;
    
    public ResultBase(Class<ResultType> cls, TimestampManager timestampManager) {
        LOGGER.entering(CLASS_NAME, "ResultBase", new Object[]{cls, timestampManager});
        
        done = false;
        
        this.cls = cls;
        this.timestampManager = timestampManager;
        listener = null;
        
        requestFrameManager = new ResultListManager<ResultFrame>();
        invalidRequestFrameList = new LinkedList<ResultFrame>();
        
        requestDataManager = new ResultListManager<ResultData>();
        requestSecondDataManager = new ResultListManager<ResultData>();
        requestDataFrameMap = new HashMap<ResultData, ResultFrame>();
    
        responseFrameManager = new ResultListManager<ResultFrame>();
        invalidFrameList = new LinkedList<ResultFrame>();
        
        responseDataManager = new ResultListManager<ResultData>();
        responseSecondDataManager = new ResultListManager<ResultData>();
        
        dataFrameMap = new HashMap<ResultData, ResultFrame>();
        
        LOGGER.exiting(CLASS_NAME, "ResultBase");
    }
    
    private ResultType self() {
        return cls.cast(this);
    }
    
    protected synchronized void setResultListener(ResultListener<ResultType> listener) {
        this.listener = listener;
    }
    
    public synchronized void begin() {
        LOGGER.entering(CLASS_NAME, "begin");
        
        if (listener != null) {
            listener.begin(self());
        }
        
        LOGGER.exiting(CLASS_NAME, "begin");
    }
    
    public synchronized void finish() {
        LOGGER.entering(CLASS_NAME, "finish");
        
        if (!done) {
            done = true;
            notifyAll();
        }
        
        if (listener != null) {
            listener.finish(self());
        }
        
        LOGGER.exiting(CLASS_NAME, "finish");
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
    
    public synchronized boolean waitData(Selector<? super ResultData> selector, int num) throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "waitData", new Object[]{selector, num});
        
        boolean result;
        
        for (;;) {
            
            if (getDataList(selector).size() >= num) {
                result = true;
                break;
            }
            
            if (done) {
                result = false;
                break;
            }
            
            wait();
        }
        
        LOGGER.exiting(CLASS_NAME, "waitData", result);
        return result;
    }
    
    public boolean waitData(int num) throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "waitData", num);
        
        boolean result = waitData(new ResultDataSelector(), num);
        
        LOGGER.exiting(CLASS_NAME, "waitData", result);
        return result;
    }
    
    public boolean waitData(Selector<? super ResultData> selector) throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "waitData", selector);
        
        boolean result = waitData(selector, 1);
        
        LOGGER.exiting(CLASS_NAME, "waitData", result);
        return result;
    }
    
    public boolean waitData() throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "waitData");
        
        boolean result = waitData(new ResultDataSelector(), 1);
        
        LOGGER.exiting(CLASS_NAME, "waitData", result);
        return result;
    }
    
    public synchronized boolean waitFrames(Selector<? super ResultFrame> selector, int num) throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "waitFrames", new Object[]{selector, num});
        
        boolean result;
        
        for (;;) {
            
            if (getFrameList(selector).size() >= num) {
                result = true;
                break;
            }
            
            if (done) {
                result = false;
                break;
            }
            
            wait();
        }
        
        LOGGER.exiting(CLASS_NAME, "waitFrames", result);
        return result;
    }
    
    public boolean waitFrames(int num) throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "waitFrames", num);
        
        boolean result = waitFrames(new ResultFrameSelector(), num);
        
        LOGGER.exiting(CLASS_NAME, "waitFrames", result);
        return result;
    }
    
    public boolean waitFrame(Selector<? super ResultFrame> selector) throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "waitFrame", selector);
        
        boolean result = waitFrames(selector, 1);
        
        LOGGER.exiting(CLASS_NAME, "waitFrame", result);
        return result;
    }
    
    public boolean waitFrame() throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "waitFrame");
        
        boolean result = waitFrames(new ResultFrameSelector(), 1);
        
        LOGGER.exiting(CLASS_NAME, "waitFrame", result);
        return result;
    }
    
    public abstract boolean isSuccessPayload(StandardPayload payload);
    
    public abstract boolean isValidPayload(StandardPayload payload);
    
    public abstract boolean isValidProperty(Property property);
    
    public boolean isValidSecondProperty(Property property) {
        LOGGER.entering(CLASS_NAME, "hasStandardPayload", property);
        
        LOGGER.exiting(CLASS_NAME, "hasStandardPayload", false);
        return false;
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
    
    public synchronized boolean addRequestFrame(Frame frame, boolean success) {
        LOGGER.entering(CLASS_NAME, "addRequestFrame", new Object[]{frame, success});
        
        boolean result = addRequestFrame(createResultFrame(frame), success);
        
        LOGGER.exiting(CLASS_NAME, "addRequestFrame", result);
        return result;
    }
    
    private ResultData createResultData(ResultFrame resultFrame, Frame frame, StandardPayload payload, Property property) {
            Node node = frame.getSender();
            ESV esv = payload.getESV();
            EOJ eoj = payload.getSEOJ();
            EPC epc = property.getEPC();
            Data data = property.getEDT();
            return new ResultData(node, esv, eoj, epc, data, resultFrame.getTimestamp());
    }
    
    public synchronized boolean addRequestFrame(ResultFrame resultFrame, boolean success) {
        LOGGER.entering(CLASS_NAME, "addRequestFrame", new Object[]{resultFrame, success});
        
        if (isDone()) {
            return false;
        }
        
        if (requestFrameManager.contains(resultFrame)) {
            LOGGER.exiting(CLASS_NAME, "addRequestFrame", false);
            return false;
        }
        
        if (!hasStandardPayload(resultFrame.getActualFrame())) {
            invalidRequestFrameList.add(resultFrame);
            LOGGER.exiting(CLASS_NAME, "addRequestFrame", false);
            return false;
        }
        
        boolean result = requestFrameManager.add(resultFrame, success);

        StandardPayload payload = resultFrame.getCommonFrame().getEDATA(StandardPayload.class);

        int count = payload.getFirstOPC();
        for (int i = 0; i < count; i++) {
            Property property = payload.getFirstPropertyAt(i);
            ResultData resultData = createResultData(resultFrame, resultFrame.getActualFrame(), payload, property);
            
            result &= requestDataManager.add(resultData, success);

            requestDataFrameMap.put(resultData, resultFrame);
        }

        int countSecond = payload.getSecondOPC();
        for (int i = 0; i < countSecond; i++) {
            Property property = payload.getSecondPropertyAt(i);
            ResultData resultData = createResultData(resultFrame, resultFrame.getActualFrame(), payload, property);
            
            result &= requestSecondDataManager.add(resultData, success);

            requestDataFrameMap.put(resultData, resultFrame);
        }
            
        if (listener != null) {
            listener.send(self(), resultFrame, success);
            
            for (ResultData resultData : requestDataManager.getKeyList(requestDataFrameMap, resultFrame)) {
                listener.send(self(), resultFrame, resultData, success);
                listener.send(self(), resultFrame, resultData, success, false);
            }
            
            for (ResultData resultData : requestSecondDataManager.getKeyList(requestDataFrameMap, resultFrame)) {
                listener.send(self(), resultFrame, resultData, success);
                listener.send(self(), resultFrame, resultData, success, true);
            }
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
        
        if (isDone()) {
            return false;
        }
        
        if (responseFrameManager.contains(resultFrame)) {
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }
        
        try {
            if (!hasStandardPayload(resultFrame.getActualFrame())) {
                invalidFrameList.add(resultFrame);
                LOGGER.exiting(CLASS_NAME, "addFrame", false);
                return false;
            }

            StandardPayload payload = resultFrame.getCommonFrame().getEDATA(StandardPayload.class);

            if (!isValidPayload(payload)) {
                invalidFrameList.add(resultFrame);
                LOGGER.exiting(CLASS_NAME, "addFrame", false);
                return false;
            }

            boolean result = responseFrameManager.add(resultFrame, isSuccessPayload(payload));

            int count = payload.getFirstOPC();
            for (int i=0; i<count; i++) {
                Property property = payload.getFirstPropertyAt(i);
                ResultData resultData = createResultData(resultFrame, resultFrame.getActualFrame(), payload, property);
                
                result &= responseDataManager.add(resultData, isValidProperty(property));

                dataFrameMap.put(resultData, resultFrame);
            }

            int countSecond = payload.getSecondOPC();
            for (int i=0; i<countSecond; i++) {
                Property property = payload.getSecondPropertyAt(i);
                ResultData resultData = createResultData(resultFrame, resultFrame.getActualFrame(), payload, property);

                result &= responseSecondDataManager.add(resultData, isValidSecondProperty(property));

                dataFrameMap.put(resultData, resultFrame);
            }
            
            if (listener != null) {
                listener.receive(self(), resultFrame);
            
                for (ResultData resultData : responseDataManager.getKeyList(dataFrameMap, resultFrame)) {
                    listener.receive(self(), resultFrame, resultData);
                    listener.receive(self(), resultFrame, resultData, false);
                }
            
                for (ResultData resultData : responseSecondDataManager.getKeyList(dataFrameMap, resultFrame)) {
                    listener.receive(self(), resultFrame, resultData);
                    listener.receive(self(), resultFrame, resultData, true);
                }
            }

            LOGGER.exiting(CLASS_NAME, "addFrame", result);
            return result;
            
        } finally {
            notifyAll();
        }
    }
    
    private synchronized ResultFrame createResultFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "createResultFrame", frame);
        
        long timestamp = timestampManager.get(frame, System.currentTimeMillis());
        ResultFrame resultFrame = new ResultFrame(frame, timestamp);
        
        LOGGER.exiting(CLASS_NAME, "createResultFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized int countRequestFrames() {
        LOGGER.entering(CLASS_NAME, "countRequestFrames");
        
        int count = requestFrameManager.size();
        
        LOGGER.exiting(CLASS_NAME, "countRequestFrames", count);
        return count;
    }
    
    public synchronized int countRequestFrames(boolean success) {
        LOGGER.entering(CLASS_NAME, "countRequestFrames", success);
        
        int count = requestFrameManager.size(success);
        
        LOGGER.exiting(CLASS_NAME, "countRequestFrames", count);
        return count;
    }
    
    public synchronized int countFrames() {
        LOGGER.entering(CLASS_NAME, "countFrames");
        
        int count = responseFrameManager.size();
        
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }
    
    public synchronized int countFrames(boolean accepted) {
        LOGGER.entering(CLASS_NAME, "countFrames", accepted);
        
        int count = responseFrameManager.size(accepted);
        
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }
    
    public synchronized ResultFrame getRequestFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getRequestFrame", index);
        
        ResultFrame frame = requestFrameManager.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrame", frame);
        return frame;
    }
    
    public synchronized ResultFrame getRequestFrame(int index, boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestFrame", new Object[]{index, success});
        
        ResultFrame frame = requestFrameManager.get(index, success);
        
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
        
        List<ResultFrame> resultList = requestFrameManager.cloneList();
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrameList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultFrame> getRequestFrameList(boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestFrameList", success);
        
        List<ResultFrame> resultList = requestFrameManager.cloneList(success);
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrameList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultFrame> getRequestFrameList(Selector<? super ResultFrame> selector) {
        LOGGER.entering(CLASS_NAME, "getRequestFrameList", selector);
        
        List<ResultFrame> resultList = requestFrameManager.collectList(selector);
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrameList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultFrame> getRequestFrameList(Selector<? super ResultFrame> selector, boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestFrameList", new Object[]{selector, success});
        
        List<ResultFrame> resultList = requestFrameManager.collectList(selector, success);
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrameList", resultList);
        return resultList;
    }
    
    public synchronized ResultFrame getFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getFrame", index);
        
        ResultFrame frame = responseFrameManager.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getFrame", frame);
        return frame;
    }
    
    public synchronized ResultFrame getFrame(int index, boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getFrame", new Object[]{index, accepted});
        
        ResultFrame frame = responseFrameManager.get(index, accepted);
        
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
        
        List<ResultFrame> resultList = responseFrameManager.cloneList();
        
        LOGGER.exiting(CLASS_NAME, "getFrameList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultFrame> getFrameList(boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getFrameList", accepted);
        
        List<ResultFrame> resultList = responseFrameManager.cloneList(accepted);
        
        LOGGER.exiting(CLASS_NAME, "getFrameList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultFrame> getFrameList(Selector<? super ResultFrame> selector) {
        LOGGER.entering(CLASS_NAME, "getFrameList", selector);
        
        List<ResultFrame> resultList = responseFrameManager.collectList(selector);
        
        LOGGER.exiting(CLASS_NAME, "getFrameList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultFrame> getFrameList(Selector<? super ResultFrame> selector, boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getFrameList", new Object[]{selector, accepted});
        
        List<ResultFrame> resultList = responseFrameManager.collectList(selector, accepted);
        
        LOGGER.exiting(CLASS_NAME, "getFrameList", resultList);
        return resultList;
    }
    
    public synchronized int countData() {
        LOGGER.entering(CLASS_NAME, "countData");
        
        int count = responseDataManager.size();
        
        LOGGER.exiting(CLASS_NAME, "countData", count);
        return count;
    }
    
    public synchronized int countData(boolean accepted) {
        LOGGER.entering(CLASS_NAME, "countData", accepted);
        
        int count = responseDataManager.size(accepted);
        
        LOGGER.exiting(CLASS_NAME, "countData", count);
        return count;
    }
    
    public synchronized ResultData getData(int index) {
        LOGGER.entering(CLASS_NAME, "getData", index);
        
        ResultData resultData = responseDataManager.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getData", resultData);
        return resultData;
    }
    
    public synchronized ResultData getData(int index, boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getData", new Object[]{index, accepted});
        
        ResultData resultData = responseDataManager.get(index, accepted);
        
        LOGGER.exiting(CLASS_NAME, "getData", resultData);
        return resultData;
    }
    
    public synchronized List<ResultData> getDataList() {
        LOGGER.entering(CLASS_NAME, "getDataList");
        
        List<ResultData> resultList = responseDataManager.cloneList();
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getDataList(boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getDataList", accepted);
        
        List<ResultData> resultList = responseDataManager.cloneList(accepted);
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getDataList(Selector<? super ResultData> selector) {
        LOGGER.entering(CLASS_NAME, "getDataList", selector);
        
        List<ResultData> resultList = responseDataManager.collectList(selector);
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getDataList(Selector<? super ResultData> selector, boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getDataList", new Object[]{selector, accepted});
        
        List<ResultData> resultList = responseDataManager.collectList(selector, accepted);
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getDataList(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "getDataList", resultFrame);
        
        List<ResultData> resultList = responseDataManager.getKeyList(dataFrameMap, resultFrame);
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getDataList(ResultFrame resultFrame, boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getDataList", new Object[]{resultFrame, accepted});
        
        List<ResultData> resultList = responseDataManager.getKeyList(dataFrameMap, resultFrame, accepted);
        
        LOGGER.exiting(CLASS_NAME, "getDataList", resultList);
        return resultList;
    }
    
    public synchronized int countSecondData() {
        LOGGER.entering(CLASS_NAME, "countSecondData");
        
        int count = responseSecondDataManager.size();
        
        LOGGER.exiting(CLASS_NAME, "countSecondData", count);
        return count;
    }
    
    public synchronized int countSecondData(boolean accepted) {
        LOGGER.entering(CLASS_NAME, "countSecondData", accepted);
        
        int count = responseSecondDataManager.size(accepted);
        
        LOGGER.exiting(CLASS_NAME, "countSecondData", count);
        return count;
    }
    
    public synchronized ResultData getSecondData(int index) {
        LOGGER.entering(CLASS_NAME, "getSecondData", index);
        
        ResultData resultData = responseSecondDataManager.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getSecondData", resultData);
        return resultData;
    }
    
    public synchronized ResultData getSecondData(int index, boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getSecondData", new Object[]{index, accepted});
        
        ResultData resultData = responseSecondDataManager.get(index, accepted);
        
        LOGGER.exiting(CLASS_NAME, "getSecondData", resultData);
        return resultData;
    }
    
    public synchronized List<ResultData> getSecondDataList() {
        LOGGER.entering(CLASS_NAME, "getSecondDataList");
        
        List<ResultData> resultList = responseSecondDataManager.cloneList();
        
        LOGGER.exiting(CLASS_NAME, "getSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getSecondDataList(boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getSecondDataList", accepted);
        
        List<ResultData> resultList = responseSecondDataManager.cloneList(accepted);
        
        LOGGER.exiting(CLASS_NAME, "getSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getSecondDataList(Selector<? super ResultData> selector) {
        LOGGER.entering(CLASS_NAME, "getSecondDataList", selector);
        
        List<ResultData> resultList = responseSecondDataManager.collectList(selector);
        
        LOGGER.exiting(CLASS_NAME, "getSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getSecondDataList(Selector<? super ResultData> selector, boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getSecondDataList", new Object[]{selector, accepted});
        
        List<ResultData> resultList = responseSecondDataManager.collectList(selector, accepted);
        
        LOGGER.exiting(CLASS_NAME, "getSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getSecondDataList(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "getSecondDataList", resultFrame);
        
        List<ResultData> resultList = responseSecondDataManager.getKeyList(dataFrameMap, resultFrame);
        
        LOGGER.exiting(CLASS_NAME, "getSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getSecondDataList(ResultFrame resultFrame, boolean accepted) {
        LOGGER.entering(CLASS_NAME, "getSecondDataList", new Object[]{resultFrame, accepted});
        
        List<ResultData> resultList = responseSecondDataManager.getKeyList(dataFrameMap, resultFrame, accepted);
        
        LOGGER.exiting(CLASS_NAME, "getSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized int countRequestData() {
        LOGGER.entering(CLASS_NAME, "countRequestData");
        
        int count = requestDataManager.size();
        
        LOGGER.exiting(CLASS_NAME, "countRequestData", count);
        return count;
    }
    
    public synchronized int countRequestData(boolean success) {
        LOGGER.entering(CLASS_NAME, "countRequestData", success);
        
        int count = requestDataManager.size(success);
        
        LOGGER.exiting(CLASS_NAME, "countRequestData", count);
        return count;
    }
    
    public synchronized ResultData getRequestData(int index) {
        LOGGER.entering(CLASS_NAME, "getRequestData", index);
        
        ResultData resultData = requestDataManager.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getRequestData", resultData);
        return resultData;
    }
    
    public synchronized ResultData getRequestData(int index, boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestData", new Object[]{index, success});
        
        ResultData resultData = requestDataManager.get(index, success);
        
        LOGGER.exiting(CLASS_NAME, "getRequestData", resultData);
        return resultData;
    }
    
    public synchronized List<ResultData> getRequestDataList() {
        LOGGER.entering(CLASS_NAME, "getRequestDataList");
        
        List<ResultData> resultList = requestDataManager.cloneList();
        
        LOGGER.exiting(CLASS_NAME, "getRequestDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestDataList(boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestDataList", success);
        
        List<ResultData> resultList = requestDataManager.cloneList(success);
        
        LOGGER.exiting(CLASS_NAME, "getRequestDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestDataList(Selector<? super ResultData> selector) {
        LOGGER.entering(CLASS_NAME, "getRequestDataList", selector);
        
        List<ResultData> resultList = requestDataManager.collectList(selector);
        
        LOGGER.exiting(CLASS_NAME, "getRequestDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestDataList(Selector<? super ResultData> selector, boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestDataList", new Object[]{selector, success});
        
        List<ResultData> resultList = requestDataManager.collectList(selector, success);
        
        LOGGER.exiting(CLASS_NAME, "getRequestDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestDataList(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "getRequestDataList", resultFrame);
        
        List<ResultData> resultList = requestDataManager.getKeyList(requestDataFrameMap, resultFrame);
        
        LOGGER.exiting(CLASS_NAME, "getRequestDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestDataList(ResultFrame resultFrame, boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestDataList", new Object[]{resultFrame, success});
        
        List<ResultData> resultList = requestDataManager.getKeyList(requestDataFrameMap, resultFrame, success);
        
        LOGGER.exiting(CLASS_NAME, "getRequestDataList", resultList);
        return resultList;
    }
    
    public synchronized int countRequestSecondData() {
        LOGGER.entering(CLASS_NAME, "countRequestSecondData");
        
        int count = requestSecondDataManager.size();
        
        LOGGER.exiting(CLASS_NAME, "countRequestSecondData", count);
        return count;
    }
    
    public synchronized int countRequestSecondData(boolean success) {
        LOGGER.entering(CLASS_NAME, "countRequestSecondData", success);
        
        int count = requestSecondDataManager.size(success);
        
        LOGGER.exiting(CLASS_NAME, "countRequestSecondData", count);
        return count;
    }
    
    public synchronized ResultData getRequestSecondData(int index) {
        LOGGER.entering(CLASS_NAME, "getRequestSecondData", index);
        
        ResultData resultData = requestSecondDataManager.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getRequestSecondData", resultData);
        return resultData;
    }
    
    public synchronized ResultData getRequestSecondData(int index, boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestSecondData", new Object[]{index, success});
        
        ResultData resultData = requestSecondDataManager.get(index, success);
        
        LOGGER.exiting(CLASS_NAME, "getRequestSecondData", resultData);
        return resultData;
    }
    
    public synchronized List<ResultData> getRequestSecondDataList() {
        LOGGER.entering(CLASS_NAME, "getRequestSecondDataList");
        
        List<ResultData> resultList = requestSecondDataManager.cloneList();
        
        LOGGER.exiting(CLASS_NAME, "getRequestSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestSecondDataList(boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestSecondDataList", success);
        
        List<ResultData> resultList = requestSecondDataManager.cloneList(success);
        
        LOGGER.exiting(CLASS_NAME, "getRequestSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestSecondDataList(Selector<? super ResultData> selector) {
        LOGGER.entering(CLASS_NAME, "getRequestSecondDataList", selector);
        
        List<ResultData> resultList = requestSecondDataManager.collectList(selector);
        
        LOGGER.exiting(CLASS_NAME, "getRequestSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestSecondDataList(Selector<? super ResultData> selector, boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestSecondDataList", new Object[]{selector, success});
        
        List<ResultData> resultList = requestSecondDataManager.collectList(selector, success);
        
        LOGGER.exiting(CLASS_NAME, "getRequestSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestSecondDataList(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "getRequestSecondDataList", resultFrame);
        
        List<ResultData> resultList = requestSecondDataManager.getKeyList(requestDataFrameMap, resultFrame);
        
        LOGGER.exiting(CLASS_NAME, "getRequestSecondDataList", resultList);
        return resultList;
    }
    
    public synchronized List<ResultData> getRequestSecondDataList(ResultFrame resultFrame, boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestSecondDataList", new Object[]{resultFrame, success});
        
        List<ResultData> resultList = requestSecondDataManager.getKeyList(requestDataFrameMap, resultFrame, success);
        
        LOGGER.exiting(CLASS_NAME, "getRequestSecondDataList", resultList);
        return resultList;
    }
    
    private static <K, V> List<K> getKeyList(final HashMap<K, V> keyValueMap, final V value, List<K> keyList) {
        LOGGER.entering(CLASS_NAME, "getKeyList", new Object[]{keyValueMap, value, keyList});
        
        Selector<K> selector = new Selector<K>() {
            @Override
            public boolean match(K key) {
                return value.equals(keyValueMap.get(key));
            }
        };
        
        List<K> resultList = new Collector<K>(selector).collect(keyList);
        
        LOGGER.exiting(CLASS_NAME, "getKeyList", resultList);
        return resultList;
    }
}
