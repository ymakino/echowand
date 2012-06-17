package echowand.app;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TransactionManager;
import echowand.net.LocalSubnet;
import echowand.net.Subnet;
import echowand.object.RemoteObject;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectTableModelTest {
    
    Subnet subnet;
    TransactionManager transactionManager;
    RemoteObject remoteObject;
    
    @Before
    public void setUp() {
        subnet = new LocalSubnet();
        transactionManager = new TransactionManager(subnet);
        remoteObject = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("0ef001"), transactionManager);
    }

    /**
     * Test of refreshCache method, of class ObjectTableModel.
     */
    @Test
    public void testRefreshCache() {
        System.out.println("refreshCache");
        ObjectTableModel instance = new ObjectTableModel();
        instance.refreshCache();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of stopRefreshCache method, of class ObjectTableModel.
     */
    @Test
    public void testStopRefreshCache() {
        System.out.println("stopRefreshCache");
        ObjectTableModel instance = new ObjectTableModel();
        instance.stopRefreshCache();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setRemoteObject method, of class ObjectTableModel.
     */
    @Test
    public void testSetRemoteObject() {
        ObjectTableModel instance = new ObjectTableModel();
        instance.setCachedObject(new CachedRemoteObject(remoteObject));
        assertEquals(1, remoteObject.countObservers());
        instance.setCachedObject(null);
        assertEquals(0, remoteObject.countObservers());
    }

    /**
     * Test of fireEPCDataUpdated method, of class ObjectTableModel.
     */
    @Test
    public void testFireEPCDataUpdated() {
        System.out.println("fireEPCDataUpdated");
        EPC epc = null;
        ObjectTableModel instance = new ObjectTableModel();
        instance.fireEPCDataUpdated(epc);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of release method, of class ObjectTableModel.
     */
    @Test
    public void testRelease() {
        System.out.println("release");
        ObjectTableModel instance = new ObjectTableModel();
        instance.release();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowCount method, of class ObjectTableModel.
     */
    @Test
    public void testGetRowCount() {
        System.out.println("getRowCount");
        ObjectTableModel instance = new ObjectTableModel();
        int expResult = 0;
        int result = instance.getRowCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColumnClass method, of class ObjectTableModel.
     */
    @Test
    public void testGetColumnClass() {
        System.out.println("getColumnClass");
        int column = 0;
        ObjectTableModel instance = new ObjectTableModel();
        Class expResult = null;
        Class result = instance.getColumnClass(column);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColumnName method, of class ObjectTableModel.
     */
    @Test
    public void testGetColumnName() {
        System.out.println("getColumnName");
        int column = 0;
        ObjectTableModel instance = new ObjectTableModel();
        String expResult = "";
        String result = instance.getColumnName(column);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColumnCount method, of class ObjectTableModel.
     */
    @Test
    public void testGetColumnCount() {
        System.out.println("getColumnCount");
        ObjectTableModel instance = new ObjectTableModel();
        int expResult = 0;
        int result = instance.getColumnCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getValueAt method, of class ObjectTableModel.
     */
    @Test
    public void testGetValueAt() {
        System.out.println("getValueAt");
        int rowIndex = 0;
        int columnIndex = 0;
        ObjectTableModel instance = new ObjectTableModel();
        Object expResult = null;
        Object result = instance.getValueAt(rowIndex, columnIndex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setValueAt method, of class ObjectTableModel.
     */
    @Test
    public void testSetValueAt() {
        System.out.println("setValueAt");
        Object aValue = null;
        int row = 0;
        int column = 0;
        ObjectTableModel instance = new ObjectTableModel();
        instance.setValueAt(aValue, row, column);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isCellEditable method, of class ObjectTableModel.
     */
    @Test
    public void testIsCellEditable() {
        System.out.println("isCellEditable");
        int rowIndex = 0;
        int columnIndex = 0;
        ObjectTableModel instance = new ObjectTableModel();
        boolean expResult = false;
        boolean result = instance.isCellEditable(rowIndex, columnIndex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
