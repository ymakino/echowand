package echowand.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IPv4ネットワークのサブネット
 * @author Yoshiki Makino
 */
public class InetSubnet implements Subnet {
    private static final Logger LOGGER = Logger.getLogger(InetSubnet.class.getName());
    private static final String CLASS_NAME = InetSubnet.class.getName();
    
    /**
     * 利用するUDPネットワーク
     */
    private UDPNetwork udpNetwork;
    
    /**
     * 利用するTCPネットワーク
     */
    private TCPNetwork tcpNetwork;
    
    private NetworkInterface networkInterface;
    private InetAddress multicastAddress;
    private int portNumber;
    
    private InetAddress localAddress;
    private InetNode groupNode;
    private InetNode localNode;
    
    private InetSubnetUDPReceiver udpReceiver;
    private InetSubnetTCPReceiver tcpReceiver;
    
    private boolean tcpEnabled = false;
    
    /**
     * InetSubnetの初期化を行う。
     * @param networkInterface ネットワークインタフェースの指定
     * @param multicastAddress マルチキャストアドレスの指定
     * @param portNumber ポート番号の指定
     * @throws SubnetException 生成に失敗した場合
     */
    protected void initialize(NetworkInterface networkInterface, InetAddress multicastAddress, int portNumber) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "initialize", new Object[]{networkInterface, multicastAddress, portNumber});
        
        if (!isValidAddress(multicastAddress)) {
            throw new SubnetException("invalid multicast address: " + multicastAddress);
        }
        
        this.networkInterface = networkInterface;
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
        
        localAddress = null;
        
        LOGGER.exiting(CLASS_NAME, "initialize");
    }
    
    /**
     * InetSubnetの初期化を行う。
     * @param localAddress ローカルアドレスの指定
     * @param multicastAddress マルチキャストアドレスの指定
     * @param portNumber ポート番号の指定
     * @throws SubnetException 生成に失敗した場合
     */
    protected void initialize(InetAddress localAddress, InetAddress multicastAddress, int portNumber) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "initialize", new Object[]{localAddress, multicastAddress, portNumber});
        if (!isValidAddress(localAddress)) {
            throw new SubnetException("invalid local address: " + localAddress);
        }
                
        if (!isValidAddress(multicastAddress)) {
            throw new SubnetException("invalid multicast address: " + multicastAddress);
        }
        
        try {
            this.networkInterface = NetworkInterface.getByInetAddress(localAddress);
        } catch (SocketException ex) {
            throw new SubnetException("catched exception", ex);
        }
        
        this.localAddress = localAddress;
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
        
        LOGGER.exiting(CLASS_NAME, "initialize");
    }

    /**
     * InetSubnetの初期化を行う。
     *
     * @param multicastAddress マルチキャストアドレスの指定
     * @param portNumber ポート番号の指定
     * @throws SubnetException 生成に失敗した場合
     */
    protected void initialize(InetAddress multicastAddress, int portNumber) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "initialize", new Object[]{multicastAddress, portNumber});
        
        if (!isValidAddress(multicastAddress)) {
            throw new SubnetException("invalid multicast address: " + multicastAddress);
        }

        this.localAddress = null;
        this.networkInterface = null;
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
        
        LOGGER.exiting(CLASS_NAME, "initialize");
    }

    private void createUDPNetwork() {
        LOGGER.entering(CLASS_NAME, "createUDPNetwork", new Object[]{multicastAddress, portNumber});
        
        if (localAddress != null) {
            udpNetwork = new UDPNetwork(this, localAddress, multicastAddress);
        } else if (networkInterface != null) {
            udpNetwork = new UDPNetwork(this, networkInterface, multicastAddress);
        } else {
            udpNetwork = new UDPNetwork(this, multicastAddress);
        }
        
        LOGGER.exiting(CLASS_NAME, "createUDPNetwork");
    }

    private void createTCPNetwork() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "createTCPNetwork", new Object[]{multicastAddress, portNumber});
        
        if (localAddress != null) {
            tcpNetwork = new TCPNetwork(this, localAddress, portNumber);
        } else {
            tcpNetwork = new TCPNetwork(this, portNumber);
        }
        
        LOGGER.exiting(CLASS_NAME, "createTCPNetwork");
    }

    private UDPNetwork getUDPNetwork() {
        return udpNetwork;
    }
    
    private TCPNetwork getTCPNetwork() {
        return tcpNetwork;
    }
    
    /**
     * TCPを有効にする。実行中に呼び出した場合には設定は変更されずfalseを返す。
     * @return 設定の変更を成功した場合にはfalse、それ以外の場合にはtrue
     */
    public synchronized boolean enableTCP() {
        if (isWorking()) {
            return false;
        }
        
        tcpEnabled = true;
        
        return true;
    }
    
    /**
     * TCPを無効にする。実行中に呼び出した場合には設定は変更されずfalseを返す。
     * @return 設定の変更を成功した場合にはfalse、それ以外の場合にはtrue
     */
    public synchronized boolean disableTCP() {
        if (isWorking()) {
            return false;
        }
        
        tcpEnabled = false;
        
        return true;
    }
    
    /**
     * TCPが有効であるかを返す。
     * @return TCPが有効であればtrue、無効であればfalse
     */
    public boolean isTCPEnabled() {
        return tcpEnabled;
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
        LOGGER.entering(CLASS_NAME, "stopService");
        
        if (!isWorking()) {
            LOGGER.exiting(CLASS_NAME, "stopService", false);
            return false;
        }
        
        stopReceiver();
        
        if (tcpEnabled) {
            getTCPNetwork().stopService();
        }

        boolean result = getUDPNetwork().stopService();
        
        LOGGER.exiting(CLASS_NAME, "stopService", result);
        return result;
    }
    
    private synchronized void startReceiver(SynchronousQueue<Frame> queue) {
        LOGGER.entering(CLASS_NAME, "startReceiver", queue);
        
        udpReceiver = new InetSubnetUDPReceiver(getUDPNetwork(), queue);
        new Thread(udpReceiver).start();

        if (tcpEnabled) {
            tcpReceiver = new InetSubnetTCPReceiver(getTCPNetwork(), queue);
            new Thread(tcpReceiver).start();
        }
        
        LOGGER.exiting(CLASS_NAME, "startReceiver");
    }
    
    private synchronized void stopReceiver() {
        LOGGER.entering(CLASS_NAME, "stopReceiver");
        
        if (udpReceiver != null) {
            udpReceiver.terminate();
            udpReceiver = null;
        }
        
        if (tcpReceiver != null) {
            tcpReceiver.terminate();
            tcpReceiver = null;
        }
        
        LOGGER.exiting(CLASS_NAME, "stopReceiver");
    }
    
    /**
     * このInetSubnetを実行する。
     * @return 停止から実行中に変更した場合はtrue、そうでなければfalse
     * @throws SubnetException 実行に失敗した場合
     */
    public synchronized boolean startService() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "startService");
        
        if (isWorking()) {
            LOGGER.exiting(CLASS_NAME, "startService", false);
            return false;
        }

        try {
            createUDPNetwork();
            boolean result = getUDPNetwork().startService();
            if (result == false) {
                LOGGER.exiting(CLASS_NAME, "startService", false);
                return false;
            }
            
            if (tcpEnabled) {
                createTCPNetwork();
                result = getTCPNetwork().startService();
                if (result == false) {
                    getUDPNetwork().stopService();
                    LOGGER.exiting(CLASS_NAME, "startService", false);
                    return false;
                }
            }

            
            receiveQueue = new SynchronousQueue<Frame>();
            startReceiver(receiveQueue);

            LOGGER.exiting(CLASS_NAME, "startService", true);
            return true;
        } catch (NetworkException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "startService", exception);
            throw exception;
        }
    }
    
    /**
     * 新たにTCP接続を確立し、フレームに設定を行う。
     * @param frame TCP接続を利用するフレーム
     * @throws SubnetException すでに接続が存在する、あるいは接続の確立に失敗した場合
     */
    public void createTCPConnection(Frame frame) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "createTCPConnection", frame);
        
        try {
            Connection connection = frame.getConnection();
            
            if (connection != null) {
                SubnetException exception = new SubnetException("connection exists: " + frame.getConnection());
                LOGGER.throwing(CLASS_NAME, "createTCPConnection", exception);
                throw exception;
            }
            
            if (getGroupNode().equals(frame.getReceiver())) {
                SubnetException exception = new SubnetException("invalid destination: " + frame.getReceiver());
                LOGGER.throwing(CLASS_NAME, "createTCPConnection", exception);
                throw exception;
            }
            
            getTCPNetwork().createConnection(frame);
        } catch (NetworkException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "createTCPConnection", exception);
            throw exception;
        }

        LOGGER.exiting(CLASS_NAME, "createTCPConnection");
    }
    
    /**
     * TCP接続を解放し、フレームに設定を行う。
     * @param frame TCP接続を開放するフレーム
     * @throws SubnetException 接続が存在しない、あるいは接続の開放に失敗した場合
     */
    public void deleteTCPConnection(Frame frame) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "deleteTCPConnection", frame);
        
        try {
            Connection connection = frame.getConnection();

            if (connection == null) {
                SubnetException exception = new SubnetException("no connection: " + frame);
                LOGGER.throwing(CLASS_NAME, "deleteTCPConnection", exception);
                throw exception;
            }

            getTCPNetwork().deleteConnection(frame);
        } catch (NetworkException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "deleteTCPConnection", exception);
            throw exception;
        }

        LOGGER.exiting(CLASS_NAME, "deleteTCPConnection");
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
        LOGGER.entering(CLASS_NAME, "send", frame);
        
        if (!isWorking()) {
            SubnetException exception = new SubnetException("not enabled");
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }

        if (!frame.getSender().isMemberOf(this)) {
            SubnetException exception = new SubnetException("invalid sender");
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }

        if (!frame.getReceiver().isMemberOf(this)) {
            SubnetException exception = new SubnetException("invalid receiver");
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }

        try {
            Connection connection = frame.getConnection();
            
            if (connection == null) {
                getUDPNetwork().send(frame);
            } else {
                getTCPNetwork().send(frame);
            }
            
            LOGGER.exiting(CLASS_NAME, "send", true);
            return true;
        } catch (NetworkException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
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
        LOGGER.entering(CLASS_NAME, "receive");
        
        if (!isWorking()) {
            SubnetException exception = new SubnetException("not enabled");
            LOGGER.throwing(CLASS_NAME, "receive", exception);
            throw exception;
        }
        
        try {
            Frame frame = receiveQueue.take();
            LOGGER.exiting(CLASS_NAME, "receive", frame);
            return frame;
        } catch (InterruptedException ex) {
            SubnetException exception = new SubnetException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "receive", exception);
            throw exception;
        }
    }
    
    private SynchronousQueue<Frame> receiveQueue = null;
    
    private class InetSubnetUDPReceiver implements Runnable {
        private UDPNetwork network;
        private SynchronousQueue<Frame> queue;
        private boolean terminated = false;
        
        public InetSubnetUDPReceiver(UDPNetwork network, SynchronousQueue<Frame> queue) {
            this.network = network;
            this.queue = queue;
        }
        
        public void terminate() {
            terminated = true;
        }
        
        @Override
        public void run() {
            try {
                while (!terminated) {
                    Frame frame = network.receive();
                    queue.put(frame);
                }
            } catch (InterruptedException ex) {
                if (terminated) {
                    LOGGER.logp(Level.INFO, CLASS_NAME, "InetSubnetUDPReceiver.run", "interrupted", ex);
                } else {
                    LOGGER.logp(Level.SEVERE, CLASS_NAME, "InetSubnetUDPReceiver.run", "interrupted", ex);
                }
            } catch (NetworkException ex) {
                if (terminated) {
                    LOGGER.logp(Level.INFO, CLASS_NAME, "InetSubnetUDPReceiver.run", "catched exception", ex);
                } else {
                    LOGGER.logp(Level.SEVERE, CLASS_NAME, "InetSubnetUDPReceiver.run", "catched exception", ex);
                }
            }
        }
    }

    private class InetSubnetTCPReceiver implements Runnable {
        private TCPNetwork network;
        private SynchronousQueue<Frame> queue;
        private boolean terminated = false;
        
        public InetSubnetTCPReceiver(TCPNetwork network, SynchronousQueue<Frame> queue) {
            this.network = network;
            this.queue = queue;
        }
        
        public void terminate() {
            terminated = true;
        }

        @Override
        public void run() {
            try {
                while (!terminated) {
                    Frame frame = network.receive();
                    queue.put(frame);
                }
            } catch (InterruptedException ex) {
                if (terminated) {
                    LOGGER.logp(Level.INFO, CLASS_NAME, "InetSubnetTCPReceiver.run", "interrupted", ex);
                } else {
                    LOGGER.logp(Level.SEVERE, CLASS_NAME, "InetSubnetTCPReceiver.run", "interrupted", ex);
                }
            } catch (NetworkException ex) {
                if (terminated) {
                    LOGGER.logp(Level.INFO, CLASS_NAME, "InetSubnetTCPReceiver.run", "catched exception", ex);
                } else {
                    LOGGER.logp(Level.SEVERE, CLASS_NAME, "InetSubnetTCPReceiver.run", "catched exception", ex);
                }
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
     * @return リモートノードのNode
     * @throws SubnetException 無効なアドレスが指定された場合
     */
    public Node getRemoteNode(InetAddress addr) throws SubnetException {
        if (isValidAddress(addr)) {
            return new InetNode(this, addr);
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
    
    private InetAddress getLocalAddress() {
        if (localAddress != null) {
            return localAddress;
        } else {
            try {
                return InetAddress.getByName("localhost");
            } catch (UnknownHostException ex) {
                Logger.getLogger(InetSubnet.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }
    
    /**
     * ローカルノードを表すNodeを返す。
     * @return ローカルノードのNode
     */
    @Override
    public synchronized Node getLocalNode() {
        if (localNode == null) {
            localNode = new InetNode(this, getLocalAddress());
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
            groupNode = new InetNode(this, multicastAddress);
        }
        
        return groupNode;
    }
}
