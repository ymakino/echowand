package echowand.service;

import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.NodeInfo;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 送受信したフレームをキャプチャする機能を持つSubnet
 * キャプチャ処理はCaptureSubnetObserverを利用して記述する
 * @author ymakino
 */
public class CaptureSubnet implements ExtendedSubnet {
    private static final Logger LOGGER = Logger.getLogger(CaptureSubnet.class.getName());
    private static final String CLASS_NAME = CaptureSubnet.class.getName();
    
    private Subnet internalSubnet;
    private LinkedList<CaptureSubnetObserver> observers;
    
    /**
     * 実際の処理で利用するSubnetを指定してCaptureSubnetを生成する。
     * @param subnet 処理に利用するSubnet
     */
    public CaptureSubnet(Subnet subnet) {
        LOGGER.entering(CLASS_NAME, "CaptureSubnet", subnet);
        
        this.internalSubnet = subnet;
        observers = new LinkedList<CaptureSubnetObserver>();
        
        LOGGER.exiting(CLASS_NAME, "CaptureSubnet");
    }
    
    @Override
    public <S extends Subnet> S getSubnet(Class<S> cls) {
        LOGGER.entering(CLASS_NAME, "getSubnet", cls);
        
        S subnet = null;
        
        if (cls.isInstance(this)) {
            subnet = cls.cast(this);
        } else if (cls.isInstance(getInternalSubnet())) {
            subnet = cls.cast(getInternalSubnet());
        } else if (getInternalSubnet() instanceof ExtendedSubnet) {
            subnet = ((ExtendedSubnet)getInternalSubnet()).getSubnet(cls);
        }
        
        LOGGER.exiting(CLASS_NAME, "getSubnet", subnet);
        return subnet;
    }
    
    /**
     * 実際の処理で利用するSubnetを返す。
     * @return 処理で利用するSubnet
     */
    @Override
    public Subnet getInternalSubnet() {
        LOGGER.entering(CLASS_NAME, "getInternalSubnet");
        
        Subnet subnet = internalSubnet;
        
        LOGGER.exiting(CLASS_NAME, "getInternalSubnet", subnet);
        return subnet;
    }

    @Override
    public void send(Frame frame) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "send", frame);
        
        boolean result = false;
        
        try {
            internalSubnet.send(frame);
            result = true;
        } finally {
            notifySent(frame, result);
        }
        
        LOGGER.exiting(CLASS_NAME, "send");
    }

    @Override
    public Frame receive() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "receive");
        
        Frame frame = internalSubnet.receive();
        notifyReceived(frame);
        
        LOGGER.exiting(CLASS_NAME, "receive", frame);
        return frame;
    }

    @Override
    public Node getLocalNode() {
        LOGGER.entering(CLASS_NAME, "getLocalNode");
        
        Node node = internalSubnet.getLocalNode();
        
        LOGGER.exiting(CLASS_NAME, "getLocalNode", node);
        return node;
    }

    @Override
    public Node getRemoteNode(NodeInfo nodeInfo) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "getRemoteNode", nodeInfo);
        
        Node node = internalSubnet.getRemoteNode(nodeInfo);
        
        LOGGER.exiting(CLASS_NAME, "getRemoteNode", node);
        return node;
    }

    @Override
    public Node getRemoteNode(String name) throws SubnetException {
        LOGGER.entering(CLASS_NAME, "getRemoteNode", name);
        
        Node node = internalSubnet.getRemoteNode(name);
        
        LOGGER.exiting(CLASS_NAME, "getRemoteNode", node);
        return node;
    }

    @Override
    public Node getGroupNode() {
        LOGGER.entering(CLASS_NAME, "getGroupNode");
        
        Node node = internalSubnet.getGroupNode();
        
        LOGGER.exiting(CLASS_NAME, "getGroupNode", node);
        return node;
    }
    
    /**
     * 登録されているCaptureSubnetObserverの個数を返す。
     * @return 登録されているCaptureSubnetObserverの数
     */
    public synchronized int countObservers() {
        LOGGER.entering(CLASS_NAME, "countObservers");
        
        int count = observers.size();
        
        LOGGER.exiting(CLASS_NAME, "countObservers", count);
        return count;
    }
    
    /**
     * index番目のCaptureSubnetObserverを返す。
     * @param index CaptureSubnetObserverのインデックス
     * @return 指定されたCaptureSubnetObserver
     */
    public synchronized CaptureSubnetObserver getObserver(int index) {
        LOGGER.entering(CLASS_NAME, "getObserver", index);
        
        CaptureSubnetObserver observer = observers.get(index);
        
        LOGGER.exiting(CLASS_NAME, "getObserver", observer);
        return observer;
    }
    
    /**
     * 指定されたCaptureSubnetObserverを追加する。
     * @param observer 追加するCaptureSubnetObserver
     * @return 追加に成功したらtrue、そうでなければfalse
     */
    public synchronized boolean addObserver(CaptureSubnetObserver observer) {
        LOGGER.entering(CLASS_NAME, "addObserver", observer);
        
        boolean result = observers.add(observer);
        
        LOGGER.exiting(CLASS_NAME, "addObserver", result);
        return result;
    }
    
    /**
     * 指定されたCaptureSubnetObserverを抹消する。
     * @param observer 抹消するCaptureSubnetObserver
     * @return 抹消に成功したらtrue、そうでなければfalse
     */
    public synchronized boolean removeObserver(CaptureSubnetObserver observer) {
        LOGGER.entering(CLASS_NAME, "removeObserver", observer);
        
        boolean result = observers.remove(observer);
        
        LOGGER.exiting(CLASS_NAME, "removeObserver", result);
        return result;
    }
    
    private synchronized LinkedList<CaptureSubnetObserver> cloneObserver() {
        return new LinkedList<CaptureSubnetObserver>(observers);
    }
    
    private void notifySent(Frame frame, boolean success) {
        LOGGER.entering(CLASS_NAME, "notifySent", new Object[]{frame, success});
        
        for (CaptureSubnetObserver observer : cloneObserver()) {
            LOGGER.logp(Level.FINE, CLASS_NAME, "notifySent", "notifySent: " + observer);
            observer.notifySent(frame, success);
        }
        
        LOGGER.exiting(CLASS_NAME, "notifySent");
    }
    
    private void notifyReceived(Frame frame) {
        LOGGER.entering(CLASS_NAME, "notifyReceived", frame);
        
        for (CaptureSubnetObserver observer : cloneObserver()) {
            LOGGER.logp(Level.FINE, CLASS_NAME, "notifyReceived", "notifyReceived: " + observer);
            observer.notifyReceived(frame);
        }
        
        LOGGER.exiting(CLASS_NAME, "notifyReceived");
    }

    @Override
    public boolean startService() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "startService");
        
        boolean result = internalSubnet.startService();
        
        LOGGER.exiting(CLASS_NAME, "startService", result);
        return result;
    }

    @Override
    public boolean stopService() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "stopService");
        
        boolean result = internalSubnet.stopService();
        
        LOGGER.exiting(CLASS_NAME, "stopService", result);
        return result;
    }

    @Override
    public boolean isInService() {
        LOGGER.entering(CLASS_NAME, "isInService");
        
        boolean result = internalSubnet.isInService();
        
        LOGGER.exiting(CLASS_NAME, "isInService", result);
        return result;
    }
}
