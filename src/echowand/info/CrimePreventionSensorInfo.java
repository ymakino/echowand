package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 防犯センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class CrimePreventionSensorInfo extends DeviceObjectInfo {
    /**
     * CrimePreventionSensorInfoを生成する
     */
    public CrimePreventionSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x02)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}
