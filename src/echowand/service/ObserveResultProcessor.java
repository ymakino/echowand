package echowand.service;

import echowand.service.result.ObserveResult;
import echowand.logic.DefaultRequestProcessor;
import echowand.net.Frame;
import echowand.net.Subnet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ObserveResultProcessor extends DefaultRequestProcessor {
    private static final Logger LOGGER = Logger.getLogger(ObserveResultProcessor.class.getName());
    private static final String CLASS_NAME = ObserveResultProcessor.class.getName();
    
    private LinkedList<ObserveResult> observeResults;
    
    public ObserveResultProcessor() {
        LOGGER.entering(CLASS_NAME, "ObserveResultProcessor");
        
        observeResults = new LinkedList<ObserveResult>();
        
        LOGGER.exiting(CLASS_NAME, "ObserveResultProcessor");
    }
    
    public synchronized boolean addResultObserve(ObserveResult observeResult) {
        LOGGER.entering(CLASS_NAME, "addResultObserve", observeResult);
        
        boolean result = observeResults.add(observeResult);
        
        LOGGER.exiting(CLASS_NAME, "addResultObserve", result);
        return result;
    }
    
    public synchronized boolean removeResultObserve(ObserveResult observeResult) {
        LOGGER.entering(CLASS_NAME, "removeResultObserve", observeResult);
        
        boolean result = observeResults.remove(observeResult);
        
        LOGGER.exiting(CLASS_NAME, "removeResultObserve", result);
        return result;
    }
    
    private synchronized List<ObserveResult> cloneResultNotifies() {
        return new ArrayList<ObserveResult>(observeResults);
    }
    
    /**
     * ESVがINFであるフレームの処理を行う。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 処理に成功した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean processINF(Subnet subnet, Frame frame, boolean processed) {
        LOGGER.entering(CLASS_NAME, "processINF", new Object[]{subnet, frame, processed});
        
        boolean result = false;
        for (ObserveResult observeResult: cloneResultNotifies()) {
            if (observeResult.shouldReceive(frame)) {
                result |= observeResult.addFrame(frame);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "processINF", result);
        return result;
    }

    /**
     * ESVがINFCであるフレームの処理を行う。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 処理に成功した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean processINFC(Subnet subnet, Frame frame, boolean processed) {
        LOGGER.entering(CLASS_NAME, "processINFC", new Object[]{subnet, frame, processed});
        
        boolean result = false;
        for (ObserveResult observeResult: cloneResultNotifies()) {
            if (observeResult.shouldReceive(frame)) {
                result |= observeResult.addFrame(frame);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "processINFC", result);
        return result;
    }
}
