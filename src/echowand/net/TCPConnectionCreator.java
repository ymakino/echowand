package echowand.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class TCPConnectionCreator {
    private LinkedList<TCPConnectionObserver> observers;
    
    public TCPConnectionCreator() {
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

    private synchronized void notifyConnected(TCPConnection connection) {
        for (TCPConnectionObserver observer : observers) {
            observer.notifyConnected(connection);
        }
    }
    
    public TCPConnection create(NodeInfo localNodeInfo, NodeInfo remoteNodeInfo, int portNumber, int timeout) throws NetworkException {
        try {
            if (!(localNodeInfo instanceof InetNodeInfo)) {
                throw new NetworkException("invalid node: "+ localNodeInfo);
            }
            
            if (!(remoteNodeInfo instanceof InetNodeInfo)) {
                throw new NetworkException("invalid node: "+ remoteNodeInfo);
            }
            
            InetNodeInfo remoteInetNodeInfo = (InetNodeInfo)remoteNodeInfo;
            InetAddress remoteAddress = remoteInetNodeInfo.getAddress();
            InetSocketAddress remoteSocketAddress = new InetSocketAddress(remoteAddress, portNumber);

            Socket socket = new Socket();
            socket.connect(remoteSocketAddress, timeout);

            TCPConnection connection = new TCPConnection(socket, localNodeInfo, remoteNodeInfo);
            
            notifyConnected(connection);
            
            return connection;
            
        } catch (IOException ex) {
            throw new NetworkException("catched exception", ex);
        }
    }
}
