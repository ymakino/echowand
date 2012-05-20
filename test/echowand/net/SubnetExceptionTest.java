package echowand.net;

import echowand.net.SubnetException;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class SubnetExceptionTest {
    
    @Test
    public void testAll() {
        SubnetException e1 = new SubnetException("message");
        assertEquals("message", e1.getMessage());
        
        Exception internal = new Exception();
        SubnetException e2 = new SubnetException("message", internal);
        assertEquals(internal, e2.getInternalException());
    }
}
