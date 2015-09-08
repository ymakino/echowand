package echowand.common;

/**
 * EOJの表現クラス
 * @author Yoshiki Makino
 */
public class EOJ {
    private ClassEOJ classEOJ;
    private byte instanceCode;
    
    /**
     * クラスグループコード、クラスコード、インスタンスコードを指定してEOJオブジェクトを生成する。
     * @param classGroupCode クラスグループコード
     * @param classCode クラスコード
     * @param instanceCode インスタンスコード
     */
    public EOJ(byte classGroupCode, byte classCode, byte instanceCode) {
            this.classEOJ = new ClassEOJ(classGroupCode, classCode);
            this.instanceCode = instanceCode;
    }
    
    /**
     * 
     * EOJを表現する文字列からEOJオブジェクトを生成する。
     * @param eoj EOJを表現する文字列
     * @throws IllegalArgumentException eojの解析に失敗
     */
    public EOJ(String eoj) throws IllegalArgumentException {
        if (eoj.length() != 6) {
            throw new IllegalArgumentException("Invalid EOJ: " + eoj);
        }
        try {
            this.classEOJ = new ClassEOJ(eoj.substring(0,4));
            this.instanceCode = (byte) Integer.parseInt(eoj.substring(4, 6), 16);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid EOJ: " + eoj, ex);
        }
    }
        
    /**
     * 
     * EOJを表現するバイト列からEOJオブジェクトを生成する。バイト配列の長さは3以上でなければならない。
     * @param bytes EOJを表現するバイト列
     */
    public EOJ(byte[] bytes) {
        this(bytes, 0);
    }
    
    /**
     * EOJを表現するバイト列からEOJオブジェクトを生成する。
     * この時、バイト列はoffsetにより指定された要素以降の3バイトを利用する。
     * @param bytes EOJを表現するバイト列
     * @param offset bytesのEOJ表現までのオフセット
     */
    public EOJ(byte[] bytes, int offset) {
        this.classEOJ = new ClassEOJ(bytes[offset], bytes[offset+1]);
        this.instanceCode = bytes[offset+2];
    }
    
    /**
     * クラスグループコードを返す。
     * @return クラスグループコード
     */
    public byte getClassGroupCode() {
        return this.classEOJ.getClassGroupCode();
        
    }
    
    /**
     * クラスコードを返す。
     * @return クラスコード
     */
    public byte getClassCode() {
        return this.classEOJ.getClassCode();
    }
    
    /**
     * インスタンスコードを返す。
     * @return インスタンスコード
     */
    public byte getInstanceCode() {
        return this.instanceCode;
    }
    
    /**
     * このEOJのクラスグループコードとクラスコードを表すClassEOJを生成する。
     * @return このEOJの暮らすグループとクラスコードより生成されたClassEOJ
     */
    public ClassEOJ getClassEOJ() {
        return classEOJ;
    }
    
    /**
     * 異なるインスタンスコードを持ったEOJを生成する。
     * @param newInstanceCode 生成するEOJに与えるインスタンスコード
     * @return 新たに生成されたEOJ
     */
    public EOJ getEOJWithInstanceCode(byte newInstanceCode) {
        return classEOJ.getEOJWithInstanceCode(newInstanceCode);
    }
    
    /**
     * このEOJのクラスグループコードとクラスコードを用いて、全インスタンス対象のEOJを生成する。
     * @return 全インスタンス対象のEOJオブジェクト
     */
    public EOJ getAllInstanceEOJ() {
        return getEOJWithInstanceCode((byte)0x00);
    }
    
    /**
     * このEOJが指定されたClassEOJのクラスグループとクラスに含まれるか調べる。
     * @param ceoj クラスグループとクラスを指定するためのClassEOJ
     * @return 指定されたClassEOJに含まれるのであればtrue、そうでなければfalse
     */
    public boolean isMemberOf(ClassEOJ ceoj) {
        return getClassEOJ().equals(ceoj);
    }
    
    /**
     * このEOJが全インスタンス対象のEOJであるかどうか調べる。インスタンスコードが0x00のEOJは前インスタンス対象のEOJである。
     * @return このEOJが全インスタンス対象であればtrue、そうでなければfalse
     */
    public boolean isAllInstance() {
        return instanceCode == 0x00;
    }
    
    /**
     * このEOJが機器オブジェクトを示す物であるか調べる。
     * @return このEOJが機器オブジェクトであればtrue、そうでなければfalse
     */
    public boolean isDeviceObject() {
        return getClassEOJ().isDeviceObject();
    }
    
    /**
     * このEOJがプロファイルオブジェクトを示す物であるか調べる。
     * @return このEOJがプロファイルオブジェクトであればtrue、そうでなければfalse
     */
    public boolean isProfileObject() {
        return getClassEOJ().isProfileObject();
    }
    
    /**
     * このEOJがノードプロファイルオブジェクトであるか調べる。
     * @return このClassEOJがノードプロファイルオブジェクトであればtrue、そうでなければfalse
     */
    public boolean isNodeProfileObject() {
        return getClassEOJ().isNodeProfileObject();
    }
    
    /**
     * このEOJを整数で表現した値を返す。
     * @return このEOJの整数表現
     */
    public int intValue() {
        return (classEOJ.intValue() << 8) | (0xff & (int)instanceCode);
    }
    
    /**
     * このEOJをバイト配列で表現したものを返す。
     * 長さ3のバイト配列で、先頭からクラスグループコード、クラスコード、インスタンスコードが順番に格納される。
     * @return EOJのバイト配列表現
     */
    public byte[] toBytes() {
        byte[] bytes = new byte[3];
        bytes[0] = classEOJ.getClassGroupCode();
        bytes[1] = classEOJ.getClassCode();
        bytes[2] = instanceCode;
        return bytes;
    }
    
    /**
     * このEOJを16進数で表現し、6文字の文字列に変換したものを返す。
     * @return EOJの文字列表現
     */
    @Override
    public String toString() {
        return String.format("%06x", intValue());
    }
    
    /**
     * このEOJが指定されたオブジェクトと等しいかどうか調べる。
     * @param otherObj 比較されるオブジェクト
     * @return オブジェクトが等しい場合にはtrue、そうでない場合にはfalse
     */
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof EOJ) {
            EOJ other = (EOJ)otherObj;
            return     this.classEOJ.equals(other.classEOJ)
                    && this.getInstanceCode() == other.getInstanceCode();
        }
        return false;
    }
    
    /**
     * このEOJのハッシュコードを返す。
     * @return このEOJのハッシュコード
     */
    @Override
    public int hashCode() {
        return intValue();
    }
}
