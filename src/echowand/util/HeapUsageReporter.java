package echowand.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

class DoubleRange {
    long count = 0;
    double ave = 0;
    double min = 0;
    double max = 0;

    public void add(double d) {
        if (count == 0) {
            ave = d;
            min = d;
            max = d;
        } else {
            ave = (count * ave + d) / (count + 1);
            if (d < min) {
                min = d;
            }
            if (d > max) {
                max = d;
            }
        }
        count++;
    }

    public double getAve() {
        return ave;
    }

    public double getMin() {
        return min;
    }
    
    public double getMax() {
        return max;
    }
}

/**
 * ヒープの使用量を定期的に表示
 * @author ymakino
 */
public class HeapUsageReporter extends TimerTask {
    private DoubleRange total;
    private DoubleRange used;
    private DoubleRange free;
    private DoubleRange max;
    private long interval;
    private boolean GCEnabled;
    private long count;

    /**
     * 指定されたインターバルで表示を行なうHeapUsageReporterを生成する。
     * 標準では、ヒープ情報取得前にGCが実行される。
     * @param interval インターバル(ミリ秒)の指定
     */
    public HeapUsageReporter(int interval) {
        this(interval, true);
    }

    /**
     * 指定されたインターバルで表示を行なうHeapUsageReporterを生成する。
     * @param interval インターバル(ミリ秒)の指定
     * @param GCEnabled ヒープ情報取得前にGCを動作させるかどうかの指定
     */
    public HeapUsageReporter(int interval, boolean GCEnabled) {
        total= new DoubleRange();
        used = new DoubleRange();
        free = new DoubleRange();
        max  = new DoubleRange();
        this.interval = interval;
        this.GCEnabled = GCEnabled;
        count = 0;
    }

    /**
     * 現在のヒープの使用量を表すHeapUsageを返す。
     * @return 現在のヒープの使用量を表すHeapUsage
     */
    public HeapUsage getHeapUsage() {
        HeapUsage usage = new HeapUsage(GCEnabled);
        total.add(usage.getTotalMemory());
        used.add(usage.getUsedMemory());
        free.add(usage.getFreeMemory());
        max.add(usage.getMaxMemory());
        count++;
        return usage;
    }
    
    /**
     * ヒープ情報取得前にGCを動作させるように設定する。
     */
    public void enableGC() {
        GCEnabled = true;
    }
    
    /**
     * ヒープ情報取得前にGCを動作させないように設定する。
     */
    public void disableGC() {
        GCEnabled = false;
    }
    
    /**
     * ヒープ情報取得前にGCを動作させるように設定されているかを返す。
     * @return GCを動作させるように設定されていれば真、それ以外の場合には偽
     */
    public boolean isGCEnabled() {
        return GCEnabled;
    }

    /**
     * ヒープの使用量の定期的な表示を開始する。
     */
    public void start() {
        System.out.println(new HeapUsage());
        new Timer().schedule(this, interval, interval);
    }

    /**
     * ヒープの使用量を表示する。
     */
    @Override
    public void run() {
        HeapUsage usage = getHeapUsage();
        String u = usage.getUnit().toString();
        System.out.printf("%d %s\n", count, usage);
        System.out.printf("\tAVE used: %d%s, free: %d%s\n", (long)used.getAve(), u, (long)free.getAve(), u);
        System.out.printf("\tMIN used: %d%s, free: %d%s\n", (long)used.getMin(), u, (long)free.getMin(), u);
        System.out.printf("\tMAX used: %d%s, free: %d%s\n", (long)used.getMax(), u, (long)free.getMax(), u);
    }

    /**
     * ヒープの使用量を表示するテスト用のmainスレッド
     * @param args コマンドラインの引数(使用しない)
     */
    public static void main(String[] args) {

        new echowand.util.HeapUsageReporter(1000).start();
        
        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException ex) {
            Logger.getLogger(HeapUsageReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }
}