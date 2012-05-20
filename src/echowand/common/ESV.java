package echowand.common;

/**
 * ESVを表現する列挙型
 * @author Yoshiki Makino
 */
public enum ESV {
    /**
     * 不適切なESVを表す。
     */
    Invalid((byte)0x00),
    
    /**
     * プロパティ値書き込み(応答不要)のESV(0x60)を表す。
     */
    SetI((byte)0x60),
    
    /**
     * プロパティ値書き込み(応答要)のESV(0x61)を表す。
     */
    SetC((byte)0x61),
    
    /**
     * プロパティ値読み出しのESV(0x62)を表す。
     */
    Get((byte)0x62),
    
    /**
     * プロパティ値通知要求のESV(0x63)を表す。
     */
    INF_REQ((byte)0x63),
    
    /**
     * プロパティ値書き込み・読み出しのESV(0x6E)を表す。
     */
    SetGet((byte)0x6e),
    
    /**
     * プロパティ値書き込み応答のESV(0x71)を表す。
     */
    Set_Res((byte)0x71),
    
    /**
     * プロパティ値読み出し応答のESV(0x72)を表す。
     */
    Get_Res((byte)0x72),
    
    /**
     * プロパティ値通知のESV(0x73)を表す。
     */
    INF((byte)0x73),
    
    /**
     * プロパティ値通知(応答要)のESV(0x74)を表す。
     */
    INFC((byte)0x74),
    
    /**
     * プロパティ値通知応答のESV(0x7A)を表す。
     */
    INFC_Res((byte)0x7a),
    
    /**
     * プロパティ値書き込み・読み出しのESV(0x7E)を表す。
     */
    SetGet_Res((byte)0x7e),
    
    /**
     * プロパティ値書き込み要求不可応答のESV(0x50)を表す。
     */
    SetI_SNA((byte)0x50),
    
    /**
     * プロパティ値書き込み要求不可応答のESV(0x51)を表す。
     */
    SetC_SNA((byte)0x51),
    
    /**
     * プロパティ値読み出し不可応答のESV(0x52)を表す。
     */
    Get_SNA((byte)0x52),
    
    /**
     * プロパティ値通知不可応答のESV(0x53)を表す。
     */
    INF_SNA((byte)0x53),
    
    /**
     * プロパティ値書き込み・読み出し不可応答のESV(0x5E)を表す。
     */
    SetGet_SNA((byte)0x5E);
    
    /**
     * ESVのバイト表現を返す
     * @return ESVのバイト表現
     */
    public byte toByte() {
        return code;
    }
    
    /**
     * このESVがINFに関するものであるか示す。
     * @return INFに関するものであればtrue、そうでなければfalse
     */
    public boolean isInfo() {
        switch (this) {
            case INF_REQ:
            case INF:
            case INFC:
            case INFC_Res:
            case INF_SNA:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * このESVがSetGetに関するものであるか示す。
     * @return SetGetに関するものであればtrue、そうでなければfalse
     */
    public boolean isSetGet() {
        switch (this) {
            case SetGet:
            case SetGet_Res:
            case SetGet_SNA:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * このESVがInvalidであるか示す。
     * @return Invalidであればtrue、そうでなければfalse
     */
    public boolean isInvalid() {
        return this == Invalid;
    }
    
    private ESV(byte code) {
        this.code = code;
    }
    
    private byte code;
    
    /**
     * 与えられたバイトに対応するESVを返す。
     * 適切なESVがない場合にはInvalidを返す。
     * @param code ESVのコード
     * @return 指定されたコードに対応するESV
     */
    public static ESV fromByte(byte code) {
        for (ESV esv : ESV.values()) {
            if (esv.code == code) {
                return esv;
            }
        }
        return Invalid;
    }
}
