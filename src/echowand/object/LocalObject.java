package echowand.object;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.ObjectInfo;
import echowand.util.Constraint;
import echowand.info.PropertyInfo;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;

/**
 * ローカルに存在するECHONETオブジェクト
 * @author Yoshiki Makino
 */
public class LocalObject implements EchonetObject {
    private EOJ eoj;
    private ObjectInfo objectInfo;
    private EnumMap<EPC, ObjectData> propertyData;
    private LinkedList<LocalObjectDelegate> delegates;
    
    /**
     * 指定されたオブジェクト情報を用いてLocaObjectを生成
     * @param objectInfo 作成するLocalObjectのオブジェクト情報
     */
    public LocalObject(ObjectInfo objectInfo) {
        this.objectInfo = objectInfo;
        this.eoj = objectInfo.getClassEOJ().getEOJWithInstanceCode((byte)0x01);
        propertyData = new EnumMap<EPC, ObjectData>(EPC.class);
        delegates = new LinkedList<LocalObjectDelegate>();
        int len = objectInfo.size();
        for (int i=0; i<len; i++) {
            PropertyInfo info = objectInfo.getAtIndex(i);
            propertyData.put(info.epc, new ObjectData(info.initialData));
        }
    }
    
    /**
     * EOJのインスタンスコードを新たに設定する。
     * @param instanceCode 設定するインスタンスコード
     */
    public void setInstanceCode(byte instanceCode) {
        this.eoj = eoj.getClassEOJ().getEOJWithInstanceCode(instanceCode);
    }
    
    /**
     * 指定されたEPCのプロパティのためにLocalObjectが内部で管理しているデータの内容を設定する。
     * @param epc 設定するデータのEPC
     * @param data 設定するデータの内容
     * @return 設定に成功したらtrue、そうでなければfalse
     */
    public boolean setInternalData(EPC epc, ObjectData data) {
        if (!contains(epc)) {
            return false;
        }
        
        propertyData.put(epc, data);
        return true;
    }
    
    /**
     * 指定されたEPCのプロパティの内容をSetの許可がなくても強制的に変更する。
     * Delegateが最初に処理を行い、Delegateの処理が成功しなかった場合にはLocalObject内部のデータを変更する。
     * @param epc 設定するデータのEPC
     * @param data 設定するデータの内容
     * @return 設定に成功したらtrue、そうでなければfalse
     */
    public boolean forceSetData(EPC epc, ObjectData data) {
        
        boolean success = setDataDelegate(epc, data);
        
        if (!success) {
            success = setInternalData(epc, data);
            if (success) {
                notifyDataChanged(epc, data);
            }
        }
        
        return success;
    }
    
    /**
     * 指定されたEPCのプロパティの内容を変更する。
     * Setの許可がないプロパティへの操作や、データの制約に従わない操作は失敗する。
     * Delegateが最初に処理を行い、Delegateの処理が成功しなかった場合にはLocalObject内部のデータを変更する。
     * @param epc 設定するデータのEPC
     * @param data 設定するデータの内容
     * @return 設定に成功したらtrue、そうでなければfalse
     */
    @Override
    public boolean setData(EPC epc, ObjectData data) {
        if (!this.isSettable(epc)) {
            return false;
        }
        
        PropertyInfo propertyInfo = objectInfo.get(epc);
        Constraint constraint = propertyInfo.constraint;
        if (!constraint.isValid(data.toBytes())) {
            return false;
        }
        
        return forceSetData(epc, data);
    }
    
    
    /**
     * 指定されたEPCのプロパティのためにLocalObjectが内部で管理しているデータの内容を返す。
     * @param epc データのEPC
     * @return プロパティのデータ、存在しない場合にはnull
     */
    public ObjectData getInternalData(EPC epc) {
        return propertyData.get(epc);
    }
    
    /**
     * 指定されたEPCのプロパティの内容をGetの許可がなくても強制的に返す。
     * Delegateが最初に処理を行い、Delegateがデータを返さない場合にはLocalObject内部のデータを返す。
     * @param epc データのEPC
     * @return プロパティのデータ、存在しない場合にはnull
     */
    public ObjectData forceGetData(EPC epc) {
        ObjectData data = getDataDelegate(epc);
        
        if (data == null) {
            data = getInternalData(epc);
        }
        
        return data;
    }
    
    /**
     * 指定されたEPCのプロパティの内容を返す。
     * Getの許可がない場合にはnullを返す。
     * Delegateが最初に処理を行い、Delegateがデータを返さない場合にはLocalObject内部のデータを返す。
     * @param epc データのEPC
     * @return プロパティのデータ、存在しない場合にはnull
     */
    @Override
    public ObjectData getData(EPC epc) {
        if (!this.isGettable(epc)) {
            return null;
        }
        
        return forceGetData(epc);
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
    public boolean contains(EPC epc) {
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
     * @param data 変化後のプロパティデータの値
     */
    public void notifyDataChanged(EPC epc, ObjectData data) {
        for (LocalObjectDelegate delegate: new ArrayList<LocalObjectDelegate>(delegates)) {
            delegate.notifyDataChanged(this, epc, data);
        }
    }
    
    private boolean setDataDelegate(EPC epc, ObjectData data) {
        boolean success = false;
        for (LocalObjectDelegate delegate: new ArrayList<LocalObjectDelegate>(delegates)) {
            success |= delegate.setData(this, epc, data);
        }
        return success;
    }
    
    private ObjectData getDataDelegate(EPC epc) {
        ObjectData data = null;
        for (LocalObjectDelegate delegate: new ArrayList<LocalObjectDelegate>(delegates)) {
            ObjectData lastData = delegate.getData(this, epc);
            if (lastData != null) {
                data = lastData;
            }
        }
        return data;
    }
    
    /**
     * Delegateを登録する。
     * @param delegate 登録するDelegate
     */
    public void addDelegate(LocalObjectDelegate delegate) {
        delegates.add(delegate);
    }
    
    /**
     * Delegateの登録を抹消する。
     * @param delegate 抹消するDelegate
     */
    public void removeDelegate(LocalObjectDelegate delegate) {
        delegates.remove(delegate);
    }
}
