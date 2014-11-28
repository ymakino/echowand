package echowand.net;

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
            this.stop();
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
