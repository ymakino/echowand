package echowand.util;

import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

class DoubleRange {
    private long count = 0;
    private double ave = 0;
    private double min = 0;
    private double max = 0;

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
    private PrintWriter writer;
    private long interval;
    private long count;

    /**
     * 指定されたインターバルで表示を行なうHeapUsageReporterを生成する。
     * 標準の出力先はSystem.outになる。
     * @param interval インターバル(ミリ秒)の指定
     */
    public HeapUsageReporter(int interval) {
        total= new DoubleRange();
        used = new DoubleRange();
        free = new DoubleRange();
        max  = new DoubleRange();
        this.interval = interval;
        writer = new PrintWriter(System.out);
        count = 0;
    }

    /**
     * 指定されたインターバルで表示を行なうHeapUsageReporterを生成する。
     * @param interval インターバル(ミリ秒)の指定
     * @param writer  出力先の指定
     */
    public HeapUsageReporter(int interval, PrintWriter writer) {
        total= new DoubleRange();
        used = new DoubleRange();
        free = new DoubleRange();
        max  = new DoubleRange();
        this.interval = interval;
        this.writer = writer;
        count = 0;
    }

    /**
     * 現在のヒープの使用量を表すHeapUsageを返す。
     * @return 現在のヒープの使用量を表すHeapUsage
     */
    public HeapUsage getHeapUsage() {
        HeapUsage usage = new HeapUsage();
        total.add(usage.getTotalMemory());
        used.add(usage.getUsedMemory());
        free.add(usage.getFreeMemory());
        max.add(usage.getMaxMemory());
        count++;
        return usage;
    }
    
    private void println(Object obj) {
        writer.println(obj.toString());
        writer.flush();
    }
    
    private void printf(String format, Object... args) {
        writer.printf(format, args);
        writer.flush();
    }

    /**
     * ヒープの使用量の定期的な表示を開始する。
     */
    public void start() {
        println(new HeapUsage());
        new Timer().schedule(this, interval, interval);
    }

    /**
     * ヒープの使用量を表示する。
     */
    @Override
    public void run() {
        HeapUsage usage = getHeapUsage();
        String u = usage.getUnit().toString();
        printf("%d %s\n", count, usage);
        printf("\tAVE used: %d%s, free: %d%s\n", (long)used.getAve(), u, (long)free.getAve(), u);
        printf("\tMIN used: %d%s, free: %d%s\n", (long)used.getMin(), u, (long)free.getMin(), u);
        printf("\tMAX used: %d%s, free: %d%s\n", (long)used.getMax(), u, (long)free.getMax(), u);
    }

    /**
     * ヒープの使用量を表示するテスト用のmainスレッドになる。
     * 1秒毎にヒープの使用量を表示し、1分後に終了する。
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