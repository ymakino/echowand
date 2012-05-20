package echowand.object;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.logic.TooManyObjectsException;
import echowand.util.Collector;
import echowand.util.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * ローカルに存在するECHONETオブジェクトを管理
 * @author Yoshiki Makino
 */
public class LocalObjectManager {
    private HashMap<EOJ, LocalObject> objectsMap;
    private LinkedList<LocalObject>   objects;
    private UnusedEOJGenerator eojGenerator;
    
    /**
     * LocalObjectManagerを生成する。
     */
    public LocalObjectManager() {
        objectsMap = new HashMap<EOJ, LocalObject>();
        objects = new LinkedList<LocalObject>();
        eojGenerator = new UnusedEOJGenerator();
    }
    
    /**
     * このLocalObjectManagerが管理しているオブジェクトの数を返す。
     * @return 管理しているオブジェクトの数
     */
    public int size() {
        return objects.size();
    }
    
    private synchronized void addObject(LocalObject object) {
        objectsMap.put(object.getEOJ(), object);
        objects.add(object);
    }
    
    /**
     * ローカルオブジェクトを登録する。
     * ローカルオブジェクトのEOJは重複がないようにインスタンスコードが更新される。
     * @param object 登録するローカルのオブジェクト
     * @exception TooManyObjectsException 新しいEOJを割り当てられない場合
     */
    public void add(LocalObject object) throws TooManyObjectsException {
        ClassEOJ classEOJ = object.getEOJ().getClassEOJ();
        EOJ newEOJ = eojGenerator.generate(classEOJ);
        object.setInstanceCode(newEOJ.getInstanceCode());
        addObject(object);
    }
    
    /**
     * 指定されたEOJのローカルオブジェクトを返す。
     * 存在しない場合にはnullを返す。
     * @param eoj EOJの指定
     * @return 指定されたEOJのローカルオブジェクト
     */
    public LocalObject get(EOJ eoj) {
        return objectsMap.get(eoj);
    }
    
    /**
     * Selectorが真を返すローカルオブジェクトを選択し、そのリストを返す。
     * @param selector ローカルオブジェクトの選択
     * @return 選択したローカルオブジェクトのリスト
     */
    public LinkedList<LocalObject> get(Selector<LocalObject> selector) {
        Collector<LocalObject> collector = new Collector<LocalObject>(selector);
        return collector.collect(new ArrayList<LocalObject>(objects));
    }
    
    /**
     * このLocalObjectで管理されているindex番目のローカルのオブジェクトを返す。
     * @param index ローカルオブジェクトのインデックス
     * @return index番目のローカルオブジェクト
     */
    public LocalObject getAtIndex(int index) {
        return objects.get(index);
    }
    
    /**
     * 指定されたClassEOJに属するローカルオブジェクトのリストを返す。
     * @param ceoj ClassEOJの指定
     * @return 指定されたClassEOJに属するローカルオブジェクトリスト
     */
    public LinkedList<LocalObject> getWithClassEOJ(final ClassEOJ ceoj) {
        return get(new Selector<LocalObject>() {
            @Override
            public boolean select(LocalObject object) {
                return object.getEOJ().isMemberOf(ceoj);
            }
        });
    }
    
    
    /**
     * 機器オブジェクトに属するローカルオブジェクトのリストを返す。
     * @return 機器オブジェクトのリスト
     */
    public LinkedList<LocalObject> getDeviceObjects() {
        return get(new Selector<LocalObject>() {
            @Override
            public boolean select(LocalObject object) {
                return object.getEOJ().isDeviceObject();
            }
        });
    }
}
