package echowand.net;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCPフレームの自動受信スレッド
 *
 * @author ymakino
 */
public class TCPConnectionReceiverThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(TCPConnectionReceiverThread.class.getName());
    private static final String CLASS_NAME = TCPConnectionReceiverThread.class.getName();

    private TCPConnection connection;
    private boolean terminated = false;

    /**
     * 指定したコネクションからフレームを受信するTCPConnectionReceiverThreadを生成する。
     *
     * @param connection コネクションの指定
     */
    public TCPConnectionReceiverThread(TCPConnection connection) {
        this.connection = connection;
    }

    /**
     * このスレッドの停止を行う。
     * 強制的な割り込みを行うわけではないので、即座に終了しない可能性がある。
     */
    public synchronized void terminate() {
        LOGGER.entering(CLASS_NAME, "terminate");

        terminated = true;

        LOGGER.exiting(CLASS_NAME, "terminate");
    }

    @Override
    public void run() {
        LOGGER.entering(CLASS_NAME, "run");

        while (!terminated) {
            
            if (connection.isInputClosed()) {
                break;
            }
            
            try {
                CommonFrame commonFrame = connection.receive();
                LOGGER.logp(Level.FINE, CLASS_NAME, "run", "receive: " + commonFrame);
                if (commonFrame == null) {
                    break;
                }
            } catch (NetworkException ex) {
                if (!connection.isInputClosed()) {
                    LOGGER.logp(Level.FINE, CLASS_NAME, "run", "catched exception", ex);
                }
            }
        }
        
        try {
            connection.close();
        } catch (NetworkException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "run", "catched exception", ex);
        }

        LOGGER.exiting(CLASS_NAME, "run");
    }
}
