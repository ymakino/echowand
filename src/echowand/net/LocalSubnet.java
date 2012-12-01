package echowand.net;

class LocalSubnetNode implements Node{
    private LocalSubnet subnet;
    private String name;
    
    public LocalSubnetNode(Subnet subnet, String name) {
        this.subnet = (LocalSubnet)subnet;
        this.name = name;
    }
    
    @Override
    public boolean isMemberOf(Subnet subnet) {
        return this.subnet == subnet;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof LocalSubnetNode) {
            LocalSubnetNode node = (LocalSubnetNode)o;
            return name.equals(node.name);
        } else {
            return false;
        }
    }
}

/**
 * ローカルのサブネット
 * LocalSubnetを生成する度にユニークなIDが割り振られる。
 * @author Yoshiki Makino
 */
public class LocalSubnet implements Subnet {
    private static int nextId = 0;
    
    private LocalNetwork network;
    private int id;
    
    private synchronized static int getNextId() {
        return nextId++;
    }
    
    private Node localNode;
    private static Node groupNode;
    private LocalNetworkPort port;
    
    /**
     * LocalSubnetを生成する。
     * デフォルトのLocalNetworkに接続する。
     */
    public LocalSubnet() {
        initialize(LocalNetwork.getDefault());
    }
    
    /**
     * LocalSubnetを生成する。
     * 指定された名前のLocalNetworkに接続する。
     * param networkName 接続するLocalNetworkの名前
     */
    public LocalSubnet(String networkName) {
        initialize(LocalNetwork.getByName(networkName));
    }
    
    private void initialize(LocalNetwork network) {
        id = getNextId();
        port = new LocalNetworkPort();
        this.network = network;
        network.addPort(port);
    }
    
    private void validatesSender(Frame frame) throws SubnetException {
        if (!frame.getSender().isMemberOf(this)) {
            throw new SubnetException("invalid sender");
        }
    }
    
    private boolean shouldLocalNodeReceive(Frame frame) {
            Node node = frame.getReceiver();
            return (node == getGroupNode() || node == getLocalNode());
    }
    
    /**
     * このLocalSubnetのサブネットにフレームを転送する。
     * フレームの送信ノードや受信ノードがこのLocalSubnetに含まれない場合には例外が発生する。
     * @param frame 送信するフレーム
     * @return 送信に成功した場合はtrue、そうでなければfalse
     * @throws SubnetException 送信に失敗した場合
     */
    @Override
    public boolean send(Frame frame) throws SubnetException {
        validatesSender(frame);
        return port.send(frame);
    }

    /**
     * このLocalSubnetのサブネットからフレームを受信する。
     * 受信を行うまで待機する。
     * @return 受信したFrame
     * @throws SubnetException 無効なフレームを受信、あるいは受信に失敗した場合
     */
    @Override
    public Frame recv() throws SubnetException {
        for (;;) {
            Frame frame = port.recv();
            
            if (shouldLocalNodeReceive(frame)) {
                return frame;
            }
        }
    }

    
    /**
     * このLocalSubnetのサブネットからフレームを受信する。
     * 受信フレームがない場合には即座に戻る。
     * @return 受信したFrame、もし受信フレームがない場合にはnull
     * @throws SubnetException 無効なフレームを受信、あるいは受信に失敗した場合
     */
    public Frame recvNoWait() throws SubnetException {
        for (;;) {
            Frame frame = port.recvNoWait();
            
            if (frame == null) {
                return null;
            }
            
            if (shouldLocalNodeReceive(frame)) {
                return frame;
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
            localNode = new LocalSubnetNode(this, "LOCAL(" + id + ")");
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
            groupNode = new LocalSubnetNode(this, "GROUP");
        }
        return groupNode;
    }

    /**
     * このLocalSubnetのIDを返す。
     * @return このLocalSubnetのID
     */
    public int getId() {
        return id;
    }
}
