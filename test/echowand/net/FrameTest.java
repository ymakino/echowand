package echowand.net;

import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Node;
import echowand.net.LocalSubnet;
import echowand.net.SubnetException;
import echowand.common.ESV;
import echowand.common.EOJ;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.*;
/**
 *
 * @author Yoshiki Makino
 */
public class FrameTest {
    
    @Test
    public void testCreation() {
        LocalSubnet subnet = new LocalSubnet();
        
        Node node = subnet.getGroupNode();
        CommonFrame cf = new CommonFrame(new EOJ("001101"), new EOJ("002201"), ESV.Get);
        Frame frame = new Frame(node, node, cf);
        assertEquals(node, frame.getReceiver());
        assertEquals(cf, frame.getCommonFrame());


        try {
            subnet.send(frame);
            assertTrue(Arrays.equals(frame.getCommonFrame().toBytes(), subnet.recv().getCommonFrame().toBytes()));
        } catch (SubnetException e) {
            e.printStackTrace();
            fail();
        }
    }
}
