package echowand.service.result;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Node;
import echowand.util.Selector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultDataSelector implements Selector<ResultData> {
    private static final Logger LOGGER = Logger.getLogger(ResultDataSelector.class.getName());
    private static final String CLASS_NAME = ResultDataSelector.class.getName();
    
    private List<Node> nodes;
    private List<EOJ> eojs;
    private List<EPC> epcs;
    
    private static <T> List<T> toList(T... objects) {
        ArrayList<T> list = new ArrayList<T>(objects.length);
        
        if (objects.length == 1 && objects[0] == null) {
            return list;
        }
        
        list.addAll(Arrays.asList(objects));
        
        return list;
    }
    
    public ResultDataSelector() {
        LOGGER.entering(CLASS_NAME, "ResultDataSelector");
        
        init(null, null, null);
        
        LOGGER.exiting(CLASS_NAME, "ResultDataSelector");
    }
    
    public ResultDataSelector(List<Node> nodes, List<EOJ> eojs, List<EPC> epcs) {
        LOGGER.entering(CLASS_NAME, "ResultDataSelector", new Object[]{nodes, eojs, epcs});
        
        init(nodes, eojs, epcs);
        
        LOGGER.exiting(CLASS_NAME, "ResultDataSelector");
    }
    
    public ResultDataSelector(Node node, EOJ eoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "ResultDataSelector", new Object[]{node, eoj, epc});
        
        init(toList(node), toList(eoj), toList(epc));
        
        LOGGER.exiting(CLASS_NAME, "ResultDataSelector");
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
    
    private boolean checkNodes(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "checkNodes", resultData);
        
        if (nodes.isEmpty()) {
            LOGGER.exiting(CLASS_NAME, "checkNodes", true);
            return true;
        }
        
        if (nodes.contains(resultData.node)) {
            LOGGER.exiting(CLASS_NAME, "checkNodes", true);
            return true;
        }

        LOGGER.exiting(CLASS_NAME, "checkNodes", false);
        return false;
    }
    
    private boolean checkEOJs(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "checkEOJs", resultData);
        
        if (eojs.isEmpty()) {
            LOGGER.exiting(CLASS_NAME, "checkEOJs", true);
            return true;
        }
        
        if (eojs.contains(resultData.eoj)) {
            LOGGER.exiting(CLASS_NAME, "checkEOJs", true);
            return true;
        }
        
        if (eojs.contains(resultData.eoj.getAllInstanceEOJ())) {
            LOGGER.exiting(CLASS_NAME, "checkEOJs", true);
            return true;
        }

        LOGGER.exiting(CLASS_NAME, "checkEOJs", false);
        return false;
    }
    
    private boolean checkEPCs(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "checkEPCs", resultData);
        
        if (epcs.isEmpty()) {
            LOGGER.exiting(CLASS_NAME, "checkEPCs", true);
            return true;
        }
        
        if (epcs.contains(resultData.epc)) {
            LOGGER.exiting(CLASS_NAME, "checkEPCs", true);
            return true;
        }

        LOGGER.exiting(CLASS_NAME, "checkEPCs", false);
        return false;
    }
    
    @Override
    public boolean match(ResultData resultData) {
        LOGGER.entering(CLASS_NAME, "match", resultData);
        
        boolean result = checkNodes(resultData) && checkEOJs(resultData) && checkEPCs(resultData);
        
        LOGGER.exiting(CLASS_NAME, "match", result);
        return result;
    }
    
    @Override
    public String toString() {
        return "ResultDataSelector{Nodes: " + nodes + ", EOJs: " + eojs + ", EPCs: " + epcs + "}";
    }
}
