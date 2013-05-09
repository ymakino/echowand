package echowand.net;

import java.io.IOException;
import java.net.*;

/**
 * IPv6ネットワークのサブネット
 * @author Yoshiki Makino
 */
public class Inet6Subnet implements Subnet {
    
    /**
     * ECHONET Liteが利用するIPv6マルチキャストアドレス
     */
    public static final String MULTICAST_ADDRESS = "ff02::1";
    
    /**
     * ECHONET Liteが利用するポート番号
     */
    public static final short  DEFAULT_PORT = 3610;
    
    /**
     * 受信データ用バッファの最大長のデフォルト
     */
    public static final short  DEFAULT_BUFSIZE = 1500;
    
    private MulticastSocket multicastSocket;
    private NetworkInterface networkInterface;
    private Inet6Address groupAddress;
    private Inet6Address localAddress;
    private Inet6Node groupNode;
    private Inet6Node localNode;
    private int bufferSize = DEFAULT_BUFSIZE;
    private boolean enable = false;
    
    /**
     * Inet6Subnetを生成する。
     * ソケットの初期化も同時に行い、このInet6Subnetを有効にする。
     */
    public Inet6Subnet() throws SubnetException {
        initInet6Subnet(true);
    }
    
    /**
     * Inet6Subnetを生成する。
     * 与えられたdoInitがtrueであればソケットの初期化も行い、このInet6Subnetを有効にする。
     * doInitがfalseであればソケットの初期化は行わず、enableが呼ばれるまで無効状態になる。
     * @param doInit ソケットの初期化処理の有無
     */
    public Inet6Subnet(boolean doInit) throws SubnetException {
        initInet6Subnet(doInit);
    }
    
    /**
     * Inet6Subnetを生成する。
     * 与えられたdoInitがtrueであればソケットの初期化も行い、このInet4Subnetを有効にする。
     * doInitがfalseであればソケットの初期化は行わず、enableが呼ばれるまで無効状態になる。
     * addressにより利用するネットワークインタフェースの指定を行う。
     * @param address 利用するネットワークインタフェースにつけられたアドレス
     * @param doInit ソケットの初期化処理の有無
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet6Subnet(Inet6Address address, boolean doInit) throws SubnetException {
        if (address == null) {
            throw new SubnetException("invalid address: " + address);
        }
        
        try {
            localAddress = address;
            networkInterface = NetworkInterface.getByInetAddress(address);
            
            if (networkInterface == null) {
                throw new SubnetException("invalid address: " + address);
            }
            
            initInet6Subnet(doInit);
        } catch (SocketException e) {
            throw new SubnetException("catched exception", e);
        }
    }
    
    /**
     * Inet6Subnetを生成する。
     * 与えられたdoInitがtrueであればソケットの初期化も行い、このInet6Subnetを有効にする。
     * doInitがfalseであればソケットの初期化は行わず、enableが呼ばれるまで無効状態になる。
     * networkInterfaceにより利用するネットワークインタフェースの指定を行う。
     * @param networkInterface 利用するネットワークインタフェース
     * @param doInit ソケットの初期化処理の有無
     * @throws SubnetException 生成に失敗した場合
     */
    public Inet6Subnet(NetworkInterface networkInterface, boolean doInit) throws SubnetException {
        if (networkInterface == null) {
            throw new SubnetException("invalid network interface: " + networkInterface);
        }
        
        this.networkInterface = networkInterface;
        initInet6Subnet(doInit);
    }
    
    private synchronized void setGroupAddress(Inet6Address address) {
        groupAddress = address;
    }
    
    private synchronized Inet6Address getGroupAddress() {
        return groupAddress;
    }
    
    private void initInet6Subnet(boolean doInit) throws SubnetException {
        
        try {
            setGroupAddress((Inet6Address)Inet6Address.getByName(MULTICAST_ADDRESS));
            if (localAddress == null) {
                localAddress = (Inet6Address)Inet6Address.getByName("::1");
            }
        } catch (UnknownHostException e) {
            throw new SubnetException("catched exception", e);
        } catch (IOException e) {
            throw new SubnetException("catched exception", e);
        }
        
        if (doInit) {
            initSocket();
        }
    }
    
    private void initSocket() throws SubnetException {
        try {
            closeSocket();
            multicastSocket = new MulticastSocket(DEFAULT_PORT);
            
            if (networkInterface != null) {
                multicastSocket.setNetworkInterface(networkInterface);
            }
            
            multicastSocket.joinGroup(getGroupAddress());
            multicastSocket.setLoopbackMode(false);
            multicastSocket.setReuseAddress(false);
            
            enable = true;
        } catch (BindException e) {
            throw new SubnetException("catched exception", e);
        } catch (IOException e) {
            closeSocket();
            throw new SubnetException("catched exception", e);
        }
    }
    
