package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 水漏れセンサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class WaterLeakSensorInfo extends DeviceObjectInfo {
    /**
     * WaterLeakSensorInfoを生成する
     */
    public WaterLeakSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x17)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}
