package echowand.app;

import echowand.net.Node;
import echowand.object.RemoteObjectManager;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Yoshiki Makino
 */
public class NodeListModel extends AbstractListModel {
    private RemoteObjectManager remoteManager;
    private List<Node> nodes;

    NodeListModel(RemoteObjectManager remoteManager) {
        this.remoteManager = remoteManager;
        this.nodes = remoteManager.getNodes();
    }
    
    private List<Node> subtractNodeList(List<Node> base, List<Node> subst) {
        LinkedList<Node> resultList = new LinkedList<Node>();
        int size = base.size();
        for (int i=0; i<size; i++) {
            Node node = base.get(i);
            if (!subst.contains(node)) {
                resultList.add(node);
            }
        }
        
        return resultList;
    }
    
    public synchronized void updateNodes() {
        List<Node> newNodes = remoteManager.getNodes();
        
        List<Node> removedNodes = subtractNodeList(nodes, newNodes);
        List<Node> addedNodes = subtractNodeList(newNodes, nodes);
        
        if (removedNodes.size() == 0 && addedNodes.size() == 0) {
            return;
        }
        
        for (int i=0; i<removedNodes.size(); i++) {
            int index = nodes.indexOf(removedNodes.get(i));
            nodes.remove(index);
            fireIntervalRemoved(this, index, index);
        }
        
        if (addedNodes.size() > 0) {
            int oldSize = nodes.size();
            int newSize = oldSize + addedNodes.size();
            nodes.addAll(addedNodes);
            fireIntervalAdded(this, oldSize, newSize - 1);
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
