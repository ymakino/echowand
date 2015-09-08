package echowand.net;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class InternalNodeInfoTest {
    
    public InternalNodeInfoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class InternalNodeInfo.
     */
    @Test
    public void testGetName() {
        InternalNodeInfo info = new InternalNodeInfo("test");
        assertEquals("test", info.getName());
    }

    /**
     * Test of toString method, of class InternalNodeInfo.
     */
    @Test
    public void testToString() {
        InternalNodeInfo info = new InternalNodeInfo("test");
        assertEquals("test", info.toString());
    }

    /**
     * Test of equals method, of class InternalNodeInfo.
     */
    @Test
    public void testEquals() {
        InternalNodeInfo info10 = new InternalNodeInfo("test1");
        InternalNodeInfo info11 = new InternalNodeInfo("test1");
        InternalNodeInfo info20 = new InternalNodeInfo("test2");
        
        assertTrue(info10.equals(info11));
        assertTrue(info11.equals(info10));
        assertFalse(info10.equals(info20));
        assertFalse(info20.equals(info10));
    }

    /**
     * Test of hashCode method, of class InternalNodeInfo.
     */
    @Test
    public void testHashCode() {
        InternalNodeInfo info10 = new InternalNodeInfo("test1");
        InternalNodeInfo info11 = new InternalNodeInfo("test1");
        InternalNodeInfo info20 = new InternalNodeInfo("test2");
        
        assertTrue(info10.hashCode() == info11.hashCode());
        assertTrue(info10.hashCode() != info20.hashCode());
    }
    
}
