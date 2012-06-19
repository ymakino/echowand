package echowand.app;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Node;
import java.util.HashMap;


class ReadableConverterMapKey {
    private Node node;
    private ClassEOJ ceoj;
    private EOJ eoj;
    private EPC epc;
    
    public ReadableConverterMapKey(Node node, ClassEOJ ceoj, EOJ eoj, EPC epc) {
        this.node = node;
        this.ceoj = ceoj;
        this.eoj = eoj;
        this.epc = epc;
    }
    
    public boolean match(Object object1, Object object2) {
        return (object1 == null) || (object2 == null) || object1.equals(object2);
    }
    
    public boolean matchNode(ReadableConverterMapKey peer) {
        return match(node, peer.node);
    }
    
    public boolean matchClassEOJ(ReadableConverterMapKey peer) {
        return match(ceoj, peer.ceoj);
    }
    
    public boolean matchEOJ(ReadableConverterMapKey peer) {
        return match(eoj, peer.eoj);
    }
    
    public boolean matchEPC(ReadableConverterMapKey peer) {
        return match(epc, peer.epc);
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public boolean equals(Object object) {
        if (! (object instanceof ReadableConverterMapKey)) {
            return false;
        }
        ReadableConverterMapKey key = (ReadableConverterMapKey)object;
        return matchNode(key) && matchClassEOJ(key) && matchEOJ(key) && matchEPC(key);
    }
}

/**
 *
 * @author Yoshiki Makino
 */
public class ReadableConverterMap {
    ReadableConverter defaultReadableConverter = new ReadableConverterSimple();
    HashMap<ReadableConverterMapKey, ReadableConverter> converterMap;
    
    private void add(EPC epc, ReadableConverter converter) {
        ReadableConverterMapKey key = new ReadableConverterMapKey(null, null, null, epc);
        converterMap.put(key, converter);
    }
    
    private void add(ClassEOJ ceoj, EPC epc, ReadableConverter converter) {
        ReadableConverterMapKey key = new ReadableConverterMapKey(null, ceoj, null, epc);
        converterMap.put(key, converter);
    }
    
