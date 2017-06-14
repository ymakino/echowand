package echowand.net;

import echowand.util.Pair;
import java.io.IOException;
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
    private SimpleSynchronousQueue<Frame> queue;

    /**
     * InetSubnetTCPReceiverThreadを生成する。
     * @param subnet 受信したフレームの送受信ノードが存在するInetSubnet
     * @param network フレームの受信を行うUDPNetwork
     * @param queue 受信したフレームの登録先となるキュー
     */
    public InetSubnetUDPReceiverThread(InetSubnet subnet, UDPNetwork network, SimpleSynchronousQueue<Frame> queue) {
        this.subnet = subnet;
        this.network = network;
        this.queue = queue;
    }
    
    private boolean doWork() throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "doWork");
        
        boolean repeat = true;
        
        try {
            Pair<InetNodeInfo, CommonFrame> pair = network.receive();
            LOGGER.logp(Level.FINE, CLASS_NAME, "doWork", "receive: " + pair);
            InetNodeInfo nodeInfo = pair.first;
            CommonFrame commonFrame = pair.second;
            Node localNode = subnet.getLocalNode();
            Node remoteNode = subnet.getRemoteNode(nodeInfo);
            queue.put(new Frame(remoteNode, localNode, commonFrame));
        } catch (SubnetException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "doWork", "invalid remoteNode", ex);
        } catch (InvalidDataException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "doWork", "invalid frame", ex);
        } catch (NetworkException ex) {
            LOGGER.logp(Level.FINE, CLASS_NAME, "doWork", "catched exception", ex);
            repeat = false;
        } catch (IOException ex) {
            LOGGER.logp(Level.FINE, CLASS_NAME, "run", "I/O error", ex);
            repeat = false;
        } catch (SimpleSynchronousQueueException ex) {
            LOGGER.logp(Level.FINE, CLASS_NAME, "run", "invalid queue", ex);
            repeat = false;
        }
            
        LOGGER.exiting(CLASS_NAME, "doWork", repeat);
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
