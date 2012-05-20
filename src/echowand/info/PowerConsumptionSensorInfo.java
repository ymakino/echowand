package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 電力量センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class PowerConsumptionSensorInfo extends BaseObjectInfo {
    /**
     * PowerConsumptionSensorInfoを生成する
     */
    public PowerConsumptionSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x22)));
        
        add(EPC.xE0, true, false, false, 4);
    }
}