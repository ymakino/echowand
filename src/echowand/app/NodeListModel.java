package echowand.app;

import echowand.net.Node;
import echowand.object.RemoteObjectManager;
import java.util.LinkedList;
import javax.swing.AbstractListModel;

/**
 *
 * @author Yoshiki Makino
 */
public class NodeListModel extends AbstractListModel {
    private RemoteObjectManager remoteManager;
    private LinkedList<Node> nodes;

    NodeListModel(RemoteObjectManager remoteManager) {
        this.remoteManager = remoteManager;
        this.nodes = remoteManager.getNodes();
    }
    
    private boolean areSameNodes(LinkedList<Node> nodes1, LinkedList<Node> nodes2) {
        if (nodes1.size() != nodes2.size()) {
            return false;
        }
        
        for (int i=0; i<nodes1.size(); i++) {
            Node node1 = nodes1.get(i);
            Node node2 = nodes2.get(i);
            if (!node1.equals(node2)) {
                return false;
            }
        }
        
        return true;
    }
    
    public void updateNodes() {
        LinkedList<Node> oldNodes = nodes;
        nodes = remoteManager.getNodes();
        if (areSameNodes(nodes, oldNodes)) {
            return;
        }
        
        if (oldNodes.size() != 0) {
            fireIntervalRemoved(this, 0, oldNodes.size() - 1);
        }
        
        if (nodes.size() != 0) {
            fireIntervalAdded(this, 0, nodes.size() - 1);
        }
    }

    @Override
    public int getSize() {
        return nodes.size();
    }

    @Override
    public Object getElementAt(int index) {
        return nodes.get(index);
    }
    
}
