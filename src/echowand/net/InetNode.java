package echowand.net;

import java.net.InetAddress;

/**
 * IPv4ネットワークのサブネットに存在するノード
 * @author Yoshiki Makino
 */
public class InetNode implements Node {
    private InetSubnet subnet;
    private InetNodeInfo nodeInfo;
        
    /**
     * InetNodeを生成する。
     * 直接生成は行わずにInetSubnetのgetRemoteNodeメソッドの利用を推奨する。
     * @param subnet このノードの存在するサブネット
     * @param address このノードのIPv4アドレス
     */
    public InetNode(InetSubnet subnet, InetAddress address) {
        this(subnet, new InetNodeInfo(address));
    }
    
    /**
     * InetNodeを生成する。
     * 直接生成は行わずにInetSubnetのgetRemoteNodeメソッドの利用を推奨する。
     * @param subnet このノードの存在するサブネット
     * @param nodeInfo このノードの情報
     */
    public InetNode(InetSubnet subnet, InetNodeInfo nodeInfo) {
        this.subnet = subnet;
        this.nodeInfo = nodeInfo;
    }
    
    /**
     * このノードのノード情報を返す。
     * @return ノード情報
     */
    @Override
    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }
    
    /**
     * IPアドレスを返す。
     * @return IPv4アドレス
     */
    public InetAddress getAddress() {
        return nodeInfo.getAddress();
    }
    
    @Override
    public boolean isMemberOf(Subnet subnet) {
        return this.subnet == subnet;
    }
    
    /**
     * このノードを文字列で表現する
     * @return ノードの文字列表現
     */
    @Override
    public String toString() {
        return nodeInfo.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (! (o instanceof InetNode)) {
            return false;
        }
        
        InetNode node = (InetNode)o;
        return (getNodeInfo().equals(node.getNodeInfo()) && subnet.equals(node.subnet));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.nodeInfo != null ? this.nodeInfo.hashCode() : 0);
        return hash;
    }
}