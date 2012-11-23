package echowand.net;

import java.net.Inet6Address;

/**
 * IPv6ネットワークのサブネットに存在するノード
 * @author Yoshiki Makino
 */
public class Inet6Node implements Node {
    private Inet6Subnet subnet;
    private Inet6Address addr;
    private int port;
        
    /**
     * Inet6Nodeを生成する。トランスポートプロトコルは常にUDPになる。
     * 直接生成は行わずにInet6SubnetのgetRemoteNodeメソッドの利用を推奨する。
     * @param subnet このノードの存在するサブネット
     * @param addr このノードのIPv6アドレス
     * @param port このノードのポート番号
     */
    public Inet6Node(Inet6Subnet subnet, Inet6Address addr, int port) {
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
     * IPv6アドレスを返す。
     * @return IPv6アドレス
     */
    public Inet6Address getAddress() {
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
        if (! (o instanceof Inet6Node)) {
            return false;
        }
        Inet6Node node = (Inet6Node)o;
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