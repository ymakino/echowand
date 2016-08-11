package echowand.logic;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * リクエストフレームを受け取り、登録された全てのRequestProcessorの適切なメソッドを呼び出す。
 * @author Yoshiki Makino
 */
public class RequestDispatcher implements Listener {
    private static final Logger logger = Logger.getLogger(RequestDispatcher.class.getName());
    private static final String className = RequestDispatcher.class.getName();
    
    private LinkedList<RequestProcessor> processors;
    
    /**
     * RequestDispatcherを生成する。
     */
    public RequestDispatcher() {
        logger.entering(className, "RequestDispatcher");
        
        processors = new LinkedList<RequestProcessor>();
        
        logger.exiting(className, "RequestDispatcher");
    }
    
    private synchronized LinkedList<RequestProcessor> cloneProcessors() {
        return new LinkedList<RequestProcessor>(processors);
    }
    
    /**
     * 指定されたRequestProcessorがリクエスト処理を行なうように登録する。
     * @param processor リクエスト処理を実行するRequestProcessor
     */
    public synchronized void addRequestProcessor(RequestProcessor processor) {
        logger.entering(className, "addRequestProcessor", processor);
        
        processors.add(processor);
        
        logger.exiting(className, "addRequestProcessor");
    }
    
    /**
     * 指定されたRequestProcessorがリクエスト処理を行なうように登録する。
     * @param index 登録するRequestProcessorのインデックス
     * @param processor リクエスト処理を実行するRequestProcessor
     */
    public synchronized void addRequestProcessor(int index, RequestProcessor processor) {
        logger.entering(className, "addRequestProcessor", processor);
        
        processors.add(index, processor);
        
        logger.exiting(className, "addRequestProcessor");
    }
    
    /**
     * 指定されたRequestProcessorがリクエスト処理を行なわないように登録を抹消する。
     * @param processor リクエスト処理を停止するRequestProcessor
     */
    public synchronized void removeRequestProcessor(RequestProcessor processor) {
        logger.entering(className, "removeRequestProcessor", processor);
        
        processors.remove(processor);
        
        logger.exiting(className, "removeRequestProcessor");
    }
    
    /**
     * 登録されているRequestProcessorの数を返す。
     * @return 登録されているRequestProcessorの数
     */
    public synchronized int countRequestProcessors() {
        logger.entering(className, "countRequestProcessor");
        
        int count = processors.size();
        
        logger.exiting(className, "countRequestProcessor", count);
        return count;
    }
    
    /**
     * index番目に登録されているRequestProcessorを返す。
     * @param index RequestProcessorのインデックス
     * @return 指定されたRequestProcessor
     */
    public synchronized RequestProcessor getRequestProcessor(int index) {
        logger.entering(className, "getRequestProcessor", index);
        
        RequestProcessor processor = processors.get(index);
        
        logger.exiting(className, "countRequestProcessor", processor);
        return processor;
    }
    
    /**
     * フレームの種類を判別して、登録されたRequestProcessorの適切なメソッドを呼び出す。
     * すでに処理済みであれば何も行なわない。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean process(Subnet subnet, Frame frame, boolean processed) {
        logger.entering(className, "process", new Object[]{subnet, frame, processed});
        
        if (processed) {
            logger.exiting(className, "process", false);
            return false;
        }
        
        if (!frame.getCommonFrame().isStandardPayload()) {
            logger.exiting(className, "process", false);
            return false;
        }
        
        boolean success = false;
        CommonFrame cf = frame.getCommonFrame();
        StandardPayload payload = cf.getEDATA(StandardPayload.class);
        
        if (payload == null) {
            logger.exiting(className, "process", false);
            return false;
        }
        
        switch (payload.getESV()) {
            case SetI:
                success = this.processSetI(subnet, frame);
                break;
            case SetC:
                success = this.processSetC(subnet, frame);
                break;
            case Get:
                success = this.processGet(subnet, frame);
                break;
            case SetGet:
                success = this.processSetGet(subnet, frame);
                break;
            case INF_REQ:
                success = this.processINF_REQ(subnet, frame);
                break;
            case INF:
                success = this.processINF(subnet, frame);
                break;
            case INFC:
                success = this.processINFC(subnet, frame);
                break;
        }
        
        logger.exiting(className, "process", success);
        return success;
    }

    /**
     * 登録されたRequestProcessorのprocessSetIを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processSetI(Subnet subnet, Frame frame) {
        logger.entering(className, "processSetI", new Object[]{subnet, frame});
        
        boolean processed = false;
        for (RequestProcessor processor : cloneProcessors()) {
            processed |= processor.processSetI(subnet, frame, processed);
        }
        
        logger.exiting(className, "processSetI", processed);
        return processed;
    }

    /**
     * 登録されたRequestProcessorのprocessSetCを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processSetC(Subnet subnet, Frame frame) {
        logger.entering(className, "processSetC", new Object[]{subnet, frame});
        
        boolean processed = false;
        for (RequestProcessor processor : cloneProcessors()) {
            processed |= processor.processSetC(subnet, frame, processed);
        }
        
        logger.exiting(className, "processSetC", processed);
        return processed;
    }
    
    /**
     * 登録されたRequestProcessorのprocessGetを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processGet(Subnet subnet, Frame frame) {
        logger.entering(className, "processGet", new Object[]{subnet, frame});
        
        boolean processed = false;
        for (RequestProcessor processor : cloneProcessors()) {
            processed |= processor.processGet(subnet, frame, processed);
        }
        
        logger.exiting(className, "processGet", processed);
        return processed;
    }
    
    /**
     * 登録されたRequestProcessorのprocessSetGetを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processSetGet(Subnet subnet, Frame frame) {
        logger.entering(className, "processSetGet", new Object[]{subnet, frame});
        
        boolean processed = false;
        for (RequestProcessor processor : cloneProcessors()) {
            processed |= processor.processSetGet(subnet, frame, processed);
        }
        
        logger.exiting(className, "processSetGet", processed);
        return processed;
    }
    
    /**
     * 登録されたRequestProcessorのprocessINF_REQを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processINF_REQ(Subnet subnet, Frame frame) {
        logger.entering(className, "processINF_REQ", new Object[]{subnet, frame});
        
        boolean processed = false;
        for (RequestProcessor processor : cloneProcessors()) {
            processed |= processor.processINF_REQ(subnet, frame, processed);
        }
        
        logger.exiting(className, "processINF_REQ", processed);
        return processed;
    }
    
    /**
     * 登録されたRequestProcessorのprocessINFを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processINF(Subnet subnet, Frame frame) {
        logger.entering(className, "processINF", new Object[]{subnet, frame});
        
        boolean processed = false;
        for (RequestProcessor processor : cloneProcessors()) {
            processed |= processor.processINF(subnet, frame, processed);
        }
        
        logger.exiting(className, "processINF", processed);
        return processed;
    }
    
    /**
     * 登録されたRequestProcessorのprocessINFCを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processINFC(Subnet subnet, Frame frame) {
        logger.entering(className, "processINFC", new Object[]{subnet, frame});
        
        boolean processed = false;
        for (RequestProcessor processor : cloneProcessors()) {
            processed |= processor.processINFC(subnet, frame, processed);
        }
        
        logger.exiting(className, "processINFC", processed);
        return processed;
    }
}
