package echowand.net;

/**
 * プログラム内でのみ有効なサブネット用のノードに関する情報
 * @author ymakino
 */
public class InternalNodeInfo implements NodeInfo {
    private String name;
    
    /**
     * 指定された名前でノード情報を生成する。
     * @param name 名前の指定
     */
    public InternalNodeInfo(String name) {
        this.name = name;
    }
    
    /**
     * このノードの名前を返す。
     * @return このノードの名前
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof InternalNodeInfo) {
            return name.equals(((InternalNodeInfo)o).name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
