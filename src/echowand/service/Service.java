package echowand.service;

import echowand.common.ClassEOJ;
import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.AnnounceTransactionConfig;
import echowand.logic.SetGetTransactionConfig;
import echowand.logic.Transaction;
import echowand.logic.TransactionListener;
import echowand.logic.TransactionManager;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.NodeInfo;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import echowand.object.EchonetObject;
import echowand.object.EchonetObjectException;
import echowand.object.InstanceListRequestExecutor;
import echowand.object.LocalObject;
import echowand.object.LocalObjectManager;
import echowand.object.ObjectData;
import echowand.object.RemoteObject;
import echowand.object.RemoteObjectManager;
import echowand.service.result.CaptureListener;
import echowand.service.result.CaptureResult;
import echowand.service.result.ResultBase;
import echowand.service.result.GetResult;
import echowand.service.result.FrameSelector;
import echowand.service.result.GetListener;
import echowand.service.result.NotifyListener;
import echowand.service.result.NotifyResult;
import echowand.service.result.ObserveListener;
import echowand.service.result.ObserveResult;
import echowand.service.result.SetGetListener;
import echowand.service.result.SetGetResult;
import echowand.service.result.SetListener;
import echowand.service.result.SetResult;
import echowand.service.result.UpdateRemoteInfoResult;
import echowand.service.result.UpdateRemoteInfoListener;
import echowand.util.Pair;
import echowand.util.Selector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ライブラリの様々な機能を提供するServiceインタフェース
 * @author ymakino
 */
