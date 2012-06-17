package echowand.util;

/**
 * 常に偽となるプロパティの値の制約を表現する。
 * @author Yoshiki Makino
 */
public class ConstraintNone implements Constraint {
        
    /**
     * 指定されたプロパティデータが制約に従っているか調べる。
     * @param data プロパティデータ
     * @return 常にfalse
     */
    @Override
    public boolean isValid(byte[] data) {
        return false;
    }
    
    @Override
    public String toString() {
        return "None";
    }
}
