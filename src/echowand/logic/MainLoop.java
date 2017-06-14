package echowand.logic;

import echowand.net.Frame;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 送受信のメインループを実行する。
 * runメソッドを実行する事で、指定されたサブネットからフレームを受信し、登録されたListenerのprocessメソッドを呼び出す。
 * Runnableインタフェースを実装しているので、Threadとして動作させることができる。
 * @author Yoshiki Makino
 */
public class MainLoop implements Runnable {
    private static final Logger logger = Logger.getLogger(MainLoop.class.getName());
    private static final String className = MainLoop.class.getName();
    
    private Subnet subnet;
    private LinkedList<Listener> listeners;
    
    /**
     * MainLoopを生成する。
     */
    public MainLoop() {
        logger.entering(className, "MainLoop");
        
        this.listeners = new LinkedList<Listener>();
        
        logger.exiting(className, "MainLoop");
    }

    /**
     * サブネットを設定する。
     * @param subnet 設定するサブネット
     */
    public void setSubnet(Subnet subnet) {
        logger.entering(className, "setSubnet", subnet);
        
        this.subnet = subnet;
        
        logger.exiting(className, "setSubnet");
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
    public Frame receiveFrame() throws SubnetException {
        logger.entering(className, "receiveFrame");
        
        Frame frame = subnet.receive();
        
        logger.exiting(className, "receiveFrame", frame);
        return frame;
    }
    
    /**
     * 指定されたListenerを登録する
     * @param listener 登録するListener
     */
    public synchronized void addListener(Listener listener) {
        logger.entering(className, "addListener", listener);
        
        listeners.add(listener);
        
        logger.exiting(className, "addListener");
    }
    
    /**
     * 指定されたListenerを登録する
     * @param index 登録するListenerのインデックス
     * @param listener 登録するListener
     */
    public synchronized void addListener(int index, Listener listener) {
        logger.entering(className, "addListener", new Object[]{index, listener});
        
        listeners.add(index, listener);
        
        logger.exiting(className, "addListener");
    }
    
    /**
     * 指定されたListenerの登録を抹消する。
     * @param listener 登録を抹消するListener
     */
    public synchronized void removeListener(Listener listener) {
        logger.entering(className, "removeListener", listener);
        
        listeners.remove(listener);
        
        logger.exiting(className, "removeListener");
    }
    
    /**
     * 登録された全Listener数を返す。
     * @return 登録されているListenerの数
     */
    public synchronized int countListeners() {
        logger.entering(className, "countListeners");
        
        int count = listeners.size();
        
        logger.exiting(className, "countListeners", count);
        return count;
    }
    
    /**
     * index番目に登録されているListenerを返す。
     * @param index Listenerのインデックス
     * @return 指定されたListener
     */
    public synchronized Listener getListener(int index) {
        logger.entering(className, "getListener", index);
        
        Listener listener = listeners.get(index);
        
        logger.exiting(className, "getListener");
        return listener;
    }
    
    private synchronized void invokeListeners(Frame frame) {
        logger.entering(className, "invokeListeners", frame);
        
        boolean processed = false;
        for (Listener listener : new ArrayList<Listener>(listeners)) {
            processed |= listener.process(subnet, frame, processed);
        }
        
        logger.exiting(className, "invokeListeners");
    }

    /**
     * メインループを実行する。
     * サブネットからフレームを受信し、全てのListenerのprocessを呼び出す。
     */
    @Override
    public void run() {
        logger.entering(className, "run");

        try {
            for (;;) {
                
                if (Thread.currentThread().isInterrupted()) {
                    logger.logp(Level.INFO, className, "run", "interrupted");
                    break;
                }
                
                Frame frame = receiveFrame();
                invokeListeners(frame);
            }
        } catch (SubnetException ex) {
            logger.logp(Level.INFO, className, "run", "cannot receive frames", ex);
        }
        
        logger.exiting(className, "run");
    }
}
