package echowand.object;

import echowand.common.EOJ;
import echowand.net.Node;
import echowand.util.Collector;
import echowand.util.Selector;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * リモートオブジェクトを管理。
 * @author Yoshiki Makino
 */
public class RemoteObjectManager {
    private static final Logger logger = Logger.getLogger(RemoteObjectManager.class.getName());
    private static final String className = RemoteObjectManager.class.getName();
    
    private HashMap<Node, HashMap<EOJ, RemoteObject>> objects;
    
    /**
     * RemoteObjectManagerを生成する。
     */
    public RemoteObjectManager() {
        logger.entering(className, "RemoteObjectManager");
        
        this.objects = new HashMap<Node, HashMap<EOJ, RemoteObject>>();
        
        logger.exiting(className, "RemoteObjectManager");
    }
    
    private synchronized HashMap<EOJ, RemoteObject> getOrCreateNodeHashMap(Node node) {
        logger.entering(className, "getOrCreateNodeHashMap", node);
        
        HashMap<EOJ, RemoteObject> map = objects.get(node);
        if (map == null) {
            map = new HashMap<EOJ, RemoteObject>();
            objects.put(node, map);
        }
        
        logger.exiting(className, "getOrCreateNodeHashMap", map);
        return map;
    }
    
    /**
     * 指定されたRemoteObjectを登録する。
     * 既にオブジェクトのEOJが登録済の場合には何も行わない。
     * @param object 登録するRemoteObject
     * @return 登録に成功した場合はtrue、失敗した場合はfalse
     */
    public synchronized boolean add(RemoteObject object) {
        logger.entering(className, "add", object);
        
        boolean result = false;
        
        HashMap<EOJ, RemoteObject> map = getOrCreateNodeHashMap(object.getNode());
        if (!map.containsKey(object.getEOJ())) {
            map.put(object.getEOJ(), object);
            result = true;
        }
        
        logger.exiting(className, "add", result);
        return result;
    }
    
    /**
     * 指定されたRemoteObjectの登録を抹消する。
     * @param object 登録を抹消するRemoteObject
     * @return 登録の抹消に成功した場合はtrue、失敗した場合はfalse
     */
    public synchronized boolean remove(RemoteObject object) {
        logger.entering(className, "remove", object);
        
        boolean result = false;
        
        HashMap<EOJ, RemoteObject> map = getOrCreateNodeHashMap(object.getNode());
        if (map.remove(object.getEOJ()) != null) {
            result = true;
        }
        
        logger.exiting(className, "remove", result);
        return result;
    }
    
    /**
     * 指定されたNode内で、指定されたEOJにより識別されるRemoteObjectを取得する。
     * @param node Nodeの指定
     * @param eoj EOJの指定
     * @return 指定したNodeとEOJで検索されたRemoteObject
     */
    public synchronized RemoteObject get(Node node, EOJ eoj) {
        logger.entering(className, "get", new Object[]{node, eoj});
        
        HashMap<EOJ, RemoteObject> map = getOrCreateNodeHashMap(node);
        RemoteObject object = map.get(eoj);
        
        logger.exiting(className, "get", object);
        return object;
    }
    
    private synchronized List<RemoteObject> getAllObjects() {
        logger.entering(className, "getAllObjects");
        
        LinkedList<RemoteObject> newList = new LinkedList<RemoteObject>();
        for (Node node : objects.keySet()) {
            HashMap<EOJ, RemoteObject> objectsAtNode = objects.get(node);
            for (RemoteObject obj : objectsAtNode.values()) {
                newList.add(obj);
            }
        }
        
        logger.exiting(className, "getAllObjects", newList);
        return newList;
    }
    
    /**
     * 指定されたNode内のRemoteObjectを取得する。
     * @param node Nodeの指定
     * @return 指定したNodeとEOJで検索されたRemoteObject
     */
    public synchronized List<RemoteObject> getAtNode(final Node node) {
        logger.entering(className, "getAtNode", node);
        
        List<RemoteObject> objectList = get(new Selector<RemoteObject>() {
            @Override
            public boolean match(RemoteObject object) {
                return object.getNode().equals(node);
            }
        });
        
        logger.exiting(className, "getAtNode", objectList);
        return objectList;
    }
    
    /**
     * Selectorが真を返すリモートオブジェクトを選択し、そのリストを返す。
     * @param selector リモートオブジェクトの選択
     * @return 選択したリモートオブジェクトのリスト
     */
    public List<RemoteObject> get(Selector<? super RemoteObject> selector) {
        logger.entering(className, "get", selector);
        
        Collector<RemoteObject> collector = new Collector<RemoteObject>(selector);
        List<RemoteObject> objectList = collector.collect(getAllObjects());
        
        logger.exiting(className, "get", objectList);
        return objectList;
    }
    
    /**
     * ノードのリストを返す。
     * @return ノードのリスト
     */
    public List<Node> getNodes() {
        logger.entering(className, "getNodes");
        
        LinkedList<Node> nodeList = new LinkedList<Node>(objects.keySet());
        
        logger.exiting(className, "getNodes", nodeList);
        return nodeList;
    }
}
