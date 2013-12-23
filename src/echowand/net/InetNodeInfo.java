package echowand.net;

import java.net.InetAddress;

/**
 * IPサブネットを利用するノードに関する情報
 * @author ymakino
 */
public class InetNodeInfo implements NodeInfo {
    private InetAddress address;
    
    /**
     * 指定されたアドレスとポート番号で、IPサブネットのノード情報を生成する。
     * @param address アドレスの指定
     */
    public InetNodeInfo(InetAddress address) {
        this.address = address;
    }
    
    /**
     * このノード情報が持っているアドレスを返す。
     * @return アドレス
     */
    public InetAddress getAddress() {
        return address;
    }
    
    /**
     * このノード情報を文字列で表現する
     * @return ノードの文字列表現
     */
    @Override
    public String toString() {
        return address.getHostAddress();
    }
    
    @Override
    public boolean equals(Object o) {
        if (! (o instanceof InetNodeInfo)) {
            return false;
        }
        
        InetNodeInfo info = (InetNodeInfo)o;
        return (this.address.equals(info.address));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.address != null ? this.address.hashCode() : 0);
        return hash;
    }
}
