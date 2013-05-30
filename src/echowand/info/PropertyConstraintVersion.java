package echowand.info;

import echowand.util.ConstraintSize;

/**
 * バージョン情報のプロパティデータ制約を表現する。
 * バージョン情報の大きさは4バイトである。
 * @author ymakino
 */
public class PropertyConstraintVersion extends ConstraintSize {
    /**
     * PropertyConstraintVersionを生成する。
     */
    public PropertyConstraintVersion() {
        super(4);
    }
}
