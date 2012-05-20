package echowand.info;

/**
 * 1バイトのプロパティデータ制約を表現する。
 * @author Yoshiki Makino
 */
public class PropertyConstraintByte extends PropertyConstraintSize {
    private boolean isSigned;
    private int minValue;
    private int maxValue;
    
    private int U(Byte b) {
        return 0x000000ff & (b);
    }
    
    /**
     * PropertyConstraintByteを生成する。
     */
    public PropertyConstraintByte() {
        this((byte)0, (byte)0xff);
    }
    
    /**
     * PropertyConstraintByteを生成する。
     * @param minValue プロパティ値の最小値
     * @param maxValue プロパティ値の最大値
     */
    public PropertyConstraintByte(byte minValue, byte maxValue) {
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
    public boolean isAcceptable(byte[] data) {
        if (!super.isAcceptable(data)) {
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
    public byte[] getInitialData() {
        return new byte[]{(byte)minValue};
    }
}
