package echowand.info;

import echowand.util.ConstraintSize;
import echowand.util.ConstraintUnion;
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
        
        ConstraintSize size1 = new ConstraintSize(9);
        ConstraintSize size2 = new ConstraintSize(17);
        ConstraintUnion c = new ConstraintUnion(size1, size2);
        add(EPC.x83, true, false, false, 9, c);
        
        add(EPC.xBF, true, true, false, 2);
        add(EPC.xD3, true, false, false, 3);
        add(EPC.xD4, true, false, false, 2);
        add(EPC.xD5, false, false, true, 1, new ConstraintSize(1, 253));
        add(EPC.xD6, true, false, false, 1, new ConstraintSize(1, 253));
        add(EPC.xD7, true, false, false, 1, new ConstraintSize(1, 17));
    }
}
