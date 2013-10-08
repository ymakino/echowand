package echowand.net;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCPフレームを受信する。
 * Runnableを実装することで、別スレッドでの動作も可能になっている。
 * @author ymakino
 */
public class TCPReceiveTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(TCPReceiveTask.class.getName());
    private static final String CLASS_NAME = TCPReceiveTask.class.getName();

    private TCPConnection connection;
    private NetworkException exception = null;
    private boolean done = false;
    private boolean fail = false;
    private boolean terminated = false;

    /**
     * 指定したコネクションからバックグラウンドでフレームを受信するTCPReceiveTaskを生成する。
     * @param connection コネクションの指定
     */
    public TCPReceiveTask(TCPConnection connection) {
        this.connection = connection;
    }

    /**
     * 処理中に発生した例外を返す。
     * @return 処理中に発生した例外
     */
    public NetworkException getException() {
        return exception;
    }

    /**
     * 処理が失敗したかどうかを返す。
     * @return 処理が失敗した場合にはtrue、それ以外の場合にはfalse
     */
    public synchronized boolean isFailed() {
        return fail;
    }

    /**
     * 処理が終了したかどうかを返す。
     * @return 処理が終了している場合にはtrue、それ以外の場合にはfalse
     */
    public synchronized boolean isDone() {
        return done;
    }
    
    public synchronized void terminate() {
        LOGGER.entering(CLASS_NAME, "terminate");
        
        terminated = true;
        
        LOGGER.exiting(CLASS_NAME, "terminate");
    }

    @Override
    public void run() {
        LOGGER.entering(CLASS_NAME, "run");
        
        try {
            while (!terminated) {
                CommonFrame commonFrame = connection.receive();
                LOGGER.logp(Level.INFO, CLASS_NAME, "run", commonFrame.toString());
            }
        } catch (NetworkException ex) {
            if (!connection.isClosed()) {
                LOGGER.logp(Level.INFO, CLASS_NAME, "run", "catched exception", ex);
                exception = ex;
                fail = true;
            }
        } finally {
            done = true;
            LOGGER.exiting(CLASS_NAME, "run");
        }
    }
}
