package echowand.app;

import echowand.common.*;
import echowand.logic.TransactionManager;
import echowand.net.*;
import echowand.object.ObjectData;
import echowand.object.RemoteObject;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectTableModelTest {
    
    Subnet subnet;
    TransactionManager transactionManager;
    RemoteObject remoteObject1;
    RemoteObject remoteObject2;
    
    @Before
    public void setUp() {
        subnet = new InternalSubnet();
        transactionManager = new TransactionManager(subnet);
        remoteObject1 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("0ef001"), transactionManager);
        remoteObject2 = new RemoteObject(subnet, subnet.getLocalNode(), new EOJ("001101"), transactionManager);
    }

    /**
     * Test of refreshCache method, of class ObjectTableModel.
     */
    @Test
    public void testRefreshCache() {
        ObjectTableModel model = new ObjectTableModel();
        
        model.setCachedObject(new CachedRemoteObject(remoteObject1));
        CachedRemoteObject object = model.getCachedObject();
        
        assertFalse(object.isCached(EPC.x80));
        
        object.setCachedData(EPC.x80, new ObjectData(new byte[]{0x00}));
        
        assertTrue(object.isCached(EPC.x80));
        
        model.refreshCache();
        
        assertTrue(model.isRefreshingCache());
        assertFalse(object.isCached(EPC.x80));
    }

    /**
     * Test of stopRefreshCache method, of class ObjectTableModel.
     */
    @Test
    public void testStopRefreshCache() {
        ObjectTableModel model = new ObjectTableModel();
        
        model.setCachedObject(new CachedRemoteObject(remoteObject1));
        model.stopRefreshCache();
        
        assertFalse(model.isRefreshingCache());
        
        model.refreshCache();
        
        assertTrue(model.isRefreshingCache());
        
        model.stopRefreshCache();
        
        assertFalse(model.isRefreshingCache());
    }

    /**
     * Test of setRemoteObject method, of class ObjectTableModel.
     */
    @Test
    public void testSetRemoteObject() {
        ObjectTableModel model = new ObjectTableModel();
        CachedRemoteObject cachedObject1 = new CachedRemoteObject(remoteObject1);
        CachedRemoteObject cachedObject2 = new CachedRemoteObject(remoteObject2);
        
        assertEquals(0, cachedObject1.countObservers());
        assertEquals(0, cachedObject2.countObservers());
        
        model.setCachedObject(cachedObject1);
        
        assertTrue(model.isRefreshingCache());
        assertEquals(1, cachedObject1.countObservers());
        assertEquals(0, cachedObject2.countObservers());
        
        model.setCachedObject(cachedObject2);
        
        assertEquals(0, cachedObject1.countObservers());
        assertEquals(1, cachedObject2.countObservers());
        
        model.setCachedObject(null);
        
        assertEquals(0, cachedObject1.countObservers());
        assertEquals(0, cachedObject2.countObservers());
    }

    /**
     * Test of release method, of class ObjectTableModel.
     */
    @Test
    public void testRelease() {
        ObjectTableModel model = new ObjectTableModel();
        model.setCachedObject(new CachedRemoteObject(remoteObject1));
        assertFalse(model.getCachedObject() == null);
        model.release();
        assertTrue(model.getCachedObject() == null);
    }

    /**
     * Test of getRowCount method, of class ObjectTableModel.
     */
    @Test
    public void testGetRowCount() {
        ObjectTableModel model = new ObjectTableModel();
        assertEquals(0, model.getRowCount());
        
        CachedRemoteObject cachedObject1 = new CachedRemoteObject(remoteObject1);
        model.setCachedObject(cachedObject1);
        model.stopRefreshCache();
        
        assertEquals(0, model.getRowCount());
        
        PropertyMap map = new PropertyMap();
        map.set(EPC.x80);
        map.set(EPC.x88);
        map.set(EPC.xE0);
        cachedObject1.setCachedData(EPC.x9D, new ObjectData(map.toBytes()));
        cachedObject1.setCachedData(EPC.x9E, new ObjectData(map.toBytes()));
        cachedObject1.setCachedData(EPC.x9F, new ObjectData(map.toBytes()));
        assertEquals(3, model.getRowCount());
    }

    /**
     * Test of getColumnClass method, of class ObjectTableModel.
     */
    @Test
    public void testGetColumnClass() {
        ObjectTableModel model = new ObjectTableModel();
        assertEquals(EPC.class, model.getColumnClass(0));
        assertEquals(Boolean.class, model.getColumnClass(1));
        assertEquals(Boolean.class, model.getColumnClass(2));
        assertEquals(Boolean.class, model.getColumnClass(3));
        assertEquals(Integer.class, model.getColumnClass(4));
        assertEquals(ObjectData.class, model.getColumnClass(5));
        assertEquals(String.class, model.getColumnClass(6));
    }

    /**
     * Test of getColumnName method, of class ObjectTableModel.
     */
    @Test
    public void testGetColumnName() {
        ObjectTableModel model = new ObjectTableModel();
        assertEquals("EPC", model.getColumnName(0));
        assertEquals("Get", model.getColumnName(1));
        assertEquals("Set", model.getColumnName(2));
        assertEquals("Anno", model.getColumnName(3));
        assertEquals("Size", model.getColumnName(4));
        assertEquals("Data", model.getColumnName(5));
        assertEquals("Formatted", model.getColumnName(6));
    }

    /**
     * Test of getColumnCount method, of class ObjectTableModel.
     */
    @Test
    public void testGetColumnCount() {
        ObjectTableModel model = new ObjectTableModel();
        assertEquals(7, model.getColumnCount());
    }

    /**
     * Test of getValueAt method, of class ObjectTableModel.
     */
    @Test
    public void testGetValueAt() {
        ObjectTableModel model = new ObjectTableModel();
        assertEquals(0, model.getRowCount());
        
        CachedRemoteObject cachedObject1 = new CachedRemoteObject(remoteObject1);
        model.setCachedObject(cachedObject1);
        model.stopRefreshCache();
        
        assertEquals(0, model.getRowCount());
        
        PropertyMap map = new PropertyMap();
        map.set(EPC.x80);
        cachedObject1.setCachedData(EPC.x9D, new ObjectData(map.toBytes()));
        map.set(EPC.x88);
        cachedObject1.setCachedData(EPC.x9E, new ObjectData(map.toBytes()));
        map.set(EPC.xE0);
        cachedObject1.setCachedData(EPC.x9F, new ObjectData(map.toBytes()));
        cachedObject1.setCachedData(EPC.x80, new ObjectData((byte)0x30));
        cachedObject1.setCachedData(EPC.x88, new ObjectData((byte)0x42));
        cachedObject1.setCachedData(EPC.xE0, new ObjectData((byte)0x12, (byte)0x34));
        
        assertEquals(EPC.x80, model.getValueAt(0, 0));
        assertEquals(true, model.getValueAt(0, 1));
        assertEquals(true, model.getValueAt(0, 2));
        assertEquals(true, model.getValueAt(0, 3));
        assertEquals(1, model.getValueAt(0, 4));
        assertEquals(new ObjectData((byte)0x30), model.getValueAt(0, 5));
        assertEquals("ON", model.getValueAt(0, 6));
        
        assertEquals(EPC.x88, model.getValueAt(1, 0));
        assertEquals(true, model.getValueAt(1, 1));
        assertEquals(true, model.getValueAt(1, 2));
        assertEquals(false, model.getValueAt(1, 3));
        assertEquals(1, model.getValueAt(1, 4));
        assertEquals(new ObjectData((byte)0x42), model.getValueAt(1, 5));
        assertEquals("NORMAL", model.getValueAt(1, 6));
        
        assertEquals(EPC.xE0, model.getValueAt(2, 0));
        assertEquals(true, model.getValueAt(2, 1));
        assertEquals(false, model.getValueAt(2, 2));
        assertEquals(false, model.getValueAt(2, 3));
        assertEquals(2, model.getValueAt(2, 4));
        assertEquals(new ObjectData((byte)0x12, (byte)0x34), model.getValueAt(2, 5));
        assertEquals("1234", model.getValueAt(2, 6));
        
    }

    /**
     * Test of setValueAt method, of class ObjectTableModel.
     */
    @Test
    public void testSetValueAt() {
        ObjectTableModel model = new ObjectTableModel();
        assertEquals(0, model.getRowCount());
        
        CachedRemoteObject cachedObject1 = new CachedRemoteObject(remoteObject1);
        model.setCachedObject(cachedObject1);
        model.stopRefreshCache();
        
        assertEquals(0, model.getRowCount());
        
        PropertyMap map = new PropertyMap();
        map.set(EPC.x80);
        cachedObject1.setCachedData(EPC.x9D, new ObjectData(map.toBytes()));
        cachedObject1.setCachedData(EPC.x9E, new ObjectData(map.toBytes()));
        cachedObject1.setCachedData(EPC.x9F, new ObjectData(map.toBytes()));
        cachedObject1.setCachedData(EPC.x80, new ObjectData((byte)0x30));
        
        Thread dummyPeer = new Thread() {
            
            private void processSetC() {
                try {
                    Frame frame;
                    CommonFrame commonFrame;
                    StandardPayload payload;
                    for (;;) {
                        frame = subnet.recv();
                        commonFrame = frame.getCommonFrame();
                        payload = (StandardPayload) commonFrame.getEDATA();

                        if (payload.getESV() == ESV.SetC && payload.getFirstPropertyAt(0).getEPC() == EPC.x80) {
                            break;
                        }
                    }
                    assertEquals(1, payload.getFirstPropertyAt(0).getPDC());
                    assertEquals(new Data((byte) 0x31), payload.getFirstPropertyAt(0).getEDT());

                    payload.setESV(ESV.Set_Res);
                    payload.getFirstPropertyAt(0).setEDT(new Data());

                    transactionManager.process(subnet, frame, false);
                } catch (SubnetException e) {
                    fail("frame = subnet.recv()");
                    e.printStackTrace();
                }
                
            }
            
            private void processGet() {
                try {
                    Frame frame = subnet.recv();
                    CommonFrame commonFrame = frame.getCommonFrame();
                    StandardPayload payload = (StandardPayload) commonFrame.getEDATA();
                    
                    assertEquals(ESV.Get, payload.getESV());
                    assertEquals(EPC.x80, payload.getFirstPropertyAt(0).getEPC());
                    
                    payload.setESV(ESV.Get_Res);
                    payload.getFirstPropertyAt(0).setEDT(new Data((byte)0x31));

                    transactionManager.process(subnet, frame, false);
                } catch (SubnetException e) {
                    fail("frame = subnet.recv()");
                    e.printStackTrace();
                }
                
            }

            @Override
            public void run() {
                processSetC();
                processGet();
            }
        };
        
        dummyPeer.start();

        model.setValueAt("31", 0, 5);
        
        assertEquals(1, model.getValueAt(0, 4));
        assertEquals(new ObjectData((byte)0x31), model.getValueAt(0, 5));
    }

    /**
     * Test of isCellEditable method, of class ObjectTableModel.
     */
    @Test
    public void testIsCellEditable() {
        ObjectTableModel model = new ObjectTableModel();
        assertEquals(0, model.getRowCount());
        
        CachedRemoteObject cachedObject1 = new CachedRemoteObject(remoteObject1);
        model.setCachedObject(cachedObject1);
        model.stopRefreshCache();
        
        assertEquals(0, model.getRowCount());
        
        PropertyMap map = new PropertyMap();
        cachedObject1.setCachedData(EPC.x9D, new ObjectData(map.toBytes()));
        cachedObject1.setCachedData(EPC.x9E, new ObjectData(map.toBytes()));
        map.set(EPC.x80);
        cachedObject1.setCachedData(EPC.x9F, new ObjectData(map.toBytes()));
        cachedObject1.setCachedData(EPC.x80, new ObjectData((byte)0x30));
        
        assertFalse(model.isCellEditable(0, 5));
        
        cachedObject1.setCachedData(EPC.x9E, new ObjectData(map.toBytes()));
        assertTrue(model.isCellEditable(0, 5));
    }
}
