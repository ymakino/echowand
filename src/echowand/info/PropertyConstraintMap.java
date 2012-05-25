package echowand.info;

import echowand.util.ConstraintSize;
import echowand.common.PropertyMap;
import java.util.Arrays;

/**
 * プロパティマップのデータ制約を表現する。
 * @author Yoshiki Makino
 */
public class PropertyConstraintMap extends ConstraintSize {
    
    /**
     * PropertyConstraintMapを生成する。
     */
    public PropertyConstraintMap() {
        super(1, 17);
    }
    
    @Override
    public boolean isValid(byte[] data) {
        PropertyMap map = new PropertyMap(data);
        return Arrays.equals(data, map.toBytes());
    }
}
