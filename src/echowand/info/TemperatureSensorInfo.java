package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 温度センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class TemperatureSensorInfo extends BaseObjectInfo {
    /**
     * TemperatureSensorInfoを生成する
     */
    public TemperatureSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x11)));
        
        add(EPC.xE0, true, false, true, 2, new PropertyConstraintTemperature());
    }
}
