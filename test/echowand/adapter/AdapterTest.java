/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package echowand.adapter;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class AdapterTest {
    
    public AdapterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of sendFrame method, of class Adapter.
     */
    @Test
    public void testSendFrame() {
        /*
        System.out.println("sendFrame");
        Frame frame = null;
        Adapter instance = new Adapter();
        instance.sendFrame(frame);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        */
    }

    /**
     * Test of addObserver method, of class Adapter.
     */
    @Test
    public void testAddObserver() {
        /*
        System.out.println("addObserver");
        Observer observer = null;
        Adapter instance = new Adapter();
        instance.addObserver(observer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        */
    }
    
    @Test
    public void testCreation() {
        Adapter adapter = new Adapter();
        assertEquals(Adapter.UNIDENTIFIED, adapter.getState());
    }
    
    @Test
    public void t() {
        
    }
}
