package echowand.util;

/**
 * ペアの表現
 * @param <T1> 一番目の値の型
 * @param <T2> 二番目の値の型
 * @author Yoshiki Makino
 */
public class Pair<T1, T2> {
    
    /**
     * ペアの一番目の値
     */
    public T1 first;
    
    /**
     * ペアの二番目番の値
     */
    public T2 second;
    
    /**
     * ペアを生成する。
     * @param first 1番目の要素
     * @param second 2番目の要素
     */
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
    
    /**
     * ペアの文字列表現を返す。
     * @return ペアの文字列表現
     */
    @Override
    public String toString() {
        return "Pair(" + first + ", " + second + ")";
    }

    /**
     * ペアのハッシュコードを返す。
     * @return ペアのハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 53 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }

    /**
     * 指定されたオブジェクトとこのペアが等しいかとうかを返す。
     * 1番目の要素と2番目の要素が両方とも等しいかどうか確認を行う。
     * @param o 指定オブジェクト
     * @return 等しいのであればtrue、そうでなければfalse
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair other = (Pair)o;
        return other.first.equals(first) && other.second.equals(second);
    }
}
