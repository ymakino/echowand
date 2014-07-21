package echowand.service.result;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ResultDataMatcherRule implements ResultDataMatcher {
    private static final Logger LOGGER = Logger.getLogger(ResultDataMatcherRule.class.getName());
    private static final String CLASS_NAME = ResultDataMatcherRule.class.getName();
    
    private List<Node> nodes;
    private List<EOJ> eojs;
    private List<EPC> epcs;
    
    public ResultDataMatcherRule() {
        LOGGER.entering(CLASS_NAME, "ResultDataMatcherRule");
        
        init(null, null, null);
        
        LOGGER.exiting(CLASS_NAME, "ResultDataMatcherRule");
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
        return "ResultDataMatcherRule{Nodes: " + nodes + ", EOJs: " + eojs + ", EPCs: " + epcs + "}";
    }
}
