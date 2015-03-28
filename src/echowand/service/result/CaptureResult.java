package echowand.service.result;

import echowand.net.Frame;
import echowand.service.CaptureResultObserver;
import java.util.ArrayList;
import java.util.Collection;
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
    
    private CaptureResultObserver observer;
    
    private LinkedList<ResultFrame> frameList;
    private LinkedList<ResultFrame> sentFrameList;
    private LinkedList<ResultFrame> receivedFrameList;
    private boolean done;
    
    public CaptureResult(CaptureResultObserver observer) {
        LOGGER.entering(CLASS_NAME, "CaptureResult", new Object[]{observer});
        
        this.observer = observer;
        
        frameList = new LinkedList<ResultFrame>();
        sentFrameList = new LinkedList<ResultFrame>();
        receivedFrameList = new LinkedList<ResultFrame>();
        
        done = false;
        
        LOGGER.exiting(CLASS_NAME, "CaptureResult");
    }
    
    public synchronized void stopCapture() {
        LOGGER.entering(CLASS_NAME, "stopCapture");
        
        if (!done) {
            observer.removeCaptureResult(this);
            done = true;
        }
        
        LOGGER.exiting(CLASS_NAME, "stopCapture");
    }
    
    public synchronized boolean isDone() {
        LOGGER.entering(CLASS_NAME, "isDone");
        
        LOGGER.exiting(CLASS_NAME, "isDone", done);
        return done;
    }
    
    public synchronized boolean addSentFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "addSentFrame", frame);
        
        boolean result = addSentFrame(createResultFrame(frame));
        
        LOGGER.exiting(CLASS_NAME, "addSentFrame", result);
        return result;
    }
    
    public synchronized boolean addSentFrame(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "addSentFrame", resultFrame);
        
        if (isDone()) {
            LOGGER.exiting(CLASS_NAME, "addSentFrame", false);
            return false;
        }
        
        if (sentFrameList.contains(resultFrame)) {
            LOGGER.exiting(CLASS_NAME, "addSentFrame", false);
            return false;
        }
        
        boolean result = true;
        
        result &= frameList.add(resultFrame);
        result &= sentFrameList.add(resultFrame);
        
        LOGGER.exiting(CLASS_NAME, "addSentFrame", result);
        return result;
    }
    
    public synchronized boolean addReceivedFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "addReceivedFrame", frame);
        
        boolean result = addReceivedFrame(createResultFrame(frame));
        
        LOGGER.exiting(CLASS_NAME, "addReceivedFrame", result);
        return result;
    }
    
    public synchronized boolean addReceivedFrame(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "addReceivedFrame", resultFrame);
        
        if (isDone()) {
            LOGGER.exiting(CLASS_NAME, "addReceivedFrame", false);
            return false;
        }
        
        if (receivedFrameList.contains(resultFrame)) {
            LOGGER.exiting(CLASS_NAME, "addReceivedFrame", false);
            return false;
        }
        
        boolean result = true;
        
        result &= frameList.add(resultFrame);
        result &= receivedFrameList.add(resultFrame);
        
        LOGGER.exiting(CLASS_NAME, "addReceivedFrame", result);
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
    
    public synchronized int countSentFrames() {
        LOGGER.entering(CLASS_NAME, "countSentFrames");
        
        int count = sentFrameList.size();
        
        LOGGER.exiting(CLASS_NAME, "countSentFrames", count);
        return count;
    }
    
    public synchronized int countReceivedFrames() {
        LOGGER.entering(CLASS_NAME, "countReceivedFrames");
        
        int count = receivedFrameList.size();
        
        LOGGER.exiting(CLASS_NAME, "countReceivedFrames", count);
        return count;
    }
    
    public synchronized ResultFrame getFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getFrame", index);
        
        ResultFrame resultFrame = frameList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized ResultFrame getSentFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getSentFrame", index);
        
        ResultFrame resultFrame = sentFrameList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getSentFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized ResultFrame getReceivedFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getReceivedFrame", index);
        
        ResultFrame resultFrame = receivedFrameList.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getReceivedFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized List<ResultFrame> getFrameList() {
        LOGGER.entering(CLASS_NAME, "getFrameList");
        
        LinkedList<ResultFrame> list = new LinkedList<ResultFrame>(frameList);
        
        LOGGER.exiting(CLASS_NAME, "getFrameList", list);
        return list;
    }
    
    public synchronized List<ResultFrame> getSentFrameList() {
        LOGGER.entering(CLASS_NAME, "getSentFrameList");
        
        LinkedList<ResultFrame> list = new LinkedList<ResultFrame>(sentFrameList);
        
        LOGGER.exiting(CLASS_NAME, "getSentFrameList", list);
        return list;
    }
    
    public synchronized List<ResultFrame> getReceivedFrameList() {
        LOGGER.entering(CLASS_NAME, "getReceivedFrameList");
        
        LinkedList<ResultFrame> list = new LinkedList<ResultFrame>(receivedFrameList);
        
        LOGGER.exiting(CLASS_NAME, "getReceivedFrameList", list);
        return list;
    }
    
    public synchronized void removeFrames(Collection<ResultFrame> removeFrames) {
        LOGGER.entering(CLASS_NAME, "removeFrames", removeFrames);
        
        sentFrameList.removeAll(removeFrames);
        receivedFrameList.removeAll(removeFrames);
        frameList.removeAll(removeFrames);
        
        LOGGER.exiting(CLASS_NAME, "removeFrames");
    }
    
    public synchronized boolean removeFrame(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "removeFrame", resultFrame);
        
        boolean result = false;
        if (frameList.contains(resultFrame)) {
            sentFrameList.remove(resultFrame);
            receivedFrameList.remove(resultFrame);
            frameList.remove(resultFrame);
            result = true;
        }
        
        LOGGER.exiting(CLASS_NAME, "removeFrame", result);
        return result;
    }
    
    public synchronized void removeFrame(int index) {
        LOGGER.entering(CLASS_NAME, "removeFrame", index);
        
        ResultFrame resultFrame = frameList.get(index);
        
        sentFrameList.remove(resultFrame);
        receivedFrameList.remove(resultFrame);
        frameList.remove(resultFrame);
        
        LOGGER.exiting(CLASS_NAME, "removeFrame");
    }
    
    public synchronized void removeSentFrame(int index) {
        LOGGER.entering(CLASS_NAME, "removeSentFrame", index);
        
        ResultFrame frame = sentFrameList.get(index);
        
        sentFrameList.remove(frame);
        frameList.remove(frame);
        
        LOGGER.exiting(CLASS_NAME, "removeSentFrame");
    }
    
    public synchronized void removeReceivedFrame(int index) {
        LOGGER.entering(CLASS_NAME, "removeReceivedFrame", index);
        
        ResultFrame frame = receivedFrameList.get(index);
        
        receivedFrameList.remove(frame);
        frameList.remove(frame);
        
        LOGGER.exiting(CLASS_NAME, "removeReceivedFrame");
    }
    
    public synchronized int truncateSentFrames(int size) {
        LOGGER.entering(CLASS_NAME, "truncateSentFrames", size);
        
        if (sentFrameList.size() < size) {
            size = sentFrameList.size();
        }
        
        removeFrames(new ArrayList<ResultFrame>(sentFrameList.subList(0, size)));
        
        LOGGER.exiting(CLASS_NAME, "truncateSentFrames", size);
        return size;
    }
    
    public synchronized int removeAllSentFrames() {
        LOGGER.entering(CLASS_NAME, "removeAllSentFrames");
        
        int size = sentFrameList.size();
        removeFrames(new ArrayList<ResultFrame>(sentFrameList));
        
        LOGGER.exiting(CLASS_NAME, "removeAllSentFrames", size);
        return size;
    }
    
    public synchronized int truncateReceivedFrames(int size) {
        LOGGER.entering(CLASS_NAME, "truncateReceivedFrames", size);
        
        if (receivedFrameList.size() < size) {
            size = receivedFrameList.size();
        }
        
        removeFrames(new ArrayList<ResultFrame>(receivedFrameList.subList(0, size)));
        
        LOGGER.exiting(CLASS_NAME, "truncateReceivedFrames", size);
        return size;
    }
    
    public synchronized int removeAllReceivedFrames() {
        LOGGER.entering(CLASS_NAME, "removeAllReceivedFrames");
        
        int size = receivedFrameList.size();
        removeFrames(new ArrayList<ResultFrame>(receivedFrameList));
        
        LOGGER.exiting(CLASS_NAME, "removeAllReceivedFrames", size);
        return size;
    }
    
    public synchronized int truncateFrames(int size) {
        LOGGER.entering(CLASS_NAME, "truncateFrames", size);
        
        if (frameList.size() < size) {
            size = frameList.size();
        }
        
        removeFrames(new ArrayList<ResultFrame>(frameList.subList(0, size)));
        
        LOGGER.exiting(CLASS_NAME, "truncateFrames", size);
        return size;
    }
    
    public synchronized int removeAllFrames() {
        LOGGER.entering(CLASS_NAME, "removeAllFrames");
        
        int size = frameList.size();
        removeFrames(new ArrayList<ResultFrame>(frameList));
        
        LOGGER.exiting(CLASS_NAME, "removeAllFrames", size);
        return size;
    }
}
