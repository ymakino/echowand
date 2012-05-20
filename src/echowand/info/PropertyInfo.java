package echowand.info;

import echowand.common.EPC;

/**
 * ObjectInfoで利用されるプロパティを表現する。
 * @author Yoshiki Makino
 */
public class PropertyInfo {
    /**
     * このプロパティのEPCを表す。
     */
    public EPC epc;
    /**
     * このプロパティがGet可能であるかどうか表す。
     */
    public boolean gettable;
    /**
     * このプロパティがSet可能であるかどうか表す。
     */
    public boolean settable;
    /**
     * このプロパティの値が変更した時に通知を行なうかを表す。
     */
    public boolean observable;
    /**
     * このプロパティの最小データサイズを表す。
     */
    public PropertyConstraint constraint;
    /**
     * このプロパティのデータを表す。
     */
    public byte[] data;
    
    /**
     * PropertyInfoを生成する。
     * 指定されたサイズのデータのみを受け付けるという制約が付与される。
     * @param epc プロパティのEPC
     * @param gettable Getの可否
     * @param settable Setの可否
     * @param observable 通知の有無
     * @param size プロパティのデータサイズ
     */
    public PropertyInfo(EPC epc, boolean gettable, boolean settable, boolean observable, int size) {
        this(epc, gettable, settable, observable, new PropertyConstraintSize(size));
    }
    
    /**
     * PropertyInfoを生成する。
     * @param epc プロパティのEPC
     * @param gettable Getの可否
     * @param settable Setの可否
     * @param observable 通知の有無
     * @param constraint プロパティの制約
     */
    public PropertyInfo(EPC epc, boolean gettable, boolean settable, boolean observable, PropertyConstraint constraint) {
        this.epc = epc;
        this.gettable = gettable;
        this.settable = settable;
        this.observable = observable;
        this.constraint = constraint;
        this.data = constraint.getInitialData();
    }
    
    /**
     * PropertyInfoを生成する。
     * 指定されたデータのサイズが制約として付与される。
     * @param epc プロパティのEPC
     * @param gettable Getの可否
     * @param settable Setの可否
     * @param observable 通知の有無
     * @param data プロパティのデータ
     */
    public PropertyInfo(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] data) {
        this(epc, gettable, settable, observable, new PropertyConstraintSize(data.length), data);
    }
    
    /**
     * PropertyInfoを生成する。
     * @param epc プロパティのEPC
     * @param gettable Getの可否
     * @param settable Setの可否
     * @param observable 通知の有無
     * @param constraint プロパティの制約
     * @param data プロパティのデータ
     */
    public PropertyInfo(EPC epc, boolean gettable, boolean settable, boolean observable, PropertyConstraint constraint, byte[] data) {
        this.epc = epc;
        this.gettable = gettable;
        this.settable = settable;
        this.observable = observable;
        this.constraint = constraint;
        this.data = data;
    }
    
    /**
     * 指定されたデータがプロパティとして設定可能か調べる。
     * @param data プロパティのデータ
     * @return プロパティとして設定可能な場合にはtrue、そうでない場合にはfalse
     */
    public boolean isAcceptable(byte[] data) {
        return constraint.isAcceptable(data);
    }
    
    /**
     * このPropertyInfoが指定されたオブジェクトと等しいかどうか調べる。
     * @param prop 比較されるオブジェクト
     * @return オブジェクトが等しい場合にはtrue、そうでない場合にはfalse
     */
    @Override
    public boolean equals(Object prop) {
        if (! (prop instanceof PropertyInfo)) {
            return false;
        }
        
        return this.epc == ((PropertyInfo)prop).epc;
    }

    
    /**
     * このPropertyInfoのハッシュコードを返す。
     * @return このEOJのハッシュコード
     */
    @Override
    public int hashCode() {
        return this.epc.hashCode();
    }
}