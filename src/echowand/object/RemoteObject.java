package echowand.object;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.PropertyMap;
import echowand.logic.SetGetTransactionConfig;
import echowand.logic.Transaction;
import echowand.logic.TransactionListener;
import echowand.logic.TransactionManager;
import echowand.net.*;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * リモートに存在するECHONETオブジェクト
 * @author Yoshiki Makino
 */
public class RemoteObject implements EchonetObject {
    private static final Logger logger = Logger.getLogger(RemoteObject.class.getName());
    private static final String className = RemoteObject.class.getName();
    
    /**
     * トランザクションのタイムアウト(ミリ秒)
     */
    public static final int TRANSACTION_TIMEOUT = 5000;
    /**
     * トランザクションの送信EOJ
     */
    public static final EOJ SOURCE_EOJ = new EOJ("0ef001");
    /**
     * GetプロパティマップのEPC
     */
    public static final EPC GET_PROPERTYMAP_EPC = EPC.x9F;
    /**
     * SetプロパティマップのEPC
     */
    public static final EPC SET_PROPERTYMAP_EPC = EPC.x9E;
    /**
     * AnnoプロパティマップのEPC
     */
    public static final EPC ANNOUNCE_PROPERTYMAP_EPC = EPC.x9D;
    
    private TransactionManager transactionManager;
    private Subnet subnet;
    private Node node;
    private EOJ eoj;
    private int timeout;
    
    private LinkedList<RemoteObjectObserver> observers;
    
    /**
     * RemoteObjectを生成する。
     * @param subnet このRemoteObjectが含まれるサブネット
     * @param node このRemoteObjectを管理しているノード
     * @param eoj このRemoteObjectのEOJ
     * @param transactionManager トランザクション生成に用いられるTransactionManager
     */
    public RemoteObject(Subnet subnet, Node node, EOJ eoj, TransactionManager transactionManager) {
        logger.entering(className, "RemoteObject", new Object[]{subnet, node, eoj, transactionManager});
        
        this.subnet = subnet;
        this.node = node;
        this.eoj = eoj;
        this.transactionManager = transactionManager;
        this.observers = new LinkedList<RemoteObjectObserver>();
        this.timeout = TRANSACTION_TIMEOUT;
        
        logger.entering(className, "RemoteObject");
    }
    
    private synchronized LinkedList<RemoteObjectObserver> cloneObservers() {
        return new LinkedList<RemoteObjectObserver>(observers);
    }
    
    /**
     * 設定されたTransactionManagerを返す。
     * @return 設定されているTransactionManager
     */
    public TransactionManager getListener() {
        return transactionManager;
    }
    
    /**
     * 設定されたTransactionManagerを返す。
     * @return 設定されているTransactionManager
     */
    public Subnet getSubnet() {
        return subnet;
    }
    
    /**
     * 設定された管理ノードを返す。
     * @return 設定されているTransactionManager
     */
    public Node getNode() {
        return node;
    }
    
    /**
     * このオブジェクトのEOJを返す。
     * @return このオブジェクトのEOJ
     */
    @Override
    public EOJ getEOJ() {
        return eoj;
    }
    
    /**
     * トランザクションのタイムアウト時間を設定する。
     * タイムアウト時間は正の整数で指定する。
     * @param timeout タイムアウト(ミリ秒)
     * @return タイムアウトの設定に成功したらtrue、そうでなければfalse
     */
    public boolean setTimeout(int timeout) {
        logger.entering(className, "setTimeout", timeout);
        
        if (timeout > 0) {
            this.timeout = timeout;
            logger.exiting(className, "setTimeout", true);
            return true;
        } else {
            logger.exiting(className, "setTimeout", false);
            return false;
        }
    }
    
    /**
     * トランザクションのタイムアウト時間を取得する。
     * @return 設定されているタイムアウトの時間(ミリ秒)
     */
    public int getTimeout() {
        logger.entering(className, "getTimeout");
        
        int timeout = this.timeout;
        logger.exiting(className, "getTimeout", timeout);
        return timeout;
    }
    
