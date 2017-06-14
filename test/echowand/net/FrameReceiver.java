package echowand.net;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
class FrameReceiver extends Thread {
    public Subnet subnet;
    public Frame receivedFrame;

    public FrameReceiver(Subnet subnet) {
        this.subnet = subnet;
    }
    
    public Frame getReceivedFrame() {
        try {
            for (int i=0; i<5; i++) {
                Thread.sleep(100);
                if (!this.isAlive()) {
                    break;
                }
            }
            
            interrupt();
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return receivedFrame;
    }

    @Override
    public void run() {
        try {
            receivedFrame = subnet.receive();
        } catch (SubnetException e) {
            e.printStackTrace();
        }
    }
}
