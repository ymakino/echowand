package echowand.net;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCPコネクションの接続要求の自動応答スレッド
 * @author ymakino
 */
public class InetSubnetTCPAcceptorThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(InetSubnetTCPAcceptorThread.class.getName());
    private static final String CLASS_NAME = InetSubnetTCPAcceptorThread.class.getName();

    private InetSubnet subnet;
    private TCPAcceptor acceptor;

    /**
     * InetSubnetTCPAcceptorThreadを生成する。
     * @param subnet 生成したTCPConnectionの登録先となるInetSubnet
     * @param acceptor 要求の受理を行うTCPAcceptor
     */
    public InetSubnetTCPAcceptorThread(InetSubnet subnet, TCPAcceptor acceptor) {
        this.subnet = subnet;
        this.acceptor = acceptor;
    }

    private boolean doWork() {
        LOGGER.entering(CLASS_NAME, "doWork");
        
        boolean repeat = true;

        try {
            TCPConnection connection = acceptor.accept();
            subnet.registerTCPConnection(connection);
            LOGGER.logp(Level.FINE, CLASS_NAME, "doWork", "accept", connection);
        } catch (NetworkException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "doWork", "cannot accept connections", ex);
            repeat = false;
        } catch (IOException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "doWork", "I/O error", ex);
            repeat = false;
        }

        LOGGER.exiting(CLASS_NAME, "doWork", repeat);
        return repeat;
    }
    
    @Override
    public void run() {
        LOGGER.entering(CLASS_NAME, "run");
        
        while (!isInterrupted()) {
            if (!doWork()) {
                break;
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "run");
    }
}
