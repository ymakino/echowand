package echowand.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IPv4ネットワークのサブネット
 * @author Yoshiki Makino
 */
public class InetSubnet implements Subnet {
    
    /**
     * 利用するデータグラムネットワーク
     */
    private UDPNetwork udpNetwork;
    
    /**
     * 利用するコネクションネットワーク
     */
    private TCPNetwork tcpNetwork;
    
    private NetworkInterface networkInterface;
    private InetAddress multicastAddress;
    private int portNumber;
    
    private InetAddress localAddress;
    private InetNode groupNode;
    private InetNode localNode;
    
    /**
     * InetSubnetの初期化を行う。
     * @param networkInterface ネットワークインタフェースの指定
     * @param multicastAddress マルチキャストアドレスの指定
     * @param portNumber ポート番号の指定
     * @throws SubnetException 生成に失敗した場合
     */
    protected void initialize(NetworkInterface networkInterface, InetAddress multicastAddress, int portNumber) throws SubnetException {
        if (!isValidAddress(multicastAddress)) {
            throw new SubnetException("invalid multicast address: " + multicastAddress);
        }
        
        this.networkInterface = networkInterface;
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
        
        localAddress = null;
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
            InetAddress address = addresses.nextElement();
            if (isValidAddress(address)) {
                localAddress = address;
                break;
            }
        }
        
