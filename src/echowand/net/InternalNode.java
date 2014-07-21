package echowand.net;

/**
 * プログラム内でのみ有効なサブネット用のノード
 * @author Yoshiki Makino
 */
public class InternalNode implements Node{
    private InternalSubnet subnet;
    private InternalNodeInfo nodeInfo;
    
    /**
     * InternalNodeを生成する。
     * 直接生成は行わずにInternalSubnetのgetRemoteNodeメソッドの利用を推奨する。
     * @param subnet このノードの存在するサブネット
     * @param name このノードの名前
     */
    public InternalNode(InternalSubnet subnet, String name) {
        this(subnet, new InternalNodeInfo(name));
    }
    
    /**
     * InternalNodeを生成する。
     * 直接生成は行わずにInternalSubnetのgetRemoteNodeメソッドの利用を推奨する。
     * @param subnet このノードの存在するサブネット
     * @param nodeInfo このノードの情報
     */
    public InternalNode(InternalSubnet subnet, InternalNodeInfo nodeInfo) {
        this.subnet = subnet;
        this.nodeInfo = nodeInfo;
    }
    
    /**
     * 名前を返す。
     * @return このノードの名前
     */
    public String getName() {
        return nodeInfo.getName();
    }
    
    @Override
    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }
    
    @Override
    public boolean isMemberOf(Subnet subnet) {
        return this.subnet == subnet;
    }
    
    @Override
    public String toString() {
        return nodeInfo.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof InternalNode) {
            InternalNode node = (InternalNode)o;
            
            InternalNetwork n1 = subnet.getNetwork();
            InternalNetwork n2 = node.subnet.getNetwork();
            
            return n1.equals(n2) && nodeInfo.equals(node.nodeInfo);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.nodeInfo != null ? this.nodeInfo.hashCode() : 0);
        return hash;
    }
}
