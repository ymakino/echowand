package echowand.common;

/**
 * EOJのクラスグループとクラスの表現クラス
 * @author Yoshiki Makino
 */
public class ClassEOJ {

    private byte classGroupCode;
    private byte classCode;
    private static ClassEOJ nodeClassEOJ = new ClassEOJ((byte) 0x0E, (byte) 0xF0);
    private static byte[] deviceCodes = new byte[]{(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06};
    private static byte profileCode = (byte) 0x0e;

    /**
     * 指定されたクラスグループコードとクラスコードを用いてClassEOJを生成する。
     * @param classGroupCode クラスグループコード
     * @param classCode クラスコード
     */
    public ClassEOJ(byte classGroupCode, byte classCode) {
        this.classGroupCode = classGroupCode;
        this.classCode = classCode;
    }
    
    /**
     * 指定された文字列を用いてClassEOJを生成する。
     * ceojは4文字の文字列で各文字は[0-9a-fA-F]でなければならない。
     * @param ceoj クラスグループコードとクラスコードを示す文字列
     * @exception IllegalArgumentException ceojの解析に失敗した場合
     */
    public ClassEOJ(String ceoj) throws IllegalArgumentException {
        if (ceoj.length() != 4) {
            throw new IllegalArgumentException("Invalid ClassEOJ: " + ceoj);
        }
        try {
            this.classGroupCode = (byte) Integer.parseInt(ceoj.substring(0, 2), 16);
            this.classCode = (byte) Integer.parseInt(ceoj.substring(2, 4), 16);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid ClassEOJ: " + ceoj, ex);
        }
    }
    
    /**
     * クラスグループコードを返す。
     * @return クラスグループコード
     */
    public byte getClassGroupCode() {
        return this.classGroupCode;
        
    }
    
    /**
     * クラスコードを返す。
     * @return クラスコード
     */
    public byte getClassCode() {
        return this.classCode;
    }
        
    /**
     * クラスグループコードとクラスコードを長さ2バイトのバイト配列として返す。
     * @return クラスグループコードとクラスコードのバイト配列
     */
    public byte[] toBytes() {
        byte[] bytes = new byte[2];
        bytes[0] = classGroupCode;
        bytes[1] = classCode;
        return bytes;
    }
    
    /**
     * クラスがノードプロファイルオブジェクトであるか調べる。
     * @return このClassEOJがノードプロファイルオブジェクトであればtrue、そうでなければfalse
     */
    public boolean isNodeProfileObject() {
        return this.equals(nodeClassEOJ);
    }
    
    /**
     * このClassEOJのクラスグループコードとクラスコードを用いて、新たにEOJオブジェクトを生成する。
     * @param instanceCode 生成したいEOJのインスタンスコード
     * @return 指定されたインスタンスコードを持ったEOJオブジェクト
     */
    public EOJ getEOJWithInstanceCode(byte instanceCode) {
        return new EOJ(classGroupCode, classCode, instanceCode);
    }
    
    /**
     * このClassEOJのクラスグループコードとクラスコードを用いて、全インスタンス対象のEOJを生成する。
     * @return 全インスタンス対象のEOJオブジェクト
     */
    public EOJ getAllInstanceEOJ() {
        return getEOJWithInstanceCode((byte)0x00);
    }
    
    
    /**
     * このClassEOJが機器オブジェクトであるか調べる。
     * @return このClassEOJが機器オブジェクトであればtrue、そうでなければfalse
     */
    public boolean isDeviceObject() {
        for (byte code: deviceCodes) {
            if (code == classGroupCode) {
                return true;
            }
        }
        return false;
    }

    /**
     * クラスがプロファイルオブジェクトであるか調べる。
     *
     * @return クラスがプロファイルオブジェクトであればtrue、そうでなければfalse
     */
    public boolean isProfileObject() {
        return profileCode == classGroupCode;
    }
    
    /**
     * クラスグループコードとクラスコードを整数に変換して返す。
     *
     * @return クラスグループコードとクラスコードの整数表現
     */
    public int intValue() {
        return ((0xff & (int)classGroupCode) << 8)
                | (0xff & (int)classCode);
    }
    
    
    /**
     * クラスグループコードとクラスコードを4桁の16進数文字列に変換して返す。
     *
     * @return 4文字の16進数文字列
     */
    @Override
    public String toString() {
        return String.format("%04x", intValue());
    }
    
    
    
    /**
     * このClassEOJと指定されたオブジェクトを比較する。
     * @param otherObj 比較されるオブジェクト
     * @return 指定されたオブジェクトがこのClassEOJと等しい場合にtrue、そうでない場合はfalse
     */
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof ClassEOJ) {
            ClassEOJ other = (ClassEOJ)otherObj;
            return     this.getClassGroupCode() == other.getClassGroupCode()
                    && this.getClassCode() == other.getClassCode();
        }
        return false;
    }
    
    
    /**
     * このClassEOJのハッシュコードを返す。
     * @return このClassEOJのハッシュコード
     */
    @Override
    public int hashCode() {
        return intValue();
    }
}
