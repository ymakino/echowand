package echowand.service;

import echowand.net.Frame;
import echowand.service.result.CaptureResult;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class CaptureResultObserver implements CaptureSubnetObserver {
    private static final Logger LOGGER = Logger.getLogger(CaptureResultObserver.class.getName());
    private static final String CLASS_NAME = CaptureResultObserver.class.getName();
    
    private LinkedList<CaptureResult> captureResults;
    
    public CaptureResultObserver() {
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
    public void notifySent(Frame frame, boolean success) {
        LOGGER.entering(CLASS_NAME, "notifySent", new Object[]{frame, success});
        
        if (success) {
            for (CaptureResult captureResult : captureResults) {
                captureResult.addSentFrame(frame);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "notifySent");
    }

    @Override
    public void notifyReceived(Frame frame) {
        LOGGER.entering(CLASS_NAME, "notifyReceived", new Object[]{frame});
        
        for (CaptureResult captureResult : captureResults) {
            captureResult.addReceivedFrame(frame);
        }
        
        LOGGER.exiting(CLASS_NAME, "notifyReceived");
    }
}
