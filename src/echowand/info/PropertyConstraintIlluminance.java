package echowand.info;

import echowand.util.Constraint;
import echowand.util.ConstraintShort;
import echowand.util.ConstraintUnion;

/**
 * 照度センサのデータ制約を表現する。
 * データの範囲は0x0000 - 0xFFFD (0 - 65533)となる。
 * オーバーフローコードとして0xFFFF、アンダーフローコードとして0xFFFEが利用される。
 * @author Yoshiki Makino
 */
public class PropertyConstraintIlluminance implements Constraint {
    private static final short MIN_VALUE = (short)0;
    private static final short MAX_VALUE = (short)0xfffd;
    private static final short OVERFLOW = (short)0xffff;
    private static final short UNDERFLOW = (short)0xfffe;
    
    private ConstraintUnion constraint;
    
    /**
     * PropertyConstraintIlluminanceを生成する。
     */
    public PropertyConstraintIlluminance() {
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
     * PropertyConstraintIlluminanceの文字列表現を返す。
     * @return PropertyConstraintIlluminanceの文字列表現
     */
    @Override
    public String toString() {
        return constraint.toString();
    }
}
