package echowand.service;

import echowand.logic.Listener;
import echowand.net.Frame;
import echowand.net.Subnet;
import echowand.service.result.CaptureResult;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class CaptureResultListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger(CaptureResultListener.class.getName());
    private static final String CLASS_NAME = CaptureResultListener.class.getName();
    
    private LinkedList<CaptureResult> captureResults;
    
    public CaptureResultListener() {
        captureResults = new LinkedList<CaptureResult>();
    }
    
    public synchronized boolean addCaptureResult(CaptureResult captureResult) {
        LOGGER.entering(CLASS_NAME, "addCaptureResult", captureResult);
        
        boolean result = captureResults.add(captureResult);
        
        LOGGER.exiting(CLASS_NAME, "addCaptureResult", result);
        return result;
    }
    
    public synchronized boolean removeCaptureResult(CaptureResult captureResult) {
        LOGGER.entering(CLASS_NAME, "removeCaptureResult", captureResult);
        
        boolean result = captureResults.remove(captureResult);
        
        LOGGER.exiting(CLASS_NAME, "removeCaptureResult", result);
        return result;
    }

    @Override
    public synchronized boolean process(Subnet subnet, Frame frame, boolean processed) {
        LOGGER.entering(CLASS_NAME, "process", new Object[]{subnet, frame, processed});
        
        for (CaptureResult captureResult : captureResults) {
            captureResult.addFrame(frame);
        }
        
        LOGGER.exiting(CLASS_NAME, "process", false);
        return false;
    }
}
