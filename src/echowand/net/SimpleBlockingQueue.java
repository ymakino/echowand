package echowand.net;

import java.util.LinkedList;

/**
 * 他のスレッドにデータを受け渡すために利用する同期キュー
 * @author ymakino
 */
public class SimpleBlockingQueue<T> {
    private boolean enabled = true;
    private LinkedList<T> queue = new LinkedList<T>();

    /**
     * データをキューに追加する。
     * @param value 追加するデータ
     * @throws InvalidQueueException キューが利用不可能の場合
     * @throws InterruptedException 割り込みが発生した場合
     */
    public synchronized void add(T value) throws InvalidQueueException {
        if (!enabled) {
            throw new InvalidQueueException("invalid queue");
        }

        queue.add(value);
        notifyAll();
    }

    /**
     * キューに入っているデータを取得する。
     * データが存在しない場合には、データが渡されるまで待機する。
     * @return 取得したデータ
     * @throws InvalidQueueException キューが利用不可能の場合
     * @throws InterruptedException 割り込みが発生した場合
     */
    public synchronized T take() throws InterruptedException, InvalidQueueException {
        if (!enabled) {
            throw new InvalidQueueException("invalid queue");
        }

        while (queue.isEmpty()) {
            wait();

            if (!enabled) {
                throw new InvalidQueueException("invalid queue");
            }
        }

        return queue.pop();
    }

    /**
     * キューが空であるか返す。
     * @return キューが空であればtrue、空でなければfalse
     */
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
    
    /**
     * このキューを利用可能にする。
     */
    public synchronized void enable() {
        if (!enabled) {
            queue.clear();
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
