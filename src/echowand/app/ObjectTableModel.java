package echowand.app;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Node;
import echowand.object.EchonetObjectException;
import echowand.object.ObjectData;


enum ColumnKind {

    EPC(0, EPC.class, "EPC"),
    GET(1, Boolean.class, "Get"),
    SET(2, Boolean.class, "Set"),
    ANNO(3, Boolean.class, "Anno"),
    SIZE(4, Integer.class, "Size"),
    DATA(5, ObjectData.class, "Data"),
    FORMATTED(6, String.class, "Formatted");
    
    private int index;
    private Class rendererClass;
    private String name;

    ColumnKind(int index, Class rendererClass, String name) {
        this.index = index;
        this.rendererClass = rendererClass;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public Class getDataClass() {
        return rendererClass;
    }

    public String getName() {
        return name;
    }

    public static int count() {
        return values().length;
    }

    public static ColumnKind valueOf(int index) {
        for (ColumnKind c : values()) {
            if (c.index == index) {
                return c;
            }
        }
        return null;
    }
}

class ObjectTableModelRefreshThread extends CachedRemoteObjectRefreshThread {

    private AbstractObjectTableModel model;

    public ObjectTableModelRefreshThread(AbstractObjectTableModel model, CachedRemoteObject cahcedObject) {
        super(cahcedObject);
        this.model = model;
    }

    @Override
    public void notifyPropertyDataChanged() {
        if (!isInvalid()) {
            model.fireTableDataChanged();
        }
    }
}

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectTableModel extends AbstractObjectTableModel {

    private ObjectTableModelNotifyObserver observer = new ObjectTableModelNotifyObserver(this);
    private CachedRemoteObject cachedObject;
    private ObjectTableModelRefreshThread refreshCacheThread;
    private ReadableConverterMap converterMap = new ReadableConverterMap();

    @Override
    public synchronized void refreshCache() {
        stopRefreshCache();
        if (cachedObject != null) {
            cachedObject.clearCache();
        }
        observer.setCachedObject(cachedObject);
        refreshCacheThread = new ObjectTableModelRefreshThread(this, cachedObject);
        refreshCacheThread.start();
    }
    
    public synchronized boolean isRefreshingCache() {
        if (refreshCacheThread == null) {
            return false;
        }
        
        return refreshCacheThread.isAlive();
    }

    public synchronized void stopRefreshCache() {
        if (refreshCacheThread != null) {
            refreshCacheThread.invalidate();
        }
        refreshCacheThread = null;
    }

    public CachedRemoteObject getCachedObject() {
        return cachedObject;
    }
    
    private synchronized boolean setCachedObjectWithoutRefresh(CachedRemoteObject cachedObject) {
        if (this.cachedObject == cachedObject) {
            return false;
        }

        if (this.cachedObject != null && this.cachedObject.equals(cachedObject)) {
            return false;
        }
        
        if (this.cachedObject != null) {
            this.cachedObject.removeObserver(observer);
        }
        
        this.cachedObject = cachedObject;
        
        if (this.cachedObject != null) {
            this.cachedObject.addObserver(observer);
        }
        
        return true;
    }

    public void setCachedObject(CachedRemoteObject cachedObject) {
        if (!setCachedObjectWithoutRefresh(cachedObject)) {
            return;
        }

        if (cachedObject != null) {
            refreshCache();
        } else {
            stopRefreshCache();
            fireTableDataChanged();
        }
    }
    
    private synchronized int getIndexOfEPC(EPC epc, CachedRemoteObject updatedObject) {
        if (cachedObject == null) {
            return -1;
        }
        
        if (!cachedObject.getEOJ().equals(updatedObject.getEOJ())) {
            return -1;
        }
        
        return cachedObject.getIndexOfEPC(epc);
    }
    
    @Override
    public void fireEPCDataUpdated(EPC epc, CachedRemoteObject updatedObject) {

        int index = getIndexOfEPC(epc, updatedObject);
        if (index >= 0) {
            fireTableCellUpdated(index, ColumnKind.SIZE.getIndex());
            fireTableCellUpdated(index, ColumnKind.DATA.getIndex());
            fireTableCellUpdated(index, ColumnKind.FORMATTED.getIndex());
        }
    }
    
    public void release() {
        setCachedObject(null);
    }

    @Override
    public int getRowCount() {
        CachedRemoteObject currentCachedObject = getCachedObject();
        
        if (currentCachedObject == null) {
            return 0;
        }
        
        return currentCachedObject.size();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        ColumnKind kind = ColumnKind.valueOf(columnIndex);
        if (kind == null) {
            return null;
        }

        return kind.getDataClass();
    }

    @Override
    public String getColumnName(int columnIndex) {
        ColumnKind kind = ColumnKind.valueOf(columnIndex);
        if (kind == null) {
            return "Unknown";
        }

        return kind.getName();
    }
    
    @Override
    public int getColumnCount() {
        return ColumnKind.count();
    }
    
    private String getReadableString(CachedRemoteObject object, EPC epc) {
        
        ObjectData data = object.getData(epc);
        if (data == null) {
            return "";
        }
        
        Node node = object.getNode();
        EOJ eoj = object.getEOJ();
        return converterMap.get(node, eoj, epc).dataToString(data);
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CachedRemoteObject currentCachedObject = getCachedObject();
        
        if (currentCachedObject == null || !currentCachedObject.isPropertyMapsCached()) {
            return null;
        }
        
        EPC epc = currentCachedObject.getEPC(rowIndex);
        
        ColumnKind kind = ColumnKind.valueOf(columnIndex);
        
        switch (kind) {
            case EPC: return epc;
            case GET: return currentCachedObject.isGettable(epc);
            case SET: return currentCachedObject.isSettable(epc);
            case ANNO: return currentCachedObject.isObservable(epc);
            case SIZE:
                ObjectData data = currentCachedObject.getData(epc);
                if (data != null) {
                    return data.size();
                } else {
                    return null;
                }
            case DATA: return currentCachedObject.getData(epc);
            case FORMATTED: return getReadableString(currentCachedObject, epc);
        }

        return null;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        CachedRemoteObject currentCachedObject = getCachedObject();
        
        if (columnIndex != ColumnKind.DATA.getIndex()) {
            return;
        }
        
        EPC epc = currentCachedObject.getEPC(rowIndex);
        
        if (epc.isInvalid()) {
            return;
        }
        
        byte[] newBytes = string2Bytes((String)aValue);
        if (newBytes == null) {
            return;
        }
        
        try {
            currentCachedObject.setData(epc, new ObjectData(newBytes));
            currentCachedObject.updateCache(epc);
            fireEPCDataUpdated(epc, currentCachedObject);
        } catch (EchonetObjectException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        CachedRemoteObject currentCachedObject = getCachedObject();
        
        if (columnIndex != ColumnKind.DATA.getIndex()) {
            return false;
        }
        
        EPC epc = currentCachedObject.getEPC(rowIndex);
        
        if (epc.isInvalid()) {
            return false;
        }
        
        return currentCachedObject.isSettable(epc);
    }
}