    private boolean isValidFrame(Frame frame) {
        logger.entering(className, "isValidFrame", frame);

        if (!frame.getSender().equals(node)) {
            logger.exiting(className, "isValidFrame", false);
            return false;
        }

        CommonFrame cf = frame.getCommonFrame();
        StandardPayload payload = cf.getEDATA(StandardPayload.class);
        
        if (payload == null) {
            logger.exiting(className, "isValidFrame", false);
            return false;
        }
        
        if (!payload.getSEOJ().equals(eoj)) {
            logger.exiting(className, "isValidFrame", false);
            return false;
        }

        logger.exiting(className, "isValidFrame", true);
        return true;
    }

    private Property getValidFirstProperty(Frame frame, EPC epc) {
        logger.entering(className, "getValidFirstProperty", new Object[]{frame, epc});
        
        if (!isValidFrame(frame)) {
            logger.exiting(className, "getValidFirstProperty", null);
            return null;
        }
        CommonFrame cf = frame.getCommonFrame();
        StandardPayload payload = cf.getEDATA(StandardPayload.class);
        
        if (payload == null) {
            logger.exiting(className, "getValidFirstProperty", null);
            return null;
        }
        
        if (payload.getFirstOPC() != 1) {
            logger.exiting(className, "getValidFirstProperty", null);
            return null;
        }
        Property property = payload.getFirstPropertyAt(0);
        if (property.getEPC() == epc) {
            logger.exiting(className, "getValidFirstProperty", property);
            return property;
        }
        
        logger.exiting(className, "getValidFirstProperty", null);
        return null;
    }

    class RemoteObjectGetTransactionListener implements TransactionListener {

        private EPC epc;
        private ObjectData data;
        private LinkedList<Data> dataList;

        public RemoteObjectGetTransactionListener(EPC epc) {
            this.epc = epc;
        }

        public ObjectData getData() {
            return data;
        }
        
        @Override
        public void begin(Transaction t) {
            logger.entering(className, "RemoteObjectGetTransactionListener.begin", t);
            
            dataList = new LinkedList<Data>();
            
            logger.exiting(className, "RemoteObjectGetTransactionListener.begin");
        }
        
        @Override
        public void send(Transaction t, Subnet subnet, Frame frame, boolean success) {
        }

        @Override
        public void receive(Transaction t, Subnet subnet, Frame frame) {
            logger.entering(className, "RemoteObjectGetTransactionListener.receive", new Object[]{t, subnet, frame});
            
            Property property = getValidFirstProperty(frame, this.epc);
            if (property != null) {
                if (property.getPDC() != 0) {
                    dataList.add(property.getEDT());
                    t.finish();
                }
            }
            
            logger.exiting(className, "RemoteObjectGetTransactionListener.receive");
        }

        @Override
        public void finish(Transaction t) {
            logger.entering(className, "RemoteObjectGetTransactionListener.finish", t);
            
            if (!dataList.isEmpty()) {
                data = new ObjectData(dataList);
            }
            dataList = null;
            
            logger.exiting(className, "RemoteObjectGetTransactionListener.finish");
        }
    }
    
    class RemoteObjectSetTransactionListener implements TransactionListener {
        private EPC epc;
        private boolean success;
        
        public RemoteObjectSetTransactionListener(EPC epc) {
            this.epc = epc;
            this.success = false;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        @Override
        public void begin(Transaction t) {
        }

        @Override
        public void send(Transaction t, Subnet subnet, Frame frame, boolean success) {
        }

        @Override
        public void receive(Transaction t, Subnet subnet, Frame frame) {
            logger.entering(className, "RemoteObjectSetTransactionListener.receive", new Object[]{t, subnet, frame});
            
            Property property = getValidFirstProperty(frame, this.epc);
            if (property != null) {
                success = (property.getPDC() == 0);
                t.finish();
            }
            
            logger.exiting(className, "RemoteObjectSetTransactionListener.receive");
        }

        @Override
        public void finish(Transaction t) {
        }
    }
    
