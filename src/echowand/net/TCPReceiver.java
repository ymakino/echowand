package echowand.net;

import echowand.util.Pair;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class TCPReceiver implements TCPConnectionObserver {
    private static final Logger LOGGER = Logger.getLogger(TCPReceiver.class.getName());
    private static final String CLASS_NAME = TCPReceiver.class.getName();
    
    private LinkedBlockingQueue<Pair<TCPConnection, CommonFrame>> receiveQueue;
    private HashMap<TCPConnection, TCPConnectionReceiverThread> receiverThreadMap;
    private boolean inService = false;

    public TCPReceiver() {
        receiveQueue = new LinkedBlockingQueue<Pair<TCPConnection, CommonFrame>>();
        receiverThreadMap = new HashMap<TCPConnection, TCPConnectionReceiverThread>();
    }
    
    /**
     * このTCPReceiverが有効であるかどうか返す。
     * @return 有効であればtrue、そうでなければfalse
     */
    public synchronized boolean isInService() {
        return inService;
    }
    
    /**
     * このTCPReceiverを有効にする。
     * @return 無効から有効に変更した場合はtrue、そうでなければfalse
     */
    public synchronized boolean startService() {
        LOGGER.entering(CLASS_NAME, "startService");
        
        if (inService) {
            LOGGER.exiting(CLASS_NAME, "startService", false);
            return false;
        }

        inService = true;

        LOGGER.exiting(CLASS_NAME, "startService", true);
        return true;
    }
    
    /**
     * このTCPReceiverを無効にする。
     * @return 有効から無効に変更した場合はtrue、そうでなければfalse
     */
    public synchronized boolean stopService() {
        LOGGER.entering(CLASS_NAME, "stopService");
        
        if (!inService) {
            LOGGER.entering(CLASS_NAME, "stopService", false);
            return false;
        }
        
        removeAllConnections();

        inService = false;

        LOGGER.entering(CLASS_NAME, "stopService", true);
        return true;
    }

    /**
     * 指定されたTCPConnectionを追加する。
     * 指定されたTCPConnectionからのフレームの受信を行うように設定される。
     * @param connection 追加するTCPConnectionの指定
     * @return 追加に成功した場合にはtrue、そうでなければfalse
     */
    public synchronized boolean addConnection(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "addConnection", connection);
        
        if (!isInService()) {
            LOGGER.exiting(CLASS_NAME, "addConnection", false);
            return false;
        }
        
        if (receiverThreadMap.containsKey(connection)) {
            LOGGER.exiting(CLASS_NAME, "addConnection", false);
            return false;
        }
        
        connection.addObserver(this);
        TCPConnectionReceiverThread receiverThread = new TCPConnectionReceiverThread(connection);
        receiverThreadMap.put(connection, receiverThread);
        receiverThread.start();
        
        LOGGER.exiting(CLASS_NAME, "addConnection", true);
        return true;
    }

    /**
     * 指定されたTCPConnectionを削除する。
     * 指定されたTCPConnectionからのフレームの受信を停止するように設定される。
     * @param connection 削除するTCPConnectionの指定
     * @return 削除に成功した場合にはtrue、そうでなければfalse
     */
    public synchronized boolean removeConnection(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "removeConnection", connection);
        
        if (!isInService()) {
            LOGGER.exiting(CLASS_NAME, "removeConnection", false);
            return false;
        }
        
        if (!receiverThreadMap.containsKey(connection)) {
            LOGGER.exiting(CLASS_NAME, "removeConnection", false);
            return false;
        }
        
        TCPConnectionReceiverThread receiverThread = receiverThreadMap.get(connection);
        receiverThread.interrupt();
        receiverThreadMap.remove(connection);
        connection.removeObserver(this);
        
        LOGGER.exiting(CLASS_NAME, "removeConnection", true);
        return true;
    }
    
    /**
     * 受信を行うために管理しているTCPConnectionを全て削除する
     */
    public synchronized void removeAllConnections() {
        LOGGER.entering(CLASS_NAME, "removeAllConnections");
        
        for (TCPConnection connection : receiverThreadMap.keySet()) {
            removeConnection(connection);
        }
        
        LOGGER.exiting(CLASS_NAME, "removeAllConnections");
    }
    
    /**
     * フレームを受信する。
     * 受信したフレームが利用したTCPConnectionと、受信したフレームの内容を返す。
     * @return 受信したフレームのTCPConnectionとフレームの内容
     * @throws InterruptedException 割り込みがあった場合
     */
    public Pair<TCPConnection, CommonFrame> receive() throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "receive");
        
        Pair<TCPConnection, CommonFrame> pair = receiveQueue.take();

        LOGGER.exiting(CLASS_NAME, "receive", pair);
        return pair;
    }
    
    @Override
    public synchronized void notifySent(TCPConnection connection, CommonFrame commonFrame) {
        LOGGER.entering(CLASS_NAME, "notifySent", new Object[]{connection, commonFrame});
        
        if (!isInService()) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "notifySent", "not working");
            return;
        }
        
        LOGGER.exiting(CLASS_NAME, "notifySent", connection);
    }

    @Override
    public synchronized void notifyReceived(TCPConnection connection, CommonFrame commonFrame) {
        LOGGER.entering(CLASS_NAME, "notifyReceived", new Object[]{connection, commonFrame});
        
        if (!isInService()) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "notifyReceived", "not working");
            return;
        }

        receiveQueue.add(new Pair<TCPConnection, CommonFrame>(connection, commonFrame));

        LOGGER.exiting(CLASS_NAME, "notifyReceived", connection);
    }

    @Override
    public synchronized void notifyClosed(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "notifyClosed", connection);
        
        if (!isInService()) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "notifyClosed", "not working");
            return;
        }

        removeConnection(connection);

        LOGGER.exiting(CLASS_NAME, "notifyClosed", connection);
    }
}
