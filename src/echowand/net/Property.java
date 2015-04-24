package echowand.net;

import echowand.common.Data;
import echowand.common.EPC;

/**
 * ペイロードのプロパティ
 * @author Yoshiki Makino
 */
public class Property {
    private EPC epc;
    private Data edt;
    
    /**
     * Propertyを生成する。
     * EPCはInvalidとして初期化される。
     */
    public Property() {
        this(EPC.Invalid);
    }
    
    /**
     * データの存在しない指定されたEPCのPropertyを生成する。
     * @param epc プロパティのEPC
     */
    public Property(EPC epc) {
        this(epc, new Data());
    }
    
    /**
     * 指定されたEPCとデータのPropertyを生成する。
     * @param epc プロパティのEPC
     * @param data プロパティのデータ
     */
    public Property(EPC epc, Data data) {
        this.epc = epc;
        this.edt = data;
    }
    
    /**
     * 指定されたバイト配列からPropertyを生成する。
     * @param bytes プロパティを含むバイト配列
     */
    public Property(byte[] bytes) {
        this(bytes, 0);
    }
    
    /**
     * 指定されたバイト配列の指定されたオフセットからPropertyを生成する。
     * @param bytes プロパティを含むバイト配列
     * @param offset バイト配列のオフセット
     */
    public Property(byte[] bytes, int offset) {
        this.epc = EPC.fromByte(bytes[offset]);
        int pdc = 0xff & (int)bytes[offset+1];
        this.edt = new Data(bytes, offset+2, pdc);
    }
    
    /**
     * EPCを設定する。
     * @param epc 設定するEPC
     */
    public void setEPC(EPC epc) {
        this.epc = epc;
    }
    
    /**
     * 設定されたEPCを返す。
     * @return 設定されたEPC
     */
    public EPC getEPC() {
        return epc;
    }
    
    /**
     * プロパティのデータ長(PDC)を返す。
     * @return プロパティのデータ長
     */
    public byte getPDC() {
        if (this.edt == null) {
            return 0;
        } else {
            return (byte)edt.size();
        }
    }
    
    /**
     * データを設定する。
     * @param edt 設定するデータ
     */
    public void setEDT(Data edt) {
        this.edt = edt;
    }
    
    /**
     * 設定されたデータを返す。
     * @return 設定されたデータ
     */
    public Data getEDT() {
        return edt;
    }
    
    /**
     * このPropertyをバイト配列で表現したものを返す。
     * @return プロパティのバイト配列表現
     */
    public byte[] toBytes() {
        int pdc = 0xff & (int)getPDC();
        byte[] bytes = new byte[2 + pdc];
        bytes[0] = epc.toByte();
        bytes[1] = getPDC();
        if (pdc > 0) {
            edt.copyBytes(0, bytes, 2, pdc);
        }
        return bytes;
    }
    
    /**
     * このPropertyをバイト配列に変換したときの長さを返す。
     * @return バイト配列の長さ
     */
    public int size() {
        return 2 + (0xff & (int)getPDC());
    }
    
    /**
     * このPropertyの文字列表現を返す。
     * @return このPropertyの文字列表現
     */
    @Override
    public String toString() {
        if (getPDC() == 0) {
            String format = "EPC=%02x PDC=%02x";
            return String.format(format, epc.toByte(), getPDC());
        } else {
            String format = "EPC=%02x PDC=%02x EDT=%s";
            return String.format(format, epc.toByte(), getPDC(), getEDT().toString());
        }
    }
}
