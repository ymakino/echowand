package echowand.app;

import echowand.common.EPC;
import echowand.object.ObjectData;
import echowand.object.RemoteObject;
import echowand.object.RemoteObjectObserver;

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectTableModelNotifyObserver implements RemoteObjectObserver {
    private AbstractObjectTableModel model;
    private CachedRemoteObject cachedObject;
    
    public ObjectTableModelNotifyObserver(AbstractObjectTableModel model) {
        this.model = model;
    }
    
    public void setCachedObject(CachedRemoteObject cachedObject) {
        this.cachedObject = cachedObject;
    }

    private boolean equalsObject(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return o1 == null && o2 == null;
        }
        
        return o1.equals(o2);
    }
    
    @Override
    public void notifyData(RemoteObject object, EPC epc, ObjectData data) {
        if (cachedObject != null) {
            ObjectData lastData = cachedObject.getData(epc);
            if (equalsObject(lastData, data)) {
                return;
            }
            
            cachedObject.setCachedData(epc, data);
            model.fireEPCDataUpdated(epc, cachedObject);
        }
    }
}