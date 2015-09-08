package echowand.service;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import java.util.logging.Logger;

/**
 * LocalObjectとCoreを保持し利用
 * @author ymakino
 */
public class LocalObjectAccessInterface {
    private static final Logger LOGGER = Logger.getLogger(LocalObjectAccessInterface.class.getName());
    private static final String CLASS_NAME = LocalObjectAccessInterface.class.getName();
    
    private Core core = null;
    private LocalObject localObject = null;
    
    /**
     * 保持するCoreを設定する。
     * @param core 保持するCore
     * @return 以前保持していたCore
     */
    public Core setCore(Core core) {
        LOGGER.entering(CLASS_NAME, "setCore", core);
        
        Core lastCore = this.core;
        this.core = core;
        
        LOGGER.exiting(CLASS_NAME, "setCore", lastCore);
        return lastCore;
    }
    
    /**
     * 保持するローカルオブジェクトを設定する。
     * @param object 保持するローカルオブジェクト
     * @return 以前保持していたローカルオブジェクト
     */
    public LocalObject setLocalObject(LocalObject object) {
        LOGGER.entering(CLASS_NAME, "setLocalObject", object);
        
        LocalObject lastObject = localObject;
        localObject = object;
        
        LOGGER.exiting(CLASS_NAME, "setLocalObject", localObject);
        return lastObject;
    }
    
    /**
     * 保持しているCoreを返す。
     * @return 保持しているCore
     */
    public Core getCore() {
        return core;
    }
    
    /**
     * 保持しているローカルオブジェクトを返す。
     * @return 保持しているローカルオブジェクト
     */
    public LocalObject getLocalObject() {
        return localObject;
    }
    
    /**
     * 保持しているローカルオブジェクトのObjectDataを取得する。
     * @param epc 取得するEPCの指定
     * @return 取得したObjectData
     */
    public ObjectData getData(EPC epc) {
        LOGGER.entering(CLASS_NAME, "getData", epc);
        
        ObjectData data = localObject.getData(epc);
        
        LOGGER.exiting(CLASS_NAME, "getData", data);
        return data;
    }
    
    /**
     * 保持しているCoreを利用しローカルオブジェクトのObjectDataを取得する。
     * @param eoj ローカルオブジェクトの指定
     * @param epc 取得するEPCの指定
     * @return 取得したObjectData
     */
    public ObjectData getData(EOJ eoj, EPC epc) {
        LOGGER.entering(CLASS_NAME, "getData", new Object[]{eoj, epc});
        
        LocalObject otherObject = core.getLocalObjectManager().get(eoj);
        
        ObjectData data = null;
        if (otherObject != null) {
            data = otherObject.getData(epc);
        }
        
        LOGGER.exiting(CLASS_NAME, "getData", data);
        return data;
    }
    
    /**
     * 保持しているローカルオブジェクトにObjectDataを設定する。
     * @param epc 設定するEPCの指定
     * @param data 設定するObjectData
     * @return 設定に成功した場合にはtrue、そうでなければfalse
     */
    public boolean setData(EPC epc, ObjectData data) {
        LOGGER.entering(CLASS_NAME, "setData", new Object[]{epc, data});
        
        boolean result = localObject.forceSetData(epc, data);
        
        LOGGER.entering(CLASS_NAME, "setData", result);
        return result;
    }
    
    /**
     * 保持しているCoreを利用しローカルオブジェクトにObjectDataを設定する。
     * @param eoj ローカルオブジェクトの指定
     * @param epc 設定するEPCの指定
     * @param data 設定するObjectData
     * @return 設定に成功した場合にはtrue、そうでなければfalse
     */
    public boolean setData(EOJ eoj, EPC epc, ObjectData data) {
        LOGGER.entering(CLASS_NAME, "setData", new Object[]{eoj, epc, data});
        
        LocalObject otherObject = core.getLocalObjectManager().get(eoj);
        
        boolean result = false;
        if (otherObject != null) {
            result = otherObject.forceSetData(epc, data);
        }
        
        LOGGER.entering(CLASS_NAME, "setData", result);
        return result;
    }
}
