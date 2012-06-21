package echowand.app;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.object.EchonetObjectException;
import echowand.object.ObjectData;
import echowand.util.Pair;
import java.util.LinkedList;

class MultipleObjectTableModelRefreshThread extends CachedRemoteObjectRefreshThread {

    private AbstractObjectTableModel model;

    public MultipleObjectTableModelRefreshThread(AbstractObjectTableModel model, CachedRemoteObject cahcedObject) {
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



class MultipleObjectTableModelTuple {
    private AbstractObjectTableModel model;
    private CachedRemoteObject cachedObject;
    private ObjectTableModelNotifyObserver observer;
    private MultipleObjectTableModelRefreshThread refreshCacheThread;
    
    public MultipleObjectTableModelTuple(AbstractObjectTableModel model, CachedRemoteObject cachedObject, ObjectTableModelNotifyObserver observer) {
        this.model = model;
        this.cachedObject = cachedObject;
        this.observer = observer;
        
        cachedObject.addObserver(observer);
    }
    
    public CachedRemoteObject getCachedObject() {
        return cachedObject;
    }
    
    public void refreshCache() {
        stopRefreshCache();
        cachedObject.clearCache();
        observer.setCachedObject(cachedObject);
        refreshCacheThread = new MultipleObjectTableModelRefreshThread(model, cachedObject);
        refreshCacheThread.start();
    }
    
    public void stopRefreshCache() {
        if (refreshCacheThread != null) {
            refreshCacheThread.invalidate();
        }
        refreshCacheThread = null;
    }
    
    public void release() {
        cachedObject.removeObserver(observer);
    }
}

/**
 *
 * @author Yoshiki Makino
 */
public class MultipleObjectTableModel extends AbstractObjectTableModel{
    private LinkedList<MultipleObjectTableModelTuple> tupleList = new LinkedList<MultipleObjectTableModelTuple>();
    
    @Override
    public synchronized void refreshCache() {
        for (MultipleObjectTableModelTuple t: tupleList) {
            t.refreshCache();
        }
    }
    
    public synchronized void stopRefreshCache() {
        for (MultipleObjectTableModelTuple t: tupleList) {
            t.stopRefreshCache();
        }
    }
    
    private boolean isSameWithCurrentList(LinkedList<CachedRemoteObject> objects) {
        if (objects.size() != tupleList.size()) {
            return false;
        }
        int size = objects.size();
        for (int i = 0; i < size; i++) {
            CachedRemoteObject newObject = objects.get(i);
            CachedRemoteObject oldObject = tupleList.get(i).getCachedObject();
            if (!newObject.equals(oldObject)) {
                return false;
            }
        }
        
        return true;
    }

    public synchronized void setCachedObjects(LinkedList<CachedRemoteObject> objects) {
        if (isSameWithCurrentList(objects)) {
            return;
        }
        
        for (MultipleObjectTableModelTuple t : tupleList) {
            t.release();
        }
        
        tupleList.clear();
        
        for (CachedRemoteObject object : objects) {
            ObjectTableModelNotifyObserver observer = new ObjectTableModelNotifyObserver(this);
            tupleList.add(new MultipleObjectTableModelTuple(this, object, observer));
        }

        refreshCache();
        
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        int rowCount = 0;
        
        for (int code = 0x80; code <= 0xff; code++) {
            EPC epc = EPC.fromByte((byte) code);
            if (isValidEPC(epc)) {
                rowCount++;
            }
        }
        
        return rowCount;
    }

    @Override
    public synchronized int getColumnCount() {
        return tupleList.size() + 1;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return EPC.class;
        } else {
            return ObjectData.class;
        }
    }
    
    @Override
    public synchronized String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "EPC";
        }
        
        int index = columnIndex - 1;
        return tupleList.get(index).getCachedObject().getEOJ().toString();
    }
    
    private synchronized boolean isValidEPC(EPC epc) {
        for (MultipleObjectTableModelTuple t : tupleList) {
            CachedRemoteObject object = t.getCachedObject();
            if (object.isValidEPC(epc)) {
                return true;
            }
        }
        
        return false;
    }

    private Pair<Integer, EPC> index2epcCache = null;
    
    private EPC index2epc(int index) {
        if (index2epcCache != null && index2epcCache.first == index) {
            return index2epcCache.second;
        }
        
        int indexCount = 0;
        
        for (int code = 0x80; code <= 0xff; code++) {
            EPC epc = EPC.fromByte((byte) code);
            if (isValidEPC(epc)) {
                if (index == indexCount) {
                    index2epcCache = new Pair<Integer, EPC>(index, epc);
                    return epc;
                }
                indexCount++;
            }
        }

        return EPC.Invalid;
    }

    private int epc2index(EPC epc) {
        int indexCount = 0;
        
        for (int code = 0x80; code <= 0xff; code++) {
            EPC cur = EPC.fromByte((byte) code);
            if (isValidEPC(cur)) {
                if (epc == cur) {
                    return indexCount;
                }
                indexCount++;
            }
        }
        return indexCount;
    }
    
    private synchronized CachedRemoteObject index2object(int index) {
        
        if (index < 0 || tupleList.size() <= index) {
            return null;
        }
        
        return tupleList.get(index).getCachedObject();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EPC epc = index2epc(rowIndex);
        
        if (epc.isInvalid()) {
            return null;
        }
        
        if (columnIndex == 0) {
            return epc;
        }
        
        CachedRemoteObject object = index2object(columnIndex - 1);
        if (object == null) {
            return null;
        }
        
        if (!object.isValidEPC(epc)) {
            return "-";
        }
        
        if (!object.isCached(epc)) {
            return null;
        }
        
        return object.getData(epc);
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
        CachedRemoteObject cachedObject = index2object(columnIndex - 1);
        if (cachedObject == null) {
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
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        
        CachedRemoteObject cachedObject = index2object(columnIndex - 1);
        if (cachedObject == null) {
            return false;
        }
        
        EPC epc = index2epc(rowIndex);
        
        if (epc.isInvalid()) {
            return false;
        }
        
        return cachedObject.isSettable(epc);
    }
    
    private synchronized LinkedList<MultipleObjectTableModelTuple> cloneTupleList() {
        return new LinkedList<MultipleObjectTableModelTuple>(tupleList);
    }
    
    @Override
    public void fireEPCDataUpdated(EPC epc, CachedRemoteObject updatedObject) {
        int rowIndex = epc2index(epc);
        EOJ updatedEOJ = updatedObject.getEOJ();
        
        
        LinkedList<MultipleObjectTableModelTuple> currentList = cloneTupleList();
        int size = currentList.size();
        for (int i = 0; i < size; i++) {
            if (updatedEOJ.equals(currentList.get(i).getCachedObject().getEOJ())) {
                int columnIndex = i + 1;
                this.fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }
}