    private SetGetTransactionConfig createSetGetTransactionConfig() {
        logger.entering(className, "createSetGetTransactionConfig");
        
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        transactionConfig.setResponseRequired(true);
        transactionConfig.setSenderNode(subnet.getLocalNode());
        transactionConfig.setReceiverNode(this.getNode());
        transactionConfig.setSourceEOJ(SOURCE_EOJ);
        transactionConfig.setDestinationEOJ(eoj);
        
        logger.exiting(className, "createSetGetTransactionConfig", transactionConfig);
        return transactionConfig;
    }
    
    private Transaction createSetGetTransaction(SetGetTransactionConfig transactionConfig, TransactionListener transactionListener) {
        logger.entering(className, "createSetGetTransaction", new Object[]{transactionConfig, transactionListener});
        
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        transaction.setTimeout(timeout);
        transaction.addTransactionListener(transactionListener);
        
        logger.exiting(className, "createSetGetTransaction", transaction);
        return transaction;
    }
    
    /**
     * 指定されたEPCのデータを返す。
     * EPCのデータを取得するためにTransactionを実行する。
     * @param epc EPCの指定
     * @return 指定したEPCのデータ
     * @throws EchonetObjectException データのGet中にエラーが発生した場合
     */
    @Override
    public ObjectData getData(EPC epc) throws EchonetObjectException {
        logger.entering(className, "getData", epc);
        
        RemoteObjectGetTransactionListener transactionListener;

        SetGetTransactionConfig transactionConfig = createSetGetTransactionConfig();
        transactionConfig.addGet(epc);

        transactionListener = new RemoteObjectGetTransactionListener(epc);
        Transaction transaction = createSetGetTransaction(transactionConfig, transactionListener);
        
        try {
            transaction.execute();
        } catch (SubnetException e) {
            EchonetObjectException exception = new EchonetObjectException("getData failed", e);
            logger.throwing(className, "getData", exception);
            throw exception;
        }
        
        try {
            transaction.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            EchonetObjectException exception = new EchonetObjectException("interrupted", e);
            logger.throwing(className, "getData", exception);
            throw exception;
        }
        
        if (transaction.countResponses() == 0) {
            EchonetObjectException exception = new EchonetObjectException("no response");
            logger.throwing(className, "getData", exception);
            throw exception;
        }
        
        ObjectData data =  transactionListener.getData();
        if (data == null) {
            EchonetObjectException exception = new EchonetObjectException("no valid data");
            logger.throwing(className, "getData", exception);
            throw exception;
        }
        
        logger.exiting(className, "getData", data);
        return data;
    }
    
    /**
     * 指定されたEPCのデータをアナウンスするように要求する。
     * @param epc EPCの指定
     * @throws EchonetObjectException ネットワークに問題が発生した場合
     */
    public void observeData(EPC epc) throws EchonetObjectException {
        logger.entering(className, "observeData", epc);
        
        RemoteObjectGetTransactionListener transactionListener;

        SetGetTransactionConfig transactionConfig = createSetGetTransactionConfig();
        transactionConfig.setAnnouncePreferred(true);
        transactionConfig.addGet(epc);

        transactionListener = new RemoteObjectGetTransactionListener(epc);
        Transaction transaction = createSetGetTransaction(transactionConfig, transactionListener);
        
        try {
            transaction.execute();
        } catch (SubnetException e) {
            EchonetObjectException exception = new EchonetObjectException("getData failed", e);
            logger.throwing(className, "observeData", exception);
            throw exception;
        }
        
        logger.exiting(className, "observeData");
    }

