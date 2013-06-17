package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 照度センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class IlluminanceSensorInfo extends DeviceObjectInfo {
    /**
     * IlluminanceSensorInfoを生成する
     */
    public IlluminanceSensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x0d)));
    }
}
