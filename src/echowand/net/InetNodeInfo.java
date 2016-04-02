package echowand.net;

import java.net.InetAddress;

/**
 * IPサブネットを利用するノードに関する情報
 * @author ymakino
 */
public class InetNodeInfo implements NodeInfo {
    private InetAddress address;
    private int portNumber;
    
    /**
     * 指定されたアドレスで、IPサブネットのノード情報を生成する。
     * @param address アドレスの指定
     */
    public InetNodeInfo(InetAddress address) {
        this.address = address;
        this.portNumber = -1;
    }
    
    /**
     * 指定されたアドレスとポート番号で、IPサブネットのノード情報を生成する。
     * ポート番号を指定しない場合には、portNumberに-1を設定して呼び出す。
     * @param address アドレスの指定
     * @param portNumber ポート番号の指定
     */
    public InetNodeInfo(InetAddress address, int portNumber) {
        this.address = address;
        
        if (portNumber < 0) {
            this.portNumber = -1;
        } else {
            this.portNumber = 0x0000ffff & portNumber;
        }
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
     * ポート番号の指定がない場合には-1を返す。
     * @return ポート番号
     */
    public int getPortNumber() {
        return portNumber;
    }
    
    public boolean hasPortNumber() {
        return portNumber != -1;
    }
    
    /**
     * このノード情報を文字列で表現する
     * @return ノードの文字列表現
     */
    @Override
    public String toString() {
        if (hasPortNumber()) {
            if (getAddress().getHostAddress().contains(":")) {
                return address.getHostAddress() + "." + portNumber;
            } else {
                return address.getHostAddress() + ":" + portNumber;
            }
        } else {
            return address.getHostAddress();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (! (o instanceof InetNodeInfo)) {
            return false;
        }
        
        InetNodeInfo info = (InetNodeInfo)o;
        return (portNumber == info.portNumber && address.equals(info.address));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.address != null ? this.address.hashCode() : 0);
        return hash;
    }
}
