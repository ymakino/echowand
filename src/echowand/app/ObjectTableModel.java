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
    public void notifyPropertyMapChanged() {
        if (!isInvalid()) {
            model.fireTableDataChanged();
        }
    }

    @Override
    public void notifyPropertyDataChanged(EPC epc) {
        if (!isInvalid()) {
            model.fireEPCDataUpdated(epc, getCachedObject());
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
    public void refreshCache() {
        stopRefreshCache();
        cachedObject.clearCache();
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

    public synchronized void setCachedObject(CachedRemoteObject cachedObject) {
        if (this.cachedObject == cachedObject) {
            return;
        }
        
        if (this.cachedObject != null) {
            this.cachedObject.removeObserver(observer);
        }
        
        this.cachedObject = cachedObject;

        if (cachedObject == null) {
            stopRefreshCache();
            fireTableDataChanged();
        } else {
            this.cachedObject.addObserver(observer);
            refreshCache();
        }
    }
    
    @Override
    public synchronized void fireEPCDataUpdated(EPC epc, CachedRemoteObject updatedObject) {
        if (cachedObject == null) {
            return;
        }
        
        if (!cachedObject.getEOJ().equals(updatedObject.getEOJ())) {
            return;
        }
        
        int index = cachedObject.getIndexOfEPC(epc);
        if (index >= 0) {
            fireTableCellUpdated(index, ColumnKind.SIZE.getIndex());
            fireTableCellUpdated(index, ColumnKind.DATA.getIndex());
            fireTableCellUpdated(index, ColumnKind.FORMATTED.getIndex());
        }
    }
    
    public synchronized void release() {
        setCachedObject(null);
    }

    @Override
    public synchronized int getRowCount() {
        if (cachedObject == null) {
            return 0;
        }
        
        return cachedObject.size();
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
    public synchronized Object getValueAt(int rowIndex, int columnIndex) {
        if (cachedObject == null || !cachedObject.isPropertyMapsCached()) {
            return null;
        }
        
        EPC epc = cachedObject.getEPC(rowIndex);
        
        ColumnKind kind = ColumnKind.valueOf(columnIndex);
        
        switch (kind) {
            case EPC: return epc;
            case GET: return cachedObject.isGettable(epc);
            case SET: return cachedObject.isSettable(epc);
            case ANNO: return cachedObject.isObservable(epc);
            case SIZE:
                ObjectData data = cachedObject.getData(epc);
                if (data != null) {
                    return data.size();
                } else {
                    return null;
                }
            case DATA: return cachedObject.getData(epc);
            case FORMATTED: return getReadableString(cachedObject, epc);
        }

        return null;
    }
    
    @Override
    public synchronized void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex != ColumnKind.DATA.getIndex()) {
            return;
        }
        
        EPC epc = cachedObject.getEPC(rowIndex);
        
        if (epc.isInvalid()) {
            return;
        }
        
        byte[] newBytes = string2Bytes((String)aValue);
        if (newBytes == null) {
            return;
        }
        
        try {
            cachedObject.setData(epc, new ObjectData(newBytes));
            cachedObject.updateCache(epc);
            fireEPCDataUpdated(epc, cachedObject);
        } catch (EchonetObjectException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public synchronized boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex != ColumnKind.DATA.getIndex()) {
            return false;
        }
        
        EPC epc = cachedObject.getEPC(rowIndex);
        
        if (epc.isInvalid()) {
            return false;
        }
        
        return cachedObject.isSettable(epc);
    }
}
