package echowand.info;

import echowand.common.ClassEOJ;

/**
 * 洗濯機クラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class WashingMachineInfo extends DeviceObjectInfo {
    /**
     * WashingMachineInfoを生成する
     */
    public WashingMachineInfo(){
        setClassEOJ(new ClassEOJ((byte)0x03, (byte)0xc5));
    }
}
