package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 水あふれセンサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class WaterOverflowSensorInfo extends DeviceObjectInfo {
    /**
     * WaterOverflowSensorInfoを生成する
     */
    public WaterOverflowSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x18)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}
