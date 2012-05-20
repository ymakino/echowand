package echowand.object;

import echowand.common.EPC;
import echowand.net.Property;
import java.util.LinkedList;
import java.util.List;

/**
 * LocalObjectに対し複数Set、Getを実行
 * @author Yoshiki Makino
 */
public class LocalSetGetAtomic implements Runnable {
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
        this.object = object;
        setProperties = new LinkedList<Property>();
        getProperties = new LinkedList<Property>();
        setResult = new LinkedList<Property>();
        getResult = new LinkedList<Property>();
    }
    
    /**
     * このLocalSetGetAtomic初期化を行う。
     * 処理終了後であっても、このメソッドを呼び出すことで、同じ処理を繰り返すことができる。
     */
    public void initialize() {
        setProperties.clear();
        getProperties.clear();
        setResult.clear();
        getResult.clear();
        success = true;
        done = false;
    }
    
    /**
     * Getではなく通知リクエストを利用しているかを設定する。
     * @param announce 通知リクエストを利用しているのであればtrue、そうでなければfalse
     */
    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }
    
    /**
     * Setを行うプロパティを追加する。
     * @param property 追加するプロパティ
     */
    public void addSet(Property property) {
        setProperties.add(property);
    }
    
    /**
     * Getを行うプロパティを追加する。
     * @param property 追加するプロパティ
     */
    public void addGet(Property property) {
        getProperties.add(property);
    }

    private boolean hasGetOrAnnouncePermission(LocalObject object, EPC epc) {
        if (announce) {
            return object.isObservable(epc);
        } else {
            return object.isGettable(epc);
        }
    }
    
    /**
     * このLocalSetGetAtomicの処理を行う
     */
    @Override
    public void run() {
        if (done) {
            return;
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
            } else {
                getResult.add(new Property(property.getEPC()));
                success = false;
            }
        }
        
        done = true;
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
