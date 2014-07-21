package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 雨センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class RainSensorInfo extends DeviceObjectInfo {
    /**
     * RainSensorInfoを生成する
     */
    public RainSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x13)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}
