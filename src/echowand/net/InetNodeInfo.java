package echowand.net;

import java.net.InetAddress;

/**
 * IPサブネットを利用するノードに関する情報
 * @author ymakino
 */
public class InetNodeInfo implements NodeInfo {
    private InetAddress address;
    private int port;
    
    /**
     * 指定されたアドレスとポート番号で、IPサブネットのノード情報を生成する。
     * @param address アドレスの指定
     * @param port ポート番号の指定
     */
    public InetNodeInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }
    
    /**
     * このノード情報が持っているアドレスを返す。
     * @return アドレス
     */
    public InetAddress getAddress() {
        return address;
    }
    
    /**
     * このノード情報が持っているポート番号を返す。
     * @return ポート番号
     */
    public int getPort() {
        return port;
    }
    
    /**
     * このノード情報を文字列で表現する
     * @return ノードの文字列表現
     */
    @Override
    public String toString() {
        return address.getHostAddress() + ":" + port;
    }
    
    @Override
    public boolean equals(Object o) {
        if (! (o instanceof InetNodeInfo)) {
            return false;
        }
        
        InetNodeInfo info = (InetNodeInfo)o;
        return (this.address.equals(info.address) && (this.port == info.port));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.address != null ? this.address.hashCode() : 0);
        hash = 71 * hash + this.port;
        return hash;
    }
}
