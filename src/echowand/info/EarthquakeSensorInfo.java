package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 地震センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class EarthquakeSensorInfo extends DeviceObjectInfo {
    /**
     * EarthquakeSensorInfoを生成する
     */
    public EarthquakeSensorInfo(){
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x05)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}