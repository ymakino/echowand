package echowand.app;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.PropertyMap;
import echowand.net.Node;
import echowand.net.Subnet;
import echowand.object.*;
import java.util.EnumMap;
import java.util.HashMap;

/**
 *
 * @author Yoshiki Makino
 */
public class CachedRemoteObject implements EchonetObject {

    private RemoteObject remoteObject;
    private EnumMap<EPC, ObjectData> dataCache;

    public CachedRemoteObject(RemoteObject remoteObject) {
        this.remoteObject = remoteObject;
        dataCache = new EnumMap<EPC, ObjectData>(EPC.class);
    }

    public void clearCache() {
        dataCache.clear();
    }
    
    public void clearCache(EPC epc) {
        dataCache.remove(epc);
    }

    public boolean isCached(EPC epc) {
        return (dataCache.get(epc) != null);
    }

    public boolean isPropertyMapsCached() {
        return isCached(EPC.x9F)
                && isCached(EPC.x9E)
                && isCached(EPC.x9D);
    }

    public void setCachedData(EPC epc, ObjectData data) {
        dataCache.put(epc, data);
    }

    public boolean updateCache(EPC epc) throws EchonetObjectException {
        setCachedData(epc, remoteObject.getData(epc));
        return isCached(epc);
    }
    
    public void observeData(EPC epc) throws EchonetObjectException {
        remoteObject.observeData(epc);
    }

    public boolean updatePropertyMapsCache() throws EchonetObjectException {
        return updateCache(EPC.x9F)
                && updateCache(EPC.x9E)
                && updateCache(EPC.x9D);
    }

    private boolean isEPCSetAtPropertyMap(EPC epc, EPC propertyMapEpc) {
        if (!isCached(propertyMapEpc)) {
            return false;
        }

        return new PropertyMap(getData(propertyMapEpc).toBytes()).isSet(epc);
    }

    @Override
    public boolean isGettable(EPC epc) {
        return isEPCSetAtPropertyMap(epc, EPC.x9F);
    }

    @Override
    public boolean isSettable(EPC epc) {
        return isEPCSetAtPropertyMap(epc, EPC.x9E);
    }

    @Override
    public boolean isObservable(EPC epc) {
        return isEPCSetAtPropertyMap(epc, EPC.x9D);
    }

    public boolean isValidEPC(EPC epc) {
        return isGettable(epc)
                || isSettable(epc)
                || isObservable(epc);
    }

    @Override
    public ObjectData getData(EPC epc) {
        return dataCache.get(epc);
    }

    private int sizeCache = -1;
    private HashMap<Integer, EPC> indexEPCCache = new HashMap<Integer, EPC>();
    
    public synchronized int size() {
        
        if (sizeCache != -1) {
            return sizeCache;
        }
        
        if (!isPropertyMapsCached()) {
            return 0;
        }

        int count = 0;

        for (byte code = (byte) 0x80; code <= (byte) 0xff; code++) {
            EPC epc = EPC.fromByte(code);
            if (isValidEPC(epc)) {
                count++;
            }
        }

        sizeCache = count;
        
        return count;
    }
    
    private synchronized boolean updateIndexEPCCache() {
        int count = 0;
        
        if (!isPropertyMapsCached()) {
            return false;
        }
        
        for (byte code = (byte) 0x80; code <= (byte) 0xff; code++) {
            EPC epc = EPC.fromByte(code);
            if (isValidEPC(epc)) {
                indexEPCCache.put(count, epc);
                count++;
            }
        }
        
        return true;
    }

    public synchronized EPC getEPC(int index) {
        
        EPC cachedEPC = indexEPCCache.get(index);
        
        if (cachedEPC != null) {
            return cachedEPC;
        }
        
        if (!updateIndexEPCCache()) {
            return null;
        }
        
        cachedEPC = indexEPCCache.get(index);
        if (cachedEPC == null) {
            indexEPCCache.put(index, EPC.Invalid);
            cachedEPC = EPC.Invalid;
        }

        return cachedEPC;
    }

    public int getIndexOfEPC(EPC epc) {
        int size = size();

        for (int i = 0; i < size; i++) {
            if (epc.equals(getEPC(i))) {
                return i;
            }
        }

        return -1;
    }

    public Subnet getSubnet() {
        return remoteObject.getSubnet();
    }
    
    public Node getNode() {
        return remoteObject.getNode();
    }
    
    @Override
    public EOJ getEOJ() {
        return remoteObject.getEOJ();
    }

    @Override
    public boolean contains(EPC epc) throws EchonetObjectException {
        return remoteObject.contains(epc);
    }

    @Override
    public boolean setData(EPC epc, ObjectData data) throws EchonetObjectException {
        boolean ret = remoteObject.setData(epc, data);
        clearCache(epc);
        return ret;
    }

    public void addObserver(RemoteObjectObserver observer) {
        remoteObject.addObserver(observer);
    }
    
    public void removeObserver(RemoteObjectObserver observer) {
        remoteObject.removeObserver(observer);
    }
    
    public int countObservers() {
        return remoteObject.countObservers();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (o instanceof CachedRemoteObject) {
            CachedRemoteObject other = (CachedRemoteObject)o;
            return remoteObject.equals(other.remoteObject);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.remoteObject != null ? this.remoteObject.hashCode() : 0);
        return hash;
    }
}
