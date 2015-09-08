package echowand.object;

import echowand.common.EPC;
import java.util.Random;
import java.util.logging.Logger;

/**
 * プロパティ値をランダムに変化させるDelegate
 * @author Yoshiki Makino
 */
public class LocalObjectRandomDelegate extends LocalObjectDefaultDelegate {
    private static final Logger logger = Logger.getLogger(LocalObjectRandomDelegate.class.getName());
    private static final String className = LocalObjectRandomDelegate.class.getName();

    private EPC epc;
    private int len;
    private static Random random = new Random();

    /**
     * LocalObjectRandomDelegateを生成する。
     * @param epc ランダムに変化させるプロパティデータのEPC
     * @param len プロパティデータのバイト数
     */
    public LocalObjectRandomDelegate(EPC epc, int len) {
        logger.entering(className, "LocalObjectRandomDelegate", new Object[]{epc, len});
        
        this.epc = epc;
        this.len = len;
        
        logger.exiting(className, "LocalObjectRandomDelegate");
    }

    /**
     * 指定されたEPCが設定されたEPCと等しい時にはランダムのバイト配列を返す。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータが要求されているオブジェクト
     * @param epc 要求プロパティデータのEPC
     */
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
        logger.entering(className, "getData", new Object[]{result, object, epc});
        
        if (this.epc == epc) {
            byte[] bytes = new byte[len];
            random.nextBytes(bytes);
            result.setGetData(new ObjectData(bytes));
        }
        
        logger.exiting(className, "LocalObjectRandomDelegate");
    }
}
