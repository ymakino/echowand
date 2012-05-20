package echowand.common;

/**
 * EPCを表す列挙型
 * @author Yoshiki Makino
 */
public enum EPC {
    /**
     * 不適切なEPC
     */
    Invalid(0x00),
    
    // How to generate the EPC list is as below.
    //
    // $ ruby << EOF
    // (0x80..0xff).each do |i|
    //   print(if (i % 4) == 0 then "    " else "" end)
    //   print(" x%02X(0x%02x)" % [i,i])
    //   print(if i != 0xff then "," else ";" end)
    //   print(if (i % 4) == 3 then "\n" else "" end)
    // end
    // EOF
    
     x80(0x80), x81(0x81), x82(0x82), x83(0x83),
     x84(0x84), x85(0x85), x86(0x86), x87(0x87),
     x88(0x88), x89(0x89), x8A(0x8a), x8B(0x8b),
     x8C(0x8c), x8D(0x8d), x8E(0x8e), x8F(0x8f),
     x90(0x90), x91(0x91), x92(0x92), x93(0x93),
     x94(0x94), x95(0x95), x96(0x96), x97(0x97),
     x98(0x98), x99(0x99), x9A(0x9a), x9B(0x9b),
     x9C(0x9c), x9D(0x9d), x9E(0x9e), x9F(0x9f),
     xA0(0xa0), xA1(0xa1), xA2(0xa2), xA3(0xa3),
     xA4(0xa4), xA5(0xa5), xA6(0xa6), xA7(0xa7),
     xA8(0xa8), xA9(0xa9), xAA(0xaa), xAB(0xab),
     xAC(0xac), xAD(0xad), xAE(0xae), xAF(0xaf),
     xB0(0xb0), xB1(0xb1), xB2(0xb2), xB3(0xb3),
     xB4(0xb4), xB5(0xb5), xB6(0xb6), xB7(0xb7),
     xB8(0xb8), xB9(0xb9), xBA(0xba), xBB(0xbb),
     xBC(0xbc), xBD(0xbd), xBE(0xbe), xBF(0xbf),
     xC0(0xc0), xC1(0xc1), xC2(0xc2), xC3(0xc3),
     xC4(0xc4), xC5(0xc5), xC6(0xc6), xC7(0xc7),
     xC8(0xc8), xC9(0xc9), xCA(0xca), xCB(0xcb),
     xCC(0xcc), xCD(0xcd), xCE(0xce), xCF(0xcf),
     xD0(0xd0), xD1(0xd1), xD2(0xd2), xD3(0xd3),
     xD4(0xd4), xD5(0xd5), xD6(0xd6), xD7(0xd7),
     xD8(0xd8), xD9(0xd9), xDA(0xda), xDB(0xdb),
     xDC(0xdc), xDD(0xdd), xDE(0xde), xDF(0xdf),
     xE0(0xe0), xE1(0xe1), xE2(0xe2), xE3(0xe3),
     xE4(0xe4), xE5(0xe5), xE6(0xe6), xE7(0xe7),
     xE8(0xe8), xE9(0xe9), xEA(0xea), xEB(0xeb),
     xEC(0xec), xED(0xed), xEE(0xee), xEF(0xef),
     xF0(0xf0), xF1(0xf1), xF2(0xf2), xF3(0xf3),
     xF4(0xf4), xF5(0xf5), xF6(0xf6), xF7(0xf7),
     xF8(0xf8), xF9(0xf9), xFA(0xfa), xFB(0xfb),
     xFC(0xfc), xFD(0xfd), xFE(0xfe), xFF(0xff);
    
    private byte code;
    private EPC(int code) {
        this.code = (byte)code;
    }
    
    /**
     * このEPCがInvalidであるか調べる。
     * @return このEPCがInvalidであればtrue、そうでなければfalse
     */
    public boolean isInvalid() {
        return this == Invalid;
    }
    
    /**
     * このEPCをバイトに変換する。
     * @return このEPCのバイト表現
     */
    public byte toByte() {
        return code;
    }
    
    /**
     * バイトからEPCに変換する。バイトが不適な場合にはInvalidを返す。
     * @param code EPCのコード
     * @return 指定されたコードに対応するEPC
     */
    public static EPC fromByte(byte code) {
        for (EPC epc : EPC.values()) {
            if (epc.code == code) {
                return epc;
            }
        }
        return Invalid;
    }
}