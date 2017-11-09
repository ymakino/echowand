package echowand.service.result;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.StandardPayload;
import echowand.object.InstanceListRequestExecutor;
import echowand.service.TimestampManager;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class UpdateRemoteInfoResult {
    private static final Logger LOGGER = Logger.getLogger(UpdateRemoteInfoResult.class.getName());
    private static final String CLASS_NAME = UpdateRemoteInfoResult.class.getName();
    
    private final EPC INSTANCE_LIST_S = EPC.xD6;

    private InstanceListRequestExecutor executor;
    private TimestampManager timestampManager;
    private LinkedList<ResultFrame> requestFrameList;
    private LinkedList<ResultFrame> requestFrameSuccessList;
    private LinkedList<ResultFrame> requestFrameFailList;
    private LinkedList<ResultFrame> frameList;
    private LinkedList<ResultFrame> errorFrameList;
    private LinkedList<ResultFrame> invalidFrameList;
    private LinkedList<Node> nodeList;
    private HashMap<Node, LinkedList<EOJ>> nodeEOJMap;
    
    private UpdateRemoteInfoListener updateRemoteInfoListener;

    public UpdateRemoteInfoResult(InstanceListRequestExecutor executor, TimestampManager timestampManager) {
        LOGGER.entering(CLASS_NAME, "UpdateRemoteInfoResult", new Object[]{executor, timestampManager});

        this.executor = executor;
        this.timestampManager = timestampManager;
        
        requestFrameList = new LinkedList<ResultFrame>();
        requestFrameSuccessList = new LinkedList<ResultFrame>();
        requestFrameFailList = new LinkedList<ResultFrame>();
        frameList = new LinkedList<ResultFrame>();
        errorFrameList = new LinkedList<ResultFrame>();
        invalidFrameList = new LinkedList<ResultFrame>();
        nodeList = new LinkedList<Node>();
        nodeEOJMap = new HashMap<Node, LinkedList<EOJ>>();

        LOGGER.exiting(CLASS_NAME, "UpdateRemoteInfoResult");
    }
    
    public synchronized void setUpdateRemoteInfoListener(UpdateRemoteInfoListener updateRemoteInfoListener) {
        LOGGER.entering(CLASS_NAME, "setUpdateRemoteInfoListener", updateRemoteInfoListener);
        
        this.updateRemoteInfoListener = updateRemoteInfoListener;
        
        LOGGER.exiting(CLASS_NAME, "setUpdateRemoteInfoListener");
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
    
    public void begin() {
        LOGGER.entering(CLASS_NAME, "begin");

        if (updateRemoteInfoListener != null) {
            updateRemoteInfoListener.begin(this);
        }

        LOGGER.exiting(CLASS_NAME, "begin");
    }
    
    public void finish() {
        LOGGER.entering(CLASS_NAME, "finish");

        if (updateRemoteInfoListener != null) {
            updateRemoteInfoListener.finish(this);
        }

        LOGGER.exiting(CLASS_NAME, "finish");
    }
    
    private LinkedList<EOJ> takeEOJs(StandardPayload payload) {
        LOGGER.entering(CLASS_NAME, "takeEOJs", payload);
        
        LinkedList<EOJ> eojs = new LinkedList<EOJ>();
        
        if (payload.getESV() != ESV.Get_Res || !payload.getSEOJ().isNodeProfileObject()) {
            LOGGER.exiting(CLASS_NAME, "takeEOJs", eojs);
            return eojs;
        }
        
        eojs.add(payload.getSEOJ());

        for (int i = 0; i < payload.getFirstOPC(); i++) {
            if (payload.getFirstPropertyAt(i).getEPC() == INSTANCE_LIST_S) {
                Data data = payload.getFirstPropertyAt(i).getEDT();

                if (data.isEmpty()) {
                    continue;
                }

                int size = 0xff & data.get(0);

                if (data.size() - 1 != size * 3) {
                    continue;
                }

                for (int j = 0; j < size; j++) {
                    int offset = 1 + j * 3;

                    byte classGroupCode = data.get(offset);
                    byte classCode = data.get(offset + 1);
                    byte instanceCode = data.get(offset + 2);
                    
                    eojs.add(new EOJ(classGroupCode, classCode, instanceCode));
                }
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "takeEOJs", eojs);
        return eojs;
    }

    private void addEOJ(Node node, EOJ eoj) {
        LOGGER.exiting(CLASS_NAME, "addEOJ", new Object[]{node, eoj});
            
        if (!nodeEOJMap.containsKey(node)) {
            nodeList.add(node);
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
        
        LOGGER.exiting(CLASS_NAME, "addEOJ");
    }
    
    public boolean hasStandardPayload(Frame frame) {
        LOGGER.entering(CLASS_NAME, "hasStandardPayload", frame);
        
        CommonFrame commonFrame = frame.getCommonFrame();
        
        boolean result = false;
        
        if (commonFrame.isStandardPayload()) {
            result = commonFrame.getEDATA(StandardPayload.class) != null;
        }
        
        LOGGER.exiting(CLASS_NAME, "hasStandardPayload", result);
        return result;
    }
    
    public synchronized boolean addRequestFrame(Frame frame, boolean success) {
        LOGGER.entering(CLASS_NAME, "addRequestFrame", new Object[]{frame, success});

        boolean result = addRequestFrame(createResultFrame(frame), success);

        LOGGER.exiting(CLASS_NAME, "addRequestFrame", result);
        return result;
    }
    
    public synchronized boolean addRequestFrame(ResultFrame resultFrame, boolean success) {
        LOGGER.entering(CLASS_NAME, "addRequestFrame", new Object[]{resultFrame, success});

        boolean result = requestFrameList.add(resultFrame);
        
        if (success) {
            result &= requestFrameSuccessList.add(resultFrame);
        } else {
            result &= requestFrameFailList.add(resultFrame);
        }
        
        if (updateRemoteInfoListener != null) {
            updateRemoteInfoListener.send(this, resultFrame, success);
        }

        LOGGER.exiting(CLASS_NAME, "addRequestFrame", result);
        return result;
    }

    public synchronized boolean addFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "addFrame", frame);

        boolean result = addFrame(createResultFrame(frame));

        LOGGER.exiting(CLASS_NAME, "addFrame", result);
        return result;
    }

    public synchronized boolean addFrame(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "addFrame", resultFrame);

        if (!resultFrame.getCommonFrame().isStandardPayload()) {
            invalidFrameList.add(resultFrame);
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }

        if (!hasStandardPayload(resultFrame.getActualFrame())) {
            invalidFrameList.add(resultFrame);
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }

        Node node = resultFrame.getSender();
        StandardPayload payload = resultFrame.getCommonFrame().getEDATA(StandardPayload.class);

        if (payload.getESV() != ESV.Get_Res && payload.getESV() != ESV.Get_SNA) {
            invalidFrameList.add(resultFrame);
            LOGGER.exiting(CLASS_NAME, "addFrame", false);
            return false;
        }

        if (payload.getESV() == ESV.Get_SNA) {
            errorFrameList.add(resultFrame);
        }

        boolean result = frameList.add(resultFrame);

        LinkedList<EOJ> eojs = takeEOJs(payload);
        
        for (EOJ eoj : eojs) {
            addEOJ(node, eoj);
        }
        
        if (updateRemoteInfoListener != null) {
            updateRemoteInfoListener.receive(this, resultFrame);
            
            if (!eojs.isEmpty()) {
                updateRemoteInfoListener.receive(this, resultFrame, node, eojs);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "addFrame", result);
        return result;
    }

    private synchronized ResultFrame createResultFrame(Frame frame) {
        LOGGER.entering(CLASS_NAME, "createResultFrame", frame);

        long timestamp = timestampManager.get(frame, System.currentTimeMillis());
        ResultFrame resultFrame = new ResultFrame(frame, timestamp);

        LOGGER.exiting(CLASS_NAME, "createResultFrame", resultFrame);
        return resultFrame;
    }

    public synchronized int countRequestFrames() {
        LOGGER.entering(CLASS_NAME, "countRequestFrames");

        int count = requestFrameList.size();
        LOGGER.exiting(CLASS_NAME, "countRequestFrames", count);
        return count;
    }

    public synchronized int countRequestFrames(boolean success) {
        LOGGER.entering(CLASS_NAME, "countRequestFrames", success);

        int count;
        
        if (success) {
            count = requestFrameSuccessList.size();
        } else {
            count = requestFrameFailList.size();
        }
        
        LOGGER.exiting(CLASS_NAME, "countRequestFrames", count);
        return count;
    }

    public synchronized ResultFrame getRequestFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getRequestFrame", index);

        ResultFrame resultFrame = requestFrameList.get(index);
        LOGGER.exiting(CLASS_NAME, "getRequestFrame", resultFrame);
        return resultFrame;
    }

    public synchronized ResultFrame getRequestFrame(int index, boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestFrame", new Object[]{index, success});

        ResultFrame resultFrame;
        
        if (success) {
            resultFrame = requestFrameSuccessList.get(index);
        } else {
            resultFrame = requestFrameFailList.get(index);
        }
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized List<ResultFrame> getRequestFrameList() {
        LOGGER.entering(CLASS_NAME, "getRequestFrameList");
        
        LinkedList<ResultFrame> resultFrameList = new LinkedList<ResultFrame>(requestFrameList);
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrameList", resultFrameList);
        return resultFrameList;
    }
    
    public synchronized List<ResultFrame> getRequestFrameList(boolean success) {
        LOGGER.entering(CLASS_NAME, "getRequestFrameList", success);
        
        LinkedList<ResultFrame> resultFrameList ;
        
        if (success) {
            resultFrameList = new LinkedList<ResultFrame>(requestFrameSuccessList);
        } else {
            resultFrameList = new LinkedList<ResultFrame>(requestFrameFailList);
        }
        
        LOGGER.exiting(CLASS_NAME, "getRequestFrameList", resultFrameList);
        return resultFrameList;
    }

    public synchronized int countFrames() {
        LOGGER.entering(CLASS_NAME, "countFrames");

        int count = frameList.size();
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }

    public synchronized ResultFrame getFrame(int index) {
        LOGGER.entering(CLASS_NAME, "getFrame", index);

        ResultFrame resultFrame = frameList.get(index);
        LOGGER.exiting(CLASS_NAME, "getFrame", resultFrame);
        return resultFrame;
    }
    
    public synchronized List<ResultFrame> getFrameList(Node node) {
        LOGGER.entering(CLASS_NAME, "getFrame");
        
        LinkedList<ResultFrame> resultFrameList = new LinkedList<ResultFrame>();
        
        for (ResultFrame resultFrame : frameList) {
            if (resultFrame.getSender().equals(node)) {
                resultFrameList.add(resultFrame);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "getFrame", resultFrameList);
        return resultFrameList;
    }
    
    public synchronized List<ResultFrame> getFrameList() {
        LOGGER.entering(CLASS_NAME, "getFrameList");
        
        LinkedList<ResultFrame> resultFrameList = new LinkedList<ResultFrame>(frameList);
        
        LOGGER.exiting(CLASS_NAME, "getFrameList", resultFrameList);
        return resultFrameList;
    }

    public synchronized int countNodes() {
        LOGGER.entering(CLASS_NAME, "countFrames");

        int count = nodeList.size();
        LOGGER.exiting(CLASS_NAME, "countFrames", count);
        return count;
    }

    public synchronized Node getNode(int index) {
        LOGGER.entering(CLASS_NAME, "getNode", index);

        Node node = nodeList.get(index);
        LOGGER.exiting(CLASS_NAME, "getNode", node);
        return node;
    }
    
    public synchronized List<Node> getNodeList() {
        LOGGER.entering(CLASS_NAME, "getNodeList");
        
        LinkedList<Node> resultNodeList = new LinkedList<Node>(nodeList);
        
        LOGGER.exiting(CLASS_NAME, "getNodeList", resultNodeList);
        return resultNodeList;
    }
    
    public synchronized List<Node> getNodeList(EOJ eoj) {
        LOGGER.entering(CLASS_NAME, "getNodeList", eoj);
        
        LinkedList<Node> resultNodeList = new LinkedList<Node>();
        
        for (Node node : nodeList) {
            if (nodeEOJMap.get(node).contains(eoj)) {
                resultNodeList.push(node);
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "getNodeList", resultNodeList);
        return resultNodeList;
    }

    public synchronized int countEOJs(Node node) {
        LOGGER.entering(CLASS_NAME, "countEOJs", node);

        LinkedList<EOJ> eojList = nodeEOJMap.get(node);
        
        if (eojList == null) {
            LOGGER.exiting(CLASS_NAME, "countEOJs", 0);
            return 0;
        }
        
        int count = eojList.size();
        LOGGER.exiting(CLASS_NAME, "countEOJs", count);
        return count;
    }

    public synchronized EOJ getEOJ(Node node, int index) {
        LOGGER.entering(CLASS_NAME, "getEOJ", new Object[]{node, index});

        LinkedList<EOJ> eojs = nodeEOJMap.get(node);
        
        if (eojs == null) {
            eojs = new LinkedList<EOJ>();
        }
        
        EOJ eoj = eojs.get(index);
        LOGGER.exiting(CLASS_NAME, "getEOJ", eoj);
        return eoj;
    }
    
    public synchronized List<EOJ> getEOJList(Node node) {
        LOGGER.entering(CLASS_NAME, "getEOJList", node);
        
        LinkedList<EOJ> eojList = nodeEOJMap.get(node);
        
        if (eojList == null) {
            eojList = new LinkedList<EOJ>();
        }
        
        LinkedList<EOJ> resultList = new LinkedList<EOJ>(eojList);
        LOGGER.exiting(CLASS_NAME, "getEOJList", resultList);
        return resultList;
    }
}
