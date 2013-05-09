package echowand.object;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.logic.TooManyObjectsException;
import java.util.HashMap;

/**
 * ユニークなEOJの生成管理
 * @author Yoshiki Makino
 */
public class UnusedEOJGenerator {
    private HashMap<ClassEOJ, Byte> usedEOJMap;
    
    /**
     * UnusedEOJGeneratorを生成する。
     */
    public UnusedEOJGenerator() {
        usedEOJMap = new HashMap<ClassEOJ, Byte>();
    }
    
    /**
     * 指定されたClassEOJに属するEOJで、利用されていないEOJを返す。
     * @param ceoj ClassEOJの指定
     * @return まだ利用されていないEOJ
     * @throws TooManyObjectsException EOJをこれ以上生成できない場合
     */
    public synchronized EOJ generate(ClassEOJ ceoj) throws TooManyObjectsException {
        byte unused = 1;
        Byte b = usedEOJMap.get(ceoj);
        
        if (b != null) {
            unused = (byte)(b + 1);
        }
        
        if (unused <= 0) {
            throw new TooManyObjectsException("too many generated eojs for " + ceoj);
        }
        
        usedEOJMap.put(ceoj, unused);
        
        return ceoj.getEOJWithInstanceCode(unused);
    }
}
