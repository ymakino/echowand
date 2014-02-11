package echowand.net;

import echowand.util.Pair;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
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
    public static final short  DEFAULT_BUFFER_SIZE = 1500;
    
    private NetworkInterface networkInterface;
    private InetAddress localAddress;
    private InetAddress multicastAddress;
    private MulticastSocket multicastSocket;
    private int portNumber;
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    private boolean working = false;
    
    /**
     * 利用するUDPネットワークおよびローカルアドレス、マルチキャストアドレスを指定してUDPNetworkを生成する。
     * @param localAddress 利用するローカルアドレスの指定
     * @param multicastAddress 利用するマルチキャストアドレスの指定
     * @param portNumber 利用するポート番号の指定
     */
    public UDPNetwork(InetAddress localAddress, InetAddress multicastAddress, int portNumber) {
        this.localAddress = localAddress;
        this.networkInterface = null;
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
    }
    
    /**
     * 利用するUDPネットワークおよびインタフェース、マルチキャストアドレスを指定してUDPNetworkを生成する。
     * @param networkInterface 利用するネットワークインタフェースの指定
     * @param multicastAddress 利用するマルチキャストアドレスの指定
     * @param portNumber 利用するポート番号の指定
     */
    public UDPNetwork(NetworkInterface networkInterface, InetAddress multicastAddress, int portNumber) {
        this.localAddress = null;
        this.networkInterface = networkInterface;
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
    }
    
    /**
     * 利用するUDPネットワークおよびインタフェース、マルチキャストアドレスを指定してUDPNetworkを生成する。
     * @param multicastAddress 利用するマルチキャストアドレスの指定
     * @param portNumber 利用するポート番号の指定
     */
    public UDPNetwork(InetAddress multicastAddress, int portNumber) {
        this.localAddress = null;
        this.networkInterface = null;
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
    }
    
    private synchronized void closeSocket() {
        LOGGER.entering(CLASS_NAME, "closeSocket");
        
        if (multicastSocket != null) {
            multicastSocket.close();
            multicastSocket = null;
        }
            
        working = false;
        
        LOGGER.exiting(CLASS_NAME, "closeSocket");
    }
    
    private synchronized void openSocket() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "openSocket");
        
        try {
            multicastSocket = new MulticastSocket(getPortNumber());

            if (localAddress != null) {
                multicastSocket.setInterface(localAddress);
            } else if (networkInterface != null) {
                multicastSocket.setNetworkInterface(networkInterface);
            }
            
            multicastSocket.joinGroup(multicastAddress);
            multicastSocket.setLoopbackMode(false);
            multicastSocket.setReuseAddress(false);

            working = true;
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
    
    /**
     * このUDPNetworkが有効であるかどうか返す。
     * @return 有効であればtrue、そうでなければfalse
     */
    public synchronized boolean isWorking() {
        return working;
    }
    
    /**
     * このUDPNetworkを無効にする。
     * @return 有効から無効に変更した場合はtrue、そうでなければfalse
     */
    public synchronized boolean stopService() {
        LOGGER.entering(CLASS_NAME, "stopService");
        boolean result;
        
        if (working) {
            closeSocket();
            result = !working;
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
        
        if (working) {
            result = false;
        } else {
            closeSocket();
            openSocket();
            result = working;
        }
        
        LOGGER.exiting(CLASS_NAME, "startService", result);
        return result;
    }
    
    /**
     * このUDPNetworkのサブネットにフレームを転送する。
     * @param remoteNodeInfo 送信先のノード情報
     * @param commonFrame 送信する共通フレーム
     * @throws NetworkException 送信に失敗した場合
     */
    public synchronized void send(InetNodeInfo remoteNodeInfo, CommonFrame commonFrame) throws NetworkException {
        LOGGER.entering(CLASS_NAME, "send", new Object[]{remoteNodeInfo, commonFrame});
        
        if (!isWorking()) {
            NetworkException exception = new NetworkException("not working");
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }
        
        byte[] data = commonFrame.toBytes();

        try {
            InetAddress receiver = remoteNodeInfo.getAddress();
            int port = getPortNumber();
            
            DatagramPacket packet = new DatagramPacket(data, data.length, receiver, port);
            
            multicastSocket.send(packet);
        } catch (IOException ex) {
            NetworkException exception = new NetworkException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "send");
    }
    
    private DatagramPacket recvPacket() throws IOException {
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
     * @throws NetworkException 無効なフレームを受信、あるいは受信に失敗した場合
     */
    public Pair<InetNodeInfo, CommonFrame> receive()  throws NetworkException {
        LOGGER.entering(CLASS_NAME, "receive");
        
        if (!isWorking()) {
            throw new NetworkException("not working");
        }
        
        try {
            DatagramPacket packet = recvPacket();
            byte[] data = getData(packet);

            CommonFrame commonFrame = new CommonFrame(data);
            
            InetAddress addr = packet.getAddress();
            
            Pair<InetNodeInfo, CommonFrame> pair = new Pair<InetNodeInfo, CommonFrame>(new InetNodeInfo(addr), commonFrame);
            LOGGER.exiting(CLASS_NAME, "receive", pair);
            return pair;
        } catch (IOException ex) {
            NetworkException exception = new NetworkException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "receive", exception);
            throw exception;
        } catch (InvalidDataException ex) {
            NetworkException exception = new NetworkException("invalid frame", ex);
            LOGGER.throwing(CLASS_NAME, "receive", exception);
            throw exception;
        }
    }
}
