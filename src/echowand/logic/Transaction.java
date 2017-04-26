package echowand.logic;

import echowand.common.EOJ;
import echowand.common.ESV;
import echowand.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * トランザクション実行クラス
 * @author Yoshiki Makino
 */
public class Transaction {
    private static final Logger logger = Logger.getLogger(Transaction.class.getName());
    private static final String className = Transaction.class.getName();
    
    private TransactionConfig transactionConfig;
    private static final int DEFAULT_TIMEOUT = 60;
    private static short nextTID;
    
    static {
        nextTID = 1;
    }
    
    private Subnet subnet;
    private TransactionManager transactionManager;
    private int timeout;
    private short tid;
    
    private Timer timer;
    private boolean done;
    private boolean waiting;
    private int countResponse;
    
    private LinkedList<TransactionListener> transactionListeners;
    
    private static EnumMap<ESV, LinkedList<ESV>> responseESVMap = new EnumMap<ESV, LinkedList<ESV>>(ESV.class);
    
    private synchronized static short getNextTID() {
        if (nextTID == 0) {
            nextTID = 1;
        }
        return nextTID++;
    }

    /**
     * Transactionを生成する。
     * 原則としてTransactionManagerを用いて生成することを推奨する。
     * @param subnet リクエスト処理が送受信されるサブネット
     * @param transactionManager Transactionオブジェクトの管理オブジェクト
     * @param transactionConfig  リクエスト処理の詳細設定
     */
    public Transaction(Subnet subnet, TransactionManager transactionManager, TransactionConfig transactionConfig) {
        logger.entering(className, "Transaction", new Object[]{subnet, transactionManager, transactionConfig});
        
        this.subnet = subnet;
        this.transactionManager = transactionManager;
        this.transactionConfig = transactionConfig;
        this.tid = getNextTID();
        this.done = false;
        this.countResponse = 0;
        this.timeout = DEFAULT_TIMEOUT;
        this.transactionListeners = new LinkedList<TransactionListener>();
        initResponseESVMap();
        
        logger.exiting(className, "Transaction");
    }
    
    private void initResponseESVMap() {
        if (responseESVMap.isEmpty()) {
            addResponseESVMap(ESV.SetC, ESV.Set_Res);
            addResponseESVMap(ESV.SetC, ESV.SetC_SNA);
            addResponseESVMap(ESV.SetI, ESV.SetI_SNA);
            addResponseESVMap(ESV.Get, ESV.Get_Res);
            addResponseESVMap(ESV.Get, ESV.Get_SNA);
            addResponseESVMap(ESV.SetGet, ESV.SetGet_Res);
            addResponseESVMap(ESV.SetGet, ESV.SetGet_SNA);
            addResponseESVMap(ESV.INF_REQ, ESV.INF);
            addResponseESVMap(ESV.INF_REQ, ESV.INF_SNA);
            addResponseESVMap(ESV.INF, ESV.INF_SNA);
            addResponseESVMap(ESV.INFC, ESV.INFC_Res);
            addResponseESVMap(ESV.INFC, ESV.INF_SNA);
        }
    }
    
    /**
     * 指定されたリクエストESVに対応するレスポンスESVの登録を行う。
     * エラーレスポンス等の登録も行う必要があるため、一つのリクエストに対して複数のレスポンスを対応させることができる。
     * @param req リクエストESVの指定
     * @param res レスポンスESVの指定
     */
    protected void addResponseESVMap(ESV req, ESV res) {
        LinkedList<ESV> esvs;
        if (responseESVMap.containsKey(req)) {
            esvs = responseESVMap.get(req);
        } else {
            esvs = new LinkedList<ESV>();
        }
        esvs.add(res);
        responseESVMap.put(req, esvs);
    }
    
    /**
     * トランザクション処理の詳細設定を返す。
     * @return リクエスト処理の詳細設定
     */
    public TransactionConfig getTransactionConfig() {
        return transactionConfig;
    }
    
    /**
     * トランザクションのレスポンス処理を行なうTransactionListenerを登録する。
     * @param listener 登録するTransactionListener
     */
    public synchronized void addTransactionListener(TransactionListener listener) {
        logger.entering(className, "addTransactionListener", listener);
        
        transactionListeners.add(listener);
        
        logger.exiting(className, "addTransactionListener");
    }
    
    
    /**
     * トランザクションのレスポンス処理を行なうTransactionListenerの登録を抹消する。
     * @param listener 登録を抹消するTransactionListener
     */
    public synchronized void removeTransactionListener(TransactionListener listener) {
        logger.entering(className, "removeTransactionListener", listener);
        
        transactionListeners.remove(listener);
        
        logger.exiting(className, "removeTransactionListener");
    }
    
