package echowand.net;

import echowand.common.Data;
import echowand.common.EPC;
import echowand.net.Property;
import echowand.object.ObjectData;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class PropertyTest {
    
    @Test
    public void testPropertyCreation() {
        Property p = new Property();
        p.setEPC(EPC.x80);
        assertEquals(EPC.x80, p.getEPC());
        
        assertEquals(0, p.getPDC());
        p.setEDT(new Data((byte)0x12, (byte)0x34));
        assertEquals(2, p.getPDC());
        Data edt = p.getEDT();
        assertEquals(2, edt.size());
        assertEquals(0x12, edt.get(0));
        assertEquals(0x34, edt.get(1));
        
        assertEquals(4, p.size());
        
        byte[] bp = p.toBytes();
        assertEquals(4, bp.length);
        assertEquals((byte)0x80, bp[0]);
        assertEquals(2, bp[1]);
        assertEquals(0x12, bp[2]);
        assertEquals(0x34, bp[3]);
    }
    
    @Test
    public void testCreationWithInitialize() {
        Property p = new Property(EPC.x80);
        assertEquals(EPC.x80, p.getEPC());
        
        Property p2 = new Property(EPC.x80, new Data((byte)0x31));
        assertEquals(EPC.x80, p2.getEPC());
        assertEquals((byte)0x01, p2.getPDC());
        assertEquals((byte)0x31, p2.getEDT().get(0));
        
        Property p3 = new Property(EPC.x80,new Data((byte)0x31));
        assertEquals(EPC.x80, p2.getEPC());
        assertEquals((byte)0x01, p2.getPDC());
        assertEquals((byte)0x31, p2.getEDT().get(0));
    }
    
    @Test
    public void testCreationFromBytes() {
        Property p1 = new Property(new byte[]{(byte)0x12, (byte)0x80, (byte)0x02, (byte)0x11, (byte)0x22}, 1);
        assertEquals(EPC.x80, p1.getEPC());
        assertEquals((byte)0x02, p1.getPDC());
        assertEquals((byte)0x11, p1.getEDT().get(0));
        assertEquals((byte)0x22, p1.getEDT().get(1));
    }
}
