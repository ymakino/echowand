package echowand.net;

import java.util.Arrays;

/**
 * 単純なバイト列のペイロード
 * @author Yoshiki Makino
 */
public class SimplePayload implements Payload {
    private byte[] payload;
    
    /**
     * SimplePayloadを長さ0で生成する。
     */
    public SimplePayload() {
        this.payload = new byte[0];
    }
    
    /**
     * 指定されたバイト配列のSimplePayloadを生成する。
     * @param payload ペイロードのバイト配列
     */
    public SimplePayload(byte[] payload) {
        this(payload, 0);
    }
    
    /**
     * 指定されたバイト配列の指定されたオフセットからSimplePayloadを生成する。
     * @param payload ペイロードを含むバイト配列
     * @param offset バイト配列のオフセット
     */
    public SimplePayload(byte[] payload, int offset) {
        int length = payload.length - offset;
        this.payload = new byte[length];
        System.arraycopy(payload, offset, this.payload, 0, length);
    }
    
    /**
     * ペイロードを設定する。
     * @param payload 設定するペイロード
     */
    public void setPayload(byte[] payload) {
        this.payload = Arrays.copyOf(payload, payload.length);
    }
    
    /**
     * 設定されたペイロードを返す。
     * @return 設定されたペイロード
     */
    public byte[] getPayload() {
        return Arrays.copyOf(payload, payload.length);
    }
    
    /**
     * このSimplePayloadをバイト配列で表現したときの長さを返す。
     * @return バイト配列の長さ
     */
    @Override
    public int size() {
        return payload.length;
    }
    
    /**
     * このSimplePayloadのバイト配列表現を返す。
     * @return このSimplePayloadのバイト配列
     */
    @Override
    public byte[] toBytes() {
        return Arrays.copyOf(payload, payload.length);
    }
    
    /**
     * このSimplePayloadの文字列表現を返す。
     * @return このSimplePayloadの文字列表現
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (byte b : toBytes()) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
