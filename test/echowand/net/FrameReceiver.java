package echowand.net;

/**
 *
 * @author ymakino
 */
class FrameReceiver extends Thread {
    public Subnet subnet;
    public Frame recvFrame;

    public FrameReceiver(Subnet subnet) {
        this.subnet = subnet;
    }
    
    public Frame getRecvFrame() {
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
        return recvFrame;
    }

    @Override
    public void run() {
        try {
            recvFrame = subnet.receive();
        } catch (SubnetException e) {
            e.printStackTrace();
        }
    }
}
