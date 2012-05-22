package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 人体検知センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class HumanDetectionSensorInfo extends BaseObjectInfo {
    /**
     * HumanDetectionSensorInfoを生成する
     */
    public HumanDetectionSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x07)));
        
        add(EPC.xB1, true, false, true, new PropertyConstraintDetection());
    }
}
