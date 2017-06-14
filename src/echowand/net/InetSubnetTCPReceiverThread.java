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
    
    private boolean doWork() throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "doWork");
        
        boolean repeat = true;
        
        try {
            Pair<TCPConnection, CommonFrame> pair = receiver.receive();
            LOGGER.logp(Level.FINE, CLASS_NAME, "runLoop", "receive: " + pair);
            TCPConnection connection = pair.first;
            CommonFrame commonFrame = pair.second;
            Node localNode = subnet.getLocalNode();
            Node remoteNode = subnet.getRemoteNode(connection.getRemoteNodeInfo());
            queue.put(new Frame(remoteNode, localNode, commonFrame, connection));
        } catch (SubnetException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "runLoop", "invalid remoteNode", ex);
        } catch (SimpleSynchronousQueueException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "run", "invalid queue", ex);
            repeat = false;
        }
        
        LOGGER.exiting(CLASS_NAME, "run", repeat);
        return repeat;
    }
    
    @Override
    public void run() {
        LOGGER.entering(CLASS_NAME, "run");
        
        try {
            while (!isInterrupted()) {
                if (!doWork()) {
                    break;
                }
            }
        } catch (InterruptedException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "run", "interrupted", ex);
        }
        
        LOGGER.exiting(CLASS_NAME, "run");
    }
}
