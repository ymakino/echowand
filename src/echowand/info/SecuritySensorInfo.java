package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 防犯センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class SecuritySensorInfo extends BaseObjectInfo {
    /**
     * SecuritySensorInfoを生成する
     */
    public SecuritySensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x02)));
        
        add(EPC.xB1, true, false, true, new PropertyConstraintDetection());
    }
}
