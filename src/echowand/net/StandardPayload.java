package echowand.net;

import echowand.common.EOJ;
import echowand.common.ESV;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;

/**
 * 標準ペイロード
 * @author Yoshiki Makino
 */
public class StandardPayload implements Payload {
    private EOJ seoj;
    private EOJ deoj;
    private ESV esv;
    private LinkedList<Property> firstProperties;
    private LinkedList<Property> secondProperties;
    
    /**
     * StandardPayloadを生成する。
     */
    public StandardPayload() {
        firstProperties = new LinkedList<Property>();
        secondProperties = new LinkedList<Property>();
    }
    
    /**
     * 指定されたSEOJ、DEOJ、ESVでStandardPayloadを生成する。
     * @param seoj 送信EOJ
     * @param deoj 宛先EOJ
     * @param esv ペイロードのESV
     */
    public StandardPayload(EOJ seoj, EOJ deoj, ESV esv) {
        this();
        this.seoj = seoj;
        this.deoj = deoj;
        this.esv = esv;
    }
    
    /**
     * 指定されたバイト配列を解析してStandardPayloadを生成する。
     * @param bytes ペイロードのバイト配列
     * @throws InvalidDataException 解析に失敗した場合
     */
    public StandardPayload(byte[] bytes) throws InvalidDataException {
        this();
        if (bytes != null) {
            parse(bytes, 0);
        }
    }
    
    /**
     * 指定されたバイト配列の指定されたオフセットから解析してStandardPayloadを生成する。
     * @param bytes ペイロードを含むバイト配列
     * @param offset バイト配列のオフセット
     * @throws InvalidDataException 解析に失敗した場合
     */
    public StandardPayload(byte[] bytes, int offset) throws InvalidDataException {
        this();
        if (bytes != null) {
            parse(bytes, offset);
        }
    }
    
    private int parse(byte[] bytes, int offset) throws InvalidDataException {
        try {
            this.seoj = new EOJ(bytes, offset);
            offset += 3;
            this.deoj = new EOJ(bytes, offset);
            offset += 3;
            this.esv = ESV.fromByte(bytes[offset++]);
            if (bytes.length > offset) {
                offset = parseProperties(firstProperties, bytes, offset);
            }
            if (esv.isSetGet()) {
                offset = parseProperties(secondProperties, bytes, offset);
            }
            return offset;
        } catch (Exception e) {
            throw new InvalidDataException("invalid data at: " + offset, e);
        }
    }
    
    private int parseProperties(LinkedList<Property> properties, byte[] bytes, int offset) {
        int len = 0xff & (int)bytes[offset++];
        for (int i=0; i<len; i++) {
            Property property = new Property(bytes, offset);
            properties.add(property);
            offset += 2 + (0xff & property.getPDC());
        }
        return offset;
    }
    
    /**
     * 送信EOJを設定する。
     * @param seoj 設定する送信EOJ
     */
    public void setSEOJ(EOJ seoj) {
        this.seoj = seoj;
    }
    
    /**
     * 設定された送信EOJを返す。
     * @return 設定された送信EOJ
     */
    public EOJ getSEOJ() {
        return this.seoj;
    }
    
    /**
     * 宛先EOJを設定する。
     * @param deoj 設定する宛先EOJ
     */
    public void setDEOJ(EOJ deoj) {
        this.deoj = deoj;
    }
    
    /**
     * 設定された宛先EOJを返す。
     * @return 設定された宛先EOJ
     */
    public EOJ getDEOJ() {
        return this.deoj;
    }
    
    /**
     * ペイロードのESVを設定する。
     * @param esv 設定するESV
     */
    public void setESV(ESV esv) {
        this.esv = esv;
    }
    
    /**
     * 設定されたESVを返す。
     * @return 設定されたESV
     */
    public ESV getESV() {
        return this.esv;
    }
    
    private boolean addProperty(LinkedList<Property> properties, Property property) {
        if (properties.size() < 0xff) {
            return properties.add(property);
        } else {
            return false;
        }
    }
    
    /**
     * プロパティを追加する(SetGetのGetを除く)。
     * @param property 追加するプロパティ
     * @return 追加に成功したときtrue、そうでなければfalse
     */
    public boolean addFirstProperty(Property property) {
        return addProperty(firstProperties, property);
    }
    
    /**
     * index番目のプロパティを返す(SetGetのGetを除く)
     * @param index プロパティのインデックス
     * @return 指定されたプロパティ
     */
    public Property getFirstPropertyAt(int index) {
        return firstProperties.get(index);
    }
    
    /**
     * プロパティ数をバイトで返す(SetGetのGetを除く)。
     * @return プロパティ数
     */
    public byte getFirstOPC() {
        return (byte)firstProperties.size();
    }
    
    /**
     * SetGetのGetのためにプロパティを追加する。
     * @param property 追加するプロパティ
     * @return 追加に成功したときtrue、そうでなければfalse
     */
    public boolean addSecondProperty(Property property) {
        return addProperty(secondProperties, property);
    }
    
    /**
     * SetGetのGetのindex番目のプロパティを返す。
     * @param index プロパティのインデックス
     * @return 指定されたプロパティ
     */
    public Property getSecondPropertyAt(int index) {
        return secondProperties.get(index);
    }
    
    /**
     * SetGetのGetのプロパティ数をバイトで返す。
     * @return プロパティ数
     */
    public byte getSecondOPC() {
        return (byte)secondProperties.size();
    }
    
    private int propertiesLength(Collection<Property> properties) {
        int len = 1;
        for (Property p : properties) {
            len += p.size();
        }
        return len;
    }

    private byte[] propertiesBytes(Collection<Property> properties) {
        int len = propertiesLength(properties);
        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.put((byte) properties.size());
        for (Property p : properties) {
            buffer.put(p.toBytes());
        }
        return buffer.array();
    }
    
    /**
     * このStandardPayloadをバイト配列で表現したときの長さを返す。
     * @return バイト配列の長さ
     */
    @Override
    public int size() {
        int len = 7;
        len += propertiesLength(firstProperties);
        if (esv.isSetGet()) {
            len += propertiesLength(secondProperties);
        }
        return len;
    }
    
    /**
     * このStandardPayloadのバイト配列表現を返す。
     * @return このSimplePayloadのバイト配列
     */
    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(size());
        buffer.put(seoj.toBytes());
        buffer.put(deoj.toBytes());
        buffer.put(esv.toByte());
        buffer.put(propertiesBytes(firstProperties));
        if (esv.isSetGet()) {
            buffer.put(propertiesBytes(secondProperties));
        }
        return buffer.array();
    }
    
    private String propertiesToString(LinkedList<Property> properties) {
        StringBuilder builder = new StringBuilder();
        for (Property property : properties) {
            builder.append("(");
            builder.append(property);
            builder.append(")");
        }
        return builder.toString();
    }

    
    /**
     * このStandardPayloadの文字列表現を返す。
     * @return このSimplePayloadの文字列表現
     */
    @Override
    public String toString() {
        String format = "SEOJ: %s DEOJ: %s ESV: %s %s";
        
        String propList1 = propertiesToString(firstProperties);
        
        String propString;
        if (esv.isSetGet()) {
            String propList2 = propertiesToString(secondProperties);
            String propFormat = "OPC1=%02x [%s] OPC2=%02x [%s]";
            propString = String.format(propFormat, getFirstOPC(), propList1, getSecondOPC(), propList2);
        } else {
            String propFormat = "OPC=%02x [%s]";
            propString = String.format(propFormat, getFirstOPC(), propList1);
        }
        return String.format(format, seoj, deoj, esv, propString);
    }
}
