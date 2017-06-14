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
    
    private TimeoutObserver observer;
    
    private static Timer timer;

    private long timeoutPeriod;
    private boolean timedOut;
    private boolean interrupted;
    
    static {
        timer = new Timer();
    }

    public TimeoutTask(long timeout) {
        this(null, timeout);
    }

    public TimeoutTask(TimeoutObserver observer, long timeoutPeriod) {
        this.observer = observer;
        this.timeoutPeriod = timeoutPeriod;
        timedOut = false;
        interrupted = false;
    }
    
    public void start() {
        timer.schedule(this, timeoutPeriod);
    }

    public boolean isTimedOut() {
        return timedOut;
    }
    
    public boolean isInterrupted() {
        return interrupted;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (interrupted) {
                return;
            }

            timedOut = true;
        }

        if (observer != null) {
            observer.notifyTimeout(this);
        }
    }

    public synchronized boolean interrupt() {
        if (timedOut) {
            return false;
        }
        
        interrupted = true;
        cancel();
        
        return true;
    }
}