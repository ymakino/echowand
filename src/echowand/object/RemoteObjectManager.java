package echowand.object;

import echowand.common.EOJ;
import echowand.net.Node;
import echowand.util.Collector;
import echowand.util.Selector;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * リモートオブジェクトを管理。
 * @author Yoshiki Makino
 */
public class RemoteObjectManager {
    private HashMap<Node, HashMap<EOJ, RemoteObject>> objects;
    
    /**
     * RemoteObjectManagerを生成する。
     */
    public RemoteObjectManager() {
        this.objects = new HashMap<Node, HashMap<EOJ, RemoteObject>>();
    }
    
    private synchronized HashMap<EOJ, RemoteObject> getOrCreateNodeHashMap(Node node) {
        HashMap<EOJ, RemoteObject> map = objects.get(node);
        if (map == null) {
            map = new HashMap<EOJ, RemoteObject>();
            objects.put(node, map);
        }
        return map;
    }
    
    /**
     * 指定されたRemoteObjectを登録する。
     * @param object 登録するRemoteObject
     */
    public synchronized void add(RemoteObject object) {
        HashMap<EOJ, RemoteObject> map = getOrCreateNodeHashMap(object.getNode());
        map.put(object.getEOJ(), object);
    }
    
    /**
     * 指定されたRemoteObjectの登録を抹消する。
     * @param object 登録を抹消するRemoteObject
     */
    public synchronized void remove(RemoteObject object) {
        HashMap<EOJ, RemoteObject> map = getOrCreateNodeHashMap(object.getNode());
        map.remove(object.getEOJ());
    }
    
    /**
     * 指定されたNode内で、指定されたEOJにより識別されるRemoteObjectを取得する。
     * @param node Nodeの指定
     * @param eoj EOJの指定
     * @return 指定したNodeとEOJで検索されたRemoteObject
     */
    public synchronized RemoteObject get(Node node, EOJ eoj) {
        HashMap<EOJ, RemoteObject> map = getOrCreateNodeHashMap(node);
        return map.get(eoj);
    }
    
    private synchronized LinkedList<RemoteObject> getAllObjects() {
        LinkedList<RemoteObject> newList = new LinkedList<RemoteObject>();
        for (Node node : objects.keySet()) {
            HashMap<EOJ, RemoteObject> objectsAtNode = objects.get(node);
            for (EOJ eoj : objectsAtNode.keySet()) {
                newList.add(objectsAtNode.get(eoj));
            }
        }
        return newList;
    }
    
    /**
     * 指定されたNode内のRemoteObjectを取得する。
     * @param node Nodeの指定
     * @param eoj EOJの指定
     * @return 指定したNodeとEOJで検索されたRemoteObject
     */
    public synchronized LinkedList<RemoteObject> getAtNode(final Node node) {
        return get(new Selector<RemoteObject>() {
            @Override
            public boolean select(RemoteObject object) {
                return object.getNode().equals(node);
            }
        });
    }
    
    /**
     * Selectorが真を返すリモートオブジェクトを選択し、そのリストを返す。
     * @param selector リモートオブジェクトの選択
     * @return 選択したリモートオブジェクトのリスト
     */
    public LinkedList<RemoteObject> get(Selector<RemoteObject> selector) {
        Collector<RemoteObject> collector = new Collector<RemoteObject>(selector);
        return collector.collect(getAllObjects());
    }
    
    /**
     * ノードのリストを返す。
     * @return ノードのリスト
     */
    public LinkedList<Node> getNodes() {
        return new LinkedList<Node>(objects.keySet());
    }
}
