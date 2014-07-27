package echowand.service;

import echowand.common.ClassEOJ;
import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
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
import echowand.service.result.FrameMatcher;
import echowand.service.result.FrameMatcherRule;
import echowand.service.result.ResultBase;
import echowand.service.result.ResultGet;
import echowand.service.result.ResultObserve;
import echowand.service.result.ResultSet;
import echowand.service.result.ResultUpdate;
import echowand.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class Service {
    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());
    private static final String CLASS_NAME = Service.class.getName();
    
    private ServiceManager serviceManager;
    
    public Service(ServiceManager serviceManager) {
        LOGGER.entering(CLASS_NAME, "Service", serviceManager);
        
        this.serviceManager = serviceManager;
        
        LOGGER.exiting(CLASS_NAME, "Service");
    }
    
    public ServiceManager getServiceManager() {
        return serviceManager;
    }
    
    public Subnet getSubnet() {
        return serviceManager.getSubnet();
    }
    
    public LocalObjectManager getLocalObjectManager() {
        return serviceManager.getLocalObjectManager();
    }
    
    public RemoteObjectManager getRemoteObjectManager() {
        return serviceManager.getRemoteObjectManager();
    }
    
    public TransactionManager getTransactionManager() {
        return serviceManager.getTransactionManager();
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
            
            LOGGER.exiting(CLASS_NAME, "ResultBaseTransactionListener.begin");
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
            
            result.done();
            
            LOGGER.exiting(CLASS_NAME, "ResultBaseTransactionListener.finish");
        }
    }
    
    private class ResultUpdateTransactionListener implements TransactionListener {
        private ResultUpdate resultUpdate;

        public ResultUpdateTransactionListener(ResultUpdate resultUpdate) {
            LOGGER.entering(CLASS_NAME, "ResultUpdateTransactionListener.begin", resultUpdate);
            
            this.resultUpdate = resultUpdate;
            
            LOGGER.exiting(CLASS_NAME, "ResultUpdateTransactionListener.begin");
        }

        @Override
        public void begin(Transaction t) {
            LOGGER.entering(CLASS_NAME, "ResultUpdateTransactionListener.begin", t);
            
            LOGGER.exiting(CLASS_NAME, "ResultUpdateTransactionListener.begin");
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
    
    private <T> List<T> toList(T... objects) {
        LinkedList<T> list = new LinkedList<T>();
        list.addAll(Arrays.asList(objects));
        return list;
    }
    
    public ResultGet doGet(Node node, ClassEOJ ceoj, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, ceoj, epc, timeout});
        
        ResultGet resultGet = doGet(node, ceoj.getAllInstanceEOJ(), toList(epc), timeout);
        
        LOGGER.exiting(CLASS_NAME, "doGet", resultGet);
        return resultGet;
    }
    
    public ResultGet doGet(Node node, EOJ eoj, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, eoj, epc, timeout});
        
        ResultGet resultGet = doGet(node, eoj, toList(epc), timeout);
        
        LOGGER.exiting(CLASS_NAME, "doGet", resultGet);
        return resultGet;
    }
    
    public ResultGet doGet(Node node, ClassEOJ ceoj, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, ceoj, epcs, timeout});
        
        ResultGet resultGet = doGet(node, ceoj.getAllInstanceEOJ(), epcs, timeout);
        
        LOGGER.exiting(CLASS_NAME, "doGet", resultGet);
        return resultGet;
    }
    
    public ResultGet doGet(Node node, EOJ eoj, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{node, eoj, epcs, timeout});
        
        ResultGet resultGet = new ResultGet();
        
        SetGetTransactionConfig transactionConfig = createGetTransactionConfig(node, eoj, epcs);
        
        Transaction transaction = new Transaction(getSubnet(), getTransactionManager(), transactionConfig);
        transaction.setTimeout(timeout);
        
        transaction.addTransactionListener(new ResultBaseTransactionListener(resultGet));
        
        transaction.execute();
        
        LOGGER.exiting(CLASS_NAME, "doGet", resultGet);
        return resultGet;
    }
    
    public ResultGet doGet(NodeInfo nodeInfo, EOJ eoj, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, eoj, epc, timeout});
        
        ResultGet resultGet = doGet(getSubnet().getRemoteNode(nodeInfo), eoj, toList(epc), timeout);
        
        LOGGER.exiting(CLASS_NAME, "doGet", resultGet);
        return resultGet;
    }
    
    public ResultGet doGet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, ceoj, epc, timeout});
        
        ResultGet resultGet = doGet(getSubnet().getRemoteNode(nodeInfo), ceoj, toList(epc), timeout);
        
        LOGGER.exiting(CLASS_NAME, "doGet", resultGet);
        return resultGet;
    }
    
    public ResultGet doGet(NodeInfo nodeInfo, EOJ eoj, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, eoj, epcs, timeout});
        
        ResultGet resultGet = doGet(getSubnet().getRemoteNode(nodeInfo), eoj, epcs, timeout);
        
        LOGGER.exiting(CLASS_NAME, "doGet", resultGet);
        return resultGet;
    }
    
    public ResultGet doGet(NodeInfo nodeInfo, ClassEOJ ceoj, List<EPC> epcs, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doGet", new Object[]{nodeInfo, ceoj, epcs, timeout});
        
        ResultGet resultGet = doGet(getSubnet().getRemoteNode(nodeInfo), ceoj, epcs, timeout);
        
        LOGGER.exiting(CLASS_NAME, "doGet", resultGet);
        return resultGet;
    }
    
    public ResultSet doSet(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, properties, timeout, responseRequired});
        
        ResultSet resultSet = new ResultSet(responseRequired);
        
        SetGetTransactionConfig transactionConfig = createSetTransactionConfig(node, eoj, properties);
        transactionConfig.setResponseRequired(responseRequired);
        
        Transaction transaction = new Transaction(getSubnet(), getTransactionManager(), transactionConfig);
        transaction.setTimeout(timeout);
        
        transaction.addTransactionListener(new ResultBaseTransactionListener(resultSet));
        
        transaction.execute();

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(Node node, EOJ eoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, epc, data, timeout});
        
        ResultSet resultSet = doSet(node, eoj, epc, data, timeout, true);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(Node node, ClassEOJ ceoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, epc, data, timeout});
        
        ResultSet resultSet = doSet(node, ceoj, epc, data, timeout, true);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(Node node, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, properties, timeout});
                
        ResultSet resultSet = doSet(node, eoj, properties, timeout, true);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(Node node, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, properties, timeout});
                
        ResultSet resultSet = doSet(node, ceoj, properties, timeout, true);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, epc, data, timeout});
                
        ResultSet resultSet = doSet(nodeInfo, eoj, epc, data, timeout, true);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc, Data data, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, epc, data, timeout});
        
        ResultSet resultSet = doSet(nodeInfo, ceoj, epc, data, timeout, true);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, properties, timeout});
        
        ResultSet resultSet = doSet(nodeInfo, eoj, properties, timeout, true);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(NodeInfo nodeInfo, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, properties, timeout});
        
        ResultSet resultSet = doSet(nodeInfo, ceoj, properties, timeout, true);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(Node node, EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, eoj, epc, data, timeout, responseRequired});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        ResultSet resultSet = doSet(node, eoj, properties, timeout, responseRequired);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(Node node, ClassEOJ ceoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, epc, data, timeout, responseRequired});
        
        LinkedList<Pair<EPC, Data>> properties = new LinkedList<Pair<EPC, Data>>();
        properties.add(new Pair<EPC, Data>(epc, data));
        ResultSet resultSet = doSet(node, ceoj.getAllInstanceEOJ(), properties, timeout, responseRequired);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(Node node, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{node, ceoj, properties, timeout, responseRequired});
        
        ResultSet resultSet = doSet(node, ceoj.getAllInstanceEOJ(), properties, timeout, responseRequired);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(NodeInfo nodeInfo, EOJ eoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, epc, data, timeout, responseRequired});
        
        ResultSet resultSet = doSet(getSubnet().getRemoteNode(nodeInfo), eoj, epc, data, timeout, responseRequired);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc, Data data, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, epc, data, timeout, responseRequired});
        
        ResultSet resultSet = doSet(getSubnet().getRemoteNode(nodeInfo), ceoj, epc, data, timeout, responseRequired);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(NodeInfo nodeInfo, EOJ eoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, eoj, properties, timeout, responseRequired});
        
        ResultSet resultSet = doSet(getSubnet().getRemoteNode(nodeInfo), eoj, properties, timeout, responseRequired);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultSet doSet(NodeInfo nodeInfo, ClassEOJ ceoj, List<Pair<EPC, Data>> properties, int timeout, boolean responseRequired) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doSet", new Object[]{nodeInfo, ceoj, properties, timeout, responseRequired});
        
        ResultSet resultSet = doSet(getSubnet().getRemoteNode(nodeInfo), ceoj, properties, timeout, responseRequired);

        LOGGER.exiting(CLASS_NAME, "doSet", resultSet);
        return resultSet;
    }
    
    public ResultUpdate doUpdate(int timeout) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doUpdate", timeout);
        
        InstanceListRequestExecutor executor = new InstanceListRequestExecutor(
                getSubnet(), getTransactionManager(), getRemoteObjectManager());
        
        ResultUpdate resultUpdate = new ResultUpdate(executor);
        ResultUpdateTransactionListener resultUpdateTransactionListener = new ResultUpdateTransactionListener(resultUpdate);

        executor.setTimeout(timeout);
        executor.addTransactionListener(resultUpdateTransactionListener);
        
        executor.execute();
        
        LOGGER.exiting(CLASS_NAME, "doUpdate", resultUpdate);
        return resultUpdate;
    }
    
    public ResultObserve doObserve(Node node, EOJ eoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, eoj, epc});
        
        ResultObserve resultObserve = doObserve(new FrameMatcherRule(node, eoj, epc));
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(Node node, ClassEOJ ceoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, ceoj, epc});
        
        ResultObserve resultObserve = doObserve(new FrameMatcherRule(node, ceoj, epc));
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(Node node, EOJ eoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, eoj, epcs});
        
        ResultObserve resultObserve = doObserve(new FrameMatcherRule(node, eoj, epcs));
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(Node node, ClassEOJ ceoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{node, ceoj, epcs});
        
        ResultObserve resultObserve = doObserve(new FrameMatcherRule(node, ceoj, epcs));
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(NodeInfo nodeInfo, EOJ eoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, eoj, epc});
        
        ResultObserve resultObserve = doObserve(getSubnet().getRemoteNode(nodeInfo), eoj, epc);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(NodeInfo nodeInfo, ClassEOJ ceoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, ceoj, epc});
        
        ResultObserve resultObserve = doObserve(getSubnet().getRemoteNode(nodeInfo), ceoj, epc);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(NodeInfo nodeInfo, EOJ eoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, eoj, epcs});
        
        ResultObserve resultObserve = doObserve(getSubnet().getRemoteNode(nodeInfo), eoj, epcs);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(NodeInfo nodeInfo, ClassEOJ ceoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodeInfo, ceoj, epcs});
        
        ResultObserve resultObserve = doObserve(getSubnet().getRemoteNode(nodeInfo), ceoj, epcs);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(EOJ eoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{eoj, epc});
        
        ResultObserve resultObserve = doObserve(null, toList(eoj), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(ClassEOJ ceoj, EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{ceoj, epc});
        
        ResultObserve resultObserve = doObserve(null, toList(ceoj.getAllInstanceEOJ()), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(EOJ eoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{eoj, epcs});
        
        ResultObserve resultObserve = doObserve(null, toList(eoj), epcs);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(ClassEOJ ceoj, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{ceoj, epcs});
        
        ResultObserve resultObserve = doObserve(null, toList(ceoj.getAllInstanceEOJ()), epcs);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(EOJ eoj) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", eoj);
        
        ResultObserve resultObserve = doObserve(null, toList(eoj), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(ClassEOJ ceoj) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", ceoj);
        
        ResultObserve resultObserve = doObserve(null, toList(ceoj.getAllInstanceEOJ()), null);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(EPC epc) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", epc);
        
        ResultObserve resultObserve = doObserve(null, new ArrayList<EOJ>(), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", epcs);
        
        ResultObserve resultObserve = doObserve(null, new ArrayList<EOJ>(), epcs);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve() {
        LOGGER.entering(CLASS_NAME, "doObserve");
        
        ResultObserve resultObserve = doObserve(new FrameMatcherRule());
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    private Node toNodeFromNodeOrNodeInfo(Object o) throws SubnetException {
        if (o instanceof Node) {
            return (Node)o;
        }
        
        if (o instanceof NodeInfo) {
            NodeInfo nodeInfo = (NodeInfo)o;
            return getSubnet().getRemoteNode(nodeInfo);
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
    
    public ResultObserve doObserve(List nodes, List<EOJ> eojs, List<EPC> epcs) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "doObserve", new Object[]{nodes, eojs, epcs});
        
        ResultObserve resultObserve = doObserve(new FrameMatcherRule(toNodesFromNodesAndNodeInfos(nodes), eojs, epcs));
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
    }
    
    public ResultObserve doObserve(FrameMatcher matcher) {
        LOGGER.entering(CLASS_NAME, "doObserve", matcher);
        
        ResultObserveProcessor processor = getServiceManager().getObserveServiceProsessor();
        ResultObserve resultObserve = new ResultObserve(matcher, processor);
        
        processor.addResultObserve(resultObserve);
        
        LOGGER.exiting(CLASS_NAME, "doObserve", resultObserve);
        return resultObserve;
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
    
    private List<EOJ> localObjectsToEOJs(List<LocalObject> objects) {
        ArrayList<EOJ> eojs = new ArrayList<EOJ>(objects.size());
        
        for (EchonetObject object: objects) {
            eojs.add(object.getEOJ());
        }
        
        return eojs;
    }
    
    private List<EOJ> remoteObjectsToEOJs(List<RemoteObject> objects) {
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
        return localObjectsToEOJs(getLocalObjectManager().getAllObjects());
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
        return remoteObjectsToEOJs(getRemoteObjectManager().getAtNode(node));
    }
    
    public List<EOJ> getRemoteEOJs(NodeInfo nodeInfo) throws SubnetException {
        Node node = getRemoteNode(nodeInfo);
        return remoteObjectsToEOJs(getRemoteObjectManager().getAtNode(node));
    }
    
    public Node getLocalNode() {
        return getSubnet().getLocalNode();
    }
    
    public Node getRemoteNode(NodeInfo nodeInfo) throws SubnetException {
        return getSubnet().getRemoteNode(nodeInfo);
    }
    
    public Node getGroupNode() {
        return getSubnet().getGroupNode();
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
    
    public RemoteObject registerRemoteObject(NodeInfo nodeInfo, EOJ eoj) throws SubnetException {
        return registerRemoteObject(getRemoteNode(nodeInfo), eoj);
    }
    
    public RemoteObject registerRemoteObject(Node node, EOJ eoj) {
        RemoteObject object = new RemoteObject(getSubnet(), node, eoj, getTransactionManager());
        getRemoteObjectManager().add(object);
        return object;
    }
}
