package echowand.info;

import echowand.util.Constraint;
import echowand.util.ConstraintShort;
import echowand.util.ConstraintUnion;

/**
 * 風速センサのデータ制約を表現する。
 * データの範囲は0x0000 - 0xfffd (0 m/sec - 655.33 m/sec)となる。
 * オーバーフローコードとして0xFFFF、アンダーフローコードとして0xFFFEが利用される。
 * @author Yoshiki Makino
 */
public class PropertyConstraintAirSpeed implements Constraint {
    private static final short MIN_VALUE = (short)0x0000;
    private static final short MAX_VALUE = (short)0xfffd;
    private static final short OVERFLOW = (short)0xffff;
    private static final short UNDERFLOW = (short)0xfffe;
    
    private ConstraintUnion constraint;
    
    /**
     * PropertyConstraintAirSpeedを生成する。
     */
    public PropertyConstraintAirSpeed() {
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
     * PropertyConstraintAirSpeedの文字列表現を返す。
     * @return PropertyConstraintAirSpeedの文字列表現
     */
    @Override
    public String toString() {
        return constraint.toString();
    }
}
