package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;
import java.util.HashSet;

/**
 * ノードプロファイルクラスの基本設定を行う。
 * @author Yoshiki Makino
 */
public class NodeProfileInfo extends BaseObjectInfo {
    /**
     * NodeProfileInfoを生成する
     */
    public NodeProfileInfo() {
        setClassEOJ(new ClassEOJ((byte)0x0e, (byte)0xf0));
        
        add(EPC.x82, true, false, false, 4);
        HashSet<Integer> set = new HashSet<Integer>();
        set.add(9); set.add(17);
        add(EPC.x83, true, false, false, new PropertyConstraintSize(set));
        add(EPC.xBF, true, true, false, 2);
        add(EPC.xD3, true, false, false, 3);
        add(EPC.xD4, true, false, false, 2);
        add(EPC.xD5, false, false, true, new PropertyConstraintSize(1, 253));
        add(EPC.xD6, true, false, false, new PropertyConstraintSize(1, 253));
        add(EPC.xD7, true, false, false, new PropertyConstraintSize(1, 17));
    }
}
