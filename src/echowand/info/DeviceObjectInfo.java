package echowand.info;

import echowand.common.EPC;
import echowand.common.PropertyMap;
import echowand.util.ConstraintByte;
import echowand.util.ConstraintSize;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 機器オブジェクト基本設定構築用クラス
 * 機器オブジェクトスーパークラスの必須プロパティが設定済みのものが作成される。
 * @author Yoshiki Makino
 */
public class DeviceObjectInfo extends BasicObjectInfo {
    private static final Logger logger = Logger.getLogger(DeviceObjectInfo.class.getName());
    private static final String className = DeviceObjectInfo.class.getName();
    
    private byte[] versionBytes = new byte[]{0x00, 0x00, asciiCode('B'), 0x00};
    
    private static byte asciiCode(char c) {
        try {
            String cstr = Character.toString(c);
            byte ccode = cstr.getBytes("ASCII")[0];
            return ccode;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DeviceObjectInfo.class.getName()).log(Level.SEVERE, null, ex);
            return 0x00;
        }
    }
    
    /**
     * DeviceObjectInfoを生成する。
     * 機器オブジェクトスーパークラス規定で必須になっているプロパティが設定済みになる。
     */
    public DeviceObjectInfo() {
        logger.entering(className, "DeviceObjectInfo");
        
        add(EPC.x80, true, false,  true, new byte[]{0x30}, new PropertyConstraintOnOff());
        add(EPC.x81, true,  true,  true, new byte[]{0x00}, new ConstraintByte());
        add(EPC.x82, true, false,  true, versionBytes, new PropertyConstraintVersion());
        add(EPC.x88, true, false,  true, new byte[]{0x42}, new PropertyConstraintDetection());
        add(EPC.x8A, true, false, false, new byte[]{0x00, 0x00, 0x00}, new ConstraintSize(3));
        add(EPC.x9D, true, false, false, new PropertyMap().toBytes(), new PropertyConstraintMap()); 
        add(EPC.x9E, true, false, false, new PropertyMap().toBytes(), new PropertyConstraintMap());
        add(EPC.x9F, true, false, false, new PropertyMap().toBytes(), new PropertyConstraintMap());
        
        logger.exiting(className, "DeviceObjectInfo");
    }
}
