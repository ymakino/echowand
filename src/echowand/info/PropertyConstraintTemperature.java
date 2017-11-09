package echowand.info;

import echowand.util.Constraint;
import echowand.util.ConstraintShort;
import echowand.util.ConstraintUnion;

/**
 * 温度センサのデータ制約を表現する。
 * データの範囲は0xF554 - 0x7FFD (-273.2C - 3276.6C)となる。
 * オーバーフローコードとして0x7FFF、アンダーフローコードとして0x8000が利用される。
 * @author Yoshiki Makino
 */
public class PropertyConstraintTemperature implements Constraint {
    private static final short MIN_VALUE = (short)0xf554;
    private static final short MAX_VALUE = (short)0x7ffd;
    private static final short OVERFLOW = (short)0x7fff;
    private static final short UNDERFLOW = (short)0x8000;
    
    private ConstraintUnion constraint;
    
    /**
     * PropertyConstraintTemperatureを生成する。
     */
    public PropertyConstraintTemperature() {
        ConstraintShort normal = new ConstraintShort(MIN_VALUE, MAX_VALUE);
        
        ConstraintShort overflow = new ConstraintShort(OVERFLOW);
        ConstraintShort underflow = new ConstraintShort(UNDERFLOW);
        ConstraintUnion invalid = new ConstraintUnion(overflow, underflow);
        
        constraint = new ConstraintUnion(normal, invalid);
    }

    @Override
    public boolean isValid(byte[] data) {
        return constraint.isValid(data);
    }
    
    /**
     * PropertyConstraintTemperatureの文字列表現を返す。
     * @return PropertyConstraintTemperatureの文字列表現
     */
    @Override
    public String toString() {
        return constraint.toString();
    }
}
