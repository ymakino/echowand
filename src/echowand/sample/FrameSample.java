package echowand.sample;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.ESV;
import echowand.net.*;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class FrameSample {
    public static void main(String[] args) {
        try {
            Inet4Subnet subnet = Inet4Subnet.startSubnet();

            CommonFrame commonFrame = new CommonFrame(new EOJ("0ef001"), new EOJ("001101"), ESV.Get);
            StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
            payload.addFirstProperty(new Property(EPC.x80));
            payload.addFirstProperty(new Property(EPC.xE0));
            
            // Frame frame = new Frame(subnet.getLocalNode(), subnet.getGroupNode(), commonFrame);
            Frame frame = new Frame(subnet.getLocalNode(), subnet.getRemoteNode(InetAddress.getByName("127.0.0.1")), commonFrame);
            subnet.send(frame);
            for (;;) {
                System.out.println("Received: " + subnet.receive());
            }
        } catch (Exception ex) {
            Logger.getLogger(FrameSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
