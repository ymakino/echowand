package echowand.service;

import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.NodeInfo;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.LinkedList;

/**
 *
 * @author ymakino
 */
public class CaptureSubnet implements Subnet {
    private Subnet internalSubnet;
    private LinkedList<CaptureSubnetObserver> observers;
    
    public CaptureSubnet(Subnet subnet) {
        this.internalSubnet = subnet;
        observers = new LinkedList<CaptureSubnetObserver>();
    }
    
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
    
    public synchronized int countObservers() {
        return observers.size();
    }
    
    public synchronized CaptureSubnetObserver getObserver(int index) {
        return observers.get(index);
    }
    
    public synchronized boolean addObserver(CaptureSubnetObserver observer) {
        return observers.add(observer);
    }
    
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
