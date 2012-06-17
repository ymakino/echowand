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

    @Override
    public void notifyData(RemoteObject object, EPC epc, ObjectData data) {
        if (cachedObject != null) {
            cachedObject.setCachedData(epc, data);
            model.fireEPCDataUpdated(epc);
        }
    }
}