package echowand.net;

import java.util.concurrent.LinkedBlockingQueue;

class LocalSubnetNode implements Node{
    private Subnet subnet;
    private String name;
    
    public LocalSubnetNode(Subnet subnet, String name) {
        this.subnet = subnet;
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
}

/**
 * ローカルのサブネット
 * @author Yoshiki Makino
 */
public class LocalSubnet implements Subnet {
    private Node localNode;
    private Node groupNode;
    private LinkedBlockingQueue<Frame> loopbackQueue = new LinkedBlockingQueue<Frame>();
    
    private void validatesSenderAndReceiver(Frame frame) throws SubnetException {
        if (!frame.getSender().isMemberOf(this)) {
            throw new SubnetException("invalid sender");
        }

        if (!frame.getReceiver().isMemberOf(this)) {
            throw new SubnetException("invalid receiver");
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
        validatesSenderAndReceiver(frame);

        try {
            if (shouldLocalNodeReceive(frame)) {
                loopbackQueue.put(cloneFrame(frame));
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            throw new SubnetException("catched exception", e);
        } catch (InvalidDataException e) {
            throw new SubnetException("invalid frame", e);
        }
    }
    
    /**
     * このLocalSubnetのサブネットからフレームを受信する。
     * 受信を行うまで待機する。
     * @return 受信したFrame
     * @throws SubnetException 無効なフレームを受信、あるいは受信に失敗した場合
     */
    @Override
    public Frame recv() throws SubnetException {
        try {
            return cloneFrame(loopbackQueue.take());
        } catch (InterruptedException e) {
            throw new SubnetException("catched exception", e);
        } catch (InvalidDataException e) {
            throw new SubnetException("invalid frame", e);
        }
    }

    
    /**
     * このLocalSubnetのサブネットからフレームを受信する。
     * 受信フレームがない場合には即座に戻る。
     * @return 受信したFrame、もし受信フレームがない場合にはnull
     * @throws SubnetException 無効なフレームを受信、あるいは受信に失敗した場合
     */
    public Frame recvNoWait() throws SubnetException {
        if (loopbackQueue.isEmpty()) {
            return null;
        }
        return recv();
    }
    
    /**
     * ローカルノードを表すNodeを返す。
     * @return ローカルノードのNode
     */
    @Override
    public synchronized Node getLocalNode() {
        if (localNode == null) {
            localNode = new LocalSubnetNode(this, "LOCAL");
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
}
