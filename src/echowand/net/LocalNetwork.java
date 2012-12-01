package echowand.net;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * プログラム内でのみ利用可能な簡易ブロードキャストネットワーク
 * @author Yoshiki Makino
 */
public class LocalNetwork {
    private static final String DEFAULT_NAME = "DEFAULT";
    private static HashMap<String, LocalNetwork> networks = new HashMap<String, LocalNetwork>();
    
    /**
     * デフォルトのLocalNetworkを返す。
     * @return デフォルトのLocalNetwork
     */
    public static LocalNetwork getDefault() {
        return getByName(DEFAULT_NAME);
    }
    
    /**
     * 指定された名前のLocalNetworkを返す。
     * 初めて利用される名前が指定された場合には新たにLocalNetworkを生成する。
     * @param name LocalNetworkの名前
     * @return 指定された名前のLocalNetwork
     */
    public static LocalNetwork getByName(String name) {
        LocalNetwork network = networks.get(name);
        
        if (network == null) {
            network = new LocalNetwork();
            networks.put(name, network);
        }
        
        return network;
    }
    
    private LinkedList<LocalNetworkPort> ports;

    /**
     * LocalNetworkを生成する。
     * 本コンストラクタによって生成されたLocalNetworkは名前による管理は行われない。
     * getDefault または getByNameの利用を推奨する。
     */
    public LocalNetwork() {
        ports = new LinkedList<LocalNetworkPort>();
    }

    /**
     * このLocalNetworkにポートを追加する。
     * @param port 追加するポート
     * @return 追加が成功した場合はtrue、そうでなければfalse
     */
    public synchronized boolean addPort(LocalNetworkPort port) {
        if (!this.equals(port.getNetwork())) {
            port.setNetwork(this);
        }
        
        if (ports.contains(port)) {
            return true;
        }
        
        return ports.add(port);
    }

    /**
     * このLocalNetworkからポートを削除する。
     * @param port 削除するポート
     * @return 削除が成功した場合はtrue、そうでなければfalse
     */
    public synchronized boolean removePort(LocalNetworkPort port) {
        if (this.equals(port.getNetwork())) {
            port.setNetwork(null);
        }
        
        return ports.remove(port);
    }
    

    /**
     * このLocalNetworkにフレームを転送する。
     * このLocalNetworkに接続している全てのポートの受信キューに、指定されたフレームが追加される。
     * @param frame 転送するフレーム
     * @throws SubnetException 転送に失敗した場合
     */
    public synchronized void broadcast(Frame frame) throws SubnetException {
        for (LocalNetworkPort port : ports) {
            port.enqueue(frame);
        }
    }
}