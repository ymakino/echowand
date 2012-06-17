package echowand.util;

/**
 * 4バイトのプロパティデータ制約を表現する。
 * @author Yoshiki Makino
 */
public class ConstraintInt extends ConstraintSize {
    private boolean isSigned;
    private long minValue;
    private long maxValue;
    
    private long U(int s) {
        return 0x00000000ffffffffL & s;
    }
    
    private int S(byte[] data) {
        long l1 = ((long)data[0]) << 24;
        long l2 = (0x000000ff & (int)data[1]) << 16;
        long l3 = (0x000000ff & (int)data[2]) << 8;
        long l4 = 0x000000ff & (int)data[3];
        return (int)(l1 | l2 | l3 | l4);
    }
    
    /**
     * ConstraintIntを生成する。
     */
    public ConstraintInt() {
        this(0x00000000, 0xffffffff);
    }
    
    /**
     * ConstraintIntを生成する。
     * @param value プロパティ値
     */
    public ConstraintInt(int value) {
        this(value, value);
    }
    
    /**
     * ConstraintIntを生成する。
     * @param minValue プロパティ値の最小値
     * @param maxValue プロパティ値の最大値
     */
    public ConstraintInt(int minValue, int maxValue) {
        super(4);
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
        
        long value;
        if (isSigned) {
            value = S(data);
        } else {
            value = U(S(data));
        }
        
        return (minValue <= value) && (value <= maxValue);
    }
    
    @Override
    public String toString() {
        return String.format("Int[0x%08x, 0x%08x]", minValue, maxValue);
    }
}
