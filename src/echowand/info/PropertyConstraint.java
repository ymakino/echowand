package echowand.info;

/**
 * プロパティの値の制約を表現する。
 * @author Yoshiki Makino
 */
public interface PropertyConstraint {
    
    /**
     * 指定されたプロパティデータが制約に従っているか調べる。
     * @param data プロパティデータ
     * @return 制約に従っている場合にはtrue、従っていない場合にはfalse
     */
    public boolean isAcceptable(byte[] data);
    
    /**
     * 初期プロパティデータを返す。
     * @return 初期プロパティデータ
     */
    public byte[] getInitialData();
}
