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
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * リモートに存在するECHONETオブジェクト
 * @author Yoshiki Makino
 */
public class RemoteObject implements EchonetObject {
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
        this.subnet = subnet;
        this.node = node;
        this.eoj = eoj;
        this.transactionManager = transactionManager;
        this.observers = new LinkedList<RemoteObjectObserver>();
        this.timeout = TRANSACTION_TIMEOUT;
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
        if (timeout > 0) {
            this.timeout = timeout;
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isValidFrame(Frame frame) {
            if (!frame.getSender().equals(node)) {
                return false;
            }
            
            CommonFrame cf = frame.getCommonFrame();
            if (! (cf.getEDATA() instanceof StandardPayload)) {
                return false;
            }
            
            StandardPayload payload = (StandardPayload) cf.getEDATA();
            if (! payload.getSEOJ().equals(eoj)) {
                return false;
            }
            
            return true;
    }
    
    private Property getValidFirstProperty(Frame frame, EPC epc) {
            if (!isValidFrame(frame)) {
                return null;
            }
            CommonFrame cf = frame.getCommonFrame();
            StandardPayload payload = (StandardPayload) cf.getEDATA();
            if (payload.getFirstOPC() != 1) {
                return null;
            }
            Property property = payload.getFirstPropertyAt(0);
            if (property.getEPC() == epc) {
                return property;
            }
            return null;
    }
    
    class RemoteObjectGetTransactionListener implements TransactionListener {
        private EPC epc;
        private ObjectData data;
        private LinkedList<Data> acc;
        
        public RemoteObjectGetTransactionListener(EPC epc) {
            this.epc = epc;
        }
        
        public ObjectData getData() {
            return data;
        }
        
        @Override
        public void begin(Transaction t) {
            acc = new LinkedList<Data>();
        }

        @Override
        public void receive(Transaction t, Subnet subnet, Frame frame) {
            Property property = getValidFirstProperty(frame, this.epc);
            if (property != null) {
                if (property.getPDC() > 0) {
                    acc.add(property.getEDT());
                    t.finish();
                }
            }
        }

        @Override
        public void finish(Transaction t) {
            if (!acc.isEmpty()) {
                data = new ObjectData(acc);
            }
            acc = null;
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
        public void receive(Transaction t, Subnet subnet, Frame frame) {
            Property property = getValidFirstProperty(frame, this.epc);
            if (property != null) {
                success = (property.getPDC() == 0);
                t.finish();
            }
        }

        @Override
        public void finish(Transaction t) {
        }
    }
    
    private SetGetTransactionConfig createSetGetTransactionConfig() {
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        transactionConfig.setResponseRequired(true);
        transactionConfig.setSenderNode(subnet.getLocalNode());
        transactionConfig.setReceiverNode(this.getNode());
        transactionConfig.setSourceEOJ(SOURCE_EOJ);
        transactionConfig.setDestinationEOJ(eoj);
        return transactionConfig;
    }
    
    private Transaction createSetGetTransaction(SetGetTransactionConfig transactionConfig, TransactionListener transactionListener) {
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        transaction.setTimeout(timeout);
        transaction.addTransactionListener(transactionListener);
        return transaction;
    }
    
    /**
     * 指定されたEPCのデータを返す。
     * EPCのデータを取得するためにTransactionを実行する。
     * @param epc EPCの指定
     * @return 指定したEPCのデータ
     * @throws EchonetObjectException ネットワークに問題が発生した場合
     */
    @Override
    public ObjectData getData(EPC epc) throws EchonetObjectException {
        RemoteObjectGetTransactionListener transactionListener;

        SetGetTransactionConfig transactionConfig = createSetGetTransactionConfig();
        transactionConfig.addGet(epc);

        transactionListener = new RemoteObjectGetTransactionListener(epc);
        Transaction transaction = createSetGetTransaction(transactionConfig, transactionListener);
        
        try {
            transaction.execute();
        } catch (SubnetException e) {
            throw new EchonetObjectException("getData failed", e);
        }
        
        try {
            transaction.join();
        } catch (InterruptedException e) {
            throw new EchonetObjectException("interrupted", e);
        }
        
        if (transaction.countResponses() == 0) {
            throw new EchonetObjectException("no response");
        }
        
        return transactionListener.getData();
    }

    /**
     * 指定されたEPCに指定されたデータをセットする。
     * EPCのデータをSetするためにTransactionを実行する。
     * @param epc EPCの指定
     * @param data セットするデータの指定
     * @return セットを受け付けた場合にはtrue、そうでなければfalse
     * @throws EchonetObjectException ネットワークに問題が発生した場合
     */
    @Override
    public boolean setData(EPC epc, ObjectData data) throws EchonetObjectException {
        RemoteObjectSetTransactionListener transactionListener;

        SetGetTransactionConfig transactionConfig = createSetGetTransactionConfig();
        transactionConfig.addSet(epc, data.getData());

        transactionListener = new RemoteObjectSetTransactionListener(epc);
        Transaction transaction = createSetGetTransaction(transactionConfig, transactionListener);

        try {
            transaction.execute();
        } catch (SubnetException e) {
            throw new EchonetObjectException("setData failed", e);
        }

        try {
            transaction.join();
        } catch (InterruptedException e) {
            throw new EchonetObjectException("interrupted", e);
        }

        return transactionListener.isSuccess();
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
    public void addObserver(RemoteObjectObserver observer) {
        observers.add(observer);
    }
    
    /**
     * プロパティデータ変更通知オブザーバの登録を抹消する。
     * @param observer 登録を抹消するオブザーバ
     */
    public void removeObserver(RemoteObjectObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * プロパティデータ変更通知オブザーバの数を返す。
     * @return オブザーバの数
     */
    public int countObservers() {
        return observers.size();
    }
    
    /**
     * プロパティデータの変更をオブザーバに通知する。
     * @param epc 通知EPC
     * @param data 通知データ
     */
    public void notifyData(EPC epc, ObjectData data) {
        for (RemoteObjectObserver observer : new ArrayList<RemoteObjectObserver>(observers)) {
            observer.notifyData(this, epc, data);
        }
    }
}
