package echowand.net;

import java.net.Inet4Address;

/**
 * IPv4ネットワークのサブネットに存在するノード
 * @author Yoshiki Makino
 */
public class Inet4Node implements Node {
    private Inet4Subnet subnet;
    private Inet4Address addr;
    private int port;
        
    /**
     * Inet4Nodeを生成する。トランスポートプロトコルは常にUDPになる。
     * 直接生成は行わずにInet4SubnetのgetRemoteNodeメソッドの利用を推奨する。
     * @param subnet このノードの存在するサブネット
     * @param addr このノードのIPv4アドレス
     * @param port このノードのポート番号
     */
    public Inet4Node(Inet4Subnet subnet, Inet4Address addr, int port) {
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
     * IPv4アドレスを返す。
     * @return IPv4アドレス
     */
    public Inet4Address getAddress() {
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
        if (! (o instanceof Inet4Node)) {
            return false;
        }
        Inet4Node node = (Inet4Node)o;
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