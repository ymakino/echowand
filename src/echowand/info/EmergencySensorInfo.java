package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 救急用センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class EmergencySensorInfo extends BaseObjectInfo {
    /**
     * EmergencySensorInfoを生成する
     */
    public EmergencySensorInfo(){
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x04)));
        
        add(EPC.xB1, true, false, true, new PropertyConstraintDetection());
    }
}