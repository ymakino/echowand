package echowand.app;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Node;

/**
 *
 * @author Yoshiki Makino
 */
public class ReadableConverterMapKey {
    private Node node;
    private ClassEOJ ceoj;
    private EOJ eoj;
    private EPC epc;
    
    public ReadableConverterMapKey(Node node, ClassEOJ ceoj, EOJ eoj, EPC epc) {
        this.node = node;
        this.ceoj = ceoj;
        this.eoj = eoj;
        this.epc = epc;
    }
    
    private static boolean includes(Object base, Object object) {
        return (base == null) || base.equals(object);
    }
    
    public boolean includes(ReadableConverterMapKey key) {
        return includes(node, key.node)
                && includes(ceoj, key.ceoj)
                && includes(eoj, key.eoj)
                && includes(epc, key.epc);
    }
    
    private static boolean isExclusivelyNull(Object object1, Object object2) {
        int b1 = (object1 == null)?0:1;
        int b2 = (object2 == null)?0:1;
        
        return (b1 ^ b2) == 1;
    }
    
    public boolean isBetterThan(ReadableConverterMapKey key) {
        if (key == null) {
            return true;
        }
        
        if (isExclusivelyNull(epc, key.epc)) {
            return key.epc == null;
        }
        
        if (isExclusivelyNull(eoj, key.eoj)) {
            return key.eoj == null;
        }
        
        if (isExclusivelyNull(ceoj, key.ceoj)) {
            return key.ceoj == null;
        }
        
        if (isExclusivelyNull(node, key.node)) {
            return key.node == null;
        }
        
        return true;
    }
    
    private static boolean equalsObject(Object object1, Object object2) {
        if (object1 == null || object2 == null) {
            return object1 == object2;
        }
        
        return object1.equals(object2);
    }
    
    private boolean equalsNode(ReadableConverterMapKey peer) {
        return equalsObject(node, peer.node);
    }
    
    private boolean equalsClassEOJ(ReadableConverterMapKey peer) {
        return equalsObject(ceoj, peer.ceoj);
    }
    
    private boolean equalsEOJ(ReadableConverterMapKey peer) {
        return equalsObject(eoj, peer.eoj);
    }
    
    private boolean equalsEPC(ReadableConverterMapKey peer) {
        return equalsObject(epc, peer.epc);
    }
    
    @Override
    public boolean equals(Object object) {
        if (! (object instanceof ReadableConverterMapKey)) {
            return false;
        }
        ReadableConverterMapKey key = (ReadableConverterMapKey)object;
        return equalsNode(key) && equalsClassEOJ(key) && equalsEOJ(key) && equalsEPC(key);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.ceoj != null ? this.ceoj.hashCode() : 0);
        hash = 53 * hash + (this.eoj != null ? this.eoj.hashCode() : 0);
        hash = 53 * hash + (this.epc != null ? this.epc.hashCode() : 0);
        return hash;
    }
}

