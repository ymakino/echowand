package echowand.net;

import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * コネクション集合を管理
 * @author ymakino
 */
public class TCPConnectionPool {
    private static final Logger LOGGER = Logger.getLogger(TCPConnectionPool.class.getName());
    private static final String CLASS_NAME = TCPConnectionPool.class.getName();
    
    private LinkedList<TCPConnection> connections;
    
    public TCPConnectionPool() {
        connections = new LinkedList<TCPConnection>();
    }
    
    public synchronized boolean contains(TCPConnection connection) {
        return connections.contains(connection);
    }
    
    public synchronized boolean addFirst(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "addFirst", connection);
        
        if (contains(connection)) {
            LOGGER.exiting(CLASS_NAME, "addFirst", false);
            return false;
        }
        
        connections.push(connection);
        
        LOGGER.exiting(CLASS_NAME, "addFirst", true);
        return true;
    }
    
    public synchronized boolean addLast(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "addLast", connection);
        
        if (contains(connection)) {
            LOGGER.exiting(CLASS_NAME, "addLast", false);
            return false;
        }
        
        boolean result = connections.add(connection);
        LOGGER.exiting(CLASS_NAME, "addLast", result);
        return result;
    }
    
    public synchronized boolean remove(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "remove", connection);
        
        boolean result = connections.remove(connection);
        LOGGER.exiting(CLASS_NAME, "remove", result);
        return result;
    }
    
    public synchronized boolean remove(NodeInfo nodeInfo) {
        LOGGER.entering(CLASS_NAME, "remove", nodeInfo);
        
        LinkedList<TCPConnection> newConnections = new LinkedList<TCPConnection>();
        
        for (TCPConnection connection : connections) {
            if (!nodeInfo.equals(connection.getRemoteNodeInfo())) {
                newConnections.add(connection);
            }
        }
        
        
        if (connections.size() == newConnections.size()) {
            LOGGER.exiting(CLASS_NAME, "remove", false);
            return false;
        }
        
        connections = newConnections;
        
        LOGGER.exiting(CLASS_NAME, "remove", true);
        return true;
    }
    
    public synchronized LinkedList<TCPConnection> getAll() {
        return new LinkedList<TCPConnection>(connections);
    }
    
    public synchronized LinkedList<TCPConnection> get(NodeInfo nodeInfo) {
        LinkedList<TCPConnection> resultConnections = new LinkedList<TCPConnection>();
        
        for (TCPConnection connection : connections) {
            if (nodeInfo.equals(connection.getRemoteNodeInfo())) {
                resultConnections.add(connection);
            }
        }
        
        return resultConnections;
    }
    
    public synchronized TCPConnection getFirst(NodeInfo nodeInfo) {
        for (TCPConnection connection : connections) {
            if (nodeInfo.equals(connection.getRemoteNodeInfo())) {
                return connection;
            }
        }
        
        return null;
    }
    
    public synchronized TCPConnection getLast(NodeInfo nodeInfo) {
        for (int i=connections.size()-1; i>=0; i--) {
            TCPConnection connection = connections.get(i);
            if (nodeInfo.equals(connection.getRemoteNodeInfo())) {
                return connection;
            }
        }
        
        return null;
    }
    
    public synchronized boolean moveToFirst(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "moveToFirst", connection);
        
        if (remove(connection)) {
            addFirst(connection);
            LOGGER.exiting(CLASS_NAME, "moveToFirst", true);
            return true;
        } else {
            LOGGER.exiting(CLASS_NAME, "moveToFirst", false);
            return false;
        }
    }
    
    public synchronized boolean moveToLast(TCPConnection connection) {
        LOGGER.entering(CLASS_NAME, "moveToLast", connection);
        
        if (remove(connection)) {
            addLast(connection);
            LOGGER.exiting(CLASS_NAME, "moveToLast", true);
            return true;
        } else {
            LOGGER.exiting(CLASS_NAME, "moveToLast", false);
            return false;
        }
    }
}