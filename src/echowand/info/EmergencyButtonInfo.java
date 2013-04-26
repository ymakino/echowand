package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 非常ボタンクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class EmergencyButtonInfo extends DeviceObjectInfo {
    /**
     * EmergencyButtonInfoを生成する
     */
    public EmergencyButtonInfo(){
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x03)));
        
        add(EPC.xB1, true, false, true, new byte[]{0x42}, new PropertyConstraintDetection());
    }
}