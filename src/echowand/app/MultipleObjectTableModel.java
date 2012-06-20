package echowand.app;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.object.ObjectData;
import java.util.LinkedList;

class MultipleObjectTableModelRefreshThread extends CachedRemoteObjectRefreshThread {

    private AbstractObjectTableModel model;

    public MultipleObjectTableModelRefreshThread(AbstractObjectTableModel model, CachedRemoteObject cahcedObject) {
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

    private synchronized LinkedList<MultipleObjectTableModelTuple> getTupleList() {
        return new LinkedList<MultipleObjectTableModelTuple>(tupleList);
    }
    
    @Override
    public void refreshCache() {
        for (MultipleObjectTableModelTuple t: getTupleList()) {
            t.refreshCache();
        }
    }
    
    public void stopRefreshCache() {
        for (MultipleObjectTableModelTuple t: getTupleList()) {
            t.stopRefreshCache();
        }
    }
    
    
    public synchronized void setCachedObjects(LinkedList<CachedRemoteObject> objects) {
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
    public int getColumnCount() {
        return getTupleList().size() + 1;
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 0) {
            return EPC.class;
        } else {
            return ObjectData.class;
        }
    }
    
    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "EPC";
        }
        return getTupleList().get(column - 1).getCachedObject().getEOJ().toString();
    }
    
    private boolean isValidEPC(EPC epc) {
        for (MultipleObjectTableModelTuple t : getTupleList()) {
            CachedRemoteObject object = t.getCachedObject();
            if (object.isValidEPC(epc)) {
                return true;
            }
        }
        
        return false;
    }

    private EPC index2epc(int index) {
        int indexCount = 0;
        
        for (int code = 0x80; code <= 0xff; code++) {
            EPC epc = EPC.fromByte((byte) code);
            if (isValidEPC(epc)) {
                if (index == indexCount) {
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

    @Override
    public Object getValueAt(int row, int column) {
        EPC epc = index2epc(row);
        
        if (epc.isInvalid()) {
            return null;
        }
        
        if (column == 0) {
            return epc;
        }
        
        CachedRemoteObject object = getTupleList().get(column - 1).getCachedObject();
        
        if (!object.isValidEPC(epc)) {
            return "-";
        }
        
        if (!object.isCached(epc)) {
            return null;
        }
        
        
        return object.getData(epc);
    }
    
    @Override
    public void fireEPCDataUpdated(EPC epc, CachedRemoteObject updatedObject) {
        int row = epc2index(epc);
        EOJ updatedEOJ = updatedObject.getEOJ();
        
        for (int i = 0; i < getTupleList().size(); i++) {
            if (updatedEOJ.equals(getTupleList().get(i).getCachedObject().getEOJ())) {
                int column = i + 1;
                this.fireTableCellUpdated(row, column);
            }
        }
    }

    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }
}
