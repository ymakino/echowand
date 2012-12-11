package echowand.net;

/**
 * プログラム内でのみ有効なサブネット用のノード
 * @author Yoshiki Makino
 */
public class InternalNode implements Node{
    private InternalSubnet subnet;
    private String name;
    
    /**
     * InternalNodeを生成する。
     * 直接生成は行わずにInternalSubnetのgetRemoteNodeメソッドの利用を推奨する。
     * @param subnet このノードの存在するサブネット
     * @param name このノードの名前
     */
    public InternalNode(InternalSubnet subnet, String name) {
        this.subnet = subnet;
        this.name = name;
    }
    
    /**
     * 名前を返す。
     * @return このノードの名前
     */
    public String getName() {
        return name;
    }
    
    @Override
    public boolean isMemberOf(Subnet subnet) {
        return this.subnet == subnet;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof InternalNode) {
            InternalNode node = (InternalNode)o;
            
            InternalNetwork n1 = subnet.getNetwork();
            InternalNetwork n2 = node.subnet.getNetwork();
            
            return n1.equals(n2) && name.equals(node.name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
