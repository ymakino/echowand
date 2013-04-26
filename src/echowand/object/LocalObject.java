package echowand.object;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.ObjectInfo;
import echowand.info.PropertyInfo;
import echowand.util.Constraint;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * ローカルに存在するECHONETオブジェクト
 * @author Yoshiki Makino
 */
public class LocalObject implements EchonetObject {
    private static final Logger logger = Logger.getLogger(LocalObject.class.getName());
    private static final String className = LocalObject.class.getName();
    
    private EOJ eoj;
    private ObjectInfo objectInfo;
    private EnumMap<EPC, ObjectData> propertyData;
    private LinkedList<LocalObjectDelegate> delegates;
    
    /**
     * 指定されたオブジェクト情報を用いてLocaObjectを生成
     * @param objectInfo 作成するLocalObjectのオブジェクト情報
     */
    public LocalObject(ObjectInfo objectInfo) {
        logger.entering(className, "LocalObject", objectInfo);
        
        this.objectInfo = objectInfo;
        this.eoj = objectInfo.getClassEOJ().getEOJWithInstanceCode((byte)0x01);
        propertyData = new EnumMap<EPC, ObjectData>(EPC.class);
        delegates = new LinkedList<LocalObjectDelegate>();
        
        int len = objectInfo.size();
        for (int i=0; i<len; i++) {
            PropertyInfo info = objectInfo.getAtIndex(i);
            propertyData.put(info.epc, new ObjectData(info.initialData));
        }
        
        logger.exiting(className, "LocalObject");
    }
    
    private synchronized LinkedList<LocalObjectDelegate> cloneDelegates() {
        return new LinkedList<LocalObjectDelegate>(delegates);
    }
    
    /**
     * EOJのインスタンスコードを新たに設定する。
     * @param instanceCode 設定するインスタンスコード
     */
    public void setInstanceCode(byte instanceCode) {
        logger.entering(className, "setInstanceCode", instanceCode);
        
        this.eoj = eoj.getClassEOJ().getEOJWithInstanceCode(instanceCode);
        
        logger.exiting(className, "setInstanceCode");
    }
    
    /**
     * 指定されたEPCのプロパティのためにLocalObjectが内部で管理しているデータの内容を設定する。
     * @param epc 設定するデータのEPC
     * @param data 設定するデータの内容
     * @return 設定に成功したらtrue、そうでなければfalse
     */
    public synchronized boolean setInternalData(EPC epc, ObjectData data) {
        logger.entering(className, "setInternalData", new Object[]{epc, data});
        
        if (!contains(epc)) {
            logger.exiting(className, "setInternalData", false);
            return false;
        }

        propertyData.put(epc, data);
        
        logger.exiting(className, "setInternalData", true);
        return true;
    }

    /**
     * 指定されたEPCのプロパティの内容をSetの許可がなくても強制的に変更する。
     * Delegateが最初に処理を行い、Delegateの処理が成功しなかった場合にはLocalObject内部のデータを変更する。
     *
     * @param epc 設定するデータのEPC
     * @param data 設定するデータの内容
     * @return 設定に成功したらtrue、そうでなければfalse
     */
    public boolean forceSetData(EPC epc, ObjectData data) {
        logger.entering(className, "forceSetData", new Object[]{epc, data});
        
        ObjectData oldData = this.getData(epc);

        boolean success = setDataDelegate(epc, data, oldData);

        if (!success) {
            success = setInternalData(epc, data);
            if (success) {
                notifyDataChanged(epc, data, oldData);
            }
        }

        logger.exiting(className, "forceSetData", success);
        return success;
    }

    /**
     * 指定されたEPCのプロパティの内容を変更する。 Setの許可がないプロパティへの操作や、データの制約に従わない操作は失敗する。
     * Delegateが最初に処理を行い、Delegateの処理が成功しなかった場合にはLocalObject内部のデータを変更する。
     *
     * @param epc 設定するデータのEPC
     * @param data 設定するデータの内容
     * @return 設定に成功したらtrue、そうでなければfalse
     */
    @Override
    public boolean setData(EPC epc, ObjectData data) {
        logger.entering(className, "setData", new Object[]{epc, data});
        
        if (!this.isSettable(epc)) {
            logger.exiting(className, "setData", false);
            return false;
        }

        PropertyInfo propertyInfo = objectInfo.get(epc);
        Constraint constraint = propertyInfo.constraint;
        if (!constraint.isValid(data.toBytes())) {
            logger.exiting(className, "setData", false);
            return false;
        }
        
        boolean ret = forceSetData(epc, data);
        logger.exiting(className, "setData", ret);
        return ret;
    }

    /**
     * 指定されたEPCのプロパティのためにLocalObjectが内部で管理しているデータの内容を返す。
     *
     * @param epc データのEPC
     * @return プロパティのデータ、存在しない場合にはnull
     */
    public synchronized ObjectData getInternalData(EPC epc) {
        logger.entering(className, "getInternalData", epc);
        
        ObjectData data = propertyData.get(epc);
        
        logger.exiting(className, "getInternalData", data);
        
        return data;
    }

