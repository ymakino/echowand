package echowand.net;

import echowand.util.Pair;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UDPを利用した通信管理
 * @author ymakino
 */
public class UDPNetwork {
    private static final Logger LOGGER = Logger.getLogger(UDPNetwork.class.getName());
    private static final String CLASS_NAME = UDPNetwork.class.getName();
    
    /**
     * 受信データ用バッファのデフォルトサイズ
     */
    public static final int  DEFAULT_BUFFER_SIZE = 1500;
    
    private NetworkInterface networkInterface;
    private List<NetworkInterface> receiverInterfaces;
    private InetAddress localAddress;
    private InetAddress multicastAddress;
    private MulticastSocket multicastSocket;
    private int portNumber;
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    private boolean inService = false;
    
    private boolean remotePortNumberEnabled;
    
    /**
     * 利用するローカルアドレス、受信インタフェース、マルチキャストアドレスおよびポート番号を指定してUDPNetworkを生成する。
     * @param localAddress 利用するローカルアドレスの指定
     * @param receiverInterfaces 受信に利用するネットワークインタフェースの指定
     * @param multicastAddress 利用するマルチキャストアドレスの指定
     * @param portNumber 利用するポート番号の指定
     */
    public UDPNetwork(InetAddress localAddress, Collection<? extends NetworkInterface> receiverInterfaces, InetAddress multicastAddress, int portNumber) {
        this.localAddress = localAddress;
        this.networkInterface = null;
        this.receiverInterfaces = new LinkedList<NetworkInterface>(receiverInterfaces);
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
    }
    
    /**
     * 利用するインタフェース、受信インタフェース、マルチキャストアドレスおよびポート番号を指定してUDPNetworkを生成する。
     * @param networkInterface 利用するネットワークインタフェースの指定
     * @param receiverInterfaces 受信に利用するネットワークインタフェースの指定
     * @param multicastAddress 利用するマルチキャストアドレスの指定
     * @param portNumber 利用するポート番号の指定
     */
    public UDPNetwork(NetworkInterface networkInterface, Collection<? extends NetworkInterface> receiverInterfaces, InetAddress multicastAddress, int portNumber) {
        this.localAddress = null;
        this.networkInterface = networkInterface;
        this.receiverInterfaces = new LinkedList<NetworkInterface>(receiverInterfaces);
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
    }
    
    /**
     * 利用する受信インタフェース、マルチキャストアドレスおよびポート番号を指定してUDPNetworkを生成する。
     * @param receiverInterfaces 受信に利用するネットワークインタフェースの指定
     * @param multicastAddress 利用するマルチキャストアドレスの指定
     * @param portNumber 利用するポート番号の指定
     */
    public UDPNetwork(Collection<? extends NetworkInterface> receiverInterfaces, InetAddress multicastAddress, int portNumber) {
        this.localAddress = null;
        this.networkInterface = null;
        this.receiverInterfaces = new LinkedList<NetworkInterface>(receiverInterfaces);
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
    }
    
    /**
     * 利用するマルチキャストアドレスおよびポート番号を指定してUDPNetworkを生成する。
     * @param multicastAddress 利用するマルチキャストアドレスの指定
     * @param portNumber 利用するポート番号の指定
     */
    public UDPNetwork(InetAddress multicastAddress, int portNumber) {
        this.localAddress = null;
        this.networkInterface = null;
        this.receiverInterfaces = new LinkedList<NetworkInterface>();
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
    }
    
    private synchronized void closeSocket() {
        LOGGER.entering(CLASS_NAME, "closeSocket");
        
        if (multicastSocket != null) {
            multicastSocket.close();
            multicastSocket = null;
        }
            
        inService = false;
        
        LOGGER.exiting(CLASS_NAME, "closeSocket");
    }
    
