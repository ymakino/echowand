package echowand.logic;

import echowand.logic.MainLoop;
import echowand.logic.Listener;
import echowand.object.LocalObjectManager;
import echowand.net.Frame;
import echowand.net.LocalSubnet;
import echowand.net.Subnet;
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
    public void testCreation() {
        MainLoop echonet = new MainLoop();
        LocalSubnet subnet = new LocalSubnet();
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
