package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.StandardPayload;
import echowand.object.InstanceListRequestExecutor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultUpdateRemoteInfo {
    private static final Logger LOGGER = Logger.getLogger(ResultUpdateRemoteInfo.class.getName());
    private static final String CLASS_NAME = ResultUpdateRemoteInfo.class.getName();
    
    private InstanceListRequestExecutor executor;
    private LinkedList<Frame> frames;
    private LinkedList<Frame> errorFrames;
    private LinkedList<Frame> invalidFrames;
    private LinkedList<Node> nodes;
    private HashMap<Node, LinkedList<EOJ>> nodeEOJMap;
    
    public ResultUpdateRemoteInfo(InstanceListRequestExecutor executor) {
        LOGGER.entering(CLASS_NAME, "ResultUpdateRemoteInfo", executor);
        
        this.executor = executor;
        frames = new LinkedList<Frame>();
        errorFrames = new LinkedList<Frame>();
        invalidFrames = new LinkedList<Frame>();
        nodes = new LinkedList<Node>();
        nodeEOJMap = new HashMap<Node, LinkedList<EOJ>>();
        
        LOGGER.exiting(CLASS_NAME, "ResultUpdateRemoteInfo");
    }
    
    public boolean isDone() {
        LOGGER.entering(CLASS_NAME, "isDone");
        
        boolean result = executor.isDone();
        
        LOGGER.exiting(CLASS_NAME, "isDone", result);
        return result;
    }
    
    public void join() throws InterruptedException {
        LOGGER.entering(CLASS_NAME, "join");
        
        executor.join();
        
        LOGGER.exiting(CLASS_NAME, "join");
    }
    
    private int addEOJs(Node node, StandardPayload payload) {
        int count = 0;
        
        if (payload.getESV() != ESV.Get_Res || !payload.getSEOJ().isNodeProfileObject()) {
            return count;
        }
        
        addEOJ(node, payload.getSEOJ());
        
        for (int i = 0; i < payload.getFirstOPC(); i++) {
            if (payload.getFirstPropertyAt(i).getEPC() == EPC.xD6) {
                Data data = payload.getFirstPropertyAt(i).getEDT();
                
                if (data.isEmpty()) {
                    continue;
                }
                
                int size = 0xff & data.get(0);
                
                if (data.size() - 1 != size * 3) {
                    continue;
                }
                
                for (int j=0; j<size; j++) {
                    int offset = 1 + j * 3;
                    
                    byte classGroupCode = data.get(offset);
                    byte classCode = data.get(offset+1);
                    byte instanceCode = data.get(offset+2);
                    
                    addEOJ(node, new EOJ(classGroupCode, classCode, instanceCode));
                    count++;
                }
            }
        }
        
        return count;
    }
    
    private void addEOJ(Node node, EOJ eoj) {
        if (!nodeEOJMap.containsKey(node)) {
            nodes.add(node);
            nodeEOJMap.put(node, new LinkedList<EOJ>());
        }
        
        LinkedList<EOJ> eojs = nodeEOJMap.get(node);
        
        if (eojs == null) {
            eojs = new LinkedList<EOJ>();
        }
        
        if (!eojs.contains(eoj)) {
            eojs.add(eoj);
        }
        
        nodeEOJMap.put(node, eojs);
    }
    
    public synchronized boolean addFrame(Frame frame) {
        
        if (!frame.getCommonFrame().isStandardPayload()) {
            invalidFrames.add(frame);
            return false;
        }
        
        if (!(frame.getCommonFrame().getEDATA() instanceof StandardPayload)) {
            invalidFrames.add(frame);
            return false;
        }
        
        Node node = frame.getSender();
        StandardPayload payload = (StandardPayload)frame.getCommonFrame().getEDATA();
        
        if (payload.getESV() != ESV.Get_Res && payload.getESV() != ESV.Get_SNA) {
            invalidFrames.add(frame);
        }
        
        addEOJs(node, payload);
        
        if (payload.getESV() == ESV.Get_SNA) {
            errorFrames.add(frame);
        }
        
        return frames.add(frame);
    }

    public synchronized int countFrames() {
        LOGGER.entering(CLASS_NAME, "countFrames");
        
        int count = frames.size();
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }

    public synchronized Frame getFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getFrame", index);
        
        Frame frame = frames.get(index);
        LOGGER.exiting(CLASS_NAME, "getFrame", frame);
        return frame;
    }
    
    public synchronized int countNodes() {
        LOGGER.entering(CLASS_NAME, "countFrames");
        
        int count = nodes.size();
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }
    
    public synchronized Node getNode(int index) {
        LOGGER.entering(CLASS_NAME, "getNode", index);
        
        Node node = nodes.get(index);
        LOGGER.exiting(CLASS_NAME, "getNode", node);
        return node;
    }
    
    public synchronized int countEOJs(Node node) {
        LOGGER.entering(CLASS_NAME, "countEOJs");
        
        LinkedList<EOJ> eojs = nodeEOJMap.get(node);
        int count = eojs.size();
        LOGGER.exiting(CLASS_NAME, "countEOJs", count);
        return count;
    }
    
    public synchronized EOJ getEOJ(Node node, int index) {
        LOGGER.entering(CLASS_NAME, "getEOJ", index);
        
        LinkedList<EOJ> eojs = nodeEOJMap.get(node);
        EOJ eoj = eojs.get(index);
        LOGGER.exiting(CLASS_NAME, "getEOJ", eoj);
        return eoj;
    }
}
