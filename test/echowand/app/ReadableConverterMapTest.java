package echowand.app;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.InternalSubnet;
import echowand.net.Node;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class ReadableConverterMapTest {
    public ReadableConverterMap converterMap;
    
    @Before
    public void setUp() {
        converterMap = new ReadableConverterMap();
        converterMap.clear();
    }

    @Test
    public void testPut() {
        InternalSubnet subnet = new InternalSubnet();
        Node node = subnet.getLocalNode();
        ClassEOJ ceoj = new ClassEOJ("0011");
        EOJ eoj = new EOJ("001101");
        EPC epc = EPC.x80;
        
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(node, ceoj, epc));
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(node, eoj, epc));
        
        ReadableConverter converter = new ReadableConverterString();
        converterMap.put(node, eoj, epc, converter);
        assertEquals(converter, converterMap.get(node, eoj, epc));
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(eoj, epc));
        assertEquals(converter, converterMap.get(node, eoj, epc));
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(node, ceoj, epc));
    }
    
    @Test
    public void testPutWildcard() {
        InternalSubnet subnet = new InternalSubnet();
        Node node = subnet.getLocalNode();
        ClassEOJ ceoj = new ClassEOJ("0011");
        EOJ eoj = new EOJ("001101");
        EPC epc = EPC.x80;
        
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(node, eoj, epc));
        
        ReadableConverter converter = new ReadableConverterString();
        
        converterMap.put(epc, converter);
        assertEquals(converter, converterMap.get(node, epc));
        assertEquals(converter, converterMap.get(eoj, epc));
        assertEquals(converter, converterMap.get(node, eoj, epc));
        assertEquals(converter, converterMap.get(ceoj, epc));
        assertEquals(converter, converterMap.get(node, ceoj, epc));
        converterMap.clear();
        
        converterMap.put(eoj, epc, converter);
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(node, epc));
        assertEquals(converter, converterMap.get(eoj, epc));
        assertEquals(converter, converterMap.get(node, eoj, epc));
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(ceoj, epc));
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(node, ceoj, epc));
        converterMap.clear();
        
        converterMap.put(ceoj, epc, converter);
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(node, epc));
        assertEquals(converter, converterMap.get(eoj, epc));
        assertEquals(converter, converterMap.get(node, eoj, epc));
        assertEquals(converter, converterMap.get(eoj, epc));
        assertEquals(converter, converterMap.get(node, ceoj, epc));
        converterMap.clear();
        
        converterMap.put(node, epc, converter);
        assertEquals(converter, converterMap.get(node, epc));
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(eoj, epc));
        assertEquals(converter, converterMap.get(node, eoj, epc));
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(eoj, epc));
        assertEquals(converter, converterMap.get(node, ceoj, epc));
        converterMap.clear();
    }
    
    @Test
    public void testPutMultiple() {
        InternalSubnet subnet = new InternalSubnet();
        Node node = subnet.getLocalNode();
        ClassEOJ ceoj = new ClassEOJ("0011");
        EOJ eoj = new EOJ("001101");
        EPC epc = EPC.x80;
        
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(node, eoj, epc));
        
        ReadableConverter converter1 = new ReadableConverterString();
        ReadableConverter converter2 = new ReadableConverterString();
        ReadableConverter converter3 = new ReadableConverterString();
        ReadableConverter converter4 = new ReadableConverterString();
        ReadableConverter converter5 = new ReadableConverterString();
        
        converterMap.put(epc, converter1);
        assertEquals(converter1, converterMap.get(node, epc));
        assertEquals(converter1, converterMap.get(eoj, epc));
        assertEquals(converter1, converterMap.get(ceoj, epc));
        assertEquals(converter1, converterMap.get(node, eoj, epc));
        assertEquals(converter1, converterMap.get(node, ceoj, epc));
        
        converterMap.put(ceoj, epc, converter2);
        assertEquals(converter1, converterMap.get(node, epc));
        assertEquals(converter2, converterMap.get(eoj, epc));
        assertEquals(converter2, converterMap.get(ceoj, epc));
        assertEquals(converter2, converterMap.get(node, eoj, epc));
        assertEquals(converter2, converterMap.get(node, ceoj, epc));
        
        
        converterMap.put(node, ceoj, epc, converter3);
        assertEquals(converter1, converterMap.get(node, epc));
        assertEquals(converter2, converterMap.get(eoj, epc));
        assertEquals(converter2, converterMap.get(ceoj, epc));
        assertEquals(converter3, converterMap.get(node, eoj, epc));
        assertEquals(converter3, converterMap.get(node, ceoj, epc));
        
        converterMap.put(eoj, epc, converter4);
        assertEquals(converter1, converterMap.get(node, epc));
        assertEquals(converter4, converterMap.get(eoj, epc));
        assertEquals(converter2, converterMap.get(ceoj, epc));
        assertEquals(converter4, converterMap.get(node, eoj, epc));
        assertEquals(converter3, converterMap.get(node, ceoj, epc));
        
        converterMap.put(node, eoj, epc, converter5);
        assertEquals(converter1, converterMap.get(node, epc));
        assertEquals(converter4, converterMap.get(eoj, epc));
        assertEquals(converter2, converterMap.get(ceoj, epc));
        assertEquals(converter5, converterMap.get(node, eoj, epc));
        assertEquals(converter3, converterMap.get(node, ceoj, epc));
    }
    
    @Test
    public void testClear() {
        InternalSubnet subnet = new InternalSubnet();
        Node node = subnet.getLocalNode();
        EOJ eoj = new EOJ("001101");
        EPC epc = EPC.x80;
        
        assertTrue(converterMap.get(node, eoj, epc) instanceof ReadableConverterSimple);
        
        ReadableConverter converter = new ReadableConverterString();
        converterMap.put(node, eoj, epc, converter);
        assertEquals(converter, converterMap.get(node, eoj, epc));
        
        converterMap.clear();
        
        assertEquals(converterMap.getDefaultConverter(), converterMap.get(node, eoj, epc));
    }
    
    @Test
    public void testDefaultConverter() {
        InternalSubnet subnet = new InternalSubnet();
        Node node = subnet.getLocalNode();
        ClassEOJ ceoj = new ClassEOJ("0011");
        EOJ eoj = new EOJ("001101");
        EPC epc = EPC.x80;
        ReadableConverter converter = new ReadableConverterString();
        converterMap.setDefaultConverter(converter);
        
        assertEquals(converter, converterMap.get(node, eoj, epc));
        assertEquals(converter, converterMap.get(eoj, epc));
        assertEquals(converter, converterMap.get(node, eoj, epc));
        assertEquals(converter, converterMap.get(node, ceoj, epc));
    }
}
