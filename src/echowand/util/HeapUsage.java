package echowand.util;

/**
 * ヒープの使用量の情報を表す。
 * @author ymakino
 */
public class HeapUsage {
    private long totalMemory;
    private long freeMemory;
    private long maxMemory;
    private Unit unit;
    
    /**
     * 利用する単位を表す。
     * バイト(Byte)、キロバイト(KByte)、メガバイト(MByte)の三つの値を持つ。
     */
    public enum Unit {
        /**
         * バイトを表す
         */
        Byte(1, "B"),
        
        /**
         * キロバイトを表す。
         */
        KByte(1024, "K"),
        
        /**
         * メガバイトを表す。
         */
        MByte(1024 * 1024, "M");
        
        private long ratio;
        private String unitSymbol;
        
        private Unit(long ratio, String unitSymbol) {
            this.ratio = ratio;
            this.unitSymbol = unitSymbol;
        }
        
        /**
         * 単位の文字列表現を返す。
         * @return 単位の文字列表現
         */
        @Override
        public String toString() {
            return unitSymbol;
        }
        
        /**
         * バイト数を決められた単位の値に変換する。
         * @param bytes バイト数の指定
         * @return 変換された値
         */
        public long convert(long bytes) {
            return bytes / ratio;
        }
    }
    
    /**
     * HeapUsagを生成し、ヒープ領域の情報を収集する。
     * doGCがtrueの場合には情報収集前にガベージコレクションを実行する。
     * @param doGC ガベージコレクションを実行するかの指定
     */
    public HeapUsage(boolean doGC) {
        if (doGC) {
            Runtime.getRuntime().gc();
        }
        totalMemory = Runtime.getRuntime().totalMemory();
        freeMemory = Runtime.getRuntime().freeMemory();
        maxMemory = Runtime.getRuntime().maxMemory();
        unit = Unit.KByte;
    }
    
    /**
     * HeapUsagを生成し、ヒープ領域の情報を収集する。
     * 情報収集前にガベージコレクションを実行する。
     */
    public HeapUsage() {
        this(true);
    }
    
    /**
     * 設定された単位を返す。
     * @return 設定された単位
     */
    public Unit getUnit() {
        return unit;
    }
    
    /**
     * 単位を設定する。
     * @param unit 設定する単位
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    /**
     * 全ヒープ領域のサイズを返す。
     * @return 全ヒープ領域のサイズ
     */
    public long getTotalMemory() {
        return getTotalMemory(unit);
    }
    
    /**
     * 使用されていないヒープ領域のサイズを返す。
     * @return 使用されていないヒープ領域のサイズ
     */
    public long getFreeMemory() {
        return getFreeMemory(unit);
    }
    
    /**
     * ヒープ領域の最大サイズを返す。
     * @return ヒープ領域の最大サイズ
     */
    public long getMaxMemory() {
        return getMaxMemory(unit);
    }
    
    /**
     * 使用中のヒープ領域のサイズを返す。
     * @return 使用中のヒープ領域のサイズ
     */
    public long getUsedMemory() {
        return getUsedMemory(unit);
    }
    
    /**
     * 指定された単位で全ヒープ領域のサイズを返す。
     * @param unit 単位の指定
     * @return 全ヒープ領域のサイズ
     */
    public long getTotalMemory(Unit unit) {
        return unit.convert(totalMemory);
    }
    
    /**
     * 指定された単位で使用されていないヒープ領域のサイズを返す。
     * @param unit 単位の指定
     * @return 使用されていないヒープ領域のサイズ
     */
    public long getFreeMemory(Unit unit) {
        return unit.convert(freeMemory);
    }
    
    /**
     * 指定された単位でヒープ領域の最大サイズを返す。
     * @param unit 単位の指定
     * @return ヒープ領域の最大サイズ
     */
    public long getMaxMemory(Unit unit) {
        return unit.convert(maxMemory);
    }
    
    /**
     * 指定された単位で使用中のヒープ領域のサイズを返す。
     * @param unit 単位の指定
     * @return 使用中のヒープ領域のサイズ
     */
    public long getUsedMemory(Unit unit) {
        return unit.convert(totalMemory - freeMemory);
    }
    
    /**
     * 全ヒープ領域のサイズの文字列表現を返す。
     * @return 全ヒープ領域のサイズの文字列表現
     */
    public String toStringTotalMemory() {
        return toStringTotalMemory(unit);
    }
    
    /**
     * 使用されていないヒープ領域のサイズの文字列表現を返す。
     * @return 使用されていないヒープ領域のサイズの文字列表現
     */
    public String toStringFreeMemory() {
        return toStringFreeMemory(unit);
    }
    
    /**
     * ヒープ領域の最大サイズの文字列表現を返す。
     * @return ヒープ領域の最大サイズの文字列表現
     */
    public String toStringMaxMemory() {
        return toStringMaxMemory(unit);
    }
    
    /**
     * 使用中のヒープ領域のサイズの文字列表現を返す。
     * @return 使用中のヒープ領域のサイズの文字列表現
     */
    public String toStringUsedMemory() {
        return toStringUsedMemory(unit);
    }
    
    /**
     * 指定された単位で全ヒープ領域のサイズの文字列表現を返す。
     * @param unit 単位の指定
     * @return 全ヒープ領域のサイズの文字列表現
     */
    public String toStringTotalMemory(Unit unit) {
        return String.format("%d%s", getTotalMemory(), unit);
    }
    
    /**
     * 指定された単位で使用されていないヒープ領域のサイズの文字列表現を返す。
     * @param unit 単位の指定
     * @return 使用されていないヒープ領域のサイズの文字列表現
     */
    public String toStringFreeMemory(Unit unit) {
        return String.format("%d%s", getFreeMemory(), unit );
    }
    
    /**
     * 指定された単位でヒープ領域の最大サイズの文字列表現を返す。
     * @param unit 単位の指定
     * @return ヒープ領域の最大サイズの文字列表現
     */
    public String toStringMaxMemory(Unit unit) {
        return String.format("%d%s", getMaxMemory(), unit);
    }
    
    /**
     * 指定された単位で使用中のヒープ領域のサイズの文字列表現を返す。
     * @param unit 単位の指定
     * @return 使用中のヒープ領域のサイズの文字列表現
     */
    public String toStringUsedMemory(Unit unit) {
        return String.format("%d%s", getUsedMemory(), unit);
    }
    
    /**
     * ヒープ領域使用状況に関する文字列表現を返す。
     * @param unit 単位の指定
     * @return ヒープ領域使用状況に関する文字列表現
     */
    public String toString(Unit unit) {
        String total = toStringTotalMemory(unit);
        String used = toStringUsedMemory(unit);
        String free = toStringFreeMemory(unit);
        String max = toStringMaxMemory(unit);
        return String.format("total: %s used: %s free: %s max: %s", total, used, free, max);
    }
    
    /**
     * ヒープ領域使用状況に関する文字列表現を返す。
     * @return ヒープ領域使用状況に関する文字列表現
     */
    @Override
    public String toString() {
        return toString(unit);
    }
    
    /**
     * 動作テスト用のメインメソッド
     * @param args 引数は利用しない
     */
    public static void main(String[] args) {
        System.out.println(System.getProperty("java.version"));
        System.out.println(new HeapUsage());
    }
}
