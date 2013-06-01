package echowand.info;

import echowand.util.Constraint;
import echowand.util.ConstraintByte;
import echowand.util.ConstraintUnion;

/**
 * 湿度センサのデータ制約を表現する。
 * データの範囲は0x00 - 0x64 (0% - 100%)となる。
 * オーバーフローコードとして0xFF、アンダーフローコードとして0xFEが利用される。
 * @author Yoshiki Makino
 */
public class PropertyConstraintHumidity implements Constraint {
    private static final byte MIN_VALUE = (byte)0x00;
    private static final byte MAX_VALUE = (byte)0x64;
    private static final byte OVERFLOW = (byte)0xff;
    private static final byte UNDERFLOW = (byte)0xfe;
    
    private ConstraintUnion constraint;
    
    /**
     * PropertyConstraintHumidityを生成する。
     */
    public PropertyConstraintHumidity() {
        ConstraintByte normal = new ConstraintByte(MIN_VALUE, MAX_VALUE);
        
        ConstraintByte overflow = new ConstraintByte(OVERFLOW);
        ConstraintByte underflow = new ConstraintByte(UNDERFLOW);
        ConstraintUnion invalid = new ConstraintUnion(overflow, underflow);
        
        constraint = new ConstraintUnion(normal, invalid);
    }

    @Override
    public boolean isValid(byte[] data) {
        return constraint.isValid(data);
    }
    
    /**
     * PropertyConstraintHumidityの文字列表現を返す。
     * @return PropertyConstraintHumidityの文字列表現
     */
    @Override
    public String toString() {
        return constraint.toString();
    }
}
