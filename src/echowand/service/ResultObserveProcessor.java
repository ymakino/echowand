package echowand.service;

import echowand.service.result.ResultObserve;
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
public class ResultObserveProcessor extends DefaultRequestProcessor {
    private static final Logger LOGGER = Logger.getLogger(ResultObserveProcessor.class.getName());
    private static final String CLASS_NAME = ResultObserveProcessor.class.getName();
    
    private LinkedList<ResultObserve> resultObserves;
    
    public ResultObserveProcessor() {
        LOGGER.entering(CLASS_NAME, "ResultObserveProcessor");
        
        resultObserves = new LinkedList<ResultObserve>();
        
        LOGGER.exiting(CLASS_NAME, "ResultObserveProcessor");
    }
    
    public synchronized boolean addResultObserve(ResultObserve resultObserve) {
        LOGGER.entering(CLASS_NAME, "addResultObserve", resultObserve);
        
        boolean result = resultObserves.add(resultObserve);
        
        LOGGER.exiting(CLASS_NAME, "addResultObserve", result);
        return result;
    }
    
    public synchronized boolean removeResultObserve(ResultObserve resultObserve) {
        LOGGER.entering(CLASS_NAME, "removeResultObserve", resultObserve);
        
        boolean result = resultObserves.remove(resultObserve);
        
        LOGGER.exiting(CLASS_NAME, "removeResultObserve", result);
        return result;
    }
    
    private synchronized List<ResultObserve> cloneResultNotifies() {
        return new ArrayList<ResultObserve>(resultObserves);
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
        for (ResultObserve resultObserve: cloneResultNotifies()) {
            if (resultObserve.shouldReceive(frame)) {
                result |= resultObserve.addFrame(frame);
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
        for (ResultObserve resultObserve: cloneResultNotifies()) {
            if (resultObserve.shouldReceive(frame)) {
                result |= resultObserve.addFrame(frame);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "processINFC", result);
        return result;
    }
}
