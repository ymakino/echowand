package echowand.service;

import echowand.object.LocalObject;
import java.util.logging.Logger;

/**
 * LocalObjectCreatorの生成結果を保持
 * @author ymakino
 */
public class LocalObjectCreatorResult {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectCreatorResult.class.getName());
    private static final String CLASS_NAME = LocalObjectCreatorResult.class.getName();
    
    /**
     * 生成したローカルオブジェクト
     */
    public final LocalObject object;
    
    /**
     * 生成したLocalObjectUpdater
     */
    public final LocalObjectUpdater updater;
    
    /**
     * ローカルオブジェクトとLocalObjectUpdaterを指定してLocalObjectCreatorResultを生成する。
     * ここで指定したLocalObjectUpdaterに対してPropertyUpdaterを登録することで、自動的にPropertyUpdaterが実行される。
     * @param object ローカルオブジェクトの指定
     * @param updater LocalObjectUpdaterの指定
     */
    public LocalObjectCreatorResult(LocalObject object, LocalObjectUpdater updater) {
        LOGGER.entering(CLASS_NAME, "LocalObjectCreatorResult", new Object[]{object, updater});
        
        this.object = object;
        this.updater = updater;
        
        LOGGER.exiting(CLASS_NAME, "LocalObjectCreatorResult");
    }
}
