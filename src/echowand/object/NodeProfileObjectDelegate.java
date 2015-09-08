package echowand.object;

import echowand.common.ClassEOJ;
import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

abstract class ListDataGenerator<DataType> {

    private LinkedList<DataType> dataList = new LinkedList<DataType>();

    protected abstract DataType extractData(LocalObject object);

    protected abstract int getMaxCount();

    protected abstract byte[] dataToBytes(DataType data);

    protected abstract int getSizeOfData();

    public boolean add(LocalObject object) {
        DataType data = extractData(object);
        if (dataList.contains(data)) {
            return false;
        }

        return dataList.add(data);
    }

    public boolean exists(LocalObject object) {
        return dataList.contains(extractData(object));
    }

    public int size() {
        return dataList.size();
    }

    public byte[] toBytes(int index) {
        int maxCount = getMaxCount();
        int firstIndex = maxCount * index;
        int lastIndex = Math.min(firstIndex + maxCount, size());
        if (lastIndex <= firstIndex) {
            if (index == 0) {
                return new byte[]{(byte) 0x00};
            } else {
                return new byte[]{};
            }
        }

        int dataBytesLength = getSizeOfData() * (lastIndex - firstIndex);
        byte[] bytes = new byte[1 + dataBytesLength];
        int offset = 0;

        bytes[offset++] = (byte) Math.min(dataList.size(), 0xff);

        for (int i = firstIndex; i < lastIndex; i++) {
            byte[] dataBytes = dataToBytes(dataList.get(i));
            System.arraycopy(dataBytes, 0, bytes, offset, getSizeOfData());
            offset += getSizeOfData();
        }

        return bytes;
    }
}

class InstanceListDataGenerator extends ListDataGenerator<EOJ> {

    @Override
    protected EOJ extractData(LocalObject object) {
        return object.getEOJ();
    }

    @Override
    protected int getMaxCount() {
        return 84;
    }

    @Override
    protected byte[] dataToBytes(EOJ data) {
        return data.toBytes();
    }

    @Override
    protected int getSizeOfData() {
        return 3;
    }
}

class ClassListDataGenerator extends ListDataGenerator<ClassEOJ> {

    @Override
    protected ClassEOJ extractData(LocalObject object) {
        return object.getEOJ().getClassEOJ();
    }

    @Override
    protected int getMaxCount() {
        return 8;
    }

    @Override
    protected byte[] dataToBytes(ClassEOJ data) {
        return data.toBytes();
    }

    @Override
    protected int getSizeOfData() {
        return 2;
    }
}

/**
 * ノードプロファイルの代理となり、0xD3-0xD7のGet命令の処理を実行。
 * @author Yoshiki Makino
 */
public class NodeProfileObjectDelegate extends LocalObjectDefaultDelegate {
    private static final Logger logger = Logger.getLogger(NodeProfileObjectDelegate.class.getName());
    private static final String className = NodeProfileObjectDelegate.class.getName();
    
    private boolean countOnlyDeviceClass = true;
    private LocalObjectManager manager;
    
    /**
     * NodeProfileObjectDelegateを生成する。
     * インスタンスリストデータを生成するためにLocalObjectManagerが必要となる。
     * @param manager ローカルオブジェクト管理を行うオブジェクト
     */
    public NodeProfileObjectDelegate(LocalObjectManager manager) {
        logger.entering(className, "NodeProfileObjectDelegate", manager);
        
        this.manager = manager;
        
        logger.exiting(className, "NodeProfileObjectDelegate");
    }

    private ObjectData getInstanceCountData() {
        logger.entering(className, "getInstanceCountData");
        
        int len = manager.getDeviceObjects().size();
        byte b0 = (byte)((len & 0x00ff0000) >> 16);
        byte b1 = (byte)((len & 0x0000ff00) >> 8);
        byte b2 = (byte)(len & 0x000000ff);
        
        ObjectData objectData = new ObjectData(b0, b1, b2);
        
        logger.exiting(className, "getInstanceCountData", objectData);
        return objectData;
    }
    
