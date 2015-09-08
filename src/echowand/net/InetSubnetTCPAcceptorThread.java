package echowand.net;

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
    private boolean terminated = false;

    /**
     * InetSubnetTCPAcceptorThreadを生成する。
     * @param subnet 生成したTCPConnectionの登録先となるInetSubnet
     * @param acceptor 要求の受理を行うTCPAcceptor
     */
    public InetSubnetTCPAcceptorThread(InetSubnet subnet, TCPAcceptor acceptor) {
        this.subnet = subnet;
        this.acceptor = acceptor;
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
        LOGGER.entering(CLASS_NAME, "run");

        while (!terminated) {
            try {
                TCPConnection connection = acceptor.accept();
                subnet.registerTCPConnection(connection);
                LOGGER.logp(Level.FINE, CLASS_NAME, "run", "accept", connection);
            } catch (NetworkException ex) {
                LOGGER.logp(Level.INFO, CLASS_NAME, "run", "catched exception", ex);
            }
        }

        LOGGER.exiting(CLASS_NAME, "run");
    }
}