    /**
     * 指定されたEPCに指定されたデータをセットする。
     * EPCのデータをSetするためにTransactionを実行する。
     * @param epc EPCの指定
     * @param data セットするデータの指定
     * @return セットを受け付けた場合にはtrue、そうでなければfalse
     * @throws EchonetObjectException データのSet中にエラーが発生した場合
     */
    @Override
    public boolean setData(EPC epc, ObjectData data) throws EchonetObjectException {
        logger.entering(className, "setData", new Object[]{epc, data});
        
        RemoteObjectSetTransactionListener transactionListener;

        SetGetTransactionConfig transactionConfig = createSetGetTransactionConfig();
        transactionConfig.addSet(epc, data.getData());

        transactionListener = new RemoteObjectSetTransactionListener(epc);
        Transaction transaction = createSetGetTransaction(transactionConfig, transactionListener);

        try {
            transaction.execute();
        } catch (SubnetException e) {
            EchonetObjectException exception = new EchonetObjectException("setData failed", e);
            logger.throwing(className, "setData", exception);
            throw exception;
        }
        
        try {
            transaction.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            EchonetObjectException exception = new EchonetObjectException("interrupted", e);
            logger.throwing(className, "setData", exception);
            throw exception;
        }

        boolean success = transactionListener.isSuccess();
        logger.exiting(className, "setData", success);
        return success;
    }

    private PropertyMap getPropertyMap(EPC epc) throws EchonetObjectException {
        return new PropertyMap(getData(epc).toBytes());
    }
    
    /**
     * 指定されたEPCのプロパティが存在するかを返す。
     * @param epc EPCの指定
     * @return 存在していればtrue、そうでなければfalse
     * @throws EchonetObjectException データの取得に失敗した場合
     */
    @Override
    public boolean contains(EPC epc) throws EchonetObjectException {
        return isGettable(epc) || isSettable(epc) || isObservable(epc);
    }
    
    /**
     * 指定されたEPCがGet可能であるかを返す。
     * @param epc EPCの指定
     * @return Get可能であればtrue、そうでなければfalse
     * @throws EchonetObjectException データの取得に失敗した場合
     */
    @Override
    public boolean isGettable(EPC epc) throws EchonetObjectException {
        return getPropertyMap(GET_PROPERTYMAP_EPC).isSet(epc);
    }
    
    /**
     * 指定されたEPCがSet可能であるかを返す。
     * @param epc EPCの指定
     * @return Set可能であればtrue、そうでなければfalse
     * @throws EchonetObjectException データの取得に失敗した場合
     */
    @Override
    public boolean isSettable(EPC epc) throws EchonetObjectException {
        return getPropertyMap(SET_PROPERTYMAP_EPC).isSet(epc);
    }
    
    /**
     * 指定されたEPCが通知を行うかを返す。
     * @param epc EPCの指定
     * @return 通知を行うのであればtrue、そうでなければfalse
     * @throws EchonetObjectException データの取得に失敗した場合
     */
    @Override
    public boolean isObservable(EPC epc) throws EchonetObjectException {
        return getPropertyMap(ANNOUNCE_PROPERTYMAP_EPC).isSet(epc);
    }
    
    /**
     * プロパティデータ変更通知オブザーバを登録する。
     * @param observer 登録するオブザーバ
     */
    public synchronized void addObserver(RemoteObjectObserver observer) {
        logger.entering(className, "addObserver", observer);
        
        observers.add(observer);
        
        logger.exiting(className, "addObserver");
    }
    
    /**
     * プロパティデータ変更通知オブザーバの登録を抹消する。
     * @param observer 登録を抹消するオブザーバ
     */
    public synchronized void removeObserver(RemoteObjectObserver observer) {
        logger.entering(className, "removeObserver", observer);
        
        observers.remove(observer);
        
        logger.exiting(className, "removeObserver");
    }
    
    /**
     * プロパティデータ変更通知オブザーバの数を返す。
     * @return オブザーバの数
     */
    public synchronized int countObservers() {
        return observers.size();
    }
    
    /**
     * プロパティデータの変更をオブザーバに通知する。
     * @param epc 通知EPC
     * @param data 通知データ
     */
    public void notifyData(EPC epc, ObjectData data) {
        logger.entering(className, "notifyData", new Object[]{epc, data});
        
        for (RemoteObjectObserver observer : cloneObservers()) {
            observer.notifyData(this, epc, data);
        }
        
        logger.exiting(className, "notifyData");
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{Node: " + node + ", EOJ: " + eoj + "}";
    }
}
