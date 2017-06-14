package echowand.net;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * InternalNetworkと他のクラスの接続ポイント
 * @author Yoshiki Makino
 */
public class InternalNetworkPort {
    private static final Logger LOGGER = Logger.getLogger(InternalNetworkPort.class.getName());
    private static final String CLASS_NAME = InternalNetworkPort.class.getName();
    
    private InternalNetwork network;
    private LinkedBlockingQueue<Frame> loopbackQueue = new LinkedBlockingQueue<Frame>();

    private Frame cloneFrame(Frame frame) throws InvalidDataException {
        CommonFrame cf = new CommonFrame(frame.getCommonFrame().toBytes());
        return new Frame(frame.getSender(), frame.getReceiver(), cf);
    }
    
    /**
     * 接続先のInternalNetworkを設定する。
     * nullが指定された場合にはInternalNetworkとの接続を切断する。
     * @param network 接続するInternalNetwork、もしくはnull
     * @return 設定に成功した場合はtrue、失敗した場合にはfalse
     */
    public synchronized boolean setNetwork(InternalNetwork network) {
        LOGGER.entering(CLASS_NAME, "setNetwork", network);
        
        if (this.network != null) {
            InternalNetwork lastNetwork = this.network;
            this.network = null;
            lastNetwork.removePort(this);
        }

        this.network = network;

        boolean result = true;

        if (network != null) {
            result = network.addPort(this);
        }
        
        LOGGER.exiting(CLASS_NAME, "setNetwork", result);
        return result;
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
     * @return  キューへの追加が成功した場合にはtrue、それ以外の場合はfalse
     * @throws SubnetException 追加に失敗した場合
     */
    public boolean enqueue(Frame frame) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "enqueue", frame);
        
        try {
            loopbackQueue.put(cloneFrame(frame));
            LOGGER.exiting(CLASS_NAME, "enqueue", true);
            return true;
        } catch (InvalidDataException e) {
            SubnetException exception = new SubnetException("invalid frame", e);
            LOGGER.throwing(CLASS_NAME, "enqueue", exception);
            throw exception;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            SubnetException exception = new SubnetException("interrupted", e);
            LOGGER.throwing(CLASS_NAME, "enqueue", exception);
            throw exception;
        }
    }
    
    /**
     * このポートを用いてフレームを送信する。
     * 指定されたフレームはこのポートが関連付けられたInternalNetworkを経由して他のポートに転送される。
     * @param frame 送信するフレーム
     * @throws SubnetException 送信に失敗した場合
     */
    public synchronized void send(Frame frame) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "send", frame);
        
        try {
            if (network != null) {
                network.broadcast(cloneFrame(frame));
            }
        } catch (InvalidDataException e) {
            SubnetException exception = new SubnetException("invalid frame", e);
            LOGGER.throwing(CLASS_NAME, "enqueue", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "send");
    }
    
    /**
     * 受信キューからフレームを取り出す。
     * キューが空の場合には、新たにフレームが追加されるまで待機する。
     * @return 受信キューから取り出されたフレーム
     * @throws SubnetException 受信に失敗した場合
     */
    public Frame receive() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "receive");
        
        try {
            Frame frame = loopbackQueue.take();
            LOGGER.exiting(CLASS_NAME, "receive", frame);
            return frame;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            SubnetException exception = new SubnetException("interrupted", e);
            LOGGER.throwing(CLASS_NAME, "enqueue", exception);
            throw exception;
        }
    }
    
    /**
     * 受信キューからフレームを取り出す。
     * キューが空の場合には、即座にnullを返す。
     * @return 受信キューから取り出されたフレーム
     */
    public Frame receiveNoWait() {
        LOGGER.entering(CLASS_NAME, "receiveNoWait");
        
        Frame frame = loopbackQueue.poll();
        
        LOGGER.exiting(CLASS_NAME, "receiveNoWait", frame);
        return frame;
    }
}