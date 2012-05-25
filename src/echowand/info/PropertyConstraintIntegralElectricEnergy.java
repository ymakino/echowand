package echowand.info;

import echowand.util.ConstraintInt;

/**
 * 電力量センサの積算電力量計測値のデータ制約を表現する。
 * データの範囲は0x00000000 - 0x3B9AC9FF (0 - 999,999.999kWh)となる。
 * オーバーフロー時は0x00000000から再びインクリメントを行う。
 * @author Yoshiki Makino
 */
public class PropertyConstraintIntegralElectricEnergy extends ConstraintInt {
    
    private static final int MIN_VALUE = (byte)0x00000000;
    private static final int MAX_VALUE = (byte)0x3b9ac9ff;
    
    /**
     * PropertyConstraintIntegralElectricEnergyを生成する。
     */
    public PropertyConstraintIntegralElectricEnergy() {
        super(MIN_VALUE, MAX_VALUE);
    }
}