    private synchronized void openSocket() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "openSocket");
        
        try {
            multicastSocket = new MulticastSocket(getPortNumber());

            if (localAddress != null) {
                multicastSocket.setInterface(localAddress);
            }
            
            if (networkInterface != null) {
                multicastSocket.setNetworkInterface(networkInterface);
            }
            
            multicastSocket.joinGroup(multicastAddress);
            
            if (!receiverInterfaces.isEmpty()) {
                InetSocketAddress saddr = new InetSocketAddress(multicastAddress, getPortNumber());
                for (NetworkInterface receiverInterface : receiverInterfaces) {
                    multicastSocket.joinGroup(saddr, receiverInterface);
                }
            }
            
            multicastSocket.setLoopbackMode(false);
            multicastSocket.setReuseAddress(false);

            inService = true;
        } catch (IOException ex) {
            closeSocket();
            NetworkException exception = new NetworkException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "openSocket", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "openSocket");
    }
    
    /**
     * 送受信に利用するポート番号を返す。
     * @return ポート番号
     */
    public int getPortNumber() {
        return portNumber;
    }
    
    public synchronized boolean setPortNumber(int portNumber) {
        LOGGER.entering(CLASS_NAME, "setPortNumber", portNumber);
        
        if (isInService()) {
            LOGGER.exiting(CLASS_NAME, "setPortNumber", false);
            return false;
        }
        
        this.portNumber = portNumber;
        
        LOGGER.exiting(CLASS_NAME, "setPortNumber", true);
        return true;
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
     * @param bufferSize バッファの最大長
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    /**
     * このUDPNetworkが有効であるかどうか返す。
     * @return 有効であればtrue、そうでなければfalse
     */
    public synchronized boolean isInService() {
        return inService;
    }
    
    /**
     * リモートノードのポート番号を認識するかを返す。
     * ポート番号を認識する場合、ポート番号が異なる場合には異なるNodeを生成する。
     * @return リモートノードのポート番号を認識する場合にあtrue、そうでなければfalse
     */
    public boolean isRemotePortNumberEnabled() {
        return remotePortNumberEnabled;
    }
    
    /**
     * リモートノードのポート番号を識別するように設定する。
     * サービス開始前に呼び出す必要がある。
     * @return 設定が成功した場合にはtrue、そうでければfalse
     */
    public synchronized boolean enableRemotePortNumber() {
        LOGGER.entering(CLASS_NAME, "enableRemotePortNumber");
        
        if (isInService()) {
            LOGGER.exiting(CLASS_NAME, "enableRemotePortNumber", false);
            return false;
        }
        
        remotePortNumberEnabled = true;
        
        LOGGER.exiting(CLASS_NAME, "enableRemotePortNumber", true);
        return true;
    }
    
    /**
     * リモートノードのポート番号を識別しないように設定する。
     * サービス開始前に呼び出す必要が有る。
     * @return 設定が成功した場合にはtrue、そうでければfalse
     */
    public synchronized boolean disableRemotePortNumber() {
        LOGGER.entering(CLASS_NAME, "disableRemotePortNumber");
        
        if (isInService()) {
            LOGGER.exiting(CLASS_NAME, "disableRemotePortNumber", false);
            return false;
        }
        
        remotePortNumberEnabled = false;
        
        LOGGER.exiting(CLASS_NAME, "disableRemotePortNumber", true);
        return true;
    }
    
    /**
     * このUDPNetworkを無効にする。
     * @return 有効から無効に変更した場合はtrue、そうでなければfalse
     */
    public synchronized boolean stopService() {
        LOGGER.entering(CLASS_NAME, "stopService");
        boolean result;
        
        if (inService) {
            closeSocket();
            result = !inService;
        } else {
            result = false;
        }
        
        LOGGER.exiting(CLASS_NAME, "stopService", result);
        return result;
    }
    
    /**
     * このUDPNetworkを有効にする。
     * @return 無効から有効に変更した場合はtrue、そうでなければfalse
     * @throws NetworkException 有効にするのに失敗した場合
     */
    public synchronized boolean startService() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "startService");
        
        boolean result;
        
        if (inService) {
            result = false;
        } else {
            closeSocket();
            openSocket();
            result = inService;
        }
        
        LOGGER.exiting(CLASS_NAME, "startService", result);
        return result;
    }
    
    /**
     * このUDPNetworkのサブネットにフレームを転送する。
     * @param remoteNodeInfo 送信先のノード情報
     * @param commonFrame 送信する共通フレーム
     * @throws NetworkException 送信に失敗した場合
     * @throws IOException I/Oエラーが発生した場合
     */
    public synchronized void send(InetNodeInfo remoteNodeInfo, CommonFrame commonFrame) throws NetworkException, IOException {
        LOGGER.entering(CLASS_NAME, "send", new Object[]{remoteNodeInfo, commonFrame});
        
        if (!isInService()) {
            NetworkException exception = new NetworkException("not working");
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }
        
        byte[] data = commonFrame.toBytes();

        InetAddress receiver = remoteNodeInfo.getAddress();
        int port = getPortNumber();

        if (remoteNodeInfo.hasPortNumber()) {
            port = remoteNodeInfo.getPortNumber();
        }

        DatagramPacket packet = new DatagramPacket(data, data.length, receiver, port);

        multicastSocket.send(packet);
        
        LOGGER.exiting(CLASS_NAME, "send");
    }
    
    private DatagramPacket receivePacket() throws IOException {
        byte[] packetData = new byte[this.bufferSize];
        DatagramPacket packet = new DatagramPacket(packetData, packetData.length);
        multicastSocket.receive(packet);
        return packet;
    }
    
    private byte[] getData(DatagramPacket packet) throws IOException {
        byte[] packetData = packet.getData();

        int len = packet.getLength();
        byte[] data = new byte[len];
        
        System.arraycopy(packetData, 0, data, 0, len);

        return data;
    }
    
    /**
     * このUDPNetworkのサブネットからフレームを受信する。
     * 受信を行うまで待機する。
     * @return 受信したFrame
     * @throws NetworkException 受信に失敗した場合
     * @throws InvalidDataException 不正なフレームを受信した場合
     * @throws IOException I/Oエラーが発生した場合
     */
    public Pair<InetNodeInfo, CommonFrame> receive() throws NetworkException, InvalidDataException, IOException {
        LOGGER.entering(CLASS_NAME, "receive");
        
        if (!isInService()) {
            throw new NetworkException("not working");
        }
        
        DatagramPacket packet = receivePacket();
        byte[] data = getData(packet);

        CommonFrame commonFrame = new CommonFrame(data);

        InetAddress addr = packet.getAddress();

        Pair<InetNodeInfo, CommonFrame> pair;

        if (isRemotePortNumberEnabled()) {
            int port = packet.getPort();
            pair = new Pair<InetNodeInfo, CommonFrame>(new InetNodeInfo(addr, port), commonFrame);
        } else {
            pair = new Pair<InetNodeInfo, CommonFrame>(new InetNodeInfo(addr), commonFrame);
        }
        
        LOGGER.exiting(CLASS_NAME, "receive", pair);
        return pair;
    }
}
