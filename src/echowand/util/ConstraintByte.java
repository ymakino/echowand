package echowand.util;

import echowand.util.ConstraintSize;

/**
 * 1バイトのプロパティデータ制約を表現する。
 * @author Yoshiki Makino
 */
public class ConstraintByte extends ConstraintSize {
    private boolean isSigned;
    private int minValue;
    private int maxValue;
    
    private int U(Byte b) {
        return 0x000000ff & (b);
    }
    
    /**
     * ConstraintByteを生成する。
     */
    public ConstraintByte() {
        this((byte)0, (byte)0xff);
    }
    
    /**
     * ConstraintByteを生成する。
     * @param value プロパティ値
     */
    public ConstraintByte(byte value) {
        this(value, value);
    }
    
    /**
     * ConstraintByteを生成する。
     * @param minValue プロパティ値の最小値
     * @param maxValue プロパティ値の最大値
     */
    public ConstraintByte(byte minValue, byte maxValue) {
        super(1);
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
            value = data[0];
        } else {
            value = U(data[0]);
        }
        return (minValue <= value) && (value <= maxValue);
    }
    
    @Override
    public String toString() {
        return String.format("Byte[0x%02x, 0x%02x]", minValue, maxValue);
    }
}
