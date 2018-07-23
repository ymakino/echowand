package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 水位センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class WaterLevelSensorInfo extends DeviceObjectInfo {
    /**
     * WaterLevelSensorInfoを生成する
     */
    public WaterLevelSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x14)));
        
        add(EPC.xE0, true, false, false, 1, new PropertyConstraintWaterLevel());
    }
}
