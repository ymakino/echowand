package echowand.util;

import echowand.util.ConstraintSize;

/**
 * 2バイトのプロパティデータ制約を表現する。
 * @author Yoshiki Makino
 */
public class ConstraintShort extends ConstraintSize {
    private boolean isSigned;
    private int minValue;
    private int maxValue;
    
    private int U(short s) {
        return 0x0000ffff & (s);
    }
    
    private short S(byte[] data) {
        int h = ((int)data[0]) << 8;
        int l = 0x000000ff & (int)data[1];
        return (short)(h | l);
    }
    
    /**
     * ConstraintShortを生成する。
     */
    public ConstraintShort() {
        this((short)0x0000, (short)0xffff);
    }
    
    /**
     * ConstraintShortを生成する。
     * @param value プロパティ値
     */
    public ConstraintShort(short value) {
        this(value, value);
    }
    
    /**
     * ConstraintShortを生成する。
     * @param minValue プロパティ値の最小値
     * @param maxValue プロパティ値の最大値
     */
    public ConstraintShort(short minValue, short maxValue) {
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
    public boolean isValid(byte[] data) {
        if (!super.isValid(data)) {
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
    public String toString() {
        return String.format("Short[0x%04x, 0x%04x]", minValue, maxValue);
    }
}
