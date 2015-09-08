package echowand.net;

import echowand.util.Pair;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UDPを利用したフレームの自動受信スレッド
 * @author ymakino
 */
public class InetSubnetUDPReceiverThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(InetSubnetUDPReceiverThread.class.getName());
    private static final String CLASS_NAME = InetSubnetUDPReceiverThread.class.getName();

    private InetSubnet subnet;
    private UDPNetwork network;
    private SynchronousQueue<Frame> queue;
    private boolean terminated = false;

    /**
     * InetSubnetTCPReceiverThreadを生成する。
     * @param subnet 受信したフレームの送受信ノードが存在するInetSubnet
     * @param network フレームの受信を行うUDPNetwork
     * @param queue 受信したフレームの登録先となるキュー
     */
    public InetSubnetUDPReceiverThread(InetSubnet subnet, UDPNetwork network, SynchronousQueue<Frame> queue) {
        this.subnet = subnet;
        this.network = network;
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
        while (!terminated) {
            try {
                Pair<InetNodeInfo, CommonFrame> pair = network.receive();
                InetNodeInfo nodeInfo = pair.first;
                CommonFrame commonFrame = pair.second;
                Node localNode = subnet.getLocalNode();
                Node remoteNode = subnet.getRemoteNode(nodeInfo);
                queue.put(new Frame(remoteNode, localNode, commonFrame));
            } catch (InterruptedException ex) {
                LOGGER.logp(Level.INFO, CLASS_NAME, "InetSubnetUDPReceiver.run", "interrupted", ex);
            } catch (NetworkException ex) {
                LOGGER.logp(Level.FINE, CLASS_NAME, "InetSubnetUDPReceiver.run", "catched exception", ex);
            } catch (SubnetException ex) {
                LOGGER.logp(Level.INFO, CLASS_NAME, "InetSubnetUDPReceiver.run", "invalid remoteNode", ex);
            }
        }
    }
}
