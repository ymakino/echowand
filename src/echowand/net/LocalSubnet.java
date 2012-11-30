package echowand.net;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

class LocalNetwork {
    private static LocalNetwork network;
    
    public static LocalNetwork getInstance() {
        if (network == null) {
            network = new LocalNetwork();
        }
        
        return network;
    }
    
    private LinkedList<LocalSubnetPort> ports;
    
    private LocalNetwork() {
        ports = new LinkedList<LocalSubnetPort>();
    }
    
    public boolean addLocalSubnet(LocalSubnetPort port) {
        return ports.add(port);
    }
    
    public boolean removeLocalSubnet(LocalSubnetPort port) {
        return ports.remove(port);
    }
    
    public synchronized void broadcast(Frame frame) throws SubnetException {
        for (LocalSubnetPort port : ports) {
            port.enqueue(frame);
        }
    }
}

class LocalSubnetPort {
    private LinkedBlockingQueue<Frame> loopbackQueue = new LinkedBlockingQueue<Frame>();

    public Frame cloneFrame(Frame frame) throws InvalidDataException {
        CommonFrame cf = new CommonFrame(frame.getCommonFrame().toBytes());
        return new Frame(frame.getSender(), frame.getReceiver(), cf);
    }
    
    public boolean enqueue(Frame frame) throws SubnetException {
        try {
                loopbackQueue.put(cloneFrame(frame));
                return true;
        } catch (InterruptedException e) {
            throw new SubnetException("catched exception", e);
        } catch (InvalidDataException e) {
            throw new SubnetException("invalid frame", e);
        }
    }
    
    public boolean send(Frame frame) throws SubnetException {
        try {
            LocalNetwork.getInstance().broadcast(cloneFrame(frame));
            return true;
        } catch (InvalidDataException e) {
            throw new SubnetException("invalid frame", e);
        }
    }
    
    public Frame recv() throws SubnetException {
        try {
            return loopbackQueue.take();
        } catch (InterruptedException e) {
            throw new SubnetException("catched exception", e);
        }
    }
    
    public Frame recvNoWait() throws SubnetException {
        if (loopbackQueue.isEmpty()) {
            return null;
        }
        return recv();
    }
}

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
 * @author Yoshiki Makino
 */
public class LocalSubnet implements Subnet {
    private static int nextId = 0;
    private int id;
    
    private synchronized static int getNextId() {
        return nextId++;
    }
    
    private Node localNode;
    private static Node groupNode;
    private LocalSubnetPort port;
    
    public LocalSubnet() {
        id = getNextId();
        port = new LocalSubnetPort();
        LocalNetwork.getInstance().addLocalSubnet(port);
    }
    
    private void validatesSender(Frame frame) throws SubnetException {
        if (!frame.getSender().isMemberOf(this)) {
            throw new SubnetException("invalid sender");
        }
    }

    private Frame cloneFrame(Frame frame) throws InvalidDataException {
        CommonFrame cf = new CommonFrame(frame.getCommonFrame().toBytes());
        return new Frame(frame.getSender(), frame.getReceiver(), cf);
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
    
    public int getId() {
        return id;
    }
}
