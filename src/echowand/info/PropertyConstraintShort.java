package echowand.info;

/**
 * 2バイトのプロパティデータ制約を表現する。
 * @author Yoshiki Makino
 */
public class PropertyConstraintShort extends PropertyConstraintSize {
    private boolean isSigned;
    private int minValue;
    private int maxValue;
    
    private int U(short s) {
        return 0x0000ffff & (s);
    }
    
    private short S(byte[] data) {
        int l = 0x000000ff & (int)data[1];
        int h = ((int)data[0]) << 8;
        return (short)(h | l);
    }
    
    /**
     * PropertyConstraintShortを生成する。
     */
    public PropertyConstraintShort() {
        this((short)0x0000, (short)0xffff);
    }
    
    /**
     * PropertyConstraintShortを生成する。
     * @param minValue プロパティ値の最小値
     * @param maxValue プロパティ値の最大値
     */
    public PropertyConstraintShort(short minValue, short maxValue) {
        super(2);
        if (minValue <= maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            isSigned = true;
        } else {
            this.minValue = U(minValue);
            this.maxValue = U(maxValue);
            isSigned = false;
        }
    }
    
    @Override
    public boolean isAcceptable(byte[] data) {
        if (!super.isAcceptable(data)) {
            return false;
        }
        
        int value;
        if (isSigned) {
            value = S(data);
        } else {
            value = U(S(data));
        }
        return (minValue <= value) && (value <= maxValue);
    }
    
    @Override
    public byte[] getInitialData() {
        byte h = (byte)(0xff & (minValue>>8));
        byte l = (byte)(0xff & minValue);
        return new byte[]{h, l};
    }
}