        if (localAddress == null) {
            throw new SubnetException("invalid interface: " + networkInterface);
        }
    }
    
    /**
     * InetSubnetの初期化を行う。
     * @param localAddress ローカルアドレスの指定
     * @param multicastAddress マルチキャストアドレスの指定
     * @param portNumber ポート番号の指定
     * @throws SubnetException 生成に失敗した場合
     */
    protected void initialize(InetAddress localAddress, InetAddress multicastAddress, int portNumber) throws SubnetException {
        if (!isValidAddress(localAddress)) {
            throw new SubnetException("invalid local address: " + localAddress);
        }
                
        if (!isValidAddress(multicastAddress)) {
            throw new SubnetException("invalid multicast address: " + multicastAddress);
        }
        
        try {
            this.localAddress = localAddress;
            networkInterface = NetworkInterface.getByInetAddress(localAddress);
            this.multicastAddress = multicastAddress;
            this.portNumber = portNumber;
        } catch (SocketException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }
    
    private void createUDPNetwork() {
        udpNetwork = new UDPNetwork(this, networkInterface, multicastAddress);
    }

    private void createTCPNetwork() throws NetworkException {
        InetNode node = (InetNode) getLocalNode();
        TCPConnectionListener listener = new TCPConnectionListener(node.getAddress(), portNumber);
        TCPConnectionCreator creator = new TCPConnectionCreator();
        tcpNetwork = new TCPNetwork(this, listener, creator);
    }

    private UDPNetwork getUDPNetwork() {
        return udpNetwork;
    }
    
    private TCPNetwork getTCPNetwork() {
        return tcpNetwork;
    }
    
    /**
     * 設定されたネットワークインタフェースを返す。
     * @return 設定されたネットワークインタフェース
     */
    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }
    
    /**
     * このInetSubnetが実行中であるか返す。
     * @return 実行中であればtrue、そうでなければfalse
     */
    public synchronized boolean isWorking() {
        UDPNetwork network = getUDPNetwork();
        
        if (network == null) {
            return false;
        }
        
        return network.isWorking();
    }
    
    /**
     * このInetSubnetを停止する。
     * @return 実行中から停止に変更した場合はtrue、そうでなければfalse
     */
    public synchronized boolean stopService() {
        if (!isWorking()) {
            return false;
        }
        
        if (tcpNetwork != null) {
            if (!tcpNetwork.stopService()) {
                return false;
            }
        }

        return getUDPNetwork().stopService();
    }
    
    /**
     * このInetSubnetを実行する。
     * @return 停止から実行中に変更した場合はtrue、そうでなければfalse
     * @throws SubnetException 実行に失敗した場合
     */
    public synchronized boolean startService() throws SubnetException {
        if (isWorking()) {
            return false;
        }

        try {
            createUDPNetwork();
            createTCPNetwork();
        
            if (tcpNetwork != null) {
                if (!tcpNetwork.startService()) {
                    return false;
                }
            }
            
            boolean result = getUDPNetwork().startService();
            
            if (result == false && tcpNetwork != null) {
                tcpNetwork.stopService();
            }

            if (receiveQueue != null) {
                for (;;) {
                    if (receiveQueue.poll() == null) {
                        break;
                    }
                }
            } else {
                receiveQueue = new SynchronousQueue<Frame>();
                new Thread(new InetSubnetUDPReceiver()).start();
                new Thread(new InetSubnetTCPReceiver()).start();
            }

            return result;
        } catch (NetworkException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }
    
    /**
     * 新たにTCP接続を確立し、フレームに設定を行う。
     * @param frame TCP接続を利用するフレーム
     * @throws SubnetException すでに接続が存在する、あるいは接続の確立に失敗した場合
     */
    public void createTCPConnection(Frame frame) throws SubnetException {
        try {
            Connection connection = frame.getConnection();
            
            if (connection != null) {
                throw new SubnetException("connection exists: " + frame.getConnection());
            }
            
            if (getGroupNode().equals(frame.getReceiver())) {
                throw new SubnetException("invalid destination: " + frame.getReceiver());
            }
            
            getTCPNetwork().createConnection(frame);
        } catch (NetworkException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }
    
    /**
     * TCP接続を解放し、フレームに設定を行う。
     * @param frame TCP接続を開放するフレーム
     * @throws SubnetException 接続が存在しない、あるいは接続の開放に失敗した場合
     */
    public void deleteTCPConnection(Frame frame) throws SubnetException {
        try {
            Connection connection = frame.getConnection();

            if (connection == null) {
                throw new SubnetException("no connection: " + frame);
            }

            getTCPNetwork().deleteConnection(frame);
        } catch (NetworkException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }
    
    /**
     * このInetSubnetのサブネットにフレームを転送する。
     * フレームの送信ノードや受信ノードがこのInetSubnetに含まれない場合には例外が発生する。
     * @param frame 送信するフレーム
     * @return 常にtrue
     * @throws SubnetException 送信に失敗した場合
     */
    @Override
    public boolean send(Frame frame) throws SubnetException {
        if (!isWorking()) {
            throw new SubnetException("not enabled");
        }

        if (!frame.getSender().isMemberOf(this)) {
            throw new SubnetException("invalid sender");
        }

        if (!frame.getReceiver().isMemberOf(this)) {
            throw new SubnetException("invalid receiver");
        }

        try {
            Connection connection = frame.getConnection();
            
            if (connection == null) {
                getUDPNetwork().send(frame);
            } else {
                getTCPNetwork().send(frame);
            }
            
            return true;
        } catch (NetworkException ex) {
            System.out.println(ex);
            throw new SubnetException("catched exception", ex);
        }
    }
    
    /**
     * このInetSubnetのサブネットからフレームを受信する。
     * 少なくとも1つのフレームの受信を行うまで待機する。
     * @return 受信したFrame
     * @throws SubnetException 無効なフレームを受信、あるいは受信に失敗した場合
     */
    @Override
    public Frame receive()  throws SubnetException {
        if (!isWorking()) {
            throw new SubnetException("not enabled");
        }
        
        try {
            return receiveQueue.take();
        } catch (InterruptedException ex) {
            throw new SubnetException("catched exception", ex);
        }
    }
    
    private SynchronousQueue<Frame> receiveQueue = null;
    
    private class InetSubnetUDPReceiver implements Runnable {
        
        @Override
        public void run() {
            try {
                for (;;) {
                    Frame frame = getUDPNetwork().receive();
                    if (isWorking()) {
                        receiveQueue.put(frame);
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(InetSubnet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NetworkException ex) {
                Logger.getLogger(InetSubnet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class InetSubnetTCPReceiver implements Runnable {

        @Override
        public void run() {
            try {
                for (;;) {
                    Frame frame = getTCPNetwork().receive();
                    if (isWorking()) {
                        receiveQueue.put(frame);
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(InetSubnet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NetworkException ex) {
                Logger.getLogger(InetSubnet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * 指定されたアドレスが有効であるか返す。
     * @param address アドレスの指定
     * @return アドレスが有効であればtrue、それ以外の場合にはfalse
     */
    public boolean isValidAddress(InetAddress address) {
        return true;
    }
    
    
    /**
     * 指定されたノード情報が有効であるか返す。
     * @param nodeInfo ノード情報の指定
     * @return ノード情報が有効であればtrue、それ以外の場合にはfalse
     */
    public boolean isValidNodeInfo(InetNodeInfo nodeInfo) {
        return isValidAddress(nodeInfo.getAddress());
    }
    
    /**
     * リモートノードを表すNodeを生成する。
     * @param addr リモートノードのIPv4アドレス
     * @param port リモートノードのポート番号
     * @return リモートノードのNode
     * @throws SubnetException 無効なアドレスが指定された場合
     */
    public Node getRemoteNode(InetAddress addr, int port) throws SubnetException {
        if (isValidAddress(addr)) {
            return new InetNode(this, addr, port);
        } else {
            throw new SubnetException("invalid address: " + addr);
        }
    }
    
    /**
     * リモートノードを表すNodeを生成する。
     * @param addr リモートノードのIPv4アドレス
     * @return リモートノードのNode
     * @throws SubnetException 無効なアドレスが指定された場合
     */
    public Node getRemoteNode(InetAddress addr) throws SubnetException {
        if (isValidAddress(addr)) {
            return new InetNode(this, addr, portNumber);
        } else {
            throw new SubnetException("invalid address: " + addr);
        }
    }
    
    /**
     * リモートノードを表すNodeを生成する。
     * @param nodeInfo リモートノードの情報
     * @return リモートノードのNode
     * @throws SubnetException 無効なノード情報が指定された場合
     */
    @Override
    public Node getRemoteNode(NodeInfo nodeInfo) throws SubnetException {
        if (nodeInfo instanceof InetNodeInfo) {
            InetNodeInfo inetNodeInfo = (InetNodeInfo)nodeInfo;
            
            if (isValidNodeInfo(inetNodeInfo)) {
                return new InetNode(this, (InetNodeInfo)nodeInfo);
            } else {
                throw new SubnetException("invalid nodeInfo: " + nodeInfo);
            }
        } else {
            throw new SubnetException("invalid nodeInfo: " + nodeInfo);
        }
    }
    
    /**
     * ローカルノードを表すNodeを返す。
     * @return ローカルノードのNode
     */
    @Override
    public synchronized Node getLocalNode() {
        if (localNode == null) {
            localNode = new InetNode(this, localAddress, portNumber);
        }
        
        return localNode;
    }
    
    /**
     * グループを表すNodeを返す。
     * このノード宛にフレームを転送するとマルチキャスト転送になる。
     * @return グループのNode
     */
    @Override
    public synchronized Node getGroupNode() {
        if (groupNode == null) {
            groupNode = new InetNode(this, multicastAddress, portNumber);
        }
        
        return groupNode;
    }
}
