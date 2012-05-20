package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 湿度センサクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class HumiditySensorInfo extends BaseObjectInfo {
    /**
     * HumiditySensorInfoを生成する
     */
    public HumiditySensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x12)));
        
        add(EPC.xE0, true, false, true, 1);
    }
}
