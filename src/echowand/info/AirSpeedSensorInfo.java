package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 風速センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class AirSpeedSensorInfo extends DeviceObjectInfo {
    /**
     * AirSpeedSensorInfoを生成する
     */
    public AirSpeedSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x1f)));
        
        add(EPC.xE0, true, false, false, 2, new PropertyConstraintAirSpeed());
    }
}
