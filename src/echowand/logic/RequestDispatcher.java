package echowand.logic;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * リクエストフレームを受け取り、登録された全てのRequestProcessorの適切なメソッドを呼び出す。
 * @author Yoshiki Makino
 */
public class RequestDispatcher implements Listener {
    private LinkedList<RequestProcessor> processors;
    
    /**
     * RequestDispatcherを生成する。
     */
    public RequestDispatcher() {
        processors = new LinkedList<RequestProcessor>();
    }
    
    /**
     * 指定されたRequestProcessorがリクエスト処理を行なうように登録する。
     * @param processor リクエスト処理を実行するRequestProcessor
     */
    public void addRequestProcessor(RequestProcessor processor) {
        processors.add(processor);
    }
    
    /**
     * 指定されたRequestProcessorがリクエスト処理を行なわないように登録を抹消する。
     * @param processor リクエスト処理を停止するRequestProcessor
     */
    public void removeRequestProcessor(RequestProcessor processor) {
        processors.remove(processor);
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
        if (processed) {
            return false;
        }
        
        if (!frame.getCommonFrame().isStandardPayload()) {
            return false;
        }
        
        CommonFrame cf = frame.getCommonFrame();
        StandardPayload payload = (StandardPayload) cf.getEDATA();
        switch (payload.getESV()) {
            case SetI:
                return this.processSetI(subnet, frame);
            case SetC:
                return this.processSetC(subnet, frame);
            case Get:
                return this.processGet(subnet, frame);
            case SetGet:
                return this.processSetGet(subnet, frame);
            case INF_REQ:
                return this.processINF_REQ(subnet, frame);
            case INF:
                return this.processINF(subnet, frame);
            case INFC:
                return this.processINFC(subnet, frame);
            default:
                return false;
        }
    }
    
    /**
     * 登録されたRequestProcessorのprocessSetIを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processSetI(Subnet subnet, Frame frame) {
        boolean processed = false;
        for (RequestProcessor processor : new ArrayList<RequestProcessor>(processors)) {
            processed |= processor.processSetI(subnet, frame, processed);
        }
        return processed;
    }

    /**
     * 登録されたRequestProcessorのprocessSetCを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processSetC(Subnet subnet, Frame frame) {
        boolean processed = false;
        for (RequestProcessor processor : new ArrayList<RequestProcessor>(processors)) {
            processed |= processor.processSetC(subnet, frame, processed);
        }
        return processed;
    }
    
    /**
     * 登録されたRequestProcessorのprocessGetを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processGet(Subnet subnet, Frame frame) {
        boolean processed = false;
        for (RequestProcessor processor : new ArrayList<RequestProcessor>(processors)) {
            processed |= processor.processGet(subnet, frame, processed);
        }
        return processed;
    }
    
    /**
     * 登録されたRequestProcessorのprocessSetGetを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processSetGet(Subnet subnet, Frame frame) {
        boolean processed = false;
        for (RequestProcessor processor : new ArrayList<RequestProcessor>(processors)) {
            processed |= processor.processSetGet(subnet, frame, processed);
        }
        return processed;
    }
    
    /**
     * 登録されたRequestProcessorのprocessINF_REQを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processINF_REQ(Subnet subnet, Frame frame) {
        boolean processed = false;
        for (RequestProcessor processor : new ArrayList<RequestProcessor>(processors)) {
            processed |= processor.processINF_REQ(subnet, frame, processed);
        }
        return processed;
    }
    
    /**
     * 登録されたRequestProcessorのprocessINFを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processINF(Subnet subnet, Frame frame) {
        boolean processed = false;
        for (RequestProcessor processor : new ArrayList<RequestProcessor>(processors)) {
            processed |= processor.processINF(subnet, frame, processed);
        }
        return processed;
    }
    
    /**
     * 登録されたRequestProcessorのprocessINFCを全て呼び出す。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @return 指定されたフレームを処理した場合にはtrue、そうでなければfalse
     */
    public boolean processINFC(Subnet subnet, Frame frame) {
        boolean processed = false;
        for (RequestProcessor processor : new ArrayList<RequestProcessor>(processors)) {
            processed |= processor.processINFC(subnet, frame, processed);
        }
        return processed;
    }
}