    /**
     * 設定されたネットワークインタフェースを返す。
     * @return 設定されたネットワークインタフェース
     */
    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }
    
    /**
     * バッファの最大長を返す。
     * @return バッファの最大長
     */
    public int getBufferSize() {
        return bufferSize;
    }
    
    
    /**
     * バッファの最大長を設定する。
     * @param bufferSize 
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    private void closeSocket() {
        if (multicastSocket != null) {
            multicastSocket.close();
            multicastSocket = null;
            enable = false;
        }
    }
    
    /**
     * このInet6Subnetが有効であるかどうか返す。
     * @return 有効であればtrue、そうでなければfalse
     */
    public synchronized boolean isEnabled() {
        return enable;
    }
    
    /**
     * このInet6Subnetを無効にする。
     * @return 有効から無効に変更した場合はtrue、そうでなければfalse
     */
    public synchronized boolean disable() {
        if (enable) {
            closeSocket();
            return !enable;
        } else {
            return false;
        }
    }
    
    /**
     * このInet6Subnetを有効にする。
     * @return 無効から有効に変更した場合はtrue、そうでなければfalse
     * @throws SubnetException 有効にするのに失敗した場合
     */
    public synchronized boolean enable() throws SubnetException {
        if (enable) {
            return false;
        } else {
            initSocket();
            return enable;
        }
    }
    
    /**
     * このInet6Subnetのサブネットにフレームを転送する。
     * フレームの送信ノードや受信ノードがこのInet6Subnetに含まれない場合には例外が発生する。
     * @param frame 送信するフレーム
     * @return 常にtrue
     * @throws SubnetException 送信に失敗した場合
     */
    @Override
    public boolean send(Frame frame) throws SubnetException {
        if (!isEnabled()) {
            throw new SubnetException("not enabled");
        }

        CommonFrame cf = frame.getCommonFrame();
        byte[] data = cf.toBytes();

        if (!frame.getSender().isMemberOf(this)) {
            throw new SubnetException("invalid sender");
        }

        if (!frame.getReceiver().isMemberOf(this)) {
            throw new SubnetException("invalid receiver");
        }

        try {
            Inet6Node node = (Inet6Node) frame.getReceiver();
            Inet6Address addr = (Inet6Address)node.getAddress();
            int port = node.getPort();
            DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
            
            multicastSocket.send(packet);

            return true;
        } catch (IOException e) {
            throw new SubnetException("catched exception", e);
        }
    }
    
    /**
     * このInet6Subnetのサブネットからフレームを受信する。
     * 受信を行うまで待機する。
     * @return 受信したFrame
     * @throws SubnetException 無効なフレームを受信、あるいは受信に失敗した場合
     */
    @Override
    public Frame recv()  throws SubnetException {
        if (!isEnabled()) {
            throw new SubnetException("not enabled");
        }
        
        try {
            byte[] packetData = new byte[this.bufferSize];

            DatagramPacket packet = new DatagramPacket(packetData, packetData.length);
            multicastSocket.receive(packet);
            int len = packet.getLength();
            byte[] data = new byte[len];
            System.arraycopy(packetData, 0, data, 0, len);

            CommonFrame cf = new CommonFrame(data);
            Inet6Address addr = (Inet6Address)packet.getAddress();
            int port = packet.getPort();

            Node node = getRemoteNode(addr, port);
            Frame frame = new Frame(node, getLocalNode(), cf);
            return frame;
        } catch (IOException e) {
            throw new SubnetException("catched exception", e);
        } catch (InvalidDataException e) {
            throw new SubnetException("invalid frame", e);
        }
    }
    
    /**
     * リモートノードを表すNodeを生成する。
     * @param addr リモートノードのIPv6アドレス
     * @param port リモートノードのポート番号
     * @return リモートノードのNode
     */
    public Node getRemoteNode(Inet6Address addr, int port) {
        return new Inet6Node(this, addr, port);
    }
    
    /**
     * リモートノードを表すNodeを生成する。
     * @param addr リモートノードのIPv6アドレス
     * @return リモートノードのNode
     */
    public Node getRemoteNode(Inet6Address addr) {
        return new Inet6Node(this, addr, DEFAULT_PORT);
    }
    
    /**
     * ローカルノードを表すNodeを返す。
     * @return ローカルノードのNode
     */
    @Override
    public synchronized Node getLocalNode() {
        if (localNode == null) {
            localNode = new Inet6Node(this, localAddress, DEFAULT_PORT);
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
            groupNode = new Inet6Node(this, groupAddress, DEFAULT_PORT);
        }
        return groupNode;
    }
}
