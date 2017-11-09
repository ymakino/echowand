package echowand.service.result;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Node;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultFrame {
    private static final Logger LOGGER = Logger.getLogger(ResultFrame.class.getName());
    private static final String CLASS_NAME = ResultFrame.class.getName();
    
    private final Frame frame;
    private final long timestamp;
    
    public ResultFrame(Frame frame, long timestamp) {
        LOGGER.entering(CLASS_NAME, "ResultFrame", new Object[]{frame, timestamp});
        
        this.frame = frame;
        this.timestamp = timestamp;
        
        LOGGER.exiting(CLASS_NAME, "ResultFrame");
    }
    
    public CommonFrame getCommonFrame() {
        return frame.getCommonFrame();
    }
    
    public Node getReceiver() {
        return frame.getReceiver();
    }
    
    public Node getSender() {
        return frame.getSender();
    }
    
    public Frame getActualFrame() {
        return frame;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "{Frame: " + frame + ", Time: " + timestamp + "}";
    }
}
