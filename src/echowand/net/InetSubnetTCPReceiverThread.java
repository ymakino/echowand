package echowand.net;

import echowand.util.Pair;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCPを利用したフレームの自動受信スレッド
 * @author ymakino
 */
public class InetSubnetTCPReceiverThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(InetSubnetTCPReceiverThread.class.getName());
    private static final String CLASS_NAME = InetSubnetTCPReceiverThread.class.getName();

    private InetSubnet subnet;
    private TCPReceiver receiver;
    private SimpleSynchronousQueue<Frame> queue;
    private boolean terminated = false;

    /**
     * InetSubnetTCPReceiverThreadを生成する。
     * @param subnet 受信したフレームの送受信ノードが存在するInetSubnet
     * @param receiver フレームの受信を行うTCPReceiver
     * @param queue 受信したフレームの登録先となるキュー
     */
    public InetSubnetTCPReceiverThread(InetSubnet subnet, TCPReceiver receiver, SimpleSynchronousQueue<Frame> queue) {
        this.subnet = subnet;
        this.receiver = receiver;
        this.queue = queue;
    }

    /**
     * このスレッドの停止を行う。
     * 強制的な割り込みを行うわけではないので、即座に終了しない可能性がある。
     */
    public void terminate() {
        LOGGER.entering(CLASS_NAME, "terminate");

        terminated = true;

        LOGGER.exiting(CLASS_NAME, "terminate");
    }

    @Override
    public void run() {
        try {
            while (!terminated) {
                try {
                    Pair<TCPConnection, CommonFrame> pair = receiver.receive();
                    LOGGER.logp(Level.FINE, CLASS_NAME, "run", "receive: " + pair);
                    TCPConnection connection = pair.first;
                    CommonFrame commonFrame = pair.second;
                    Node localNode = subnet.getLocalNode();
                    Node remoteNode = subnet.getRemoteNode(connection.getRemoteNodeInfo());
                    queue.put(new Frame(remoteNode, localNode, commonFrame, connection));
                } catch (SubnetException ex) {
                    LOGGER.logp(Level.INFO, CLASS_NAME, "run", "invalid remoteNode", ex);
                }
            }
        } catch (InterruptedException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "run", "interrupted", ex);
        } catch (InvalidQueueException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "run", "invalid queue", ex);
        } catch (NetworkException ex) {
            LOGGER.logp(Level.FINE, CLASS_NAME, "run", "catched exception", ex);
        }
    }
}
