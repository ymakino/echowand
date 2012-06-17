package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;
import echowand.common.PropertyMap;
import echowand.util.Constraint;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * ECHONETオブジェクト基本設定構築用クラス
 * @author Yoshiki Makino
 */
public class BaseObjectInfo implements ObjectInfo {
    public static final Logger logger = Logger.getLogger(BaseObjectInfo.class.getName());
    private static final String className = BaseObjectInfo.class.getName();
    
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
        logger.entering(className, "BaseObjectInfo");
        
        add(EPC.x80, true, false,  true, new byte[]{0x30}, new PropertyConstraintOnOff());
        add(EPC.x88, true, false,  true, new byte[]{0x42}, new PropertyConstraintDetection());
        add(EPC.x9D, true, false, false, new PropertyMap().toBytes(), new PropertyConstraintMap()); 
        add(EPC.x9E, true, false, false, new PropertyMap().toBytes(), new PropertyConstraintMap());
        add(EPC.x9F, true, false, false, new PropertyMap().toBytes(), new PropertyConstraintMap());
        
        logger.exiting(className, "BaseObjectInfo");
    }

    private synchronized void updatePropertyMapsInNeeds() {
        logger.entering(className, "updatePropertyMapsInNeeds");
        
        if (!needsUpdatePropertyMap) {
            logger.exiting(className, "updatePropertyMapsInNeeds");
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

        logger.exiting(className, "updatePropertyMapsInNeeds");
    }

    private void makeUpdatePropertyMapNeeded() {
        logger.entering(className, "makeUpdatePropertyMapNeeded");
        
        needsUpdatePropertyMap = true;
        
        logger.exiting(className, "makeUpdatePropertyMapNeeded");
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
        logger.entering(className, "setClassEOJ", ceoj);
        
        classEOJ = ceoj;
        
        logger.exiting(className, "setClassEOJ");
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
    public final boolean add(EPC epc, boolean gettable, boolean settable, boolean observable, int size, Constraint constraint) {
        return add(new PropertyInfo(epc, gettable, settable, observable, size, constraint));
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
    public final boolean add(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] data, Constraint constraint) {
        return add(new PropertyInfo(epc, gettable, settable, observable, data, constraint));
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトにプロパティを追加する。
     * @param prop 追加するプロパティ
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public final boolean add(PropertyInfo prop) {
        logger.entering(className, "add", prop);
        
        makeUpdatePropertyMapNeeded();
        makePropListOutdated();
        boolean status = addWithoutUpdating(prop);
        
        logger.exiting(className, "add", status);
        return status;
    }
    
    private boolean addWithoutUpdating(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] data) {
        return addWithoutUpdating(new PropertyInfo(epc, gettable, settable, observable, data));
    }
    
    private boolean addWithoutUpdating(PropertyInfo prop) {
        logger.entering(className, "addWithoutUpdating", prop);
        
        props.remove(prop);
        boolean status = props.add(prop);
        
        logger.exiting(className, "addWithoutUpdating", status);
        return status;
    }
    
    private void makePropListOutdated() {
        logger.entering(className, "makePropListOutdated");
        
        propList = null;
        
        logger.exiting(className, "makePropListOutdated");
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトのプロパティのリストを返す。
     * @return プロパティのリスト
     */
    private ArrayList<PropertyInfo> getPropList() {
        logger.entering(className, "getPropList");
        
        if (propList == null) {
            propList = new ArrayList<PropertyInfo>(props);
        }
        
        logger.exiting(className, "getPropList", propList);
        return propList;
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトのindex番目のプロパティを返す。
     * @param index プロパティのインデックス
     * @return index番目のプロパティ
     */
    @Override
    public PropertyInfo getAtIndex(int index) {
        logger.entering(className, "getAtIndex", index);
        
        updatePropertyMapsInNeeds();
        PropertyInfo propertyInfo = getPropList().get(index);
        
        logger.exiting(className, "getAtIndex", propertyInfo);
        return propertyInfo;
    }
    
    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトの指定されたEPCに対応するプロパティを返す。
     * @param epc プロパティのEPC
     * @return 指定されたEPCに対応するプロパティ
     */
    @Override
    public PropertyInfo get(EPC epc) {
        logger.entering(className, "get", epc);

        updatePropertyMapsInNeeds();
        for (PropertyInfo prop : getPropList()) {
            if (prop.epc == epc) {
                logger.exiting(className, "get", prop);
                return prop;
            }
        }

        PropertyInfo propertyInfo = new PropertyInfo(epc, false, false, false, 0);
        logger.exiting(className, "get", propertyInfo);
        return propertyInfo;
    }

    /**
     * このBaseObjectInfoが表現するECHONETオブジェクトの全プロパティ設定数を返す。
     *
     * @return 全プロパティ設定数
     */
    @Override
    public final int size() {
        return getPropList().size();
    }
}
