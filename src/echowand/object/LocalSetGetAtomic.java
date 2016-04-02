package echowand.object;

import echowand.common.EPC;
import echowand.net.Property;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * LocalObjectに対し複数Set、Getを実行
 * @author Yoshiki Makino
 */
public class LocalSetGetAtomic implements Runnable {
    private static final Logger logger = Logger.getLogger(LocalSetGetAtomic.class.getName());
    private static final String className = LocalSetGetAtomic.class.getName();
    
    private LocalObject object;
    private LinkedList<Property> setProperties;
    private LinkedList<Property> getProperties;
    private LinkedList<Property> setResult;
    private LinkedList<Property> getResult;
    private boolean announce = false;
    private boolean success = true;
    private boolean done = false;
    
    /**
     * LocalSetGetAtomicを生成
     * @param object このLocalSetGetAtomicのターゲットオブジェクト
     */
    public LocalSetGetAtomic(LocalObject object) {
        logger.entering(className, "LocalSetGetAtomic", object);
        
        this.object = object;
        setProperties = new LinkedList<Property>();
        getProperties = new LinkedList<Property>();
        setResult = new LinkedList<Property>();
        getResult = new LinkedList<Property>();
        
        logger.exiting(className, "LocalSetGetAtomic");
    }
    
    /**
     * このLocalSetGetAtomic初期化を行う。
     * 処理終了後であっても、このメソッドを呼び出すことで、同じ処理を繰り返すことができる。
     */
    public void initialize() {
        logger.entering(className, "initialize", object);
        
        setProperties.clear();
        getProperties.clear();
        setResult.clear();
        getResult.clear();
        success = true;
        done = false;
        
        logger.exiting(className, "initialize");
    }
    
    /**
     * Getではなく通知リクエストを利用しているかを設定する。
     * @param announce 通知リクエストを利用しているのであればtrue、そうでなければfalse
     */
    public void setAnnounce(boolean announce) {
        logger.entering(className, "setAnnounce", announce);
        
        this.announce = announce;
        
        logger.exiting(className, "setAnnounce");
    }
    
    /**
     * Setを行うプロパティを追加する。
     * @param property 追加するプロパティ
     */
    public void addSet(Property property) {
        logger.entering(className, "addSet", property);
        
        setProperties.add(property);
        
        logger.exiting(className, "addSet");
    }
    
    /**
     * Getを行うプロパティを追加する。
     * @param property 追加するプロパティ
     */
    public void addGet(Property property) {
        logger.entering(className, "addGet", property);
        
        getProperties.add(property);
        
        logger.exiting(className, "addGet");
    }

    private boolean hasGetOrAnnouncePermission(LocalObject object, EPC epc) {
        logger.entering(className, "hasGetOrAnnouncePermission", new Object[]{object, epc});
        
        boolean permission;
        if (announce) {
            permission = object.isGettable(epc) || object.isObservable(epc);
        } else {
            permission = object.isGettable(epc);
        }
        
        logger.exiting(className, "hasGetOrAnnouncePermission", permission);
        return permission;
    }
    
    private boolean doSetGet() {
        logger.entering(className, "doSetGet");

        if (done) {
            logger.exiting(className, "doSetGet", false);
            return false;
        }

        for (Property property : setProperties) {
            if (object.setData(property.getEPC(), new ObjectData(property.getEDT()))) {
                setResult.add(new Property(property.getEPC()));
            } else {
                setResult.add(property);
                success = false;
            }
        }

        for (Property property : getProperties) {
            ObjectData data = null;

            if (hasGetOrAnnouncePermission(object, property.getEPC())) {
                data = object.forceGetData(property.getEPC());
            }

            if (data != null) {
                getResult.add(new Property(property.getEPC(), data.getData()));
                
                if (announce) {
                    for (int i=0; i<data.getExtraSize(); i++) {
                        getResult.add(new Property(property.getEPC(), data.getExtraDataAt(i)));
                    }
                }
                
            } else {
                getResult.add(new Property(property.getEPC()));
                success = false;
            }
        }

        done = true;

        logger.exiting(className, "doSetGet", true);
        return true;
    }

    /**
     * このLocalSetGetAtomicの処理を行う
     */
    @Override
    public void run() {
        logger.entering(className, "run");

        synchronized (object) {
            doSetGet();
        }
        
        logger.exiting(className, "run");
    }
    
    /**
     * Set要求の結果を返す。
     * Setに成功したプロパティについては結果のプロパティデータの長さが0になる。
     * @return Set要求の結果のプロパティリスト
     */
    public List<Property> getSetResult() {
        return setResult;
    }
    
    /**
     * Get要求の結果を返す。
     * Getに成功したプロパティの結果にはプロパティデータの長さが0以外になる。
     * @return Get要求の結果のプロパティリスト
     */
    public List<Property> getGetResult() {
        return getResult;
    }
    
    /**
     * このLocalSetGetAtomicの処理が終了したかを返す。
     * @return 終了していればtrue、そうでなければfalse
     */
    public boolean isDone() {
        return done;
    }
    
    /**
     * このLocalSetGetAtomicが成功したかどうかを返す。
     * @return 成功した場合にはtrue、そうでなければfalse
     */
    public boolean isSuccess() {
        return success;
    }
}
