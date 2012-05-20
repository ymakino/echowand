package echowand.common;

import echowand.common.ESV;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Yoshiki Makino
 */
public class ESVTest {

    @Test
    public void testToByte() {
        assertEquals((byte) 0x00, ESV.Invalid.toByte());
        assertEquals((byte) 0x60, ESV.SetI.toByte());
        assertEquals((byte) 0x61, ESV.SetC.toByte());
        assertEquals((byte) 0x62, ESV.Get.toByte());
        assertEquals((byte) 0x63, ESV.INF_REQ.toByte());
        assertEquals((byte) 0x6e, ESV.SetGet.toByte());
        assertEquals((byte) 0x71, ESV.Set_Res.toByte());
        assertEquals((byte) 0x72, ESV.Get_Res.toByte());
        assertEquals((byte) 0x73, ESV.INF.toByte());
        assertEquals((byte) 0x74, ESV.INFC.toByte());
        assertEquals((byte) 0x7a, ESV.INFC_Res.toByte());
        assertEquals((byte) 0x7e, ESV.SetGet_Res.toByte());
        assertEquals((byte) 0x50, ESV.SetI_SNA.toByte());
        assertEquals((byte) 0x51, ESV.SetC_SNA.toByte());
        assertEquals((byte) 0x52, ESV.Get_SNA.toByte());
        assertEquals((byte) 0x53, ESV.INF_SNA.toByte());
        assertEquals((byte) 0x5e, ESV.SetGet_SNA.toByte());
    }
    
    @Test
    public void testIsSetGet() {
        assertFalse(ESV.Invalid.isSetGet());
        assertFalse(ESV.SetI.isSetGet());
        assertFalse(ESV.SetC.isSetGet());
        assertFalse(ESV.Get.isSetGet());
        assertFalse(ESV.INF_REQ.isSetGet());
        assertTrue(ESV.SetGet.isSetGet());
        assertFalse(ESV.Set_Res.isSetGet());
        assertFalse(ESV.Get_Res.isSetGet());
        assertFalse(ESV.INF.isSetGet());
        assertFalse(ESV.INFC.isSetGet());
        assertFalse(ESV.INFC_Res.isSetGet());
        assertTrue(ESV.SetGet_Res.isSetGet());
        assertFalse(ESV.SetI_SNA.isSetGet());
        assertFalse(ESV.SetC_SNA.isSetGet());
        assertFalse(ESV.Get_SNA.isSetGet());
        assertFalse(ESV.INF_SNA.isSetGet());
        assertTrue(ESV.SetGet_SNA.isSetGet());
    }
    
    @Test
    public void testIsInvalid() {
        assertTrue(ESV.Invalid.isInvalid());
        assertFalse(ESV.SetI.isInvalid());
        assertFalse(ESV.SetC.isInvalid());
        assertFalse(ESV.Get.isInvalid());
        assertFalse(ESV.INF_REQ.isInvalid());
        assertFalse(ESV.SetGet.isInvalid());
        assertFalse(ESV.Set_Res.isInvalid());
        assertFalse(ESV.Get_Res.isInvalid());
        assertFalse(ESV.INF.isInvalid());
        assertFalse(ESV.INFC.isInvalid());
        assertFalse(ESV.INFC_Res.isInvalid());
        assertFalse(ESV.SetGet_Res.isInvalid());
        assertFalse(ESV.SetI_SNA.isInvalid());
        assertFalse(ESV.SetC_SNA.isInvalid());
        assertFalse(ESV.Get_SNA.isInvalid());
        assertFalse(ESV.INF_SNA.isInvalid());
        assertFalse(ESV.SetGet_SNA.isInvalid());
    }
}
