package echowand.info;

import echowand.util.ConstraintByte;

/**
 * ON/OFFプロパティデータ制約を表現する。
 * 0x30と0x31の二値のみを許可する制約になる。
 * @author Yoshiki Makino
 */
public class PropertyConstraintOnOff extends ConstraintByte {
    public static final byte ON=0x30;
    public static final byte OFF=0x31;
    
    /**
     * PropertyConstraintOnOffを生成する。
     */
    public PropertyConstraintOnOff() {
        super(ON, OFF);
    }
}
