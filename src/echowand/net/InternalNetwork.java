package echowand.net;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * プログラム内でのみ利用可能な簡易ブロードキャストネットワーク
 * @author Yoshiki Makino
 */
public class InternalNetwork {
    private static final String DEFAULT_NAME = "DEFAULT";
    private static HashMap<String, InternalNetwork> networks = new HashMap<String, InternalNetwork>();
    
    /**
     * デフォルトのInternalNetworkを返す。
     * @return デフォルトのInternalNetwork
     */
    public static InternalNetwork getDefault() {
        return getByName(DEFAULT_NAME);
    }
    
    /**
     * 指定された名前のInternalNetworkを返す。
     * 初めて利用される名前が指定された場合には新たにInternalNetworkを生成する。
     * @param name InternalNetworkの名前
     * @return 指定された名前のInternalNetwork
     */
    public static InternalNetwork getByName(String name) {
        InternalNetwork network = networks.get(name);
        
        if (network == null) {
            network = new InternalNetwork();
            networks.put(name, network);
        }
        
        return network;
    }
    
    private LinkedList<InternalNetworkPort> ports;

    /**
     * InternalNetworkを生成する。
     * 本コンストラクタによって生成されたInternalNetworkは名前による管理は行われない。
     * getDefault または getByNameの利用を推奨する。
     */
    public InternalNetwork() {
        ports = new LinkedList<InternalNetworkPort>();
    }

    /**
     * このInternalNetworkにポートを追加する。
     * @param port 追加するポート
     * @return 追加が成功した場合はtrue、そうでなければfalse
     */
    public synchronized boolean addPort(InternalNetworkPort port) {
        if (!this.equals(port.getNetwork())) {
            port.setNetwork(this);
        }
        
        if (ports.contains(port)) {
            return true;
        }
        
        return ports.add(port);
    }

    /**
     * このInternalNetworkからポートを削除する。
     * @param port 削除するポート
     * @return 削除が成功した場合はtrue、そうでなければfalse
     */
    public synchronized boolean removePort(InternalNetworkPort port) {
        if (this.equals(port.getNetwork())) {
            port.setNetwork(null);
        }
        
        return ports.remove(port);
    }
    

    /**
     * このInternalNetworkにフレームを転送する。
     * このInternalNetworkに接続している全てのポートの受信キューに、指定されたフレームが追加される。
     * @param frame 転送するフレーム
     * @throws SubnetException 転送に失敗した場合
     */
    public synchronized void broadcast(Frame frame) throws SubnetException {
        for (InternalNetworkPort port : ports) {
            port.enqueue(frame);
        }
    }
}