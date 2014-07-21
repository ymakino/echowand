package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;
import echowand.util.ConstraintByte;
import echowand.util.ConstraintUnion;

/**
 * 一般照明クラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class GeneralLightingInfo extends DeviceObjectInfo {
    /**
     * GeneralLightingInfoを生成する
     */
    public GeneralLightingInfo() {
        setClassEOJ(new ClassEOJ((byte)0x02, (byte)(0x90)));
        
        add(EPC.x80, true, true, true, new byte[]{0x31}, new PropertyConstraintOnOff());
        add(EPC.xB6, true, true, false, new byte[]{0x41}, new ConstraintUnion(new ConstraintByte((byte)0x41, (byte)0x43), new ConstraintByte((byte)0x45)));
    }
}
