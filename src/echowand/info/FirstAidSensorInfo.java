package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 救急用センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class FirstAidSensorInfo extends DeviceObjectInfo {
    /**
     * FirstAidSensorInfoを生成する
     */
    public FirstAidSensorInfo(){
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x04)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}