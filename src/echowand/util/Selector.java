package echowand.util;

/**
 * 指定されたオブジェクトを選択するか決める。
 * @param <T> 指令されるオブジェクトの型
 * @author Yoshiki Makino
 */
public interface Selector<T> {
    /**
     * 指定されたオブジェクトを選択するかどうかを返す。
     * @param object オブジェクトの指定
     * @return 選択するのであればtrue、そうでなければfalse
     */
    public boolean match(T object);
}