    public ReadableConverterMap() {
        converterMap = new HashMap<ReadableConverterMapKey, ReadableConverter>();
        add(EPC.x80, new ReadableConverterToggle((byte)0x30, "ON", (byte)0x31, "OFF"));
        add(EPC.x82, new ReadableConverterVersion());
        add(EPC.x84, new ReadableConverterUnsignedInteger("W"));
        add(EPC.x85, new ReadableConverterReal(1000, "kWh"));
        add(EPC.x87, new ReadableConverterPercentage());
        add(EPC.x88, new ReadableConverterToggle((byte)0x41, "FAILURE", (byte)0x42, "NORMAL"));
        add(EPC.x8C, new ReadableConverterString());
        add(EPC.x8D, new ReadableConverterString());
        add(EPC.x8E, new ReadableConverterDate());
        add(EPC.x8F, new ReadableConverterToggle((byte)0x41, "SAVING", (byte)0x42, "NORMAL"));
        add(EPC.x97, new ReadableConverterTime());
        add(EPC.x98, new ReadableConverterDate());
        add(EPC.x99, new ReadableConverterUnsignedInteger());
        
        add(EPC.x9D, new ReadableConverterPropertyMap());
        add(EPC.x9E, new ReadableConverterPropertyMap());
        add(EPC.x9F, new ReadableConverterPropertyMap());
        
        HashMap<Byte, String> LevelMap = new HashMap<Byte, String>();
        for (int i=1; i<=8; i++) {
            LevelMap.put((byte)(0x30 + i), String.format("LEVEL-%d", i));
        }
        add(new ClassEOJ("0001"), EPC.xB0, new ReadableConverterMultipleToggle(LevelMap));
        add(new ClassEOJ("0001"), EPC.xB1, new ReadableConverterToggle((byte)0x41, "LEAKING", (byte)0x42, "NORMAL"));
        
        add(new ClassEOJ("0011"), EPC.xE0, new ReadableConverterReal(10, "\u2103"));
        add(new ClassEOJ("0012"), EPC.xE0, new ReadableConverterPercentage());
        
        
        
        HashMap<Byte, String> operationModeMap = new HashMap<Byte, String>();
        operationModeMap.put((byte)0x41, "AUTO");
        operationModeMap.put((byte)0x42, "COOLER");
        operationModeMap.put((byte)0x43, "HEATER");
        operationModeMap.put((byte)0x44, "DEHUMID");
        operationModeMap.put((byte)0x45, "BLOWER");
        operationModeMap.put((byte)0x40, "OTHER");
        add(new ClassEOJ("0130"), EPC.xB0, new ReadableConverterMultipleToggle(operationModeMap));
        add(new ClassEOJ("0130"), EPC.xB1, new ReadableConverterToggle((byte)0x41, "AUTO", (byte)0x42, "MANUAL"));
        
        HashMap<Byte, String> quickModeMap = new HashMap<Byte, String>();
        quickModeMap.put((byte)0x41, "NORMAL");
        quickModeMap.put((byte)0x42, "RAPID");
        quickModeMap.put((byte)0x43, "SILENT");
        add(new ClassEOJ("0130"), EPC.xB2, new ReadableConverterMultipleToggle(quickModeMap));
        add(new ClassEOJ("0130"), EPC.xB3, new ReadableConverterUnsignedInteger("\u2103"));
        add(new ClassEOJ("0130"), EPC.xB4, new ReadableConverterPercentage());
        add(new ClassEOJ("0130"), EPC.xB5, new ReadableConverterUnsignedInteger("\u2103"));
        add(new ClassEOJ("0130"), EPC.xB6, new ReadableConverterUnsignedInteger("\u2103"));
        add(new ClassEOJ("0130"), EPC.xB7, new ReadableConverterUnsignedInteger("\u2103"));
        add(new ClassEOJ("0130"), EPC.xB8, new ReadableConverterUnsignedInteger("W"));
        add(new ClassEOJ("0130"), EPC.xB9, new ReadableConverterReal(10, "A"));
        add(new ClassEOJ("0130"), EPC.xBA, new ReadableConverterPercentage());
        add(new ClassEOJ("0130"), EPC.xBB, new ReadableConverterInteger("\u2103"));
        add(new ClassEOJ("0130"), EPC.xBC, new ReadableConverterUnsignedInteger("\u2103"));
        add(new ClassEOJ("0130"), EPC.xBD, new ReadableConverterInteger("\u2103"));
        add(new ClassEOJ("0130"), EPC.xBE, new ReadableConverterInteger("\u2103"));
        add(new ClassEOJ("0130"), EPC.xBF, new ReadableConverterReal(10, "\u2103"));
        
        HashMap<Byte, String> fanModeMap = new HashMap<Byte, String>();
        operationModeMap.put((byte)0x41, "AUTO");
        for (int i=1; i<=8; i++) {
            fanModeMap.put((byte)(0x30 + i), String.format("LEVEL-%d", i));
        }
        add(new ClassEOJ("0130"), EPC.xA0, new ReadableConverterMultipleToggle(fanModeMap));
        
        HashMap<Byte, String> directionModeMap = new HashMap<Byte, String>();
        directionModeMap.put((byte)0x41, "AUTO");
        directionModeMap.put((byte)0x42, "MANUAL");
        directionModeMap.put((byte)0x43, "AUTO VERTICAL");
        directionModeMap.put((byte)0x44, "AUTO HORIZONTAL");
        add(new ClassEOJ("0130"), EPC.xA1, new ReadableConverterMultipleToggle(directionModeMap));
        
        HashMap<Byte, String> swingModeMap = new HashMap<Byte, String>();
        swingModeMap.put((byte)0x31, "OFF");
        swingModeMap.put((byte)0x41, "VERTICAL");
        swingModeMap.put((byte)0x42, "HORIZONTAL");
        swingModeMap.put((byte)0x43, "BOTH");
        add(new ClassEOJ("0130"), EPC.xA3, new ReadableConverterMultipleToggle(swingModeMap));
        
        
        HashMap<Byte, String> verticalMap = new HashMap<Byte, String>();
        verticalMap.put((byte)0x41, "HIGHEST");
        verticalMap.put((byte)0x42, "LOWEST");
        verticalMap.put((byte)0x43, "MIDDLE");
        verticalMap.put((byte)0x44, "HIGHER");
        verticalMap.put((byte)0x45, "LOWER");
        add(new ClassEOJ("0130"), EPC.xA4, new ReadableConverterMultipleToggle(verticalMap));
        
        
        HashMap<Byte, String> horizontalMap = new HashMap<Byte, String>();
        horizontalMap.put((byte)0x41, "---OO");
        horizontalMap.put((byte)0x42, "OO---");
        horizontalMap.put((byte)0x43, "-OOO-");
        horizontalMap.put((byte)0x44, "OO-OO");
        for (int i=0x51; i<=0x6f; i++) {
            if (i == 0x53 || i == 0x5E || i == 0x68 || i == 0x6B) {
                continue;
            }
            horizontalMap.put((byte)i, code2str(i-50));
        }
        add(new ClassEOJ("0130"), EPC.xA5, new ReadableConverterMultipleToggle(horizontalMap));
        
        HashMap<Byte, String> specialModeMap = new HashMap<Byte, String>();
        specialModeMap.put((byte)0x40, "NORMAL");
        specialModeMap.put((byte)0x41, "DEFROST");
        specialModeMap.put((byte)0x42, "PREHEAT");
        specialModeMap.put((byte)0x43, "EXHAUST");
        add(new ClassEOJ("0130"), EPC.xAA, new ReadableConverterMultipleToggle(specialModeMap));
        
        add(new ClassEOJ("0130"), EPC.xAB, new ReadableConverterToggle((byte)0x40, "NORMAL", (byte)0x41, "NONPRIORITY"));
        
        HashMap<Byte, String> ventilationModeMap = new HashMap<Byte, String>();
        ventilationModeMap.put((byte)0x41, "ON(EVACUATION)");
        ventilationModeMap.put((byte)0x42, "OFF");
        ventilationModeMap.put((byte)0x43, "ON(INTAKE)");
        add(new ClassEOJ("0130"), EPC.xC0, new ReadableConverterMultipleToggle(ventilationModeMap));
        
        add(new ClassEOJ("0130"), EPC.xC1, new ReadableConverterToggle((byte)0x41, "ON", (byte)0x42, "OFF"));
        
    }
    
    private String code2str(int c) {
        char[] cs = new char[5];
        for (int i=0; i<5; i++) {
            cs[i] = ((c & (0x10 >> i)) != 0)?'O':'-';
        }
        
        return String.format("%c%c%c%c%c", cs[0], cs[1], cs[2], cs[3], cs[4]);
    }
    
    public void add(Node node, ClassEOJ ceoj, EOJ eoj, EPC epc, ReadableConverter converter) {
        ReadableConverterMapKey key = new ReadableConverterMapKey(node, ceoj, eoj, epc);
        converterMap.put(key, converter);
    }
    
    public ReadableConverter get(Node node, ClassEOJ ceoj, EOJ eoj, EPC epc) {
        ReadableConverterMapKey key = new ReadableConverterMapKey(node, ceoj, eoj, epc);
        ReadableConverter converter = converterMap.get(key);
        if (converter == null) {
            return defaultReadableConverter;
        }
        
        return converter;
    }
}
