package echowand.info;

/**
 * ON/OFFプロパティデータ制約を表現する。
 * 0x30と0x31の二値のみを許可する制約になる。
 * @author Yoshiki Makino
 */
public class PropertyConstraintOnOff extends PropertyConstraintByte {
    
    /**
     * PropertyConstraintOnOffを生成する。
     */
    public PropertyConstraintOnOff() {
        super((byte)0x30, (byte)0x31);
    }
}
