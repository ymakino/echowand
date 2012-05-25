package echowand.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


class InetSubnetNode implements Node {
    private InetSubnet subnet;
    private InetAddress addr;
    private int port;
    
    public InetSubnetNode(InetSubnet subnet, InetAddress addr, int port) {
        this.subnet = subnet;
        this.addr = addr;
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }
    
    public InetAddress getAddress() {
        return addr;
    }
    
    @Override
    public boolean isMemberOf(Subnet subnet) {
        return this.subnet == subnet;
    }
    
    @Override
    public String toString() {
        return addr.getHostAddress() + ":" + port;
    }
    
    @Override
    public boolean equals(Object o) {
        if (! (o instanceof InetSubnetNode)) {
            return false;
        }
        InetSubnetNode node = (InetSubnetNode)o;
        return (this.addr.equals(node.addr) && this.port == node.port && this.subnet.equals(node.subnet));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.addr != null ? this.addr.hashCode() : 0);
        hash = 67 * hash + this.port;
        return hash;
    }
}

/**
 * IPネットワークのサブネット
 * @author Yoshiki Makino
 */
public class InetSubnet implements Subnet {
    /**
     * ECHONET Liteが利用するIPマルチキャストアドレス
     */
    public static final String ECHONET_ADDRESS = "224.0.23.0";
    /**
     * ECHONET Liteが利用するポート番号
     */
    public static final short  ECHONET_PORT = 3610;
    /**
     * 受信データ用バッファの最大長
     */
    public static final short  DEFAULT_BUFSIZE = 1500;
    
    private MulticastSocket socket;
    private InetAddress group;
    private InetAddress local;
    private InetSubnetNode groupNode;
    private InetSubnetNode localNode;
    private int bufferSize = DEFAULT_BUFSIZE;
    private boolean enable = false;
    
    /**
     * InetSubnetを生成する。
     * ソケットの初期化も同時に行い、このInetSubnetを有効にする。
     */
    public InetSubnet() {
        this(true);
    }
    
    /**
     * InetSubnetを生成する。
     * 与えられたdoInitがtrueであればソケットの初期化も行い、このInetSubnetを有効にする。
     * doInitがfalseであればソケットの初期化は行わず、enableが呼ばれるまで無効状態になる。
     * @param doInit ソケットの初期化処理の有無
     */
    public InetSubnet(boolean doInit) {
        try {
            group = InetAddress.getByName(ECHONET_ADDRESS);
            local = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (doInit) {
            enable = initSocket();
        }
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
    
    private boolean initSocket() {
        try {
            closeSocket();
            socket = new MulticastSocket(ECHONET_PORT);
            socket.joinGroup(group);
            socket.setLoopbackMode(false);
            socket.setReuseAddress(false);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            closeSocket();
            return false;
        }
    }
    
    private void closeSocket() {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }
    
    /**
     * このInetSubnetが有効であるかどうか返す。
     * @return 有効であればtrue、そうでなければfalse
     */
    public synchronized boolean isEnabled() {
        return enable;
    }
    
    /**
     * このInetSubnetを無効にする。
     * @return 有効から無効に変更した場合はtrue、そうでなければfalse
     */
    public synchronized boolean disable() {
        if (enable) {
            closeSocket();
            enable = false;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * このInetSubnetを有効にする。
     * @return 無効から有効に変更した場合はtrue、そうでなければfalse
     */
    public synchronized boolean enable() {
        if (enable) {
            return false;
        } else {
            enable = initSocket();
            return enable;
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
        if (!enable) {
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
            InetSubnetNode node = (InetSubnetNode) frame.getReceiver();
            InetAddress addr = node.getAddress();
            int port = node.getPort();
            DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
            
            socket.send(packet);

            return true;
        } catch (IOException e) {
            throw new SubnetException("catched exception", e);
        }
    }
    
    /**
     * このInetSubnetのサブネットからフレームを受信する。
     * 受信を行うまで待機する。
     * @return 受信したFrame
     * @throws SubnetException 無効なフレームを受信、あるいは受信に失敗した場合
     */
    @Override
    public Frame recv()  throws SubnetException {
        if (!enable) {
            throw new SubnetException("not enabled");
        }
        
        try {
            byte[] packetData = new byte[this.bufferSize];

            DatagramPacket packet = new DatagramPacket(packetData, packetData.length);
            socket.receive(packet);
            int len = packet.getLength();
            byte[] data = new byte[len];
            System.arraycopy(packetData, 0, data, 0, len);

            CommonFrame cf = new CommonFrame(data);
            InetAddress addr = packet.getAddress();
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
     * @param addr リモートノードのIPアドレス
     * @param port リモートノードのポート番号
     * @return リモートノードのNode
     */
    public Node getRemoteNode(InetAddress addr, int port) {
        return new InetSubnetNode(this, addr, port);
    }
    
    /**
     * リモートノードを表すNodeを生成する。
     * @param addr リモートノードのIPアドレス
     * @return リモートノードのNode
     */
    public Node getRemoteNode(InetAddress addr) {
        return new InetSubnetNode(this, addr, ECHONET_PORT);
    }
    
    /**
     * ローカルノードを表すNodeを返す。
     * @return ローカルノードのNode
     */
    @Override
    public synchronized Node getLocalNode() {
        if (localNode == null) {
            localNode = new InetSubnetNode(this, local, ECHONET_PORT);
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
            groupNode = new InetSubnetNode(this, group, ECHONET_PORT);
        }
        return groupNode;
    }
}
