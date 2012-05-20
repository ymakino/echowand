package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * ガス漏れセンサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class GasLeakSensorInfo extends BaseObjectInfo {
    /**
     * GasLeakSensorInfoを生成する
     */
    public GasLeakSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x01)));
        
        add(EPC.xB1, true, false, true, new PropertyConstraintDetection());
    }
}
