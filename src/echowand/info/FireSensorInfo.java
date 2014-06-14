package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 火災センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class FireSensorInfo extends DeviceObjectInfo {
    /**
     * FireSensorInfoを生成する
     */
    public FireSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x19)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}
