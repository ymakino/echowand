package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;
import echowand.common.PropertyMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * ECHONETオブジェクト基本設定構築用クラス
 * @author Yoshiki Makino
 */
public class BaseObjectInfo implements ObjectInfo {
    
    private class PropertyInfoComparator implements Comparator<PropertyInfo> {
        @Override
        public int compare(PropertyInfo p1, PropertyInfo p2) {
            int c1 = (p1.epc.toByte() & 0x000000ff);
            int c2 = (p2.epc.toByte() & 0x000000ff);
            return c1 - c2;
        }
    }
    
    private ClassEOJ classEOJ;
    private TreeSet<PropertyInfo> props = new TreeSet<PropertyInfo>(new PropertyInfoComparator());
    private ArrayList<PropertyInfo> propList = null;
    private boolean needsUpdatePropertyMap = true;
    
    /**
     * BaseObjectInfoを生成する。
     */
    public BaseObjectInfo() {
        add(EPC.x80, true, false,  true, new PropertyConstraintOnOff());
        add(EPC.x88, true, false,  true, new PropertyConstraintDetection());
        add(EPC.x9D, true, false, false, new PropertyConstraintMap()); 
        add(EPC.x9E, true, false, false, new PropertyConstraintMap());
        add(EPC.x9F, true, false, false, new PropertyConstraintMap());
    }

    private synchronized void updatePropertyMapsInNeeds() {
        if (!needsUpdatePropertyMap) {
            return;
        }

        needsUpdatePropertyMap = false;

        PropertyMap annoMap = new PropertyMap();
        PropertyMap setMap = new PropertyMap();
        PropertyMap getMap = new PropertyMap();
        int count = size();
        for (int i = 0; i < count; i++) {
            PropertyInfo info = getAtIndex(i);
            if (info.gettable) {
                getMap.set(info.epc);
            }
            if (info.settable) {
                setMap.set(info.epc);
            }
            if (info.observable) {
                annoMap.set(info.epc);
            }
        }
        
        PropertyInfo annoInfo = get(EPC.x9D);
        addWithoutUpdating(annoInfo.epc, annoInfo.gettable, annoInfo.settable, annoInfo.observable, annoMap.toBytes());
        PropertyInfo setInfo = get(EPC.x9E);
        addWithoutUpdating(setInfo.epc, setInfo.gettable, setInfo.settable, setInfo.observable, setMap.toBytes());
        PropertyInfo getInfo = get(EPC.x9F);
        addWithoutUpdating(getInfo.epc, getInfo.gettable, getInfo.settable, getInfo.observable, getMap.toBytes());
        
        makePropListOutdated();
    }

    private void makeUpdatePropertyMapNeeded() {
        needsUpdatePropertyMap = true;
    }
    
    /**
     * このBaseObjectInfoが表現しているECHONETオブジェクトのClassEOJを返す。
     * @return このBaseObjectInfoのClassEOJ
     */
    @Override
    public ClassEOJ getClassEOJ() {
        return classEOJ;
    }
    
    /**
     * このBaseObjectInfoが表現しているECHONETオブジェクトのClassEOJを設定する。
     * @param ceoj このBaseObjectInfoが表現するECHONETオブジェクトのClassEOJ
     */
    public final void setClassEOJ(ClassEOJ ceoj) {
        classEOJ = ceoj;
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトにプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param gettable Setの可否
     * @param settable Getの可否
     * @param observable 通知の有無
     * @param size プロパティのデータサイズ
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public final boolean add(EPC epc, boolean gettable, boolean settable, boolean observable, int size) {
        return add(new PropertyInfo(epc, gettable, settable, observable, size));
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトにプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param gettable Setの可否
     * @param settable Getの可否
     * @param observable 通知の有無
     * @param constraint プロパティの制約
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public final boolean add(EPC epc, boolean gettable, boolean settable, boolean observable, PropertyConstraint constraint) {
        return add(new PropertyInfo(epc, gettable, settable, observable, constraint));
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトにプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param gettable Setの可否
     * @param settable Getの可否
     * @param observable 通知の有無
     * @param data プロパティのデータ
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public final boolean add(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] data) {
        return add(new PropertyInfo(epc, gettable, settable, observable, data));
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトにプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param gettable Setの可否
     * @param settable Getの可否
     * @param observable 通知の有無
     * @param constraint プロパティの制約
     * @param data プロパティのデータ
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public final boolean add(EPC epc, boolean gettable, boolean settable, boolean observable, PropertyConstraint constraint, byte[] data) {
        return add(new PropertyInfo(epc, gettable, settable, observable, constraint, data));
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトにプロパティを追加する。
     * @param prop 追加するプロパティ
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public final boolean add(PropertyInfo prop) {
        makeUpdatePropertyMapNeeded();
        makePropListOutdated();
        return addWithoutUpdating(prop);
    }
    
    private boolean addWithoutUpdating(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] data) {
        return addWithoutUpdating(new PropertyInfo(epc, gettable, settable, observable, data));
    }
    
    private boolean addWithoutUpdating(PropertyInfo prop) {
        props.remove(prop);
        return props.add(prop);
    }
    
    private void makePropListOutdated() {
        propList = null;
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトのプロパティのリストを返す。
     * @return プロパティのリスト
     */
    private ArrayList<PropertyInfo> getPropList() {
        if (propList == null) {
            propList = new ArrayList<PropertyInfo>(props);
        }
        return propList;
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトのindex番目のプロパティを返す。
     * @param index プロパティのインデックス
     * @return index番目のプロパティ
     */
    @Override
    public PropertyInfo getAtIndex(int index) {
        updatePropertyMapsInNeeds();
        return getPropList().get(index);
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトの指定されたEPCに対応するプロパティを返す。
     * @param epc プロパティのEPC
     * @return 指定されたEPCに対応するプロパティ
     */
    @Override
    public PropertyInfo get(EPC epc) {
        updatePropertyMapsInNeeds();
        for (PropertyInfo prop : getPropList()) {
            if (prop.epc == epc) {
                return prop;
            }
        }
    
        return new PropertyInfo(epc, false, false, false, 0);
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトの全プロパティ設定数を返す。
     * @return 全プロパティ設定数
     */
    @Override
    public final int size() {
        return getPropList().size();
    }
}