    private synchronized List<TransactionListener> cloneTransactionListeners() {
        return new ArrayList<TransactionListener>(transactionListeners);
    }
    
    private void doCallBeginTransactionListeners() {
        logger.entering(className, "doCallBeginTransactionListeners");
        
        for (TransactionListener l : cloneTransactionListeners()) {
            l.begin(this);
        }
        
        logger.exiting(className, "doCallBeginTransactionListeners");
    }
    
    private void doCallSentTransactionListeners(Frame frame, boolean success) {
        logger.entering(className, "doCallSentTransactionListeners", frame);
        
        for (TransactionListener l : cloneTransactionListeners()) {
            l.send(this, subnet, frame, success);
        }
        
        logger.exiting(className, "doCallSentTransactionListeners");
    }
    
    private void doCallReceiveTransactionListeners(Frame frame) {
        logger.entering(className, "doCallReceiveTransactionListeners", frame);
        
        for (TransactionListener l : cloneTransactionListeners()) {
            l.receive(this, subnet, frame);
        }
        
        logger.exiting(className, "doCallReceiveTransactionListeners");
    }
    
    private void doCallFinishTransactionListeners() {
        logger.entering(className, "doCallFinishTransactionListeners");
        
        for (TransactionListener l : cloneTransactionListeners()) {
            l.finish(this);
        }
        
        logger.exiting(className, "doCallFinishTransactionListeners");
    }
    
    /**
     * 登録済みのTransactionListenerの個数を返す。
     * @return 登録済みのTransactionListener数
     */
    public synchronized int countTransactionListeners() {
        logger.entering(className, "countTransactionListeners", timeout);
        
        int count = transactionListeners.size();
        
        logger.exiting(className, "countTransactionListeners", count);
        return count;
    }
    
    /**
     * トランザクションのタイムアウトをミリ秒単位で設定する
     * @param timeout タイムアウトの時間(ミリ秒)
     */
    public synchronized void setTimeout(int timeout) {
        logger.entering(className, "setTimeout", timeout);
        
        this.timeout = timeout;
        
        logger.exiting(className, "setTimeout");
    }
    
    /**
     * トランザクションのタイムアウト時間を返す。
     * @return timeout タイムアウトの時間(ミリ秒)
     */
    public synchronized int getTimeout() {
        return timeout;
    }
    
    /**
     * トランザクションのTIDを返す。
     * @return リクエスト処理のTID
     */
    public short getTID() {
        return tid;
    }
    
    private StandardPayload createPayload(int index) {
        logger.entering(className, "createPayload", index);
        
        StandardPayload payload = new StandardPayload();
        payload.setDEOJ(transactionConfig.getDestinationEOJ());
        payload.setSEOJ(transactionConfig.getSourceEOJ());
        
        payload.setESV(transactionConfig.getESV());
        
        transactionConfig.addPayloadProperties(index, payload);
        
        logger.exiting(className, "createPayload", payload);
        return payload;
    }
    
    private boolean sendRequest() {
        logger.entering(className, "sendRequest");
        
        int count = transactionConfig.getCountPayloads();
        
        if (count == 0) {
            logger.exiting(className, "sendRequest", false);
            return false;
        }
        
        boolean result = true;
        
        for (int i = 0; i < count; i++) {
            StandardPayload payload = createPayload(i);

            CommonFrame cf = new CommonFrame();
            cf.setEDATA(payload);
            cf.setTID(tid);
            Frame frame = new Frame(transactionConfig.getSenderNode(), transactionConfig.getReceiverNode(), cf);
            
            boolean success = true;
            
            try {
                subnet.send(frame);
            } catch (SubnetException ex) {
                success = false;
                logger.logp(Level.INFO, className, "sendRequest", "catched exception", ex);
            }
            
            doCallSentTransactionListeners(frame, success);
            
            result &= success;
        }
        
        logger.exiting(className, "sendRequest", result);
        return result;
    }
    
    private boolean isValidTransactionESVPair(ESV req, ESV res) {
        logger.entering(className, "isValidTransactionESVPair", new Object[]{req, res});

        LinkedList<ESV> esvs = responseESVMap.get(req);
        boolean valid = false;
        if (esvs != null) {
            valid = esvs.contains(res);
        }

        logger.exiting(className, "isValidTransactionESVPair", valid);
        return valid;
    }

