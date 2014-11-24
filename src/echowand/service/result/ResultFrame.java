package echowand.service.result;

import echowand.net.Frame;

/**
 *
 * @author ymakino
 */
public class ResultFrame {
    public final Frame frame;
    public final long time;
    
    public ResultFrame(Frame frame, long time) {
        this.frame = frame;
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "{Frame: " + frame + ", Time: " + time + "}";
    }
}
