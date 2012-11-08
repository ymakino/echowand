package echowand.net;

import java.net.InetAddress;

/**
 * IPネットワークのサブネットに存在するノード
 * @author Yoshiki Makino
 */
public class InetNode implements Node {
    private InetSubnet subnet;
    private InetAddress addr;
    private int port;
        
    /**
     * InetNodeを生成する。トランスポートプロトコルは常にUDPになる。
     * 直接生成は行わずにInetSubnetのgetRemoteNodeメソッドの利用を推奨する。
     * @param subnet このノードの存在するサブネット
     * @param addr このノードのIPアドレス
     * @param port このノードのポート番号
     */
    public InetNode(InetSubnet subnet, InetAddress addr, int port) {
        this.subnet = subnet;
        this.addr = addr;
        this.port = port;
    }
    
    /**
     * ポート番号を返す。
     * @return ポート番号
     */
    public int getPort() {
        return port;
    }
    
    /**
     * IPアドレスを返す。
     * @return IPアドレス
     */
    public InetAddress getAddress() {
        return addr;
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
        return addr.getHostAddress() + ":" + port;
    }
    
    @Override
    public boolean equals(Object o) {
        if (! (o instanceof InetNode)) {
            return false;
        }
        InetNode node = (InetNode)o;
        return (this.addr.equals(node.addr) && this.port == node.port && this.subnet.equals(node.subnet));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.addr != null ? this.addr.hashCode() : 0);
        hash = 67 * hash + this.port;
        return hash;
    }
}