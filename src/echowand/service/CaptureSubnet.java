package echowand.service;

import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.NodeInfo;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.LinkedList;

/**
 * 送受信したフレームをキャプチャする機能を持つSubnet
 * キャプチャ処理はCaptureSubnetObserverを利用して記述する
 * @author ymakino
 */
public class CaptureSubnet implements ExtendedSubnet {
    private Subnet internalSubnet;
    private LinkedList<CaptureSubnetObserver> observers;
    
    /**
     * 実際の処理で利用するSubnetを指定してCaptureSubnetを生成する。
     * @param subnet 処理に利用するSubnet
     */
    public CaptureSubnet(Subnet subnet) {
        this.internalSubnet = subnet;
        observers = new LinkedList<CaptureSubnetObserver>();
    }
    
    @Override
    public <S extends Subnet> S getSubnet(Class<S> cls) {
        if (cls.isInstance(this)) {
            return cls.cast(this);
        } else if (cls.isInstance(getInternalSubnet())) {
            return cls.cast(getInternalSubnet());
        } else if (getInternalSubnet() instanceof ExtendedSubnet) {
            return ((ExtendedSubnet)getInternalSubnet()).getSubnet(cls);
        } else {
            return null;
        }
    }
    
    /**
     * 実際の処理で利用するSubnetを返す。
     * @return 処理で利用するSubnet
     */
    @Override
    public Subnet getInternalSubnet() {
        return internalSubnet;
    }

    @Override
    public boolean send(Frame frame) throws SubnetException {
        boolean result = internalSubnet.send(frame);
        notifySent(frame, result);
        return result;
    }

    @Override
    public Frame receive() throws SubnetException {
        Frame frame = internalSubnet.receive();
        notifyReceived(frame);
        return frame;
    }

    @Override
    public Node getLocalNode() {
        return internalSubnet.getLocalNode();
    }

    @Override
    public Node getRemoteNode(NodeInfo nodeInfo) throws SubnetException {
        return internalSubnet.getRemoteNode(nodeInfo);
    }

    @Override
    public Node getRemoteNode(String name) throws SubnetException {
        return internalSubnet.getRemoteNode(name);
    }

    @Override
    public Node getGroupNode() {
        return internalSubnet.getGroupNode();
    }
    
    /**
     * 登録されているCaptureSubnetObserverの個数を返す。
     * @return 登録されているCaptureSubnetObserverの数
     */
    public synchronized int countObservers() {
        return observers.size();
    }
    
    /**
     * index番目のCaptureSubnetObserverを返す。
     * @param index CaptureSubnetObserverのインデックス
     * @return 指定されたCaptureSubnetObserver
     */
    public synchronized CaptureSubnetObserver getObserver(int index) {
        return observers.get(index);
    }
    
    /**
     * 指定されたCaptureSubnetObserverを追加する。
     * @param observer 追加するCaptureSubnetObserver
     * @return 追加に成功したらtrue、そうでなければfalse
     */
    public synchronized boolean addObserver(CaptureSubnetObserver observer) {
        return observers.add(observer);
    }
    
    /**
     * 指定されたCaptureSubnetObserverを抹消する。
     * @param observer 抹消するCaptureSubnetObserver
     * @return 抹消に成功したらtrue、そうでなければfalse
     */
    public synchronized boolean removeObserver(CaptureSubnetObserver observer) {
        return observers.remove(observer);
    }
    
    private synchronized LinkedList<CaptureSubnetObserver> cloneObserver() {
        return new LinkedList<CaptureSubnetObserver>(observers);
    }
    
    private void notifySent(Frame frame, boolean success) {
        for (CaptureSubnetObserver observer : cloneObserver()) {
            observer.notifySent(frame, success);
        }
    }
    
    private void notifyReceived(Frame frame) {
        for (CaptureSubnetObserver observer : cloneObserver()) {
            observer.notifyReceived(frame);
        }
    }
}
