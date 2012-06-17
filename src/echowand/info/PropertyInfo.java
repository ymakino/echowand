package echowand.info;

import echowand.common.Data;
import echowand.common.EPC;
import echowand.util.Constraint;
import echowand.util.ConstraintSize;

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
    public Constraint constraint;
    /**
     * このプロパティの初期値を表す。
     */
    public byte[] initialData;
    
    /**
     * PropertyInfoを生成する。
     * 指定されたサイズのデータのみを受け付けるという制約が付与される。
     * @param epc プロパティのEPC
     * @param gettable Getの可否
     * @param settable Setの可否
     * @param observable 通知の有無
     * @param initialSize プロパティの初期データサイズ
     */
    public PropertyInfo(EPC epc, boolean gettable, boolean settable, boolean observable, int initialSize) {
        this(epc, gettable, settable, observable, new byte[initialSize]);
    }
    
    /**
     * PropertyInfoを生成する。
     * @param epc プロパティのEPC
     * @param gettable Getの可否
     * @param settable Setの可否
     * @param observable 通知の有無
     * @param initialSize プロパティの初期データサイズ
     * @param constraint プロパティの制約
     */
    public PropertyInfo(EPC epc, boolean gettable, boolean settable, boolean observable, int initialSize, Constraint constraint) {
        this(epc, gettable, settable, observable, new byte[initialSize], constraint);
    }
    
    /**
     * PropertyInfoを生成する。
     * 指定されたデータのサイズが制約として付与される。
     * @param epc プロパティのEPC
     * @param gettable Getの可否
     * @param settable Setの可否
     * @param observable 通知の有無
     * @param initialData プロパティの初期データ
     */
    public PropertyInfo(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] initialData) {
        this(epc, gettable, settable, observable, initialData, new ConstraintSize(initialData.length));
    }
    
    /**
     * PropertyInfoを生成する。
     * @param epc プロパティのEPC
     * @param gettable Getの可否
     * @param settable Setの可否
     * @param observable 通知の有無
     * @param initialData プロパティの初期データ
     * @param constraint プロパティの制約
     */
    public PropertyInfo(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] initialData, Constraint constraint) {
        this.epc = epc;
        this.gettable = gettable;
        this.settable = settable;
        this.observable = observable;
        this.constraint = constraint;
        this.initialData = initialData;
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
     * @return このPropertyInfoのハッシュコード
     */
    @Override
    public int hashCode() {
        return this.epc.hashCode();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PropertyInfo(EPC: ");
        builder.append(epc);
        builder.append(" Gettable: ");
        builder.append(gettable);
        builder.append(" Settable: ");
        builder.append(settable);
        builder.append(" Observable: ");
        builder.append(observable);
        builder.append(" InitialData: ");
        builder.append(new Data(initialData));
        builder.append(" Constraint: ");
        builder.append(constraint);
        builder.append(")");
        return builder.toString();
    }
}