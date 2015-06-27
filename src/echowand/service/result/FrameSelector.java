package echowand.service.result;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.StandardPayload;
import echowand.util.Selector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class FrameSelector implements Selector<Frame> {
    private static final Logger LOGGER = Logger.getLogger(FrameSelector.class.getName());
    private static final String CLASS_NAME = FrameSelector.class.getName();
    
    private ArrayList<Node> nodes;
    private ArrayList<EOJ> eojs;
    private ArrayList<EPC> epcs;
    
    private static <T> List<T> toList(T... objects) {
        ArrayList<T> list = new ArrayList<T>(objects.length);
        
        if (objects.length == 1 && objects[0] == null) {
            return list;
        }
        
        list.addAll(Arrays.asList(objects));
        
        return list;
    }
    
    public FrameSelector() {
        LOGGER.entering(CLASS_NAME, "FrameSelector");
        
        this.nodes = new ArrayList<Node>();
        this.eojs = new ArrayList<EOJ>();
        this.epcs = new ArrayList<EPC>();
        
        LOGGER.exiting(CLASS_NAME, "FrameSelector");
    }
    
    public FrameSelector(Node node, EOJ eoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "FrameSelector", new Object[]{node, eoj, epc});
        
        init(toList(node), toList(eoj), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "FrameSelector");
    }
    
    public FrameSelector(List<Node> nodes, EOJ eoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "FrameSelector", new Object[]{nodes, eoj, epc});
        
        init(nodes, toList(eoj), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "FrameSelector");
    }
    
    public FrameSelector(Node node, EOJ eoj, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "FrameSelector", new Object[]{node, eoj, epcs});
        
        init(toList(node), toList(eoj), epcs);
        
        LOGGER.exiting(CLASS_NAME, "FrameSelector");
    }
    
    public FrameSelector(List<Node> nodes, EOJ eoj, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "FrameSelector", new Object[]{nodes, eoj, epcs});
        
        init(nodes, toList(eoj), epcs);
        
        LOGGER.exiting(CLASS_NAME, "FrameSelector");
    }
    
    public FrameSelector(Node node, ClassEOJ ceoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "FrameSelector", new Object[]{node, ceoj, epc});
        
        init(toList(node), toList(ceoj.getAllInstanceEOJ()), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "FrameSelector");
    }
    
    public FrameSelector(List<Node> nodes, ClassEOJ ceoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "FrameSelector", new Object[]{nodes, ceoj, epc});
        
        init(nodes, toList(ceoj.getAllInstanceEOJ()), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "FrameSelector");
    }
    
    public FrameSelector(Node node, ClassEOJ ceoj, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "FrameSelector", new Object[]{node, ceoj, epcs});
        
        init(toList(node), toList(ceoj.getAllInstanceEOJ()), epcs);
        
        LOGGER.exiting(CLASS_NAME, "FrameSelector");
    }
    
    public FrameSelector(List<Node> nodes, ClassEOJ ceoj, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "FrameSelector", new Object[]{nodes, ceoj, epcs});
        
        init(nodes, toList(ceoj.getAllInstanceEOJ()), epcs);
        
        LOGGER.exiting(CLASS_NAME, "FrameSelector");
    }
    
    public FrameSelector(List<Node> nodes, List<EOJ> eojs, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "FrameSelector", new Object[]{nodes, eojs, epcs});
        
        init(nodes, eojs, epcs);
        
        LOGGER.exiting(CLASS_NAME, "FrameSelector");
    }
    
    private void init(List<Node> nodes, List<EOJ> eojs, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "init", new Object[]{nodes, eojs, epcs});
        
        if (nodes == null) {
            this.nodes = new ArrayList<Node>();
        } else {
            this.nodes = new ArrayList<Node>(nodes);
        }
        
        if (eojs == null) {
            this.eojs = new ArrayList<EOJ>();
        } else {
            this.eojs = new ArrayList<EOJ>(eojs);
        }
        
        if (epcs == null) {
            this.epcs = new ArrayList<EPC>();
        } else {
            this.epcs = new ArrayList<EPC>(epcs);
        }
            
        LOGGER.exiting(CLASS_NAME, "init");
    }
    
    private boolean checkNodes(Frame frame) {
        LOGGER.entering(CLASS_NAME, "checkNodes", frame);
        
        if (nodes.isEmpty()) {
            LOGGER.exiting(CLASS_NAME, "checkNodes", true);
            return true;
        }
        
        if (nodes.contains(frame.getSender())) {
            LOGGER.exiting(CLASS_NAME, "checkNodes", true);
            return true;
        }

        LOGGER.exiting(CLASS_NAME, "checkNodes", false);
        return false;
    }
    
    private boolean checkEOJs(Frame frame) {
        LOGGER.entering(CLASS_NAME, "checkEOJs", frame);
        
        StandardPayload payload = frame.getCommonFrame().getEDATA(StandardPayload.class);
        
        if (payload == null) {
            LOGGER.exiting(CLASS_NAME, "checkEOJs", false);
            return false;
        }
        
        if (eojs.isEmpty()) {
            LOGGER.exiting(CLASS_NAME, "checkEOJs", true);
            return true;
        }
        
        if (eojs.contains(payload.getSEOJ())) {
            LOGGER.exiting(CLASS_NAME, "checkEOJs", true);
            return true;
        }
        
        if (eojs.contains(payload.getSEOJ().getAllInstanceEOJ())) {
            LOGGER.exiting(CLASS_NAME, "checkEOJs", true);
            return true;
        }

        LOGGER.exiting(CLASS_NAME, "checkEOJs", false);
        return false;
    }
    
    private boolean checkEPCs(Frame frame) {
        LOGGER.entering(CLASS_NAME, "checkEPCs", frame);
        
        StandardPayload payload = frame.getCommonFrame().getEDATA(StandardPayload.class);
        
        if (payload == null) {
            LOGGER.exiting(CLASS_NAME, "checkEPCs", false);
            return false;
        }
        
        if (epcs.isEmpty()) {
            LOGGER.exiting(CLASS_NAME, "checkEPCs", true);
            return true;
        }

        int count = payload.getFirstOPC();
        for (int i = 0; i < count; i++) {
            EPC epc = payload.getFirstPropertyAt(i).getEPC();
            if (epcs.contains(epc)) {
                LOGGER.exiting(CLASS_NAME, "checkEPCs", true);
                return true;
            }
        }

        LOGGER.exiting(CLASS_NAME, "checkEPCs", false);
        return false;
    }
    
    @Override
    public boolean match(Frame frame) {
        LOGGER.entering(CLASS_NAME, "match", frame);
        
        boolean result = checkNodes(frame) && checkEOJs(frame) && checkEPCs(frame);
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        return "FrameSelector{Nodes: " + nodes + ", EOJs: " + eojs + ", EPCs: " + epcs + "}";
    }
}