    private List<LocalObject> getObjectsForClassCountData() {
        logger.entering(className, "getObjectsForClassCountData");
        
        List<LocalObject> resultList;
        if (countOnlyDeviceClass) {
            resultList = manager.getDeviceObjects();
        } else {
            resultList = manager.getAllObjects();
        }
        
        logger.exiting(className, "getObjectsForClassCountData", resultList);
        return resultList;
    }
    
    private ObjectData getClassCountData() {
        logger.entering(className, "getClassCountData");
        
        HashSet<ClassEOJ> classSet = new HashSet<ClassEOJ>();
        List<LocalObject> objects = getObjectsForClassCountData();
        for (LocalObject object : objects) {
            classSet.add(object.getEOJ().getClassEOJ());
        }
        int len = classSet.size();
        byte b0 = (byte)((len & 0x0000ff00) >> 8);
        byte b1 = (byte)(len & 0x000000ff);
        ObjectData objectData = new ObjectData(b0, b1);
        
        logger.exiting(className, "getClassCountData", objectData);
        return objectData;
    }

    private Data getListData(int index, ListDataGenerator generator) {
        logger.entering(className, "getListData", new Object[]{index, generator});
        
        for (LocalObject object : manager.getDeviceObjects()) {
            generator.add(object);
        }
        
        Data data = new Data(generator.toBytes(index));
        
        logger.exiting(className, "getListData", data);
        return data;
    }
    
    private ObjectData getListS(ListDataGenerator generator) {
        logger.entering(className, "getListS", generator);
        
        LinkedList<Data> dataList = new LinkedList<Data>();
        int index = 0;
        
        for (;;) {
            Data data = getListData(index++, generator);
            if (data.isEmpty()) {
                break;
            }
            
            dataList.add(data);
        }
        
        ObjectData objectData = new ObjectData(dataList);
        
        logger.exiting(className, "getListS", objectData);
        return objectData;
    }
    
    private ObjectData getInstanceListS() {
        logger.entering(className, "getInstanceListS");
        
        ObjectData objectData = getListS(new InstanceListDataGenerator());
        
        logger.exiting(className, "getInstanceListS", objectData);
        return objectData;
    }

    private ObjectData getClassListS() {
        logger.entering(className, "getClassListS");

        ObjectData objectData = getListS(new ClassListDataGenerator());

        logger.exiting(className, "getClassListS", objectData);
        return objectData;
    }

    /**
     * デバイスオブジェクトのみを自ノードクラス数(0xD4)に含めるかどうかを返す。
     * @return デバイスオブジェクトのみを含む場合にはtrue、そうでなければfalse
     */
    public boolean getCountOnlyDeviceClass() {
        return countOnlyDeviceClass;
    }
    
    /**
     * デバイスオブジェクトのみを自ノードクラス数(0xD4)に含めるかどうかを設定する。
     * @param onlyDeviceClass デバイスオブジェクトのみを含めるかどうか指定
     */
    public void setCountOnlyDeviceClass(boolean onlyDeviceClass) {
        countOnlyDeviceClass = onlyDeviceClass;
    }
    
    /**
     * 0xD3から0xD7までのプロパティデータを生成する。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータが要求されているオブジェクト
     * @param epc 要求プロパティデータのEPC
     */
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
        logger.entering(className, "getData", new Object[]{result, object, epc});

        switch (epc) {
            case xD3:
                result.setGetData(getInstanceCountData());
                break;
            case xD4:
                result.setGetData(getClassCountData());
                break;
            case xD5:
                result.setGetData(getInstanceListS());
                break;
            case xD6:
                result.setGetData(getInstanceListS());
                break;
            case xD7:
                result.setGetData(getClassListS());
                break;
        }
        
        logger.exiting(className, "getData");
    }
}
