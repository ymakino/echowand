package echowand.net;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * コネクションの要求応答のバックグラウンド処理
 * @author ymakino
 */
public class TCPAcceptTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(TCPAcceptTask.class.getName());
    private static final String CLASS_NAME = TCPAcceptTask.class.getName();

    private TCPConnectionListener listener;
    private NetworkException exception = null;
    private boolean done = false;
    private boolean fail = false;
    private boolean terminated = false;

    /**
     * ConnectionAccepterを生成する。
     * @param listener 要求応答を行うConnectionListener
     */
    public TCPAcceptTask(TCPConnectionListener listener) {
        this.listener = listener;
    }

    /**
     * 処理中に発生した例外を返す。
     * @return 処理中に発生した例外
     */
    public NetworkException getException() {
        return exception;
    }

    /**
     * 処理中にエラーが発生したかどうかを返す。
     *
     * @return エラーが発生した場合にはtrue、それ以外の場合にはfalse
     */
    public boolean isFailed() {
        return fail;
    }

    /**
     * 処理が終了したかどうかを返す。
     *
     * @return 終了している場合にはtrue、それ以外の場合にはfalse
     */
    public boolean isDone() {
        return done;
    }
    
    public void terminate() {
        LOGGER.entering(CLASS_NAME, "terminate");
        
        terminated = true;
        
        LOGGER.exiting(CLASS_NAME, "terminate");
    }

    @Override
    public void run() {
        LOGGER.entering(CLASS_NAME, "run");

        try {
            while (!terminated) {
                TCPConnection connection = listener.accept();
                LOGGER.logp(Level.INFO, CLASS_NAME, "run", connection.toString());
            }
        } catch (NetworkException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "run", "catched exception", ex);
            exception = ex;
            fail = true;
        } finally {
            LOGGER.logp(Level.INFO, CLASS_NAME, "run", "done");
            done = true;
        }

        LOGGER.exiting(CLASS_NAME, "run");
    }
}
