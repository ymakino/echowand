package echowand.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class TCPConnectionListener {
    private static final Logger LOGGER = Logger.getLogger(TCPConnectionListener.class.getName());
    private static final String CLASS_NAME = TCPConnectionListener.class.getName();
    
    private InetAddress address;
    private int port;
    private ServerSocket serverSocket;
    private LinkedList<TCPConnectionObserver> observers;
    
    public TCPConnectionListener(int port) throws NetworkException {
        this(null, port);
    }
    
    public TCPConnectionListener(InetAddress address, int port) throws NetworkException {
        this.address = address;
        this.port = port;
        observers = new LinkedList<TCPConnectionObserver>();
    }
    /**
     * コネクション確立時に通知を行うオブジェクトの追加
     *
     * @param observer 追加する通知オブジェクト
     * @return 通知オブジェクトの追加に成功した場合にはtrue、それ以外の場合にはfalse
     */
    public synchronized boolean addObserver(TCPConnectionObserver observer) {
        return observers.add(observer);
    }

    /**
     * コネクション確立時に通知を行うオブジェクトの削除
     *
     * @param observer 削除する通知オブジェクト
     * @return 通知オブジェクトの削除に成功した場合にはtrue、それ以外の場合にはfalse
     */
    public synchronized boolean removeObserver(TCPConnectionObserver observer) {
        return observers.remove(observer);
    }

    private synchronized void notifyAccept(TCPConnection connection) {
        for (TCPConnectionObserver observer : observers) {
            observer.notifyAccepted(connection);
        }
    }
    
    public synchronized boolean isWorking() {
        return serverSocket != null;
    }

    public TCPConnection accept() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "accept");
        
        try {
            Socket socket = serverSocket.accept();
            NodeInfo localNodeInfo = new InetNodeInfo(socket.getLocalAddress());
            NodeInfo remoteNodeInfo = new InetNodeInfo(socket.getInetAddress());
            TCPConnection connection = new TCPConnection(socket, localNodeInfo, remoteNodeInfo);
            
            notifyAccept(connection);
            
            LOGGER.exiting(CLASS_NAME, "accept", connection);
            return connection;
        } catch (IOException ex) {
            NetworkException exception = new NetworkException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "accept", exception);
            throw exception;
        }
    }
    
    public synchronized void startService() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "startService");
            
        if (serverSocket != null) {
            NetworkException exception = new NetworkException("still working");
            LOGGER.throwing(CLASS_NAME, "startService", exception);
            throw exception;
        }
        
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            
            InetSocketAddress saddr;
            
            if (address == null) {
                saddr = new InetSocketAddress(port);
            } else {
                saddr = new InetSocketAddress(address, port);
            }
            serverSocket.bind(saddr);
        } catch (IOException ex) {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ex1) {
                }
            }
            
            NetworkException exception = new NetworkException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "startService", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "startService");
    }
    
    public synchronized void stopService() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "stopService");
        
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                NetworkException exception = new NetworkException("catched exception", ex);
                LOGGER.throwing(CLASS_NAME, "stopService", exception);
                throw exception;
            }

            serverSocket = null;
        }
        
        LOGGER.exiting(CLASS_NAME, "stopService");
    }
}