    /**
     * 受信したレスポンスフレームの処理を行なう。
     * @param frame 受信したフレーム
     * @return フレームの処理に成功した場合にはtrue、そうでなければfalse
     */
    public synchronized boolean receiveResponse(Frame frame) {
        logger.entering(className, "receiveResponse");
        
        if (!this.waiting) {
            logger.exiting(className, "receiveResponse", false);
            return false;
        }
        
        if (!frame.getCommonFrame().isStandardPayload()) {
            logger.exiting(className, "receiveResponse", false);
            return false;
        }
        
        CommonFrame cf = frame.getCommonFrame();
        
        if (cf.getTID() != this.getTID()) {
            logger.exiting(className, "receiveResponse", false);
            return false;
        }
        
        StandardPayload payload = cf.getEDATA(StandardPayload.class);
        
        if (payload == null) {
            logger.exiting(className, "receiveResponse", false);
            return false;
        }
        
        EOJ responseSEOJ = payload.getSEOJ();
        EOJ responseDEOJ = payload.getDEOJ();
        EOJ requestDEOJ = transactionConfig.getDestinationEOJ();
        EOJ requestSEOJ = transactionConfig.getSourceEOJ();
        
        if (!responseSEOJ.equals(requestDEOJ)) {
            if (!requestDEOJ.isAllInstance()) {
                logger.exiting(className, "receiveResponse", false);
                return false;
            }

            if (!responseSEOJ.getClassEOJ().equals(requestDEOJ.getClassEOJ())) {
                logger.exiting(className, "receiveResponse", false);
                return false;
            }
        }
        if (!responseDEOJ.equals(requestSEOJ)) {
            logger.exiting(className, "receiveResponse", false);
            return false;
        }

        ESV reqESV = transactionConfig.getESV();
        ESV resESV = payload.getESV();
        
        if (!isValidTransactionESVPair(reqESV, resESV)) {
            logger.exiting(className, "receiveResponse", false);
            return false;
        }
        
        this.countResponse++;
        
        doCallReceiveTransactionListeners(frame);
        
        logger.exiting(className, "receiveResponse", true);
        return true;
    }
    
    /**
     * トランザクションを終了する。
     */
    public synchronized void finish() {
        logger.entering(className, "finish");
        
        if (!this.done) {
            this.waiting = false;
            this.done = true;

            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        
            transactionManager.removeTransaction(this);
            
            doCallFinishTransactionListeners();
            
            notifyAll();
        }
        
        logger.exiting(className, "finish");
    }
    
    private static class TimeoutTimerTask extends TimerTask {
        public Transaction t;
        public TimeoutTimerTask(Transaction t) {
            this.t = t;
        }

        @Override
        public void run() {
            logger.entering(className, "TimeoutTimerTask.run");
            t.finish();
            logger.exiting(className, "TimeoutTimerTask.run");
        }
    }
    /**
     * トランザクションを開始する。
     * @throws SubnetException フレームの生成や送信に失敗した場合 
     */
    public synchronized void execute() throws SubnetException {
        logger.entering(className, "execute");
        
        if (this.waiting || this.done) {
            logger.exiting(className, "execute");
            return;
        }
        
        doCallBeginTransactionListeners();

        this.waiting = true;
        
        transactionManager.addTransaction(this);
        
        boolean success = sendRequest();
        
        int timeout = getTimeout();
        
        if (timeout == 0) {
            finish();
        } else if (timeout > 0) {
            timer = new Timer(true);
            timer.schedule(new TimeoutTimerTask(this), timeout);
        }
        
        if (!success) {
            throw new SubnetException("sendRequest failed");
        }

        logger.exiting(className, "execute");
    }

    /**
     * トランザクションが終了するまで待つ。
     * @throws InterruptedException 割り込みが発生した場合
     */
    public synchronized void join() throws InterruptedException {
        logger.entering(className, "join");
        
        while (isWaitingResponse()) {
            wait();
        }
        
        logger.exiting(className, "join");
    }
    
    /**
     * 受信したレスポンスフレームの数を返す。
     * @return 受信したレスポンスフレーム数
     */
    public synchronized int countResponses() {
        logger.entering(className, "countResponses");
        
        int count = this.countResponse;
        
        logger.exiting(className, "countResponses", count);
        return count;
    }
    
    /**
     * トランザクションが処理中であるか示す。
     * @return トランザクションが処理中であればtrue、そうでなければfalse
     */
    public synchronized boolean isWaitingResponse() {
        return waiting;
    }

    /**
     * トランザクションが終了しているか示す。
     * @return トランザクションが終了していればtrue、そうでなければfalse
     */
    public synchronized boolean isDone() {
        return done;
    }
}
