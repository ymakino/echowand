package echowand.net;

import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class TCPNetwork implements TCPConnectionObserver {
    
    private static final Logger LOGGER = Logger.getLogger(TCPNetwork.class.getName());
    private static final String CLASS_NAME = TCPNetwork.class.getName();
    
    private static final int CREATE_TIMEOUT = 10 * 1000;
    
    private Subnet subnet;
    private TCPConnectionCreator creator;
    private TCPConnectionListener listener;
    private int portNumber;
    private LinkedBlockingQueue<Frame> receiveQueue;
    private TCPConnectionPool connectionPool;
    private TCPAcceptTask acceptTask;
    private int createTimeout = CREATE_TIMEOUT;
    private boolean working = false;
    
    public TCPNetwork(Subnet subnet, int portNumber) throws NetworkException {
        this.subnet = subnet;
        this.portNumber = portNumber;
        this.listener = new TCPConnectionListener(portNumber);
        this.creator = new TCPConnectionCreator();
        receiveQueue = new LinkedBlockingQueue<Frame>();
        connectionPool = new TCPConnectionPool();
        acceptTask = null;
    }

    public TCPNetwork(Subnet subnet, InetAddress localAddress, int portNumber) throws NetworkException {
        this.subnet = subnet;
        this.portNumber = portNumber;
        this.listener = new TCPConnectionListener(localAddress, portNumber);
        this.creator = new TCPConnectionCreator();
        receiveQueue = new LinkedBlockingQueue<Frame>();
        connectionPool = new TCPConnectionPool();
        acceptTask = null;
    }
    
    public int getPortNumber() {
        return portNumber;
    }
    
    /**
     * このTCPNetworkが実行中であるかどうか返す。
     * @return 実行中であればtrue、そうでなければfalse
     */
    public synchronized boolean isWorking() {
        return working;
    }
    
    public synchronized boolean startService() {
        LOGGER.entering(CLASS_NAME, "startService");
        
        if (working) {
            LOGGER.exiting(CLASS_NAME, "startService", false);
            return false;
        }
        
        if (creator != null) {
            creator.addObserver(this);
        }
        
        if (listener != null) {
            listener.addObserver(this);
            
            try {
                acceptTask = new TCPAcceptTask(listener);
                listener.startService();
                new Thread(acceptTask).start();
            } catch (NetworkException ex) {
                LOGGER.logp(Level.INFO, CLASS_NAME, "startService", "catched exception", ex);
                LOGGER.exiting(CLASS_NAME, "startService", false);
                return false;
            }
        }
        
        working = true;
        
        LOGGER.exiting(CLASS_NAME, "startService", true);
        return true;
    }
    
    private void clearConnectionPool() {
        for (TCPConnection connection : connectionPool.getAll()) {
            removeConnection(connection);
        }
    }
    
    public synchronized boolean stopService() {
        LOGGER.entering(CLASS_NAME, "stopService");
        
        if (!working) {
            LOGGER.entering(CLASS_NAME, "stopService", false);
            return false;
        }
        
        clearConnectionPool();
        
        if (creator != null) {
            creator.removeObserver(this);
        }
        
        if (listener != null) {
            listener.removeObserver(this);
            
            try {
                acceptTask.terminate();
                acceptTask = null;
                listener.stopService();
            } catch (NetworkException ex) {
                LOGGER.logp(Level.INFO, CLASS_NAME, "stopService", "catched exception", ex);
            }
        }

        working = false;

        LOGGER.entering(CLASS_NAME, "stopService", true);
        return true;
    }
    
    public TCPConnectionListener getConnectionListener() {
        return listener;
    }
    
    public TCPConnectionCreator getConnectionCreator() {
        return creator;
    }
    
    public void setCreateTimeout(int createTimeout) {
        this.createTimeout = createTimeout;
    }
    
    public int getCreateTimeout() {
        return createTimeout;
    }

    private synchronized boolean addConnection(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "addConnection", connection);
        
        if (!isWorking()) {
            LOGGER.exiting(CLASS_NAME, "addConnection", false);
            return false;
        }
        
        boolean result = connectionPool.addFirst(connection);
        
        if (!result) {
            LOGGER.exiting(CLASS_NAME, "addConnection", false);
            return false;
        }
        
        connection.addObserver(this);
        new Thread(new TCPReceiveTask(connection)).start();
        
        LOGGER.exiting(CLASS_NAME, "addConnection", true);
        return true;
    }
    
    private synchronized boolean removeConnection(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "removeConnection", connection);
        
        if (!isWorking()) {
            LOGGER.exiting(CLASS_NAME, "removeConnection", false);
            return false;
        }
        
        boolean result = connectionPool.remove(connection);
        
        if (!result) {
            LOGGER.exiting(CLASS_NAME, "removeConnection", false);
            return false;
        }
        
        connection.removeObserver(this);
        
        LOGGER.exiting(CLASS_NAME, "removeConnection", true);
        return true;
    }
    
    public TCPConnection createConnection(Frame frame) throws NetworkException {
        LOGGER.entering(CLASS_NAME, "createConnection", frame);
        
        if (!isWorking()) {
            NetworkException exception = new NetworkException("not working");
            LOGGER.throwing(CLASS_NAME, "createConnection", exception);
            throw exception;
        }
        
        if (creator == null) {
            NetworkException exception = new NetworkException("no creator");
            LOGGER.throwing(CLASS_NAME, "createConnection", exception);
            throw exception;
        }
        
        NodeInfo localNodeInfo = frame.getSender().getNodeInfo();
        NodeInfo remoteNodeInfo = frame.getReceiver().getNodeInfo();
        TCPConnection connection = creator.create(localNodeInfo, remoteNodeInfo, getPortNumber(), getCreateTimeout());

        if (connection == null) {
            NetworkException exception = new NetworkException("cannot create connection: " + remoteNodeInfo);
            LOGGER.throwing(CLASS_NAME, "createConnection", exception);
            throw exception;
        }

        frame.setConnection(connection);
        
        LOGGER.exiting(CLASS_NAME, "createConnection", connection);
        return connection;
    }
    
    public TCPConnection deleteConnection(Frame frame) throws NetworkException {
        LOGGER.entering(CLASS_NAME, "deleteConnection", frame);

        Connection connection = frame.getConnection();

        if (connection == null) {
            return null;
        }

        if (!(connection instanceof TCPConnection)) {
            NetworkException exception = new NetworkException("invalid connection: " + connection);
            LOGGER.throwing(CLASS_NAME, "deleteConnection", exception);
            throw exception;
        }

        TCPConnection tcpConnection = (TCPConnection) connection;
        
        tcpConnection.removeObserver(this);
        removeConnection(tcpConnection);
        frame.setConnection(null);

        LOGGER.exiting(CLASS_NAME, "deleteConnection", tcpConnection);
        return tcpConnection;
    }
    
    public void send(Frame frame) throws NetworkException {
        LOGGER.entering(CLASS_NAME, "send", frame);
        
        if (!isWorking()) {
            NetworkException exception = new NetworkException("not working");
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }
        
        Connection connection = frame.getConnection();
        
        if (connection == null) {
            NetworkException exception = new NetworkException("no connection: " + frame);
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }
        
        if (!(connection instanceof TCPConnection)) {
            NetworkException exception = new NetworkException("invalid connection: " + connection);
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }
        
        TCPConnection tcpConnection = (TCPConnection)connection;
        
        if (!connectionPool.contains(tcpConnection)) {
            NetworkException exception = new NetworkException("no such connection: " + tcpConnection);
            LOGGER.throwing(CLASS_NAME, "send", exception);
            throw exception;
        }
        
        connection.send(frame.getCommonFrame());
        LOGGER.exiting(CLASS_NAME, "send");
    }
    
    public Frame receive() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "receive");
        
        if (!isWorking()) {
            NetworkException exception = new NetworkException("not working");
            LOGGER.throwing(CLASS_NAME, "receive", exception);
            throw exception;
        }
        
        if (listener == null) {
            NetworkException exception = new NetworkException("no listener");
            LOGGER.throwing(CLASS_NAME, "receive", exception);
            throw exception;
        }
        
        try {
            Frame frame = receiveQueue.take();
            LOGGER.exiting(CLASS_NAME, "receive", frame);
            return frame;
        } catch (InterruptedException ex) {
            NetworkException exception = new NetworkException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "receive", exception);
            throw exception;
        }
    }
    
    @Override
    public synchronized void notifySent(TCPConnection connection, CommonFrame commonfram) {
        LOGGER.entering(CLASS_NAME, "notifySent", connection);
        
        try {
            connection.closeOutput();
        } catch (NetworkException ex) {
            LOGGER.logp(Level.INFO, CLASS_NAME, "notifySent", "catched exception", ex);
        }
        
        LOGGER.exiting(CLASS_NAME, "notifySent", connection);
    }

    @Override
    public synchronized void notifyReceived(TCPConnection connection, CommonFrame commonFrame) {
        LOGGER.entering(CLASS_NAME, "notifyReceive", connection);
        
        if (isWorking()) {
            try {
                connection.closeInput();
                
                Node localNode = subnet.getLocalNode();
                Node remoteNode = subnet.getRemoteNode(connection.getRemoteNodeInfo());
                Frame frame = new Frame(remoteNode, localNode, commonFrame, connection);
                receiveQueue.add(frame);
            } catch (SubnetException ex) {
                LOGGER.logp(Level.INFO, CLASS_NAME, "notifyReceive", "catched exception", ex);
            } catch (NetworkException ex) {
                LOGGER.logp(Level.INFO, CLASS_NAME, "notifyReceive", "catched exception", ex);
            }
        }
            
        LOGGER.exiting(CLASS_NAME, "notifyReceive", connection);
    }

    @Override
    public synchronized void notifyAccepted(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "notifyAccepted", connection);
        
        if (isWorking()) {
            addConnection(connection);
        }
        
        LOGGER.exiting(CLASS_NAME, "notifyAccepted", connection);
    }

    @Override
    public synchronized void notifyClosed(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "notifyClosed", connection);
        
        if (isWorking()) {
            removeConnection(connection);
        }
            
        LOGGER.exiting(CLASS_NAME, "notifyClosed", connection);
    }

    @Override
    public void notifyConnected(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "notifyConnected", connection);
        
        if (isWorking()) {
            addConnection(connection);
        }
        
        LOGGER.exiting(CLASS_NAME, "notifyConnected", connection);
    }
}
