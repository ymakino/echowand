package echowand.common;

import java.util.Arrays;

/**
 * 変更不可なバイト配列データ表現
 * @author Yoshiki Makino
 */
public class Data {
    private byte[] data;
    
    /**
     * 指定されたバイト列を用いてDataを生成する。
     * @param data データのバイト配列
     */
    public Data(byte... data) {
        this.data = Arrays.copyOf(data, data.length);
    }
    
    
    /**
     * 指定されたバイト配列を用いてDataを生成する。
     * @param data データを含むバイト配列
     * @param offset データまでのオフセット
     * @param length データの長さ
     */
    public Data(byte[] data, int offset, int length) {
        this.data = new byte[length];
        System.arraycopy(data, offset, this.data, 0, length);
    }
    
    /**
     * データのindex番目のバイトを返す
     * @param index バイトのインデックス
     * @return index番目のバイト
     */
    public byte get(int index) {
        return data[index];
    }
    
    /**
     * データ長を返す。
     * @return データ長
     */
    public int size() {
        return data.length;
    }
    
    /**
     * データが存在するかどうかを返す。
     * @return データの有無
     */
    public boolean isEmpty() {
        return data.length == 0;
    }

    /**
     * データのバイト配列を返す。
     * @return データのバイト配列
     */
    public byte[] toBytes() {
        return Arrays.copyOf(data, data.length);
    }
    
    /**
     * 指定されたオフセット、長さのデータの部分バイト配列を返す。
     * @param offset 指定されたデータのオフセット
     * @param length 指定されたデータの長さ
     * @return 指定されたデータの部分バイト配列
     */
    public byte[] toBytes(int offset, int length) {
        byte[] newData = new byte[length];
        System.arraycopy(this.data, offset, newData, 0, length);
        return newData;
    }
    
    /**
     * 指定されたオフセット、長さのデータの部分バイト配列をコピーする。
     * @param srcOffset コピー元のデータのオフセット
     * @param destData コピー先の配列
     * @param destOffset コピー先の配列のオフセット
     * @param length コピーするデータの長さ
     */
    public void copyBytes(int srcOffset, byte[] destData, int destOffset, int length) {
            System.arraycopy(data, srcOffset, destData, destOffset, length);
    }
    
    /**
     * データの文字列表現を返す。
     * @return データの文字列表現
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<data.length; i++) {
            builder.append(String.format("%02x", data[i]));
        }
        return builder.toString();
    }
    
    /**
     * このDataのハッシュコードを返す。
     * @return このEOJのハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Arrays.hashCode(this.data);
        return hash;
    }
    
    /**
     * このDataが指定されたオブジェクトと等しいかどうか調べる。
     * 拡張データ領域も等しいかどうか調べる。
     * @param otherObj 比較されるオブジェクト
     * @return オブジェクトが等しい場合にはtrue、そうでない場合にはfalse
     */
    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Data)) {
            return false;
        }
        
        Data other = (Data)otherObj;
        return Arrays.equals(data, other.data);
    }
}
