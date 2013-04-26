package echowand.info;

import echowand.util.ConstraintSize;
import echowand.util.ConstraintUnion;
import echowand.common.ClassEOJ;
import echowand.common.EPC;
import echowand.common.PropertyMap;
import java.util.HashSet;

/**
 * ノードプロファイルクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class NodeProfileInfo extends BasicObjectInfo {
    private byte[] versionBytes = {0x01, 0x01, 0x01, 0x00};
    
    /**
     * NodeProfileInfoを生成する。
     * ノードプロファイルクラス規定で必須になっているプロパティが追加される。
     */
    public NodeProfileInfo() {
        setClassEOJ(new ClassEOJ((byte)0x0e, (byte)0xf0));
        
        ConstraintSize size1 = new ConstraintSize(9);
        ConstraintSize size2 = new ConstraintSize(17);
        ConstraintUnion c = new ConstraintUnion(size1, size2);
        add(EPC.x80, true, false,  true, new byte[]{0x30}, new PropertyConstraintOnOff());
        add(EPC.x82, true, false,  true, versionBytes, new PropertyConstraintVersion());
        add(EPC.x83, true, false, false, 9, c);
        add(EPC.x9D, true, false, false, new PropertyMap().toBytes(), new PropertyConstraintMap()); 
        add(EPC.x9E, true, false, false, new PropertyMap().toBytes(), new PropertyConstraintMap());
        add(EPC.x9F, true, false, false, new PropertyMap().toBytes(), new PropertyConstraintMap());
        
        add(EPC.xBF, true, true, false, new byte[]{(byte)0x80, (byte)0x00});
        add(EPC.xD3, true, false, false, 3);
        add(EPC.xD4, true, false, false, 2);
        add(EPC.xD5, false, false, true, 1, new ConstraintSize(1, 253));
        add(EPC.xD6, true, false, false, 1, new ConstraintSize(1, 253));
        add(EPC.xD7, true, false, false, 1, new ConstraintSize(1, 17));
    }
}
