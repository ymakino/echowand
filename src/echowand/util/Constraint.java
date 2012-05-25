package echowand.util;

/**
 * プロパティの値の制約を表現する。
 * @author Yoshiki Makino
 */
public interface Constraint {
    
    /**
     * 指定されたプロパティデータが制約に従っているか調べる。
     * @param data プロパティデータ
     * @return 制約に従っている場合にはtrue、従っていない場合にはfalse
     */
    public boolean isValid(byte[] data);
}