public class Service {
    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());
    private static final String CLASS_NAME = Service.class.getName();
    
    private Core core;
    
    /**
     * 利用するCoreを指定してServiceを生成する。
     * @param core 利用するCore
     */
    public Service(Core core) {
        LOGGER.entering(CLASS_NAME, "Service", core);
        
        this.core = core;
        
        LOGGER.exiting(CLASS_NAME, "Service");
    }
    
    /**
     * 利用するCoreを返す。
     * @return 利用するCore
     */
    public Core getCore() {
        return core;
    }
    
    /**
     * 利用するSubnetを返す。
     * @return 利用するSubnet
     */
    public Subnet getSubnet() {
        return core.getSubnet();
    }
    
    /**
     * 利用するLocalObjectManagerを返す。
     * @return 利用するLocalObjectManager
     */
    public LocalObjectManager getLocalObjectManager() {
        return core.getLocalObjectManager();
    }
    
    /**
     * 利用するRemoteObjectManagerを返す。
     * @return 利用するRemoteObjectManager
     */
    public RemoteObjectManager getRemoteObjectManager() {
        return core.getRemoteObjectManager();
    }
    
    /**
     * 利用するTransactionManagerを返す。
     * @return 利用するTransactionManager
     */
    public TransactionManager getTransactionManager() {
        return core.getTransactionManager();
    }
    
    private class ResultBaseTransactionListener implements TransactionListener {
        ResultBase result;
        
        public ResultBaseTransactionListener(ResultBase result) {
            LOGGER.entering(CLASS_NAME, "ResultBaseTransactionListener", result);
            
            this.result = result;
            
            LOGGER.entering(CLASS_NAME, "ResultBaseTransactionListener");
        }

        @Override
        public void begin(Transaction t) {
            LOGGER.entering(CLASS_NAME, "ResultBaseTransactionListener.begin", t);
            
            result.begin();
            
            LOGGER.exiting(CLASS_NAME, "ResultBaseTransactionListener.begin");
        }

        @Override
        public void send(Transaction t, Subnet subnet, Frame frame, boolean success) {
            LOGGER.entering(CLASS_NAME, "ResultBaseTransactionListener.sent", new Object[]{t, subnet, frame, success});
            
            result.addRequestFrame(frame, success);
            
            LOGGER.exiting(CLASS_NAME, "ResultBaseTransactionListener.sent");
        }

        @Override
        public void receive(Transaction t, Subnet subnet, Frame frame) {
            LOGGER.entering(CLASS_NAME, "ResultBaseTransactionListener.receive", new Object[]{t, subnet, frame});
            
            result.addFrame(frame);
            
            LOGGER.exiting(CLASS_NAME, "ResultBaseTransactionListener.receive");
        }

        @Override
        public void finish(Transaction t) {
            LOGGER.entering(CLASS_NAME, "ResultBaseTransactionListener.finish", t);
            
            result.finish();
            
            LOGGER.exiting(CLASS_NAME, "ResultBaseTransactionListener.finish");
        }
    }
    
    private class ResultUpdateTransactionListener implements TransactionListener {
        private UpdateRemoteInfoResult resultUpdate;

        public ResultUpdateTransactionListener(UpdateRemoteInfoResult resultUpdate) {
            LOGGER.entering(CLASS_NAME, "ResultUpdateTransactionListener", resultUpdate);
            
            this.resultUpdate = resultUpdate;
            
            LOGGER.exiting(CLASS_NAME, "ResultUpdateTransactionListener");
        }

        @Override
        public void begin(Transaction t) {
            LOGGER.entering(CLASS_NAME, "ResultUpdateTransactionListener.begin", t);
            
            resultUpdate.begin();
            
            LOGGER.exiting(CLASS_NAME, "ResultUpdateTransactionListener.begin");
        }

        @Override
        public void send(Transaction t, Subnet subnet, Frame frame, boolean success) {
            LOGGER.entering(CLASS_NAME, "ResultUpdateTransactionListener.send", new Object[]{t, subnet, frame, success});
            
            resultUpdate.addRequestFrame(frame, success);
            
            LOGGER.exiting(CLASS_NAME, "ResultUpdateTransactionListener.send");
        }

        @Override
        public void receive(Transaction t, Subnet subnet, Frame frame) {
            LOGGER.entering(CLASS_NAME, "ResultUpdateTransactionListener.receive", new Object[]{t, subnet, frame});
            
            resultUpdate.addFrame(frame);
            
            LOGGER.exiting(CLASS_NAME, "ResultUpdateTransactionListener.receive");
        }

        @Override
        public void finish(Transaction t) {
            LOGGER.entering(CLASS_NAME, "ResultUpdateTransactionListener.finish", t);
            
            resultUpdate.finish();
            
            LOGGER.exiting(CLASS_NAME, "ResultUpdateTransactionListener.finish");
        }
    }
    
    private SetGetTransactionConfig createGetTransactionConfig(Node node, EOJ eoj, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "createGetTransactionConfig", new Object[]{node, eoj, epcs});
            
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        
        transactionConfig.setReceiverNode(node);
        transactionConfig.setDestinationEOJ(eoj);
        transactionConfig.setSenderNode(getSubnet().getLocalNode());
        transactionConfig.setSourceEOJ(new EOJ("0ef001"));
        
        for (EPC epc: epcs) {
            transactionConfig.addGet(epc);
        }
        
        LOGGER.exiting(CLASS_NAME, "createGetTransactionConfig", transactionConfig);
        return transactionConfig;
    }
    
    private SetGetTransactionConfig createSetTransactionConfig(Node node, EOJ eoj, List<Pair<EPC,Data>> properties) {
        LOGGER.entering(CLASS_NAME, "createSetTransactionConfig", new Object[]{node, eoj, properties});
        
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        
        transactionConfig.setReceiverNode(node);
        transactionConfig.setDestinationEOJ(eoj);
        transactionConfig.setSenderNode(getSubnet().getLocalNode());
        transactionConfig.setSourceEOJ(new EOJ("0ef001"));
        
        for (Pair<EPC,Data> property: properties) {
            transactionConfig.addSet(property.first, property.second);
        }
        
        LOGGER.exiting(CLASS_NAME, "createSetTransactionConfig", transactionConfig);
        return transactionConfig;
    }
    
    private SetGetTransactionConfig createSetGetTransactionConfig(Node node, EOJ eoj, List<Pair<EPC,Data>> properties, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "createSetGetTransactionConfig", new Object[]{node, eoj, properties});
        
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        
        transactionConfig.setReceiverNode(node);
        transactionConfig.setDestinationEOJ(eoj);
        transactionConfig.setSenderNode(getSubnet().getLocalNode());
        transactionConfig.setSourceEOJ(new EOJ("0ef001"));
        
        for (Pair<EPC,Data> property: properties) {
            transactionConfig.addSet(property.first, property.second);
        }
        
        for (EPC epc: epcs) {
            transactionConfig.addGet(epc);
        }
        
        LOGGER.exiting(CLASS_NAME, "createSetGetTransactionConfig", transactionConfig);
        return transactionConfig;
    }
    
    private <T> List<T> toList(T... objects) {
        LinkedList<T> list = new LinkedList<T>();
        
        if (objects.length != 1 || objects[0] != null) {
            list.addAll(Arrays.asList(objects));
        }
        
        return list;
    }
    
    private <T> List toObjectList(T... objects) {
        LinkedList<T> list = new LinkedList<T>();
        
        if (objects.length != 1 || objects[0] != null) {
            list.addAll(Arrays.asList(objects));
        }
        
        return list;
    }
    
    private Thread createExclusiveThread(final Runnable runnable) {
        return new Thread() {
            @Override
            public void run() {
                synchronized (getCore().getMainLoop()) {
                    runnable.run();
                }
            }
        };
    }
    
    public Thread runExclusive(final Runnable runnable) {
        Thread th = createExclusiveThread(runnable);
        th.start();
        return th;
    }
    
    public GetResult doGet(Node node, EOJ eoj, List<EPC> epcs, int timeout, GetListener getListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, eoj, epcs, timeout, getListener});
        
        GetResult getResult = new GetResult(core.getTimestampManager());
        getResult.setGetListener(getListener);
        
        SetGetTransactionConfig transactionConfig = createGetTransactionConfig(node, eoj, epcs);
        
        Transaction transaction = new Transaction(getSubnet(), getTransactionManager(), transactionConfig);
        transaction.setTimeout(timeout);
        
        transaction.addTransactionListener(new ResultBaseTransactionListener(getResult));
        
        transaction.execute();
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }
    
    public SetResult doSet(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, properties, timeout, responseRequired, setListener});
        
        SetResult setResult = new SetResult(responseRequired, core.getTimestampManager());
        setResult.setSetListener(setListener);
        
        SetGetTransactionConfig transactionConfig = createSetTransactionConfig(node, eoj, properties);
        transactionConfig.setResponseRequired(responseRequired);
        
        Transaction transaction = new Transaction(getSubnet(), getTransactionManager(), transactionConfig);
        transaction.setTimeout(timeout);
        
        transaction.addTransactionListener(new ResultBaseTransactionListener(setResult));
        
        transaction.execute();

        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }
    
    public SetGetResult doSetGet(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, List<EPC> epcs, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, eoj, properties, epcs, timeout, setGetListener});
        
        SetGetResult setGetResult = new SetGetResult(core.getTimestampManager());
        setGetResult.setSetGetListener(setGetListener);
        
        SetGetTransactionConfig transactionConfig = createSetGetTransactionConfig(node, eoj, properties, epcs);
        
        Transaction transaction = new Transaction(getSubnet(), getTransactionManager(), transactionConfig);
        transaction.setTimeout(timeout);
        
        transaction.addTransactionListener(new ResultBaseTransactionListener(setGetResult));
        
        transaction.execute();

        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }
    
    public UpdateRemoteInfoResult doUpdateRemoteInfo(Node node, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doUpdateRemoteInfo", new Object[]{node, timeout});
        
        UpdateRemoteInfoResult updateRemoteInfoResult = doUpdateRemoteInfo(node, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doUpdateRemoteInfo", updateRemoteInfoResult);
        return updateRemoteInfoResult;
    }
    
    public UpdateRemoteInfoResult doUpdateRemoteInfo(NodeInfo nodeInfo, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doUpdateRemoteInfo", new Object[]{nodeInfo, timeout});
        
        UpdateRemoteInfoResult updateRemoteInfoResult = doUpdateRemoteInfo(getRemoteNode(nodeInfo), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doUpdateRemoteInfo", updateRemoteInfoResult);
        return updateRemoteInfoResult;
    }
    
    public UpdateRemoteInfoResult doUpdateRemoteInfo(int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doUpdateRemoteInfo", timeout);
        
        UpdateRemoteInfoResult updateRemoteInfoResult = doUpdateRemoteInfo(getGroupNode(), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doUpdateRemoteInfo", updateRemoteInfoResult);
        return updateRemoteInfoResult;
    }
    
    public UpdateRemoteInfoResult doUpdateRemoteInfo(Node node, int timeout, UpdateRemoteInfoListener updateRemoteInfoListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doUpdateRemoteInfo", new Object[]{node, timeout, updateRemoteInfoListener});
        
        InstanceListRequestExecutor executor = new InstanceListRequestExecutor(
                getSubnet(), getTransactionManager(), getRemoteObjectManager());
        
        executor.setNode(node);
        
        UpdateRemoteInfoResult updateRemoteInfoResult = new UpdateRemoteInfoResult(executor, core.getTimestampManager());
        
        if (updateRemoteInfoListener != null) {
            updateRemoteInfoResult.setUpdateRemoteInfoListener(updateRemoteInfoListener);
        }
        
        executor.setTimeout(timeout);
        ResultUpdateTransactionListener resultUpdateTransactionListener = new ResultUpdateTransactionListener(updateRemoteInfoResult);
        executor.addTransactionListener(resultUpdateTransactionListener);
        
        executor.execute();
        
        LOGGER.exiting(CLASS_NAME, "doUpdateRemoteInfo", updateRemoteInfoResult);
        return updateRemoteInfoResult;
    }
    
    public UpdateRemoteInfoResult doUpdateRemoteInfo(NodeInfo nodeInfo, int timeout, UpdateRemoteInfoListener updateRemoteInfoListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doUpdateRemoteInfo", new Object[]{nodeInfo, timeout, updateRemoteInfoListener});
        
        UpdateRemoteInfoResult updateRemoteInfoResult = doUpdateRemoteInfo(getRemoteNode(nodeInfo), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doUpdateRemoteInfo", updateRemoteInfoResult);
        return updateRemoteInfoResult;
    }
    
    public UpdateRemoteInfoResult doUpdateRemoteInfo(int timeout, UpdateRemoteInfoListener updateRemoteInfoListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doUpdateRemoteInfo", new Object[]{timeout, updateRemoteInfoListener});
        
        UpdateRemoteInfoResult updateRemoteInfoResult = doUpdateRemoteInfo(getGroupNode(), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doUpdateRemoteInfo", updateRemoteInfoResult);
        return updateRemoteInfoResult;
    }
    
    private Node toNodeFromNodeOrNodeInfo(Object o) throws SubnetException {
        if (o instanceof Node) {
            return (Node)o;
        }
        
        if (o instanceof NodeInfo) {
            NodeInfo nodeInfo = (NodeInfo)o;
            return getRemoteNode(nodeInfo);
        }
        
        throw new SubnetException("Invalid node: " + o);
    }
    
    private List<Node> toNodesFromNodesAndNodeInfos(List nodes) throws SubnetException {
        LinkedList<Node> newNodes = new LinkedList<Node>();
        
        if (nodes == null) {
            return newNodes;
        }
        
        for (Object object: nodes) {
            newNodes.add(toNodeFromNodeOrNodeInfo(object));
        }
        
        return newNodes;
    }
    
    private EOJ toEOJsFromEOJsAndClassEOJ(Object o) throws SubnetException {
        if (o instanceof EOJ) {
            return (EOJ)o;
        }
        
        if (o instanceof ClassEOJ) {
            ClassEOJ ceoj = (ClassEOJ)o;
            return ceoj.getAllInstanceEOJ();
        }
        
        throw new SubnetException("Invalid node: " + o);
    }
    
    private List<EOJ> toEOJsFromEOJsAndClassEOJs(List eojs) throws SubnetException {
        LinkedList<EOJ> newEOJs = new LinkedList<EOJ>();
        
        if (eojs == null) {
            return newEOJs;
        }
        
        for (Object object: eojs) {
            newEOJs.add(toEOJsFromEOJsAndClassEOJ(object));
        }
        
        return newEOJs;
    }
    
    public ObserveResult doObserve(Selector<? super Frame> selector, ObserveListener observeListener) {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{selector, observeListener});
        
        ObserveResultProcessor processor = getCore().getObserveResultProsessor();
        ObserveResult observeResult = new ObserveResult(selector, processor, core.getTimestampManager());
        observeResult.setObserveListener(observeListener);
        processor.addObserveResult(observeResult);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }
    
    public ObserveResult doObserve(List nodes, List eojs, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eojs, epcs});
        
        ObserveResult observeResult = doObserve(new FrameSelector(toNodesFromNodesAndNodeInfos(nodes), toEOJsFromEOJsAndClassEOJs(eojs), epcs), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }
    
    public boolean isCaptureEnabled() {
        return getCore().isCaptureEnabled();
    }
    
    public CaptureResult doCapture() {
        LOGGER.entering(CLASS_NAME, "doCapture");
        
        CaptureResult captureResult = doCapture(null);
        
        LOGGER.exiting(CLASS_NAME, "doCapture", captureResult);
        return captureResult;
    }
    
    public CaptureResult doCapture(CaptureListener captureListener) {
        LOGGER.entering(CLASS_NAME, "doCapture");
        
        CaptureResultObserver observer = getCore().getCaptureResultObserver();
        CaptureResult captureResult = new CaptureResult(observer, core.getTimestampManager());
        captureResult.setCaptureListener(captureListener);
        observer.addCaptureResult(captureResult);
        
        LOGGER.exiting(CLASS_NAME, "doCapture", captureResult);
        return captureResult;
    }
    
    public ObjectData getLocalData(EOJ eoj, EPC epc) throws ObjectNotFoundException {
        LOGGER.entering(CLASS_NAME, "getLocalData", new Object[]{eoj, epc});
        
        LocalObject localObject = getLocalObject(eoj);
        
        if (localObject == null) {
            ObjectNotFoundException exception = new ObjectNotFoundException(eoj.toString());
            LOGGER.throwing(CLASS_NAME, "getLocalData", exception);
            throw exception;
        }
        
        ObjectData objectData = localObject.getData(epc);
        
        LOGGER.exiting(CLASS_NAME, "getLocalData", objectData);
        return objectData;
    }
    
    public boolean setLocalData(EOJ eoj, EPC epc, ObjectData data) throws ObjectNotFoundException {
        LOGGER.entering(CLASS_NAME, "setLocalData", new Object[]{eoj, epc, data});
        
        LocalObject localObject = getLocalObject(eoj);
        
        if (localObject == null) {
            ObjectNotFoundException exception = new ObjectNotFoundException(eoj.toString());
            LOGGER.throwing(CLASS_NAME, "setLocalData", exception);
            throw exception;
        }
        
        boolean result = localObject.forceSetData(epc, data);
        
        LOGGER.exiting(CLASS_NAME, "setLocalData", result);
        return result;
    }
    
    public ObjectData getRemoteData(NodeInfo nodeInfo, EOJ eoj, EPC epc) throws ObjectNotFoundException, SubnetException, EchonetObjectException {
        LOGGER.entering(CLASS_NAME, "getRemoteData", new Object[]{nodeInfo, eoj, epc});
        
        ObjectData objectData = getRemoteData(getRemoteNode(nodeInfo), eoj, epc);
        
        LOGGER.exiting(CLASS_NAME, "getRemoteData", objectData);
        return objectData;
    }
    
    public ObjectData getRemoteData(Node node, EOJ eoj, EPC epc) throws ObjectNotFoundException, EchonetObjectException {
        LOGGER.entering(CLASS_NAME, "getRemoteData", new Object[]{node, eoj, epc});
        
        RemoteObject remoteObject = getRemoteObject(node, eoj);
        
        if (remoteObject == null) {
            ObjectNotFoundException exception = new ObjectNotFoundException(eoj.toString());
            LOGGER.throwing(CLASS_NAME, "getRemoteData", exception);
            throw exception;
        }
        
        ObjectData objectData = remoteObject.getData(epc);
        
        LOGGER.exiting(CLASS_NAME, "getRemoteData", objectData);
        return objectData;
    }
    
    
    public boolean setRemoteData(NodeInfo nodeInfo, EOJ eoj, EPC epc, ObjectData data) throws ObjectNotFoundException, SubnetException, EchonetObjectException {
        LOGGER.entering(CLASS_NAME, "setRemoteData", new Object[]{nodeInfo, eoj, epc, data});
        
        boolean result = setRemoteData(getRemoteNode(nodeInfo), eoj, epc, data);
        
        LOGGER.exiting(CLASS_NAME, "setRemoteData", result);
        return result;
    }
    
    public boolean setRemoteData(Node node, EOJ eoj, EPC epc, ObjectData data) throws ObjectNotFoundException, EchonetObjectException {
        LOGGER.entering(CLASS_NAME, "setRemoteData", new Object[]{eoj, epc, data});
        
        RemoteObject remoteObject = getRemoteObject(node, eoj);
        
        if (remoteObject == null) {
            ObjectNotFoundException exception = new ObjectNotFoundException(eoj.toString());
            LOGGER.throwing(CLASS_NAME, "setRemoteData", exception);
            throw exception;
        }
        
        boolean result = remoteObject.setData(epc, data);
        
        LOGGER.exiting(CLASS_NAME, "setRemoteData", result);
        return result;
    }
    
    public List<Node> getRemoteNodes() {
        return new ArrayList<Node>(getRemoteObjectManager().getNodes());
    }
    
    private List<EOJ> echonetObjectsToEOJs(Collection<? extends EchonetObject> objects) {
        ArrayList<EOJ> eojs = new ArrayList<EOJ>(objects.size());
        
        for (EchonetObject object: objects) {
            eojs.add(object.getEOJ());
        }
        
        return eojs;
    }
    
    public int countLocalEOJs() {
        return getLocalObjectManager().size();
    }
    
    public EOJ getLocalEOJ(int index) {
        return getLocalObjectManager().getAtIndex(index).getEOJ();
    }
    
    public List<EOJ> getLocalEOJs() {
        return echonetObjectsToEOJs(getLocalObjectManager().getAllObjects());
    }
    
    public int countRemoteEOJs(Node node) {
        return getRemoteObjectManager().getAtNode(node).size();
    }
    
    public int countRemoteEOJs(NodeInfo nodeInfo) throws SubnetException {
        Node node = getRemoteNode(nodeInfo);
        return getRemoteObjectManager().getAtNode(node).size();
    }
    
    public EOJ getRemoteEOJ(Node node, int index) {
        return getRemoteObjectManager().getAtNode(node).get(index).getEOJ();
    }
    
    public EOJ getRemoteEOJ(NodeInfo nodeInfo, int index) throws SubnetException {
        Node node = getRemoteNode(nodeInfo);
        return getRemoteObjectManager().getAtNode(node).get(index).getEOJ();
    }
    
    public List<EOJ> getRemoteEOJs(Node node) {
        return echonetObjectsToEOJs(getRemoteObjectManager().getAtNode(node));
    }
    
    public List<EOJ> getRemoteEOJs(NodeInfo nodeInfo) throws SubnetException {
        Node node = getRemoteNode(nodeInfo);
        return echonetObjectsToEOJs(getRemoteObjectManager().getAtNode(node));
    }
    
    public Node getLocalNode() {
        return getSubnet().getLocalNode();
    }
    
    public Node getRemoteNode(String name) throws SubnetException {
        return getSubnet().getRemoteNode(name);
    }
    
    public Node getRemoteNode(NodeInfo nodeInfo) throws SubnetException {
        return getSubnet().getRemoteNode(nodeInfo);
    }
    
    public Node getGroupNode() {
        return getSubnet().getGroupNode();
    }
    
    public List<LocalObject> getLocalObjects() {
        return getLocalObjectManager().getDeviceObjects();
    }
    
    public LocalObject getLocalObject(EOJ eoj) {
        return getLocalObjectManager().get(eoj);
    }
    
    public RemoteObject getRemoteObject(NodeInfo nodeInfo, EOJ eoj) throws SubnetException {
        return getRemoteObject(getRemoteNode(nodeInfo), eoj);
    }
    
    public RemoteObject getRemoteObject(Node node, EOJ eoj) {
        return getRemoteObjectManager().get(node, eoj);
    }
    
    public boolean registerRemoteEOJ(NodeInfo nodeInfo, EOJ eoj) throws SubnetException {
        return registerRemoteEOJ(getRemoteNode(nodeInfo), eoj);
    }
    
    public boolean registerRemoteEOJ(Node node, EOJ eoj) {
        RemoteObject object = new RemoteObject(getSubnet(), node, eoj, getTransactionManager());
        return getRemoteObjectManager().add(object);
    }
    
    public NotifyResult doNotify(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, properties, timeout, responseRequired, notifyListener});
        
        NotifyResult notifyResult = new NotifyResult(responseRequired, core.getTimestampManager());
        notifyResult.setNotifyListener(notifyListener);
        
        AnnounceTransactionConfig transactionConfig = new AnnounceTransactionConfig();
        transactionConfig.setResponseRequired(responseRequired);
        
        for (Pair<EPC, Data> pair : properties) {
            transactionConfig.addAnnounce(pair.first, pair.second);
        }
        
        transactionConfig.setReceiverNode(core.getSubnet().getGroupNode());
        transactionConfig.setSenderNode(core.getSubnet().getLocalNode());
        transactionConfig.setDestinationEOJ(new EOJ("0ef001"));
        transactionConfig.setSourceEOJ(eoj);
        
        Transaction transaction = core.getTransactionManager().createTransaction(transactionConfig);
        transaction.setTimeout(timeout);
        
        transaction.addTransactionListener(new ResultBaseTransactionListener(notifyResult));
        
        transaction.execute();
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }
    
    public NotifyResult doNotifyInstanceList(Node node, int timeout, boolean responseRequired, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{node, timeout, responseRequired, notifyListener});
        
        EOJ eoj = getCore().getNodeProfileObject().getEOJ();
        EPC epc = EPC.xD5;
        ObjectData data = getCore().getNodeProfileObject().forceGetData(epc);
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data.getData()));
        for (int i=0; i<data.getExtraSize(); i++) {
            properties.add(new Pair<EPC, Data>(epc, data.getExtraDataAt(i)));
        }
        
        NotifyResult notifyResult = doNotify(node, eoj, properties, timeout, responseRequired, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }
    
    public GetResult doGet(Node node, EOJ eoj, EPC epc, int timeout, GetListener getListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, eoj, epc, timeout, getListener});
        
        GetResult getResult = doGet(node, eoj, toList(epc), timeout, getListener);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(NodeInfo nodeInfo, EOJ eoj, EPC epc, int timeout, GetListener getListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, eoj, epc, timeout, getListener});
        
        GetResult getResult = doGet(getRemoteNode(nodeInfo), eoj, toList(epc), timeout, getListener);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(Node node, ClassEOJ ceoj, EPC epc, int timeout, GetListener getListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, ceoj, epc, timeout, getListener});
        
        GetResult getResult = doGet(node, ceoj.getAllInstanceEOJ(), toList(epc), timeout, getListener);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc, int timeout, GetListener getListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, ceoj, epc, timeout, getListener});
        
        GetResult getResult = doGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), toList(epc), timeout, getListener);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(NodeInfo nodeInfo, EOJ eoj, List<EPC> epcs, int timeout, GetListener getListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, eoj, epcs, timeout, getListener});
        
        GetResult getResult = doGet(getRemoteNode(nodeInfo), eoj, epcs, timeout, getListener);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(Node node, ClassEOJ ceoj, List<EPC> epcs, int timeout, GetListener getListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, ceoj, epcs, timeout, getListener});
        
        GetResult getResult = doGet(node, ceoj.getAllInstanceEOJ(), epcs, timeout, getListener);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(NodeInfo nodeInfo, ClassEOJ ceoj, List<EPC> epcs, int timeout, GetListener getListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, ceoj, epcs, timeout, getListener});
        
        GetResult getResult = doGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), epcs, timeout, getListener);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(Node node, EOJ eoj, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, eoj, epc, timeout});
        
        GetResult getResult = doGet(node, eoj, toList(epc), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(NodeInfo nodeInfo, EOJ eoj, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, eoj, epc, timeout});
        
        GetResult getResult = doGet(getRemoteNode(nodeInfo), eoj, toList(epc), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(Node node, ClassEOJ ceoj, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, ceoj, epc, timeout});
        
        GetResult getResult = doGet(node, ceoj.getAllInstanceEOJ(), toList(epc), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, ceoj, epc, timeout});
        
        GetResult getResult = doGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), toList(epc), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(Node node, EOJ eoj, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, eoj, epcs, timeout});
        
        GetResult getResult = doGet(node, eoj, epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(NodeInfo nodeInfo, EOJ eoj, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, eoj, epcs, timeout});
        
        GetResult getResult = doGet(getRemoteNode(nodeInfo), eoj, epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(Node node, ClassEOJ ceoj, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, ceoj, epcs, timeout});
        
        GetResult getResult = doGet(node, ceoj.getAllInstanceEOJ(), epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public GetResult doGet(NodeInfo nodeInfo, ClassEOJ ceoj, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, ceoj, epcs, timeout});
        
        GetResult getResult = doGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doGet", getResult);
        return getResult;
    }

    public SetResult doSet(Node node, EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, epc, data, timeout, responseRequired, setListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(node, eoj, properties, timeout, responseRequired, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, epc, data, timeout, responseRequired, setListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(getRemoteNode(nodeInfo), eoj, properties, timeout, responseRequired, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, ClassEOJ ceoj, EPC epc, Data data, int timeout, boolean responseRequired, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, epc, data, timeout, responseRequired, setListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(node, ceoj.getAllInstanceEOJ(), properties, timeout, responseRequired, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc, Data data, int timeout, boolean responseRequired, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, epc, data, timeout, responseRequired, setListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, timeout, responseRequired, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, EOJ eoj, EPC epc, Data data, int timeout, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, epc, data, timeout, setListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(node, eoj, properties, timeout, false, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, int timeout, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, epc, data, timeout, setListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(getRemoteNode(nodeInfo), eoj, properties, timeout, false, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, ClassEOJ ceoj, EPC epc, Data data, int timeout, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, epc, data, timeout, setListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(node, ceoj.getAllInstanceEOJ(), properties, timeout, false, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc, Data data, int timeout, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, epc, data, timeout, setListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, timeout, false, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, epc, data, timeout, responseRequired});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(node, eoj, properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, epc, data, timeout, responseRequired});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(getRemoteNode(nodeInfo), eoj, properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, ClassEOJ ceoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, epc, data, timeout, responseRequired});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(node, ceoj.getAllInstanceEOJ(), properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, epc, data, timeout, responseRequired});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, EOJ eoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, epc, data, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(node, eoj, properties, timeout, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, epc, data, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(getRemoteNode(nodeInfo), eoj, properties, timeout, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, ClassEOJ ceoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, epc, data, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(node, ceoj.getAllInstanceEOJ(), properties, timeout, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, epc, data, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        SetResult setResult = doSet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, timeout, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, properties, timeout, responseRequired, setListener});
        
        SetResult setResult = doSet(getRemoteNode(nodeInfo), eoj, properties, timeout, responseRequired, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, properties, timeout, responseRequired, setListener});
        
        SetResult setResult = doSet(node, ceoj.getAllInstanceEOJ(), properties, timeout, responseRequired, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, properties, timeout, responseRequired, setListener});
        
        SetResult setResult = doSet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, timeout, responseRequired, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, properties, timeout, setListener});
        
        SetResult setResult = doSet(node, eoj, properties, timeout, false, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, properties, timeout, setListener});
        
        SetResult setResult = doSet(getRemoteNode(nodeInfo), eoj, properties, timeout, false, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, properties, timeout, setListener});
        
        SetResult setResult = doSet(node, ceoj.getAllInstanceEOJ(), properties, timeout, false, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout, SetListener setListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, properties, timeout, setListener});
        
        SetResult setResult = doSet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, timeout, false, setListener);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, properties, timeout, responseRequired});
        
        SetResult setResult = doSet(node, eoj, properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, properties, timeout, responseRequired});
        
        SetResult setResult = doSet(getRemoteNode(nodeInfo), eoj, properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, properties, timeout, responseRequired});
        
        SetResult setResult = doSet(node, ceoj.getAllInstanceEOJ(), properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, properties, timeout, responseRequired});
        
        SetResult setResult = doSet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, properties, timeout});
        
        SetResult setResult = doSet(node, eoj, properties, timeout, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, properties, timeout});
        
        SetResult setResult = doSet(getRemoteNode(nodeInfo), eoj, properties, timeout, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(Node node, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, properties, timeout});
        
        SetResult setResult = doSet(node, ceoj.getAllInstanceEOJ(), properties, timeout, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetResult doSet(NodeInfo nodeInfo, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, properties, timeout});
        
        SetResult setResult = doSet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, timeout, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doSet", setResult);
        return setResult;
    }

    public SetGetResult doSetGet(Node node, EOJ eoj, EPC setEPC, Data data, EPC getEPC, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, eoj, setEPC, data, getEPC, timeout, setGetListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(node, eoj, properties, toList(getEPC), timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, EOJ eoj, EPC setEPC, Data data, EPC getEPC, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, eoj, setEPC, data, getEPC, timeout, setGetListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), eoj, properties, toList(getEPC), timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, ClassEOJ ceoj, EPC setEPC, Data data, EPC getEPC, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, ceoj, setEPC, data, getEPC, timeout, setGetListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(node, ceoj.getAllInstanceEOJ(), properties, toList(getEPC), timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC setEPC, Data data, EPC getEPC, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, ceoj, setEPC, data, getEPC, timeout, setGetListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, toList(getEPC), timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, EOJ eoj, EPC setEPC, Data data, List<EPC> epcs, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, eoj, setEPC, data, epcs, timeout, setGetListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(node, eoj, properties, epcs, timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, EOJ eoj, EPC setEPC, Data data, List<EPC> epcs, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, eoj, setEPC, data, epcs, timeout, setGetListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), eoj, properties, epcs, timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, ClassEOJ ceoj, EPC setEPC, Data data, List<EPC> epcs, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, ceoj, setEPC, data, epcs, timeout, setGetListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(node, ceoj.getAllInstanceEOJ(), properties, epcs, timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC setEPC, Data data, List<EPC> epcs, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, ceoj, setEPC, data, epcs, timeout, setGetListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, epcs, timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, EOJ eoj, EPC setEPC, Data data, EPC getEPC, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, eoj, setEPC, data, getEPC, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(node, eoj, properties, toList(getEPC), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, EOJ eoj, EPC setEPC, Data data, EPC getEPC, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, eoj, setEPC, data, getEPC, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), eoj, properties, toList(getEPC), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, ClassEOJ ceoj, EPC setEPC, Data data, EPC getEPC, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, ceoj, setEPC, data, getEPC, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(node, ceoj.getAllInstanceEOJ(), properties, toList(getEPC), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC setEPC, Data data, EPC getEPC, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, ceoj, setEPC, data, getEPC, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, toList(getEPC), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, EOJ eoj, EPC setEPC, Data data, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, eoj, setEPC, data, epcs, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(node, eoj, properties, epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, EOJ eoj, EPC setEPC, Data data, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, eoj, setEPC, data, epcs, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), eoj, properties, epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, ClassEOJ ceoj, EPC setEPC, Data data, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, ceoj, setEPC, data, epcs, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(node, ceoj.getAllInstanceEOJ(), properties, epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC setEPC, Data data, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, ceoj, setEPC, data, epcs, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(setEPC, data));
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, EPC epc, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, eoj, properties, epc, timeout, setGetListener});
        
        SetGetResult setGetResult = doSetGet(node, eoj, properties, toList(epc), timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, EPC epc, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, eoj, properties, epc, timeout, setGetListener});
        
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), eoj, properties, toList(epc), timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, EPC epc, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, ceoj, properties, epc, timeout, setGetListener});
        
        SetGetResult setGetResult = doSetGet(node, ceoj.getAllInstanceEOJ(), properties, toList(epc), timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, EPC epc, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, ceoj, properties, epc, timeout, setGetListener});
        
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, toList(epc), timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, List<EPC> epcs, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, eoj, properties, epcs, timeout, setGetListener});
        
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), eoj, properties, epcs, timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, List<EPC> epcs, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, ceoj, properties, epcs, timeout, setGetListener});
        
        SetGetResult setGetResult = doSetGet(node, ceoj.getAllInstanceEOJ(), properties, epcs, timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, List<EPC> epcs, int timeout, SetGetListener setGetListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, ceoj, properties, epcs, timeout, setGetListener});
        
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, epcs, timeout, setGetListener);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, eoj, properties, epc, timeout});
        
        SetGetResult setGetResult = doSetGet(node, eoj, properties, toList(epc), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, eoj, properties, epc, timeout});
        
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), eoj, properties, toList(epc), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, ceoj, properties, epc, timeout});
        
        SetGetResult setGetResult = doSetGet(node, ceoj.getAllInstanceEOJ(), properties, toList(epc), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, ceoj, properties, epc, timeout});
        
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, toList(epc), timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, eoj, properties, epcs, timeout});
        
        SetGetResult setGetResult = doSetGet(node, eoj, properties, epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, eoj, properties, epcs, timeout});
        
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), eoj, properties, epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(Node node, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{node, ceoj, properties, epcs, timeout});
        
        SetGetResult setGetResult = doSetGet(node, ceoj.getAllInstanceEOJ(), properties, epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public SetGetResult doSetGet(NodeInfo nodeInfo, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSetGet", new Object[]{nodeInfo, ceoj, properties, epcs, timeout});
        
        SetGetResult setGetResult = doSetGet(getRemoteNode(nodeInfo), ceoj.getAllInstanceEOJ(), properties, epcs, timeout, null);
        
        LOGGER.exiting(CLASS_NAME, "doSetGet", setGetResult);
        return setGetResult;
    }

    public ObserveResult doObserve(ObserveListener observeListener) {
        LOGGER.entering(CLASS_NAME, "doObserve", observeListener);
        
        ObserveResult observeResult = doObserve(new FrameSelector(), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Selector<? super Frame> selector) {
        LOGGER.entering(CLASS_NAME, "doObserve", selector);
        
        ObserveResult observeResult = doObserve(selector, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve() {
        LOGGER.entering(CLASS_NAME, "doObserve");
        
        ObserveResult observeResult = doObserve(new FrameSelector(), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, EOJ eoj, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, eoj, epcs, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(eoj), epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, EOJ eoj, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, eoj, epcs, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(eoj), epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(EOJ eoj, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{eoj, epcs, observeListener});
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(eoj), epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, ClassEOJ ceoj, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, ceoj, epcs, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(ceoj.getAllInstanceEOJ()), epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, ClassEOJ ceoj, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, ceoj, epcs, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(ceoj.getAllInstanceEOJ()), epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(ClassEOJ ceoj, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{ceoj, epcs, observeListener});
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(ceoj.getAllInstanceEOJ()), epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, epcs, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(node), (List)null, epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, epcs, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), (List)null, epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{epcs, observeListener});
        
        ObserveResult observeResult = doObserve((List)null, (List)null, epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, EOJ eoj, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, eoj, epc, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(eoj), toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, EOJ eoj, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, eoj, epc, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(eoj), toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(EOJ eoj, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{eoj, epc, observeListener});
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(eoj), toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, ClassEOJ ceoj, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, ceoj, epc, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(ceoj.getAllInstanceEOJ()), toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, ceoj, epc, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(ceoj.getAllInstanceEOJ()), toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(ClassEOJ ceoj, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{ceoj, epc, observeListener});
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(ceoj.getAllInstanceEOJ()), toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, epc, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(node), (List)null, toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, epc, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), (List)null, toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{epc, observeListener});
        
        ObserveResult observeResult = doObserve((List)null, (List)null, toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, EOJ eoj, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, eoj, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(eoj), (List<EPC>)null, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, EOJ eoj, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, eoj, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(eoj), (List<EPC>)null, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(EOJ eoj, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{eoj, observeListener});
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(eoj), (List<EPC>)null, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, ClassEOJ ceoj, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, ceoj, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(ceoj.getAllInstanceEOJ()), (List<EPC>)null, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, ClassEOJ ceoj, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, ceoj, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(ceoj.getAllInstanceEOJ()), (List<EPC>)null, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(ClassEOJ ceoj, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{ceoj, observeListener});
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(ceoj.getAllInstanceEOJ()), (List<EPC>)null, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(node), (List)null, (List<EPC>)null, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, observeListener});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), (List)null, (List<EPC>)null, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, EOJ eoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, eoj, epcs});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(eoj), epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, EOJ eoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, eoj, epcs});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(eoj), epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(EOJ eoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{eoj, epcs});
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(eoj), epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, ClassEOJ ceoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, ceoj, epcs});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(ceoj.getAllInstanceEOJ()), epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, ClassEOJ ceoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, ceoj, epcs});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(ceoj.getAllInstanceEOJ()), epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(ClassEOJ ceoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{ceoj, epcs});
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(ceoj.getAllInstanceEOJ()), epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, epcs});
        
        ObserveResult observeResult = doObserve(toObjectList(node), (List)null, epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, epcs});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), (List)null, epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", epcs);
        
        ObserveResult observeResult = doObserve((List)null, (List)null, epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, EOJ eoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, eoj, epc});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(eoj), toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, EOJ eoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, eoj, epc});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(eoj), toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(EOJ eoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{eoj, epc});
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(eoj), toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, ClassEOJ ceoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, ceoj, epc});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(ceoj.getAllInstanceEOJ()), toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, ceoj, epc});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(ceoj.getAllInstanceEOJ()), toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(ClassEOJ ceoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{ceoj, epc});
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(ceoj.getAllInstanceEOJ()), toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, epc});
        
        ObserveResult observeResult = doObserve(toObjectList(node), (List)null, toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, epc});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), (List)null, toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", epc);
        
        ObserveResult observeResult = doObserve((List)null, (List)null, toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, EOJ eoj) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, eoj});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(eoj), (List<EPC>)null, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, EOJ eoj) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, eoj});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(eoj), (List<EPC>)null, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(EOJ eoj) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", eoj);
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(eoj), (List<EPC>)null, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node, ClassEOJ ceoj) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, ceoj});
        
        ObserveResult observeResult = doObserve(toObjectList(node), toObjectList(ceoj.getAllInstanceEOJ()), (List<EPC>)null, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo, ClassEOJ ceoj) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, ceoj});
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), toObjectList(ceoj.getAllInstanceEOJ()), (List<EPC>)null, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(ClassEOJ ceoj) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", ceoj);
        
        ObserveResult observeResult = doObserve((List)null, toObjectList(ceoj.getAllInstanceEOJ()), (List<EPC>)null, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(Node node) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", node);
        
        ObserveResult observeResult = doObserve(toObjectList(node), (List)null, (List<EPC>)null, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(NodeInfo nodeInfo) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", nodeInfo);
        
        ObserveResult observeResult = doObserve(toObjectList(nodeInfo), (List)null, (List<EPC>)null, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, EOJ eoj, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eoj, epcs, observeListener});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(eoj), epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, ClassEOJ ceoj, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, ceoj, epcs, observeListener});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(ceoj.getAllInstanceEOJ()), epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, List<EPC> epcs, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, epcs, observeListener});
        
        ObserveResult observeResult = doObserve(nodes, (List)null, epcs, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, List eojs, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eojs, epc, observeListener});
        
        ObserveResult observeResult = doObserve(nodes, eojs, toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, EOJ eoj, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eoj, epc, observeListener});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(eoj), toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, ClassEOJ ceoj, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, ceoj, epc, observeListener});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(ceoj.getAllInstanceEOJ()), toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, EPC epc, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, epc, observeListener});
        
        ObserveResult observeResult = doObserve(nodes, (List)null, toList(epc), observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, EOJ eoj, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eoj, observeListener});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(eoj), (List<EPC>)null, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, ClassEOJ ceoj, ObserveListener observeListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, ceoj, observeListener});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(ceoj.getAllInstanceEOJ()), (List<EPC>)null, observeListener);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, List eojs, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eojs, epcs});
        
        ObserveResult observeResult = doObserve(nodes, eojs, epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, EOJ eoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eoj, epcs});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(eoj), epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, ClassEOJ ceoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, ceoj, epcs});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(ceoj.getAllInstanceEOJ()), epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, epcs});
        
        ObserveResult observeResult = doObserve(nodes, (List)null, epcs, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, List eojs, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eojs, epc});
        
        ObserveResult observeResult = doObserve(nodes, eojs, toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, EOJ eoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eoj, epc});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(eoj), toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, ClassEOJ ceoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, ceoj, epc});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(ceoj.getAllInstanceEOJ()), toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, epc});
        
        ObserveResult observeResult = doObserve(nodes, (List)null, toList(epc), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, EOJ eoj) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eoj});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(eoj), (List<EPC>)null, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public ObserveResult doObserve(List nodes, ClassEOJ ceoj) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, ceoj});
        
        ObserveResult observeResult = doObserve(nodes, toObjectList(ceoj.getAllInstanceEOJ()), (List<EPC>)null, null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", observeResult);
        return observeResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, epc, data, timeout, responseRequired, notifyListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(node, eoj, properties, timeout, responseRequired, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, epc, data, timeout, responseRequired, notifyListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, timeout, responseRequired, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, epc, data, timeout, responseRequired, notifyListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, timeout, responseRequired, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, EPC epc, Data data, int timeout, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, epc, data, timeout, notifyListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(node, eoj, properties, timeout, true, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, int timeout, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, epc, data, timeout, notifyListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, timeout, true, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, EPC epc, Data data, int timeout, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, epc, data, timeout, notifyListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, timeout, true, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, EPC epc, Data data, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, epc, data, notifyListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(node, eoj, properties, 0, false, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, epc, data, notifyListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, 0, false, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, EPC epc, Data data, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, epc, data, notifyListener});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, 0, false, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, epc, data, timeout, responseRequired});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(node, eoj, properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, epc, data, timeout, responseRequired});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, epc, data, timeout, responseRequired});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, epc, data, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(node, eoj, properties, timeout, true, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, epc, data, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, timeout, true, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, epc, data, timeout});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, timeout, true, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, EPC epc, Data data) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, epc, data});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(node, eoj, properties, 0, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, epc, data});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, 0, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, EPC epc, Data data) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, epc, data});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, 0, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, properties, timeout, responseRequired, notifyListener});
        
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, timeout, responseRequired, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, properties, timeout, responseRequired, notifyListener});
        
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, timeout, responseRequired, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, properties, timeout, notifyListener});
        
        NotifyResult notifyResult = doNotify(node, eoj, properties, timeout, true, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, properties, timeout, notifyListener});
        
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, timeout, true, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, properties, timeout, notifyListener});
        
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, timeout, true, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, properties, notifyListener});
        
        NotifyResult notifyResult = doNotify(node, eoj, properties, 0, false, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, properties, notifyListener});
        
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, 0, false, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, List<Pair<EPC, Data>> properties, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, properties, notifyListener});
        
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, 0, false, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, properties, timeout, responseRequired});
        
        NotifyResult notifyResult = doNotify(node, eoj, properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, properties, timeout, responseRequired});
        
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, properties, timeout, responseRequired});
        
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, properties, timeout});
        
        NotifyResult notifyResult = doNotify(node, eoj, properties, timeout, true, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, properties, timeout});
        
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, timeout, true, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, properties, timeout});
        
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, timeout, true, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(Node node, EOJ eoj, List<Pair<EPC, Data>> properties) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{node, eoj, properties});
        
        NotifyResult notifyResult = doNotify(node, eoj, properties, 0, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{nodeInfo, eoj, properties});
        
        NotifyResult notifyResult = doNotify(getRemoteNode(nodeInfo), eoj, properties, 0, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotify(EOJ eoj, List<Pair<EPC, Data>> properties) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotify", new Object[]{eoj, properties});
        
        NotifyResult notifyResult = doNotify((Node)null, eoj, properties, 0, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotify", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(NodeInfo nodeInfo, int timeout, boolean responseRequired, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{nodeInfo, timeout, responseRequired, notifyListener});
        
        NotifyResult notifyResult = doNotifyInstanceList(getRemoteNode(nodeInfo), timeout, responseRequired, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(int timeout, boolean responseRequired, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{timeout, responseRequired, notifyListener});
        
        NotifyResult notifyResult = doNotifyInstanceList((Node)null, timeout, responseRequired, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(Node node, int timeout, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{node, timeout, notifyListener});
        
        NotifyResult notifyResult = doNotifyInstanceList(node, timeout, true, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(NodeInfo nodeInfo, int timeout, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{nodeInfo, timeout, notifyListener});
        
        NotifyResult notifyResult = doNotifyInstanceList(getRemoteNode(nodeInfo), timeout, true, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(int timeout, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{timeout, notifyListener});
        
        NotifyResult notifyResult = doNotifyInstanceList((Node)null, timeout, true, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(Node node, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{node, notifyListener});
        
        NotifyResult notifyResult = doNotifyInstanceList(node, 0, false, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(NodeInfo nodeInfo, NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{nodeInfo, notifyListener});
        
        NotifyResult notifyResult = doNotifyInstanceList(getRemoteNode(nodeInfo), 0, false, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(NotifyListener notifyListener) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", notifyListener);
        
        NotifyResult notifyResult = doNotifyInstanceList((Node)null, 0, false, notifyListener);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(Node node, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{node, timeout, responseRequired});
        
        NotifyResult notifyResult = doNotifyInstanceList(node, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(NodeInfo nodeInfo, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{nodeInfo, timeout, responseRequired});
        
        NotifyResult notifyResult = doNotifyInstanceList(getRemoteNode(nodeInfo), timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{timeout, responseRequired});
        
        NotifyResult notifyResult = doNotifyInstanceList((Node)null, timeout, responseRequired, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(Node node, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{node, timeout});
        
        NotifyResult notifyResult = doNotifyInstanceList(node, timeout, true, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(NodeInfo nodeInfo, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", new Object[]{nodeInfo, timeout});
        
        NotifyResult notifyResult = doNotifyInstanceList(getRemoteNode(nodeInfo), timeout, true, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", timeout);
        
        NotifyResult notifyResult = doNotifyInstanceList((Node)null, timeout, true, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(Node node) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", node);
        
        NotifyResult notifyResult = doNotifyInstanceList(node, 0, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList(NodeInfo nodeInfo) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList", nodeInfo);
        
        NotifyResult notifyResult = doNotifyInstanceList(getRemoteNode(nodeInfo), 0, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

    public NotifyResult doNotifyInstanceList() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doNotifyInstanceList");
        
        NotifyResult notifyResult = doNotifyInstanceList((Node)null, 0, false, null);
        
        LOGGER.exiting(CLASS_NAME, "doNotifyInstanceList", notifyResult);
        return notifyResult;
    }

}
