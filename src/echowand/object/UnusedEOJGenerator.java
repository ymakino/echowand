package echowand.object;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.logic.TooManyObjectsException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * ユニークなEOJの生成管理
 * @author Yoshiki Makino
 */
public class UnusedEOJGenerator {
    private HashMap<ClassEOJ, Byte> usedEOJMap;
    private HashSet<EOJ> usedEOJSet;
    
    /**
     * UnusedEOJGeneratorを生成する。
     */
    public UnusedEOJGenerator() {
        usedEOJMap = new HashMap<ClassEOJ, Byte>();
        usedEOJSet = new HashSet<EOJ>();
    }
    
    private boolean isValidInstanceCode(byte instanceCode) {
        return (1 <= instanceCode && instanceCode <= 0x7f);
    }
    
    /**
     * 指定されたClassEOJに属するEOJで、利用されていないEOJを返す。
     * @param ceoj ClassEOJの指定
     * @return まだ利用されていないEOJ
     * @throws TooManyObjectsException EOJをこれ以上生成できない場合
     */
    public synchronized EOJ generate(ClassEOJ ceoj) throws TooManyObjectsException {
        byte unusedCode = 1;
        Byte b = usedEOJMap.get(ceoj);
        
        if (b != null) {
            unusedCode = (byte)(b + 1);
        }
        
        for (;;) {
            if (!isValidInstanceCode(unusedCode)) {
                throw new TooManyObjectsException("too many generated eojs for " + ceoj);
            }
            
            if (!usedEOJSet.contains(ceoj.getEOJWithInstanceCode(unusedCode))) {
                break;
            }
            
            unusedCode += 1;
        }

        usedEOJMap.put(ceoj, unusedCode);
        
        return ceoj.getEOJWithInstanceCode(unusedCode);
    }
    
    /**
     * 指定されたEOJを利用済みとして登録する。
     * @param eoj EOJの指定
     */
    public synchronized void addUsed(EOJ eoj) {
        usedEOJSet.add(eoj);
    }
    
    /**
     * 指定されたEOJが利用済みであるかを返す。
     * @param eoj EOJの指定
     * @return 利用済みの場合にはtrue、そうでなければfalse
     */
    public synchronized boolean isUsed(EOJ eoj) {
        Byte b = usedEOJMap.get(eoj.getClassEOJ());
        if (b != null && (eoj.getInstanceCode() <= b)) {
            return true;
        }
        
        return usedEOJSet.contains(eoj);
    }
}
