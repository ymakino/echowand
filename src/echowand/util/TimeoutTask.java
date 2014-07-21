package echowand.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class TimeoutTask extends TimerTask {
    
    private TimeoutObserver target;
    
    private static Timer timer;

    private long timeout;
    private boolean done;
    private boolean terminated;
    
    static {
        timer = new Timer();
    }

    public TimeoutTask(long timeout) {
        this(null, timeout);
    }

    public TimeoutTask(TimeoutObserver target, long timeout) {
        this.target = target;
        this.timeout = timeout;
        done = false;
        terminated = false;
    }
    
    public void start() {
        timer.schedule(this, timeout);
    }

    public synchronized boolean isDone() {
        return done;
    }
    
    public synchronized boolean isTerminated() {
        return terminated;
    }

    @Override
    public synchronized void run() {
        if (!terminated) {
            done = true;
            
            if (target != null) {
                target.notifyTimeout(this);
            }
        }
    }

    public synchronized void terminate() {
        terminated = true;
        cancel();
    }
}