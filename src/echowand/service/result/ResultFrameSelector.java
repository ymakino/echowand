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
public class ResultFrameSelector implements Selector<ResultFrame> {
    private static final Logger LOGGER = Logger.getLogger(ResultFrameSelector.class.getName());
    private static final String CLASS_NAME = ResultFrameSelector.class.getName();
    
    private ArrayList<Node> nodes;
    private ArrayList<EOJ> eojs;
    private ArrayList<EPC> epcs;
    
    private static <T> List<T> toList(T... objects) {
        
        if (objects.length == 1 && objects[0] == null) {
            return new ArrayList<T>();
        }
        
        ArrayList<T> list = new ArrayList<T>(Arrays.asList(objects));
        
        return list;
    }
    
    public ResultFrameSelector() {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector");
        
        init(null, null, null);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(Node node) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", node);
        
        init(toList(node), null, null);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(EOJ eoj) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", eoj);
        
        init(null, toList(eoj), null);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(EPC epc) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", epc);
        
        init(null, null, toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(Node node, EOJ eoj) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{node, eoj});
        
        init(toList(node), toList(eoj), null);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(EOJ eoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{eoj, epc});
        
        init(null, toList(eoj), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(ClassEOJ ceoj) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", ceoj);
        
        init(null, toList(ceoj.getAllInstanceEOJ()), null);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(Node node, ClassEOJ ceoj) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{node, ceoj});
        
        init(toList(node), toList(ceoj.getAllInstanceEOJ()), null);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(ClassEOJ ceoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{ceoj, epc});
        
        init(null, toList(ceoj.getAllInstanceEOJ()), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(Node node, EOJ eoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{node, eoj, epc});
        
        init(toList(node), toList(eoj), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(List<? extends Node> nodes, EOJ eoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{nodes, eoj, epc});
        
        init(nodes, toList(eoj), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(Node node, EOJ eoj, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{node, eoj, epcs});
        
        init(toList(node), toList(eoj), epcs);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(List<? extends Node> nodes, EOJ eoj, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{nodes, eoj, epcs});
        
        init(nodes, toList(eoj), epcs);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(Node node, ClassEOJ ceoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{node, ceoj, epc});
        
        init(toList(node), toList(ceoj.getAllInstanceEOJ()), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(List<? extends Node> nodes, ClassEOJ ceoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{nodes, ceoj, epc});
        
        init(nodes, toList(ceoj.getAllInstanceEOJ()), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(Node node, ClassEOJ ceoj, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{node, ceoj, epcs});
        
        init(toList(node), toList(ceoj.getAllInstanceEOJ()), epcs);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(List<? extends Node> nodes, ClassEOJ ceoj, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{nodes, ceoj, epcs});
        
        init(nodes, toList(ceoj.getAllInstanceEOJ()), epcs);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    public ResultFrameSelector(List<? extends Node> nodes, List<EOJ> eojs, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "ResultFrameSelector", new Object[]{nodes, eojs, epcs});
        
        init(nodes, eojs, epcs);
        
        LOGGER.exiting(CLASS_NAME, "ResultFrameSelector");
    }
    
    private void init(List<? extends Node> nodes, List<EOJ> eojs, List<EPC> epcs) {
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
    
    private boolean checkNodes(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "checkNodes", resultFrame);
        
        if (nodes.isEmpty()) {
            LOGGER.exiting(CLASS_NAME, "checkNodes", true);
            return true;
        }
        
        if (nodes.contains(resultFrame.getSender())) {
            LOGGER.exiting(CLASS_NAME, "checkNodes", true);
            return true;
        }

        LOGGER.exiting(CLASS_NAME, "checkNodes", false);
        return false;
    }
    
    private boolean checkEOJs(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "checkEOJs", resultFrame);
        
        if (eojs.isEmpty()) {
            LOGGER.exiting(CLASS_NAME, "checkEOJs", true);
            return true;
        }
        
        StandardPayload payload = resultFrame.getCommonFrame().getEDATA(StandardPayload.class);
        
        if (payload == null) {
            LOGGER.exiting(CLASS_NAME, "checkEOJs", false);
            return false;
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
    
    private boolean checkEPCs(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "checkEPCs", resultFrame);
        
        if (epcs.isEmpty()) {
            LOGGER.exiting(CLASS_NAME, "checkEPCs", true);
            return true;
        }
        
        StandardPayload payload = resultFrame.getCommonFrame().getEDATA(StandardPayload.class);
        
        if (payload == null) {
            LOGGER.exiting(CLASS_NAME, "checkEPCs", false);
            return false;
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
    public boolean match(ResultFrame resultFrame) {
        LOGGER.entering(CLASS_NAME, "match", resultFrame);
        
        boolean result = checkNodes(resultFrame) && checkEOJs(resultFrame) && checkEPCs(resultFrame);
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        return "ResultFrameSelector{Nodes: " + nodes + ", EOJs: " + eojs + ", EPCs: " + epcs + "}";
    }
}
