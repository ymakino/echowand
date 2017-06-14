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
public class InternalNodeTest {
    
    public InternalNodeTest() {
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
    
    @Test
    public void testCreation() throws SubnetException {
        InternalSubnet subnet1 = InternalSubnet.startSubnet();
        InternalSubnet subnet2 = InternalSubnet.startSubnet();
        
        InternalNode node1 = new InternalNode(subnet1, "node1");
        InternalNode node2 = new InternalNode(subnet2, "node2");
        
        assertEquals("node1", node1.getName());
        assertEquals("node2", node2.getName());
        
        assertTrue(node1.isMemberOf(subnet1));
        assertFalse(node1.isMemberOf(subnet2));
        assertFalse(node2.isMemberOf(subnet1));
        assertTrue(node2.isMemberOf(subnet2));
    }
}
