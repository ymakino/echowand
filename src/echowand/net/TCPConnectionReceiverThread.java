package echowand.net;

import java.io.IOException;
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

    /**
     * 指定したコネクションからフレームを受信するTCPConnectionReceiverThreadを生成する。
     *
     * @param connection コネクションの指定
     */
    public TCPConnectionReceiverThread(TCPConnection connection) {
        this.connection = connection;
    }
    
    private boolean doWork() {
        LOGGER.entering(CLASS_NAME, "doWork");
        
        boolean repeat = true;
        
        try {
            if (connection.isInputClosed()) {
                LOGGER.logp(Level.FINE, CLASS_NAME, "run", "input stream is closed");
                repeat = false;
            } else {
                CommonFrame commonFrame = connection.receive();

                LOGGER.logp(Level.FINE, CLASS_NAME, "run", "receive: " + commonFrame);

                if (commonFrame == null) {
                    repeat = false;
                }
            }
        } catch (NetworkException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "run", "catched exception", ex);
            repeat = false;
        }
        
        LOGGER.exiting(CLASS_NAME, "doWork", repeat);
        return repeat;
    }
    
    private void cleanUp() {
        try {
            connection.close();
        } catch (NetworkException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "cleanUp", "catched exception", ex);
        }
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
        } finally {
            cleanUp();
        }
        
        LOGGER.exiting(CLASS_NAME, "run");
    }
}
