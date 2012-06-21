package echowand.app;

import echowand.common.EPC;
import echowand.object.EchonetObjectException;

/**
 *
 * @author Yoshiki Makino
 */
public class CachedRemoteObjectRefreshThread extends Thread {

    private CachedRemoteObject cachedObject;
    private boolean done = false;
    private boolean valid = true;

    public CachedRemoteObjectRefreshThread(CachedRemoteObject cahcedObject) {
        this.cachedObject = cahcedObject;
    }
    
    public CachedRemoteObject getCachedObject() {
        return cachedObject;
    }

    public boolean isDone() {
        return done;
    }

    public synchronized void invalidate() {
        this.valid = false;
    }
    
    public synchronized boolean isInvalid() {
        return !valid;
    }

    public synchronized void notifyPropertyDataChanged() {
    }

    private boolean updateCacheOfEPC(EPC epc) {
        if (cachedObject.isCached(epc)) {
            return true;
        }
        
        try {
            if (cachedObject.isGettable(epc)) {
                return cachedObject.updateCache(epc);
            } else if (cachedObject.isObservable(epc)) {
                cachedObject.observeData(epc);
                return true;
            }
        } catch (EchonetObjectException e) {
            e.printStackTrace();
            return false;
        }
        
        return false;
    }

    private boolean updateAllCache() {
        boolean success = true;

        try {
            cachedObject.updatePropertyMapsCache();
            
            int size = cachedObject.size();
            for (int i = 0; valid && i < size; i++) {
                success &= updateCacheOfEPC(cachedObject.getEPC(i));
            }
            
            if (valid) {
                notifyPropertyDataChanged();
            }
            
        } catch (EchonetObjectException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            boolean success = updateAllCache();
            if (success) {
                break;
            }
        }

        done = true;
    }
}
