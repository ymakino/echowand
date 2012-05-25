package echowand.info;

import echowand.util.ConstraintSize;
import echowand.util.Constraint;
import echowand.common.EPC;
import java.util.Arrays;

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
     * @param size プロパティのデータサイズ
     */
    public PropertyInfo(EPC epc, boolean gettable, boolean settable, boolean observable, int size) {
        this(epc, gettable, settable, observable, new byte[size]);
    }
    
    /**
     * PropertyInfoを生成する。
     * @param epc プロパティのEPC
     * @param gettable Getの可否
     * @param settable Setの可否
     * @param observable 通知の有無
     * @param size プロパティのデータサイズ
     * @param constraint プロパティの制約
     */
    public PropertyInfo(EPC epc, boolean gettable, boolean settable, boolean observable, int size, Constraint constraint) {
        this(epc, gettable, settable, observable, new byte[size], constraint);
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
        this(epc, gettable, settable, observable, data, new ConstraintSize(data.length));
    }
    
    /**
     * PropertyInfoを生成する。
     * @param epc プロパティのEPC
     * @param gettable Getの可否
     * @param settable Setの可否
     * @param observable 通知の有無
     * @param data プロパティのデータ
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
}