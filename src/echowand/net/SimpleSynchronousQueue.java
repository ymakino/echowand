package echowand.net;

/**
 * データを同期的に他のスレッドに渡すために利用する同期キュー
 * @author ymakino
 */
public class SimpleSynchronousQueue<T> {
    private boolean enabled = true;
    private boolean processing = false;
    private T currentValue;
    
    private synchronized void waitOrThrow() throws SimpleSynchronousQueueException, InterruptedException {
        wait();

        if (!enabled) {
            throw new SimpleSynchronousQueueException("invalid queue");
        }
    }
    
    /**
     * 同期キューからデータを取得する。
     * データが存在しない場合には、データが渡されるまで待機する。
     * @return 取得したデータ
     * @throws SimpleSynchronousQueueException キューが利用不可能の場合
     * @throws InterruptedException 割り込みが発生した場合
     */
    public synchronized T take() throws SimpleSynchronousQueueException, InterruptedException {
        if (!enabled) {
            throw new SimpleSynchronousQueueException("invalid queue");
        }

        while (currentValue == null) {
            waitOrThrow();
        }

        T value = currentValue;
        currentValue = null;

        notifyAll();

        return value;
    }
    
    /**
     * 同期キューにデータを追加する。
     * 他のスレッドがデータを取得するまで待機する。
     * @param value 受け渡すデータ
     * @throws SimpleSynchronousQueueException キューが利用不可能の場合
     * @throws InterruptedException 割り込みが発生した場合
     */
    public synchronized void put(T value) throws SimpleSynchronousQueueException, InterruptedException {
        if (!enabled) {
            throw new SimpleSynchronousQueueException("invalid queue");
        }

        while (processing) {
            waitOrThrow();
        }
        
        try {
            processing = true;
            
            currentValue = value;

            notifyAll();

            while (currentValue != null) {
                waitOrThrow();
            }
        } finally {
            processing = false;
        }
    }
    
    /**
     * このキューを利用可能にする。
     */
    public synchronized void enable() {
        if (!enabled) {
            currentValue = null;
            enabled = true;
        }
    }
    
    /**
     * このキューを利用不可能にする。
     * 利用不可能なキューを利用しようとすると例外が発生するようになる。
     */
    public synchronized void disable() {
        enabled = false;
        notifyAll();
    }
    
    /**
     * このキューが利用可能であるか返す。
     * @return 利用可能であればtrue、利用不可能であればfalse
     */
    public boolean isEnabled() {
        return enabled;
    }
}