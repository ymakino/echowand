package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 漏電センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class ElectricLeakSensorInfo extends BaseObjectInfo {
    /**
     * ElectricLeakSensorInfoを生成する
     */
    public ElectricLeakSensorInfo(){
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x05)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}