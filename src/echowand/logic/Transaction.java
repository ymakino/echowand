package echowand.logic;

import echowand.net.Frame;
import echowand.net.SubnetException;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import echowand.net.CommonFrame;
import echowand.common.EOJ;
import echowand.common.ESV;
import java.util.*;

/**
 * トランザクション実行クラス
 * @author Yoshiki Makino
 */
public class Transaction {
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
    
    private EnumMap<ESV, LinkedList<ESV>> responseESVMap;
    
    private static short getNextTID() {
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
        this.subnet = subnet;
        this.transactionManager = transactionManager;
        this.transactionConfig = transactionConfig;
        this.tid = getNextTID();
        this.done = false;
        this.countResponse = 0;
        this.timeout = DEFAULT_TIMEOUT;
        this.responseESVMap = new EnumMap<ESV, LinkedList<ESV>>(ESV.class);
        this.transactionListeners = new LinkedList<TransactionListener>();
        initResponseESVMap();
    }
    
    private void initResponseESVMap() {
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
    public void addTransactionListener(TransactionListener listener) {
        transactionListeners.add(listener);
    }
    
    
    /**
     * トランザクションのレスポンス処理を行なうTransactionListenerの登録を抹消する。
     * @param listener 登録を抹消するTransactionListener
     */
    public void removeTransactionListener(TransactionListener listener) {
        transactionListeners.remove(listener);
    }
    
    private void doCallBeginTransactionListeners() {
        for (TransactionListener l : new ArrayList<TransactionListener>(transactionListeners)) {
            l.begin(this);
        }
    }
    
    private void doCallReceiveTransactionListeners(Frame frame) {
        for (TransactionListener l : new ArrayList<TransactionListener>(transactionListeners)) {
            l.receive(this, subnet, frame);
        }
    }
    
    private void doCallFinishTransactionListeners() {
        for (TransactionListener l : new ArrayList<TransactionListener>(transactionListeners)) {
            l.finish(this);
        }
    }
    
    /**
     * 登録済みのTransactionListenerの個数を返す。
     * @return 登録済みのTransactionListener数
     */
    public int countTransactionListeners() {
        return transactionListeners.size();
    }
    
    /**
     * トランザクションのタイムアウトをミリ秒単位で設定する
     * @param timeout タイムアウトの時間(ミリ秒)
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    /**
     * トランザクションのTIDを返す。
     * @return リクエスト処理のTID
     */
    public short getTID() {
        return tid;
    }
    
    private StandardPayload createPayload(int index) {
        StandardPayload payload = new StandardPayload();
        payload.setDEOJ(transactionConfig.getDestinationEOJ());
        payload.setSEOJ(transactionConfig.getSourceEOJ());
        
        payload.setESV(transactionConfig.getESV());
        
        transactionConfig.addPayloadProperties(index, payload);
        
        return payload;
    }
    
    private boolean sendRequest() throws SubnetException {
        int count = transactionConfig.getCountPayloads();
        for (int i = 0; i < count; i++) {
            StandardPayload payload = createPayload(i);

            CommonFrame cf = new CommonFrame();
            cf.setEDATA(payload);
            cf.setTID(tid);
            Frame frame = new Frame(transactionConfig.getSenderNode(), transactionConfig.getReceiverNode(), cf);
            subnet.send(frame);
        }
        return true;
    }
    
    private boolean isValidTransactionESVPair(ESV req, ESV res) {
        LinkedList<ESV> esvs = responseESVMap.get(req);
        if (esvs == null) {
            return false;
        }
        return esvs.contains(res);
    }
    
    /**
     * 受信したレスポンスフレームの処理を行なう。
     * @param frame 受信したフレーム
     * @return フレームの処理に成功した場合にはtrue、そうでなければfalse
     */
    public synchronized boolean recvResponse(Frame frame) {
        if (!this.waiting) {
            return false;
        }
        
        if (!frame.getCommonFrame().isStandardPayload()) {
            return false;
        }
        
        CommonFrame cf = frame.getCommonFrame();
        
        if (cf.getTID() != this.getTID()) {
            return false;
        }
        
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        EOJ responseSEOJ = payload.getSEOJ();
        EOJ responseDEOJ = payload.getDEOJ();
        EOJ requestDEOJ = transactionConfig.getDestinationEOJ();
        EOJ requestSEOJ = transactionConfig.getSourceEOJ();
        
        if (!responseSEOJ.equals(requestDEOJ)) {
            if (!requestDEOJ.isAllInstance()) {
                return false;
            }

            if (!responseSEOJ.getClassEOJ().equals(requestDEOJ.getClassEOJ())) {
                return false;
            }
        }
        if (!responseDEOJ.equals(requestSEOJ)) {
            return false;
        }

        ESV reqESV = transactionConfig.getESV();
        ESV resESV = payload.getESV();
        
        if (!isValidTransactionESVPair(reqESV, resESV)) {
            return false;
        }
        
        this.countResponse++;
        
        doCallReceiveTransactionListeners(frame);
        
        return true;
    }
    
    /**
     * トランザクションを終了する。
     */
    public synchronized void finish() {
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
    }
    
    private class TimeoutTimerTask extends TimerTask {
        public Transaction t;
        public TimeoutTimerTask(Transaction t) {
            this.t = t;
        }
        @Override
        public void run() {
            t.finish();
        }
    }
    /**
     * トランザクションを開始する。
     * @throws SubnetException フレームの生成や送信に失敗した場合 
     */
    public synchronized void execute() throws SubnetException {
        if (this.waiting || this.done) {
            return;
        }
        
        doCallBeginTransactionListeners();

        this.waiting = true;
        
        transactionManager.addTransaction(this);
        
        sendRequest();
        if (timeout > 0) {
            timer = new Timer(true);
            timer.schedule(new TimeoutTimerTask(this), timeout);
        }
    }
    
    /**
     * トランザクションが終了するまで待つ。
     * @throws InterruptedException 割り込みが発生した場合
     */
    public synchronized void join() throws InterruptedException {
        if (!isWaiting()) {
            return;
        }
        
        wait();
    }
    
    /**
     * 受信したレスポンスフレームの数を返す。
     * @return 受信したレスポンスフレーム数
     */
    public synchronized int countResponses() {
        return this.countResponse;
    }
    
    /**
     * トランザクションが処理中であるか示す。
     * @return トランザクションが処理中であればtrue、そうでなければfalse
     */
    public synchronized boolean isWaiting() {
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
