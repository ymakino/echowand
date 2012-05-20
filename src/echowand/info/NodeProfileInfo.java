package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

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
        
        add(EPC.x8A, true, false, false, 3);
        add(EPC.x8E, true, false, false, 4);
        add(EPC.x9D, true, false, false, 17);
        add(EPC.x9E, true, false, false, 17);
        add(EPC.x9F, true, false, false, 17);
        
        
        add(EPC.x80, true, false, false, 1);
        add(EPC.x82, true, false, false, 4);
        add(EPC.x83, true, false, false, 17);
        add(EPC.xBF, true, true, false, 2);
        add(EPC.xD3, true, false, false, 3);
        add(EPC.xD4, true, false, false, 2);
        // add(EPC.xD5, false, false, true, 253);
        add(EPC.xD5, true, false, true, 253);
        add(EPC.xD6, true, false, false, 253);
        add(EPC.xD7, true, false, false, 17);
    }
}
