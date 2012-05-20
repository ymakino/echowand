package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * 家庭用エアコンクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class HomeAirConditionerInfo extends BaseObjectInfo {
    /**
     * HomeAirConditionerInfoを生成する
     */
    public HomeAirConditionerInfo(){
        setClassEOJ(new ClassEOJ((byte)0x01, (byte)0x30));
        
        add(EPC.xB0, true, true, true, new PropertyConstraintByte((byte)0x40, (byte)0x45), new byte[]{(byte)0x41});
        add(EPC.xB3, true, true, false, new PropertyConstraintByte((byte)0x00, (byte)0x32), new byte[]{(byte)0x1c});
    }
}
