package echowand.info;

/**
 * ある状態を感知しているかというデータ制約を表現する。
 * 0x41と0x42の二値のみを許可する制約になる。
 * @author Yoshiki Makino
 */
public class PropertyConstraintDetection extends PropertyConstraintByte {
    
    /**
     * PropertyConstraintDetectionを生成する。
     */
    public PropertyConstraintDetection() {
        super((byte)0x41, (byte)0x42);
    }
    
    @Override
    public byte[] getInitialData() {
        return new byte[]{(byte)0x42};
    }
}
