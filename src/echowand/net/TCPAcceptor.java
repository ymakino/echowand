package echowand.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 新たなTCP接続の受付
 * @author ymakino
 */
public class TCPAcceptor {
    private static final Logger LOGGER = Logger.getLogger(TCPAcceptor.class.getName());
    private static final String CLASS_NAME = TCPAcceptor.class.getName();
    
    private InetAddress address;
    private int portNumber;
    private ServerSocket serverSocket;
    private LinkedList<TCPAcceptorObserver> observers;
    
    /**
     * 接続待ちポート番号を指定して、TCPAcceptorを生成する。
     * @param portNumber 接続待ちポート番号の指定
     */
    public TCPAcceptor(int portNumber) {
        this(null, portNumber);
    }
    
    /**
     * 接続待ちポート番号とIPアドレスを指定して、TCPAcceptorを生成する。
     * @param address 接続待ちIPアドレスの指定
     * @param portNumber 接続待ちポート番号の指定
     */
    public TCPAcceptor(InetAddress address, int portNumber) {
        this.address = address;
        this.portNumber = portNumber;
        observers = new LinkedList<TCPAcceptorObserver>();
    }
    
    public int getPortNumber() {
        return portNumber;
    }
    
    public boolean setPortNumber(int portNumber) {
        LOGGER.entering(CLASS_NAME, "setPortNumber", portNumber);
        
        if (isInService()) {
            LOGGER.exiting(CLASS_NAME, "setPortNumber", false);
            return false;
        }
        
        this.portNumber = portNumber;
        
        LOGGER.exiting(CLASS_NAME, "setPortNumber", true);
        return true;
    }
    
    private synchronized LinkedList<TCPAcceptorObserver> cloneObservers() {
        return new LinkedList<TCPAcceptorObserver>(observers);
    }
    
    private void notifyAccepted(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "notifyAccepted");
        
        for (TCPAcceptorObserver observer : cloneObservers()) {
            observer.notifyAccepted(connection);
        }
        
        LOGGER.exiting(CLASS_NAME, "notifyAccepted");
    }
    
    /**
     * オブザーバを追加
     * @param observer オブザーバが追加された場合にはtrue、そうでなければfalse
     * @return 追加に成功した場合にはtrue、そうでなければfalse
     */
    public synchronized boolean addObserver(TCPAcceptorObserver observer) {
        return observers.add(observer);
    }
    
    /**
     * オブザーバを削除
     * @param observer オブザーバが削除された場合にはtrue、そうでなければfalse
     * @return 削除に成功した場合にはtrue、そうでなければfalse
     */
    public synchronized boolean removeObserver(TCPAcceptorObserver observer) {
        return observers.remove(observer);
    }
    
    /**
     * このTCPAcceptorが有効であるかどうか返す。
     * @return 有効であればtrue、そうでなければfalse
     */
    public synchronized boolean isInService() {
        return serverSocket != null;
    }
    
    private synchronized  ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * 新たに生成されたTCPAcceptorを返す。
     * @return 新たに生成されたTCPConnection
     * @throws NetworkException TCPConnectionの生成に失敗した場合
     * @throws IOException I/Oエラーが発生した場合
     */
    public TCPConnection accept() throws NetworkException, IOException {
        LOGGER.entering(CLASS_NAME, "accept");
        
        ServerSocket ss = getServerSocket();
        
        if (ss == null) {
            NetworkException exception = new NetworkException("not working");
            LOGGER.throwing(CLASS_NAME, "accept", exception);
            throw exception;
        }
        
        Socket socket = ss.accept();
        NodeInfo localNodeInfo = new InetNodeInfo(socket.getLocalAddress());
        NodeInfo remoteNodeInfo = new InetNodeInfo(socket.getInetAddress());
        TCPConnection connection = new TCPConnection(socket, localNodeInfo, remoteNodeInfo);

        notifyAccepted(connection);

        LOGGER.exiting(CLASS_NAME, "accept", connection);
        return connection;
    }
    
    /**
     * このTCPAcceptorを有効にする。
     * @return 無効から有効に変更した場合はtrue、そうでなければfalse
     * @throws NetworkException 有効にするのに失敗した場合
     */
    public synchronized boolean startService() throws NetworkException {
        LOGGER.entering(CLASS_NAME, "startService");

        if (serverSocket != null) {
            LOGGER.exiting(CLASS_NAME, "startService", false);
            return false;
        }
        
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            
            InetSocketAddress saddr;
            
            if (address == null) {
                saddr = new InetSocketAddress(portNumber);
            } else {
                saddr = new InetSocketAddress(address, portNumber);
            }
            serverSocket.bind(saddr);
        } catch (IOException ex) {
            NetworkException exception = new NetworkException("catched exception", ex);
            LOGGER.throwing(CLASS_NAME, "startService", exception);
            throw exception;
        }
        
        LOGGER.exiting(CLASS_NAME, "startService", true);
        return true;
    }
    
    /**
     * このTCPAcceptorを無効にする。
     * @return 有効から無効に変更した場合はtrue、そうでなければfalse
     */
    public synchronized boolean stopService() {
        LOGGER.entering(CLASS_NAME, "stopService");
        
        boolean result;
        
        if (serverSocket == null) {
            result = false;
        } else {
            try {
                serverSocket.close();
                result = true;
            } catch (IOException ex) {
                LOGGER.logp(Level.WARNING, CLASS_NAME, "stopService", "catched exception", ex);
                result = false;
            }

            serverSocket = null;
        }

        LOGGER.exiting(CLASS_NAME, "stopService", result);
        return result;
    }
}
