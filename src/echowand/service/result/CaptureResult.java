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
    
    private LinkedList<ResultFrame> frames;
    private LinkedList<ResultFrame> sentFrames;
    private LinkedList<ResultFrame> receivedFrames;
    private boolean done;
    
    public CaptureResult(CaptureResultObserver observer) {
        LOGGER.entering(CLASS_NAME, "CaptureResult", new Object[]{observer});
        
        this.observer = observer;
        
        frames = new LinkedList<ResultFrame>();
        sentFrames = new LinkedList<ResultFrame>();
        receivedFrames = new LinkedList<ResultFrame>();
        
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
    
    public boolean addSentFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "addSentFrame", frame);
        
        boolean result = true;
        ResultFrame resultFrame = getResultFrame(frame);
        
        result &= frames.add(resultFrame);
        result &= sentFrames.add(resultFrame);
        
        LOGGER.exiting(CLASS_NAME, "addSentFrame", result);
        return result;
    }
    
    public boolean addReceivedFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "addReceivedFrame", frame);
        
        boolean result = true;
        ResultFrame resultFrame = getResultFrame(frame);
        
        result &= frames.add(resultFrame);
        result &= receivedFrames.add(resultFrame);
        
        LOGGER.exiting(CLASS_NAME, "addReceivedFrame", result);
        return result;
    }
    
    private ResultFrame getResultFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "getResultFrame", frame);
        
        long time = System.currentTimeMillis();
        ResultFrame resultFrame = new ResultFrame(frame, time);
        
        LOGGER.exiting(CLASS_NAME, "getResultFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized int countFrames() {
        LOGGER.entering(CLASS_NAME, "countFrames");
        
        int count = frames.size();
        
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }
    
    public synchronized int countSentFrames() {
        LOGGER.entering(CLASS_NAME, "countSentFrames");
        
        int count = sentFrames.size();
        
        LOGGER.exiting(CLASS_NAME, "countSentFrames", count);
        return count;
    }
    
    public synchronized int countReceivedFrames() {
        LOGGER.entering(CLASS_NAME, "countReceivedFrames");
        
        int count = receivedFrames.size();
        
        LOGGER.exiting(CLASS_NAME, "countReceivedFrames", count);
        return count;
    }
    
    public synchronized Frame getFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getFrame", index);
        
        Frame frame = frames.get(index).frame;
        
        LOGGER.exiting(CLASS_NAME, "getFrame", frame);
        return frame;
    }
    
    public synchronized Frame getSentFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getSentFrame", index);
        
        Frame frame = sentFrames.get(index).frame;
        
        LOGGER.exiting(CLASS_NAME, "getSentFrame", frame);
        return frame;
    }
    
    public synchronized Frame getReceivedFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getReceivedFrame", index);
        
        Frame frame = receivedFrames.get(index).frame;
        
        LOGGER.exiting(CLASS_NAME, "getReceivedFrame", frame);
        return frame;
    }
    
    public synchronized ResultFrame getResultFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getResultFrame", index);
        
        ResultFrame resultFrame = frames.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getResultFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized ResultFrame getSentResultFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getSentResultFrame", index);
        
        ResultFrame resultFrame = sentFrames.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getSentResultFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized ResultFrame getReceivedResultFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getReceivedResultFrame", index);
        
        ResultFrame resultFrame = receivedFrames.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getReceivedResultFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized List<ResultFrame> getResultFrameList() {
        LOGGER.entering(CLASS_NAME, "getResultFrameList");
        
        LinkedList<ResultFrame> list = new LinkedList<ResultFrame>(frames);
        
        LOGGER.exiting(CLASS_NAME, "getResultFrameList", list);
        return list;
    }
    
    public synchronized List<ResultFrame> getSentResultFrameList() {
        LOGGER.entering(CLASS_NAME, "getSentResultFrameList");
        
        LinkedList<ResultFrame> list = new LinkedList<ResultFrame>(sentFrames);
        
        LOGGER.exiting(CLASS_NAME, "getSentResultFrameList", list);
        return list;
    }
    
    public synchronized List<ResultFrame> getReceivedResultFrameList() {
        LOGGER.entering(CLASS_NAME, "getReceivedResultFrameList");
        
        LinkedList<ResultFrame> list = new LinkedList<ResultFrame>(receivedFrames);
        
        LOGGER.exiting(CLASS_NAME, "getReceivedResultFrameList", list);
        return list;
    }
    
    public synchronized void removeResultFrames(Collection<ResultFrame> removeFrames) {
        sentFrames.removeAll(removeFrames);
        receivedFrames.removeAll(removeFrames);
        frames.removeAll(removeFrames);
    }
    
    public synchronized int removeSentFrames(int size) {
        LOGGER.entering(CLASS_NAME, "removeSentFrameList", size);
        
        if (sentFrames.size() < size) {
            size = sentFrames.size();
        }
        
        removeResultFrames(new ArrayList<ResultFrame>(sentFrames.subList(0, size)));
        
        LOGGER.exiting(CLASS_NAME, "removeSentFrameList", size);
        return size;
    }
    
    public synchronized int removeAllSentFrames() {
        LOGGER.entering(CLASS_NAME, "removeAllSentFrameList");
        
        int size = sentFrames.size();
        removeResultFrames(new ArrayList<ResultFrame>(sentFrames));
        
        LOGGER.exiting(CLASS_NAME, "removeAllSentFrameList", size);
        return size;
    }
    
    public synchronized int removeReceivedFrames(int size) {
        LOGGER.entering(CLASS_NAME, "removeReceivedFrameList", size);
        
        if (receivedFrames.size() < size) {
            size = receivedFrames.size();
        }
        
        removeResultFrames(new ArrayList<ResultFrame>(receivedFrames.subList(0, size)));
        
        LOGGER.exiting(CLASS_NAME, "removeReceivedFrameList", size);
        return size;
    }
    
    public synchronized int removeAllReceivedFrames() {
        LOGGER.entering(CLASS_NAME, "removeAllReceivedFrameList");
        
        int size = receivedFrames.size();
        removeResultFrames(new ArrayList<ResultFrame>(receivedFrames));
        
        LOGGER.exiting(CLASS_NAME, "removeAllReceivedFrameList", size);
        return size;
    }
    
    public synchronized int removeFrames(int size) {
        LOGGER.entering(CLASS_NAME, "removeFrameList", size);
        
        if (frames.size() < size) {
            size = frames.size();
        }
        
        removeResultFrames(new ArrayList<ResultFrame>(frames.subList(0, size)));
        
        LOGGER.exiting(CLASS_NAME, "removeFrameList", size);
        return size;
    }
    
    public synchronized int removeAllFrames() {
        LOGGER.entering(CLASS_NAME, "removeAllFrameList");
        
        int size = frames.size();
        removeResultFrames(new ArrayList<ResultFrame>(frames));
        
        LOGGER.exiting(CLASS_NAME, "removeAllFrameList", size);
        return size;
    }
}
