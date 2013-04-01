package echowand.util;

/**
 *
 * @author ymakino
 */
public class HeapUsage {
    private long totalMemory;
    private long freeMemory;
    private long maxMemory;
    private Unit unit;
    
    public enum Unit {
        Byte(1, "B"),
        KByte(1024, "K"),
        MByte(1024 * 1024, "M");
        
        private long ratio;
        private String unitSymbol;
        
        private Unit(long ratio, String unitSymbol) {
            this.ratio = ratio;
            this.unitSymbol = unitSymbol;
        }
        
        @Override
        public String toString() {
            return unitSymbol;
        }
        
        public long convert(long value) {
            return value / ratio;
        }
    }
    
    public HeapUsage(boolean doGC) {
        if (doGC) {
            Runtime.getRuntime().gc();
        }
        totalMemory = Runtime.getRuntime().totalMemory();
        freeMemory = Runtime.getRuntime().freeMemory();
        maxMemory = Runtime.getRuntime().maxMemory();
        unit = Unit.KByte;
    }
    
    public HeapUsage() {
        this(true);
    }
    
    public Unit getUnit() {
        return unit;
    }
    
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    public long getTotalMemory() {
        return getTotalMemory(unit);
    }
    
    public long getFreeMemory() {
        return getFreeMemory(unit);
    }
    
    public long getMaxMemory() {
        return getMaxMemory(unit);
    }
    
    public long getUsedMemory() {
        return getUsedMemory(unit);
    }
    
    public long getTotalMemory(Unit u) {
        return u.convert(totalMemory);
    }
    
    public long getFreeMemory(Unit u) {
        return u.convert(freeMemory);
    }
    
    public long getMaxMemory(Unit u) {
        return u.convert(maxMemory);
    }
    
    public long getUsedMemory(Unit u) {
        return u.convert(totalMemory - freeMemory);
    }
    
    public String toStringTotalMemory() {
        return toStringTotalMemory(unit);
    }
    
    public String toStringFreeMemory() {
        return toStringFreeMemory(unit);
    }
    
    public String toStringMaxMemory() {
        return toStringMaxMemory(unit);
    }
    
    public String toStringUsedMemory() {
        return toStringUsedMemory(unit);
    }
    
    public String toStringTotalMemory(Unit u) {
        return String.format("%d%s", getTotalMemory(), u);
    }
    
    public String toStringFreeMemory(Unit u) {
        return String.format("%d%s", getFreeMemory(), u);
    }
    
    public String toStringMaxMemory(Unit u) {
        return String.format("%d%s", getMaxMemory(), u);
    }
    
    public String toStringUsedMemory(Unit u) {
        return String.format("%d%s", getUsedMemory(), u);
    }
    
    public String toString(Unit u) {
        String total = toStringTotalMemory(u);
        String used = toStringUsedMemory(u);
        String free = toStringFreeMemory(u);
        String max = toStringMaxMemory(u);
        return String.format("total: %s used: %s free: %s max: %s", total, used, free, max);
    }
    
    @Override
    public String toString() {
        return toString(unit);
    }
    
    public static void main(String[] args) {
        System.out.println(System.getProperty("java.version"));
        System.out.println(new HeapUsage());
    }
}
