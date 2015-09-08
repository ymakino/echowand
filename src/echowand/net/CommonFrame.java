package echowand.net;

import echowand.common.EOJ;
import echowand.common.ESV;
import java.nio.ByteBuffer;

/**
 * 下位ネットワーク層に依存しない共通フレーム
 * @author Yoshiki Makino
 */
public class CommonFrame {
    private byte ehd1;
    private byte ehd2;
    private short tid;
    private Payload edata;
    
    /**
     * ECHONET Liteである事を表す共通フレームの1バイト目
     */
    public static final byte EHD1_ECHONET_LITE=(byte)0x10;
    
    /**
     * 標準ペイロード形式である事を表す共通フレームの2バイト目
     */
    public static final byte EHD2_STANDARD_PAYLOAD=(byte)0x81;
    
    /**
     * 任意ペイロード形式である事を表す共通フレームの2バイト目
     */
    public static final byte EHD2_ARBITRARY_PAYLOAD=(byte)0x82;
   
    /**
     * CommonFrameを生成する。
     * EDATAは生成されないため、setPayloadメソッドで追加する必要がある。
     */
    public CommonFrame() {
        ehd1 = EHD1_ECHONET_LITE;
        ehd2 = EHD2_ARBITRARY_PAYLOAD;
        tid = (short) 0x0000;
        edata = new SimplePayload();
    }
    
    /**
     * CommonFrameを生成する。
     * 指定されたSEOJ、DEOJ、ESVで初期化されたEDATAも作成される。
     * @param seoj 送信EOJ
     * @param deoj 宛先EOJ
     * @param esv ペイロードのESV
     */
    public CommonFrame(EOJ seoj, EOJ deoj, ESV esv) {
        ehd1 = EHD1_ECHONET_LITE;
        ehd2 = EHD2_STANDARD_PAYLOAD;
        tid = (short) 0x0000;
        edata = new StandardPayload(seoj, deoj, esv);
    }
    
    /**
     * CommonFrameを生成する。
     * 指定されたバイト配列の中身をフレームとみなし初期化を行う。
     * @param bytes フレームのバイト配列
     * @throws InvalidDataException バイト配列の解析に失敗した場合
     */
    public CommonFrame(byte[] bytes) throws InvalidDataException {
        int offset = 0;
        try {
            this.ehd1 = bytes[offset++];
            this.ehd2 = bytes[offset++];
            this.tid = (short) ((0xff & (int) bytes[offset++]) << 8);
            this.tid |= (short) (0xff & (int) bytes[offset++]);
            if (this.ehd2 == EHD2_STANDARD_PAYLOAD) {
                this.edata = new StandardPayload(bytes, offset);
            } else {
                this.edata = new SimplePayload(bytes, offset);
            }
        } catch (Exception e) {
            throw new InvalidDataException("invalid data at: " + offset, e);
        }
    }
    
    /**
     * このCommonFrameの1バイト目を返す。
     * @return このCommonFrameの1バイト目
     */
    public byte getEHD1() {
        return ehd1;
    }
    
    /**
     * このCommonFrameの2バイト目を返す。
     * @return このCommonFrameの2バイト目
     */
    public byte getEHD2() {
        return ehd2;
    }
    
    /**
     * このCommonFrameのトランザクションID(TID)を返す。
     * @return  このCommonFrameのTID
     */
    public short getTID() {
        return tid;
    }
    
    
    /**
     * このCommonFrameのトランザクションID(TID)を設定する。
     * @param tid 設定するTID
     */
    public void setTID(short tid) {
        this.tid = tid;
    }
    
    /**
     * このCommonFrameのペイロードを返す。
     * @return このCommonFrameのペイロード
     */
    public Payload getEDATA() {
        return edata;
    }
    
    /**
     * ペイロードの型を指定してCommonFrameのペイロードを返す。
     * ペイロードを指定された型に変換できない場合にはnullを返す。
     * @param <P> 取得するペイロードの型
     * @param cls ペイロードの型の指定
     * @return このCommonFrameのペイロード
     */
    public <P extends Payload> P getEDATA(Class<P> cls) {
        try {
            return cls.cast(edata);
        } catch (ClassCastException ex) {
            return null;
        }
    }
    
    /**
     * このCommonFrameのペイロードを設定する。
     * @param payload 設定するペイロード
     */
    public void setEDATA(Payload payload) {
        if (payload instanceof StandardPayload) {
            ehd2 = EHD2_STANDARD_PAYLOAD;
        } else {
            ehd2 = EHD2_ARBITRARY_PAYLOAD;
        }
        this.edata = payload;
    }
    
    /**
     * このCommonFrameがECHONET Liteであるか返す。
     * @return ECHONET Liteのフレームであればtrue、そうでなければfalse
     */
    public boolean isEchonetLite() {
        return (ehd1 == EHD1_ECHONET_LITE);
    }
    
    /**
     * このCommonFrameのペイロードが標準形式であるか返す。
     * @return 標準形式であればtrue、そうでなければfalse
     */
    public boolean isStandardPayload() {
        return (ehd2 == EHD2_STANDARD_PAYLOAD);
    }
    
    /**
     * このCommonFrameをフレームとして正式なバイト配列に変換する。
     * @return バイト配列に変換されたCommonFrame
     */
    public byte[] toBytes() {
        int len = 4;
        if (edata != null) {
            len += edata.size();
        }
        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.put(ehd1);
        buffer.put(ehd2);
        buffer.putShort(tid);
        if (edata != null) {
            buffer.put(edata.toBytes());
        }
        return buffer.array();
    }
    
    /**
     * このCommonFrameを文字列で表す。
     * @return CommonFrameを表現する文字列
     */
    @Override
    public String toString() {
        String format = "EHD1=%02x EHD2=%02x TID=%04x EDATA=[%s]";
        return String.format(format, ehd1, ehd2, tid, edata);
    }
}
