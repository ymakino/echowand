package echowand.logic;

import echowand.net.Frame;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 送受信のメインループを実行する。
 * runメソッドを実行する事で、指定されたサブネットからフレームを受信し、登録されたListenerのprocessメソッドを呼び出す。
 * Runnableインタフェースを実装しているので、Threadとして動作させることができる。
 * @author Yoshiki Makino
 */
public class MainLoop implements Runnable {
    private Subnet subnet;
    private LinkedList<Listener> listeners;
    
    /**
     * MainLoopを生成する。
     */
    public MainLoop() {
        this.listeners = new LinkedList<Listener>();
    }

    /**
     * サブネットを設定する。
     * @param subnet 設定するサブネット
     */
    public void setSubnet(Subnet subnet) {
        this.subnet = subnet;
    }
    
    /**
     * 設定されたサブネットを取得する。
     * @return 設定されたサブネット
     */
    public Subnet getSubnet() {
        return this.subnet;
    }
    
    /**
     * サブネットからフレームを受信する。
     * @return 受信したフレーム
     * @throws SubnetException 受信に失敗した場合
     */
    public Frame recvFrame() throws SubnetException {
        return subnet.recv();
    }
    
    /**
     * 指定されたListenerを登録する
     * @param listener 登録するListener
     */
    public synchronized void addListener(Listener listener) {
        listeners.add(listener);
    }
    
    /**
     * 指定されたListenerの登録を抹消する。
     * @param listener 登録を抹消するListener
     */
    public synchronized void removeListener(Listener listener) {
        listeners.remove(listener);
    }
    
    /**
     * 登録された全Listener数を返す。
     * @return 登録されているListenerの数
     */
    public synchronized int countListeners() {
        return listeners.size();
    }
    
    private synchronized void invokeListeners(Frame frame) {
        boolean processed = false;
        for (Listener listener : new ArrayList<Listener>(listeners)) {
            processed |= listener.process(subnet, frame, processed);
        }
    }

    /**
     * メインループを実行する。
     * サブネットからフレームを受信し、全てのListenerのprocessを呼び出す。
     */
    @Override
    public void run() {
        for (;;) {
            try {
                Frame frame = recvFrame();
                invokeListeners(frame);
            } catch(SubnetException e) {
                e.printStackTrace();
            }
        }
    }
}
