package echowand.object;

import echowand.common.ClassEOJ;
import echowand.common.Data;
import echowand.common.EPC;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * ノードプロファイルの代理となり、0xD3-0xD7のGet命令の処理を実行。
 * @author Yoshiki Makino
 */
public class NodeProfileObjectDelegate implements LocalObjectDelegate {
    private LocalObjectManager manager;
    
    /**
     * NodeProfileObjectDelegateを生成する。
     * インスタンスリストデータを生成するためにLocalObjectManagerが必要となる。
     * @param manager ローカルオブジェクト管理を行うオブジェクト
     */
    public NodeProfileObjectDelegate(LocalObjectManager manager) {
        this.manager = manager;
    }

    private ObjectData getInstanceCountData() {
        int len = manager.getDeviceObjects().size();
        byte b0 = (byte)((len & 0x00ff0000) >> 16);
        byte b1 = (byte)((len & 0x0000ff00) >> 8);
        byte b2 = (byte)(len & 0x000000ff);
        return new ObjectData(b0, b1, b2);
    }
    private ObjectData getClassCountData() {
        HashSet<ClassEOJ> uniqueMap = new HashSet<ClassEOJ>();
        List<LocalObject> deviceObjects = manager.getDeviceObjects();
        for (LocalObject object : deviceObjects) {
            uniqueMap.add(object.getEOJ().getClassEOJ());
        }
        int len = uniqueMap.size();
        byte b0 = (byte)((len & 0x0000ff00) >> 8);
        byte b1 = (byte)(len & 0x000000ff);
        return new ObjectData(b0, b1);
    }
    private Data getClassOrInstanceBytes(int index, boolean useClass) {
        int max = useClass?(8):(84);
        int lenUnit = useClass?(2):(3);
        List<LocalObject> deviceObjects = manager.getDeviceObjects();
        int size = deviceObjects.size();
        int offset = max * index;
        if (offset >= size) {
            return new Data((byte)0x00);
        }
        int len = Math.min(max, size - offset);
        byte[] bytes = new byte[1 + len * lenUnit];
        bytes[0] = (byte)Math.min(size, 0xff);
        for (int i=0; i<len; i++) {
            LocalObject object = deviceObjects.get(i+offset);
            int nextOffset = 1 + lenUnit * i;
            if (useClass) {
                byte[] classBytes = object.getEOJ().getClassEOJ().toBytes();
                System.arraycopy(classBytes, 0, bytes, nextOffset, classBytes.length);
            } else {
                byte[] eojBytes = object.getEOJ().toBytes();
                System.arraycopy(eojBytes, 0, bytes, nextOffset, eojBytes.length);
            }
        }
        return new Data(bytes);
    }
    private Data getInstanceBytes(int index) {
        return getClassOrInstanceBytes(index, false);
    }
    private Data getClassBytes(int index) {
        return getClassOrInstanceBytes(index, true);
    }
    private ObjectData getInstanceListS() {
        LinkedList<Data> list = new LinkedList<Data>();
        Data bytes = getInstanceBytes(0);
        int i = 1;
        do {
            list.add(bytes);
            bytes = getInstanceBytes(i++);
        } while (bytes.size() != 1);
        
        return new ObjectData(list);
    }
    private ObjectData getClassListS() {
        LinkedList<Data> list = new LinkedList<Data>();
        Data bytes = getClassBytes(0);
        int i = 1;
        do {
            list.add(bytes);
            bytes = getClassBytes(i++);
        } while (bytes.size() != 1);
        
        return new ObjectData(list);
    }
    
    /**
     * 0xD3から0xD7までのプロパティデータを生成する。
     * @param object プロパティのデータを要求されているオブジェクト
     * @param epc 要求されているプロパティのEPC
     * @return 指定されたEPCのプロパティのデータ
     */
    @Override
    public ObjectData getData(LocalObject object, EPC epc) {
        switch (epc) {
            case xD3: return getInstanceCountData();
            case xD4: return getClassCountData();
            case xD5: return getInstanceListS();
            case xD6: return getInstanceListS();
            case xD7: return getClassListS();
            default: return null;
        }
    }
    
    /**
     * プロパティデータのSet処理の時に呼ばれる。
     * 特に処理を行わずにfalseを返す。
     * @param object プロパティのデータの設定を要求されているオブジェクト
     * @param epc 要求されているプロパティのEPC
     * @param data プロパティの更新データ
     * @return 常にfalse
     */
    @Override
    public boolean setData(LocalObject object, EPC epc, ObjectData data) {
        return false;
    }
    
    /**
     * 指定されたLocalObjectのプロパティデータが更新された時に呼び出される。
     * 特に処理は行わない。
     * @param object プロパティデータが更新されたオブジェクト
     * @param epc 更新されたプロパティのEPC
     * @param data 更新されたデータ
     */
    @Override
    public void notifyDataChanged(LocalObject object, EPC epc, ObjectData data) {}
}
