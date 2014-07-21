package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 風呂水位センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class BathWaterLevelSensorInfo extends DeviceObjectInfo {
    /**
     * BathWaterLevelSensorInfoを生成する
     */
    public BathWaterLevelSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x15)));
        
        add(EPC.xE0, true, false, false, 1, new PropertyConstraintWaterLevel());
    }
}
