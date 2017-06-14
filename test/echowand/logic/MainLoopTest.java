package echowand.logic;

import echowand.logic.MainLoop;
import echowand.logic.Listener;
import echowand.object.LocalObjectManager;
import echowand.net.Frame;
import echowand.net.InternalSubnet;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
class DummyListener implements Listener {
    @Override
    public boolean process(Subnet subnet, Frame frame, boolean processed) {
        return false;
    }
}
public class MainLoopTest {
    
    @Test
    public void testCreation() throws SubnetException {
        MainLoop echonet = new MainLoop();
        InternalSubnet subnet = InternalSubnet.startSubnet();
        echonet.setSubnet(subnet);
        assertEquals(subnet, echonet.getSubnet());
        
        Listener listener = new DummyListener();
        assertEquals(0, echonet.countListeners());
        echonet.addListener(listener);
        assertEquals(1, echonet.countListeners());
        echonet.removeListener(listener);
        assertEquals(0, echonet.countListeners());
    }
}
