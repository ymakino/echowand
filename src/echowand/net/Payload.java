package echowand.net;

/**
 * ペイロードの共通インタフェース
 * @author Yoshiki Makino
 */
public interface Payload {
    /**
     * ペイロードをバイト列で表現した時の長さを返す。
     * @return ペイロード長
     */
    public int size();
    
    /**
     * ペイロードのバイト列表現を返す。
     * @return ペイロードのバイト配列
     */
    public byte[] toBytes();
}
