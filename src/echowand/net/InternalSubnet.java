package echowand.net;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * プログラム内でのみ有効なサブネット
 * InternalSubnetを生成する度にユニークなIDが割り振られる。
 * @author Yoshiki Makino
 */
public class InternalSubnet implements Subnet {
    private static final Logger LOGGER = Logger.getLogger(InternalSubnet.class.getName());
    private static final String CLASS_NAME = InternalSubnet.class.getName();
    
    private static int nextId = 0;
    
    private boolean inService = false;
    private InternalNetwork network;
    private int id;
    
    private synchronized static int getNextId() {
        return nextId++;
    }
    
    private Node localNode;
    private Node groupNode;
    private InternalNetworkPort port;
    
    /**
     * InternalSubnetを生成する。
     * デフォルトのInternalNetworkに接続する。
     */
    public InternalSubnet() {
        initialize(InternalNetwork.getDefault());
    }
    
    /**
     * InternalSubnetを生成する。
     * 指定された名前のInternalNetworkに接続する。
     * @param networkName 接続するInternalNetworkの名前の指定
     */
    public InternalSubnet(String networkName) {
        initialize(InternalNetwork.getByName(networkName));
    }
    
    /**
     * InternalSubnetを生成し、サービスを開始する。
     * @return 生成したInternalSubnet
     */
    public static InternalSubnet startSubnet() throws SubnetException {
        InternalSubnet subnet = new InternalSubnet();
        subnet.startService();
        return subnet;
    }
    
    /**
     * InternalSubnetを生成する。
     * networkNameにより接続するInternalNetworkの指定を行う。
     * @param networkName 接続するInternalNetworkの名前の指定
     * @return 生成したInternalSubnet
     */
    public static InternalSubnet startSubnet(String networkName) throws SubnetException {
        InternalSubnet subnet = new InternalSubnet(networkName);
        subnet.startService();
        return subnet;
    }
    
    private void initialize(InternalNetwork network) {
        id = getNextId();
        port = new InternalNetworkPort();
        this.network = network;
    }
    
    private void validateSender(Frame frame) throws SubnetException {
        if (!frame.getSender().isMemberOf(this)) {
            throw new SubnetException("invalid sender");
        }
    }
    
    private void validateReceiver(Frame frame) throws SubnetException {
        if (! (frame.getReceiver() instanceof InternalNode)) {
            throw new SubnetException("invalid receiver");
        }
    }
    
    private boolean shouldLocalNodeReceive(Frame frame) {
            Node node = frame.getReceiver();
            return (node.equals(getGroupNode()) || node.equals(getLocalNode()));
    }
    
    /**
     * このInternalSubnetが含まれるInternalNetworkを返す。
     * @return このInternalSubnetが含まれるInternalNetwork
     */
    public InternalNetwork getNetwork() {
        return network;
    }
    
    /**
     * このInternalSubnetのサブネットにフレームを転送する。
     * フレームの送信ノードや受信ノードがこのInternalSubnetに含まれない場合には例外が発生する。
     * @param frame 送信するフレーム
     * @throws SubnetException 送信に失敗した場合
     */
    @Override
    public void send(Frame frame) throws SubnetException {
        validateSender(frame);
        validateReceiver(frame);
        port.send(frame);
    }

    /**
     * このInternalSubnetのサブネットからフレームを受信する。
     * 受信を行うまで待機する。
     * @return 受信したFrame
     * @throws SubnetException 受信に失敗した場合
     */
    @Override
    public Frame receive() throws SubnetException {
        for (;;) {
            Frame frame = port.receive();

            if (shouldLocalNodeReceive(frame)) {
                return frame;
            }
        }
    }

    
    /**
     * このInternalSubnetのサブネットからフレームを受信する。
     * 受信フレームがない場合には即座に戻る。
     * @return 受信したFrame、もし受信フレームがない場合にはnull
     * @throws SubnetException 受信に失敗した場合
     */
    public Frame receiveNoWait() throws SubnetException {
        for (;;) {
            Frame frame = port.receiveNoWait();
            
            if (frame == null) {
                return null;
            }
            
            if (shouldLocalNodeReceive(frame)) {
                return frame;
            }
        }
    }
    
    /**
     * リモートノードを表すNodeを生成する。
     * @param name リモートノードの名前
     * @return リモートノードのNode
     */
    @Override
    public synchronized Node getRemoteNode(String name) {
        return new InternalNode(this, name);
    }
    
    /**
     * リモートノードを表すNodeを生成する。
     * @param nodeInfo リモートノードの情報
     * @return リモートノードのNode
     */
    @Override
    public synchronized Node getRemoteNode(NodeInfo nodeInfo) throws SubnetException {
        if (nodeInfo instanceof InternalNodeInfo) {
            return new InternalNode(this, (InternalNodeInfo)nodeInfo);
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
            localNode = new InternalNode(this, "LOCAL(" + id + ")");
        }
        return localNode;
    }
    
    /**
     * グループを表すNodeを返す。
     * @return グループのNode
     */
    @Override
    public synchronized Node getGroupNode() {
        if (groupNode == null) {
            groupNode = new InternalNode(this, "GROUP");
        }
        return groupNode;
    }

    /**
     * このInternalSubnetのIDを返す。
     * @return このInternalSubnetのID
     */
    public int getId() {
        return id;
    }

    @Override
    public synchronized boolean startService() throws SubnetException {
        if (inService) {
            return false;
        }
        
        boolean result = network.addPort(port);
        
        if (result == false) {
            SubnetException exception = new SubnetException("cannot add port");
            LOGGER.throwing(CLASS_NAME, "startService", exception);
            throw exception;
        }
        
        inService = true;
        
        return result;
    }

    @Override
    public synchronized boolean stopService() {
        if (!inService) {
            return false;
        }
        
        boolean result = network.removePort(port);
        
        if (result == false) {
            LOGGER.logp(Level.WARNING, CLASS_NAME, "stopService", "cannot remove port");
        }
        
        inService = false;
        
        return true;
    }

    @Override
    public synchronized boolean isInService() {
        return inService;
    }
}
