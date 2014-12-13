package echowand.net;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * InternalNetworkと他のクラスの接続ポイント
 * @author Yoshiki Makino
 */
public class InternalNetworkPort {
    private InternalNetwork network;
    private SimpleBlockingQueue<Frame> loopbackQueue = new SimpleBlockingQueue<Frame>();

    private Frame cloneFrame(Frame frame) throws InvalidDataException {
        CommonFrame cf = new CommonFrame(frame.getCommonFrame().toBytes());
        return new Frame(frame.getSender(), frame.getReceiver(), cf);
    }
    
    /**
     * 接続先のInternalNetworkを設定する。
     * nullが指定された場合にはInternalNetworkとの接続を切断する。
     * @param network 接続するInternalNetwork、もしくはnull
     * @return 設定に精巧した場合はtrue、失敗した場合にはfalse
     */
    public synchronized boolean setNetwork(InternalNetwork network) {
        if (this.network != null) {
            network.removePort(this);
        }

        this.network = network;

        if (network == null) {
            return true;
        }

        return network.addPort(this);
    }
    
    /**
     * このInternalNetworkPortが接続しているInternalNetworkを返す。
     * @return このポートが接続しているInternalNetwork
     */
    public synchronized InternalNetwork getNetwork() {
        return network;
    }
    
    /**
     * 受信キューにフレームを追加する
     * @param frame 受信キューに追加するフレーム
     * @throws SubnetException 追加に失敗した場合
     */
    public void enqueue(Frame frame) throws SubnetException {
        try {
            loopbackQueue.add(cloneFrame(frame));
        } catch (InvalidDataException e) {
            throw new SubnetException("invalid frame", e);
        } catch (InvalidQueueException e) {
            throw new SubnetException("invalid queue", e);
        }
    }
    
    /**
     * このポートを用いてフレームを送信する。
     * 指定されたフレームはこのポートが関連付けられたInternalNetworkを経由して他のポートに転送される。
     * @param frame 転送するフレーム
     * @return  キューへの追加が成功した場合にはtrue、それ以外の場合はfalse
     * @throws SubnetException 転送に失敗した場合
     */
    public synchronized boolean send(Frame frame) throws SubnetException {
        try {
            if (network != null) {
                network.broadcast(cloneFrame(frame));
            }
            return true;
        } catch (InvalidDataException e) {
            throw new SubnetException("invalid frame", e);
        }
    }
    
    /**
     * 受信キューからフレームを取り出す。
     * キューが空の場合には、新たにフレームが追加されるまで待機する。
     * @return 受信キューから取り出されたフレーム
     * @throws SubnetException 取り出しに失敗した場合
     */
    public Frame receive() throws SubnetException {
        try {
            return loopbackQueue.take();
        } catch (InterruptedException e) {
            throw new SubnetException("catched exception", e);
        } catch (InvalidQueueException e) {
            throw new SubnetException("catched exception", e);
        }
    }
    
    /**
     * 受信キューからフレームを取り出す。
     * キューが空の場合には、即座にnullを返す。
     * @return 受信キューから取り出されたフレーム
     * @throws SubnetException 取り出しに失敗した場合
     */
    public Frame receiveNoWait() throws SubnetException {
        if (loopbackQueue.isEmpty()) {
            return null;
        }
        return receive();
    }
}