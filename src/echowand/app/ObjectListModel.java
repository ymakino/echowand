package echowand.app;

import echowand.net.Node;
import echowand.object.RemoteObject;
import echowand.object.RemoteObjectManager;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectListModel extends AbstractListModel {
    private RemoteObjectManager remoteManager;
    private LinkedList<Node> nodes;
    private LinkedList<RemoteObject> objects;

    ObjectListModel(RemoteObjectManager remoteManager) {
        this.remoteManager = remoteManager;
        this.nodes = new LinkedList<Node>();
        this.objects = new LinkedList<RemoteObject>();
    }
    
    private boolean areSameObjects(LinkedList<RemoteObject> objects1, LinkedList<RemoteObject> objects2) {
        if (objects1.size() != objects2.size()) {
            return false;
        }
        
        for (int i=0; i<objects1.size(); i++) {
            RemoteObject object1 = objects1.get(i);
            RemoteObject object2 = objects2.get(i);
            if (!object1.equals(object2)) {
                return false;
            }
        }
        
        return true;
    }
    
    private LinkedList<RemoteObject> getObjectsAtNode(Node node) {
        return remoteManager.getAtNode(node);
    }
    
    public void updateObjects() {
        LinkedList<RemoteObject> oldObjects = objects;
        objects = new LinkedList<RemoteObject>();
        
        for (Node node : nodes) {
            objects.addAll(getObjectsAtNode(node));
        }
        
        if (areSameObjects(objects, oldObjects)) {
            return;
        }
        
        if (oldObjects.size() > 0) {
            fireIntervalRemoved(this, 0, oldObjects.size() - 1);
        }
        
        if (objects.size() > 0) {
            fireIntervalAdded(this, 0, objects.size() - 1);
        }
    }

    @Override
    public int getSize() {
        return objects.size();
    }

    @Override
    public Object getElementAt(int index) {
        return objects.get(index);
    }

    void selectNodes(List<Node> nodes) {
        this.nodes = new LinkedList<Node>(nodes);
        updateObjects();
    }
    
}
