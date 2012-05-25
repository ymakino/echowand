package echowand.info;

import echowand.util.ConstraintByte;

/**
 * ある状態を感知しているかというデータ制約を表現する。
 * 0x41と0x42の二値のみを許可する制約になる。
 * @author Yoshiki Makino
 */
public class PropertyConstraintDetection extends ConstraintByte {
    
    /**
     * PropertyConstraintDetectionを生成する。
     */
    public PropertyConstraintDetection() {
        super((byte)0x41, (byte)0x42);
    }
}
