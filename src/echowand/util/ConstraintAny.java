package echowand.util;

/**
 * 常に真となるプロパティの値の制約を表現する。
 * @author Yoshiki Makino
 */
public class ConstraintAny implements Constraint {
    
    /**
     * 指定されたプロパティデータが制約に従っているか調べる。
     * @param data プロパティデータ
     * @return 常にtrue
     */
    @Override
    public boolean isValid(byte[] data) {
        return true;
    }
    
    @Override
    public String toString() {
        return "Any";
    }
}
