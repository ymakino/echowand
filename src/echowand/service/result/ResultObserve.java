package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.service.ResultObserveProcessor;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultObserve {
    private static final Logger LOGGER = Logger.getLogger(ResultObserve.class.getName());
    private static final String CLASS_NAME = ResultObserve.class.getName();
    
    private FrameMatcher matcher;
    private ResultObserveProcessor processor;
    private LinkedList<ResultData> dataList;
    private LinkedList<Frame> frames;
    private boolean done;
    
    public ResultObserve(FrameMatcher matcher, ResultObserveProcessor processor) {
        LOGGER.entering(CLASS_NAME, "ResultObserve", new Object[]{matcher, processor});
        
        this.matcher = matcher;
        this.processor = processor;
        this.dataList = new LinkedList<ResultData>();
        this.frames = new LinkedList<Frame>();
        done = false;
        
        LOGGER.exiting(CLASS_NAME, "ResultObserve");
    }
    
    public synchronized void done() {
        LOGGER.entering(CLASS_NAME, "done");
        
        if (!done) {
            processor.removeResultObserve(this);
            done = true;
        }
        
        LOGGER.exiting(CLASS_NAME, "done");
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
        
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        
        int count = payload.getFirstOPC();
        for (int i=0; i<count; i++) {
            Property property = payload.getFirstPropertyAt(i);
            
            Node node = frame.getSender();
            EOJ eoj = payload.getSEOJ();
            EPC epc = property.getEPC();
            Data data = property.getEDT();
            
            dataList.add(new ResultData(node, eoj, epc, data));
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
