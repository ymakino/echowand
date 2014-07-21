package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.Property;
import echowand.net.StandardPayload;
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
    
    private LinkedList<Frame> frames;
    private LinkedList<Frame> errorFrames;
    private LinkedList<Frame> invalidFrames;
    private LinkedList<ResultData> dataList;
    private LinkedList<ResultData> errorDataList;
    
    public ResultBase() {
        LOGGER.entering(CLASS_NAME, "ResultBase");
        
        done = false;
        frames = new LinkedList<Frame>();
        errorFrames = new LinkedList<Frame>();
        invalidFrames = new LinkedList<Frame>();
        dataList = new LinkedList<ResultData>();
        errorDataList = new LinkedList<ResultData>();
        
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
        
        long time = System.currentTimeMillis();
        
        if (!hasStandardPayload(frame)) {
            invalidFrames.add(frame);
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }

        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        
        if (!isValidPayload(payload)) {
            invalidFrames.add(frame);
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }
        
        int count = payload.getFirstOPC();
        for (int i=0; i<count; i++) {
            Property property = payload.getFirstPropertyAt(i);
            
            Node node = frame.getSender();
            EOJ eoj = payload.getSEOJ();
            EPC epc = property.getEPC();
            Data data = property.getEDT();
            
            if (isValidProperty(property)) {
                dataList.add(new ResultData(node, eoj, epc, data, time));
            } else {
                errorDataList.add(new ResultData(node, eoj, epc, data, time));
            }
        }
        
        if (!isSuccessPayload(payload)) {
            errorFrames.add(frame);
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