    /**
     * 指定されたEPCのプロパティの内容をGetの許可がなくても強制的に返す。
     * Delegateが最初に処理を行い、Delegateがデータを返さない場合にはLocalObject内部のデータを返す。
     *
     * @param epc データのEPC
     * @return プロパティのデータ、存在しない場合にはnull
     */
    public ObjectData forceGetData(EPC epc) {
        logger.entering(className, "forceGetData", epc);
        
        ObjectData data = getDataDelegate(epc);

        if (data == null) {
            data = getInternalData(epc);
        }

        logger.exiting(className, "forceGetData", data);
        return data;
    }

    /**
     * 指定されたEPCのプロパティの内容を返す。 Getの許可がない場合にはnullを返す。
     * Delegateが最初に処理を行い、Delegateがデータを返さない場合にはLocalObject内部のデータを返す。
     *
     * @param epc データのEPC
     * @return プロパティのデータ、存在しない場合にはnull
     */
    @Override
    public ObjectData getData(EPC epc) {
        logger.entering(className, "getData", epc);
        
        if (!this.isGettable(epc)) {
            logger.exiting(className, "getData", null);
            return null;
        }

        ObjectData data = forceGetData(epc);
        
        logger.exiting(className, "getData", data);
        return data;
    }

    /**
     * このオブジェクトのEOJを返す。
     * @return このオブジェクトのEOJ
     */
    @Override
    public EOJ getEOJ() {
        return eoj;
    }
    
    /**
     * 指定されたEPCのプロパティが存在するかを返す。
     * @param epc EPCの指定
     * @return 存在していればtrue、そうでなければfalse
     */
    @Override
    public synchronized boolean contains(EPC epc) {
        return propertyData.containsKey(epc);
    }
    
    /**
     * 指定されたEPCがGet可能であるかを返す。
     * @param epc EPCの指定
     * @return Get可能であればtrue、そうでなければfalse
     */
    @Override
    public boolean isGettable(EPC epc) {
        return objectInfo.get(epc).gettable;
    }
    
    /**
     * 指定されたEPCがSet可能であるかを返す。
     * @param epc EPCの指定
     * @return Set可能であればtrue、そうでなければfalse
     */
    @Override
    public boolean isSettable(EPC epc) {
        return objectInfo.get(epc).settable;
    }
    
    /**
     * 指定されたEPCが通知を行うかを返す。
     * @param epc EPCの指定
     * @return 通知を行うのであればtrue、そうでなければfalse
     */
    @Override
    public boolean isObservable(EPC epc) {
        return objectInfo.get(epc).observable;
    }
    
    /**
     * 指定されたEPCのプロパティデータが変化したことを通知する。
     * @param epc 変化したプロパティのEPC
     * @param curData 現在のプロパティデータ
     * @param oldData 以前のプロパティデータ
     */
    public void notifyDataChanged(EPC epc, ObjectData curData, ObjectData oldData) {
        logger.entering(className, "notifyDataChanged", new Object[]{epc, curData, oldData});
        
        for (LocalObjectDelegate delegate: cloneDelegates()) {
            delegate.notifyDataChanged(this, epc, curData, oldData);
        }
        
        logger.exiting(className, "notifyDataChanged");
    }
    
    private boolean setDataDelegate(EPC epc, ObjectData newData, ObjectData curData) {
        logger.entering(className, "setDataDelegate", new Object[]{epc, newData, curData});
        
        boolean success = false;
        for (LocalObjectDelegate delegate: cloneDelegates()) {
            success |= delegate.setData(this, epc, newData, curData);
        }
        
        logger.exiting(className, "setDataDelegate", success);
        return success;
    }
    
    private ObjectData getDataDelegate(EPC epc) {
        logger.entering(className, "getDataDelegate", new Object[]{epc});
        
        ObjectData data = null;
        for (LocalObjectDelegate delegate: cloneDelegates()) {
            ObjectData lastData = delegate.getData(this, epc);
            if (lastData != null) {
                data = lastData;
            }
        }
        
        logger.exiting(className, "getDataDelegate", data);
        return data;
    }
    
    /**
     * Delegateを登録する。
     * @param delegate 登録するDelegate
     */
    public synchronized void addDelegate(LocalObjectDelegate delegate) {
        logger.entering(className, "addDelegate", delegate);
        
        delegates.add(delegate);
        
        logger.entering(className, "addDelegate");
    }
    
    /**
     * Delegateの登録を抹消する。
     * @param delegate 抹消するDelegate
     */
    public synchronized void removeDelegate(LocalObjectDelegate delegate) {
        logger.entering(className, "removeDelegate", delegate);
        
        delegates.remove(delegate);
        
        logger.entering(className, "removeDelegate");
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{EOJ: " + eoj + "}";
    }
}
