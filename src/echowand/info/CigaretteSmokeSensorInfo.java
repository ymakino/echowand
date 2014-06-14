package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * タバコ煙センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class CigaretteSmokeSensorInfo extends DeviceObjectInfo {
    /**
     * CigaretteSmokeSensorInfoを生成する
     */
    public CigaretteSmokeSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x1a)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}
