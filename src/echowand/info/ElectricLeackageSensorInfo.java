package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 漏電センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class ElectricLeackageSensorInfo extends BaseObjectInfo {
    /**
     * ElectricLeackageSensorInfoを生成する
     */
    public ElectricLeackageSensorInfo(){
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x05)));
        
        add(EPC.xB1, true, false, true, new PropertyConstraintDetection());
    }
}