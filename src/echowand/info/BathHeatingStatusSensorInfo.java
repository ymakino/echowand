package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 風呂沸き上がりセンサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class BathHeatingStatusSensorInfo extends DeviceObjectInfo {
    /**
     * BathHeatingStatusSensorInfoを生成する
     */
    public BathHeatingStatusSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x16)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}
