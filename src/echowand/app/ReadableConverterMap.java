package echowand.app;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Node;
import java.util.HashMap;


/**
 *
 * @author Yoshiki Makino
 */
public class ReadableConverterMap {
    ReadableConverter defaultReadableConverter = new ReadableConverterSimple();
    HashMap<ReadableConverterMapKey, ReadableConverter> converterMap;
    HashMap<ReadableConverterMapKey, ReadableConverter> cachedMap;;
    
    private void putInternal(EPC epc, ReadableConverter converter) {
        ReadableConverterMapKey key = new ReadableConverterMapKey(null, null, null, epc);
        converterMap.put(key, converter);
        cachedMap.clear();
    }
    
    private void putInternal(ClassEOJ ceoj, EPC epc, ReadableConverter converter) {
        ReadableConverterMapKey key = new ReadableConverterMapKey(null, ceoj, null, epc);
        converterMap.put(key, converter);
        cachedMap.clear();
    }
    
    public ReadableConverterMap() {
        converterMap = new HashMap<ReadableConverterMapKey, ReadableConverter>();
        cachedMap = new HashMap<ReadableConverterMapKey, ReadableConverter>();
        
        putInternal(EPC.x80, new ReadableConverterToggle((byte)0x30, "ON", (byte)0x31, "OFF"));
        putInternal(EPC.x82, new ReadableConverterStandardVersion());
        putInternal(EPC.x84, new ReadableConverterUnsignedInteger("W"));
        putInternal(EPC.x85, new ReadableConverterReal(1000, "kWh"));
        putInternal(EPC.x87, new ReadableConverterPercentage());
        putInternal(EPC.x88, new ReadableConverterToggle((byte)0x41, "FAILURE", (byte)0x42, "NORMAL"));
        putInternal(EPC.x8C, new ReadableConverterString());
        putInternal(EPC.x8D, new ReadableConverterString());
        putInternal(EPC.x8E, new ReadableConverterDate());
        putInternal(EPC.x8F, new ReadableConverterToggle((byte)0x41, "SAVING", (byte)0x42, "NORMAL"));
        putInternal(EPC.x97, new ReadableConverterTime());
        putInternal(EPC.x98, new ReadableConverterDate());
        putInternal(EPC.x99, new ReadableConverterUnsignedInteger());
        
        putInternal(EPC.x9D, new ReadableConverterPropertyMap());
        putInternal(EPC.x9E, new ReadableConverterPropertyMap());
        putInternal(EPC.x9F, new ReadableConverterPropertyMap());
        
        putInternal(new ClassEOJ("0ef0"), EPC.x82, new ReadableConverterVersion());
        
        HashMap<Byte, String> LevelMap = new HashMap<Byte, String>();
        for (int i=1; i<=8; i++) {
            LevelMap.put((byte)(0x30 + i), String.format("LEVEL-%d", i));
        }
        putInternal(new ClassEOJ("0001"), EPC.xB0, new ReadableConverterMultipleToggle(LevelMap));
        putInternal(new ClassEOJ("0001"), EPC.xB1, new ReadableConverterToggle((byte)0x41, "LEAKING", (byte)0x42, "NORMAL"));
        
        putInternal(new ClassEOJ("0011"), EPC.xE0, new ReadableConverterReal(10, "\u2103"));
        putInternal(new ClassEOJ("0012"), EPC.xE0, new ReadableConverterPercentage());
        
        
        
        HashMap<Byte, String> operationModeMap = new HashMap<Byte, String>();
        operationModeMap.put((byte)0x41, "AUTO");
        operationModeMap.put((byte)0x42, "COOLER");
        operationModeMap.put((byte)0x43, "HEATER");
        operationModeMap.put((byte)0x44, "DEHUMID");
        operationModeMap.put((byte)0x45, "BLOWER");
        operationModeMap.put((byte)0x40, "OTHER");
        putInternal(new ClassEOJ("0130"), EPC.xB0, new ReadableConverterMultipleToggle(operationModeMap));
        putInternal(new ClassEOJ("0130"), EPC.xB1, new ReadableConverterToggle((byte)0x41, "AUTO", (byte)0x42, "MANUAL"));
        
        HashMap<Byte, String> quickModeMap = new HashMap<Byte, String>();
        quickModeMap.put((byte)0x41, "NORMAL");
        quickModeMap.put((byte)0x42, "RAPID");
        quickModeMap.put((byte)0x43, "SILENT");
        putInternal(new ClassEOJ("0130"), EPC.xB2, new ReadableConverterMultipleToggle(quickModeMap));
        putInternal(new ClassEOJ("0130"), EPC.xB3, new ReadableConverterUnsignedInteger("\u2103"));
        putInternal(new ClassEOJ("0130"), EPC.xB4, new ReadableConverterPercentage());
        putInternal(new ClassEOJ("0130"), EPC.xB5, new ReadableConverterUnsignedInteger("\u2103"));
        putInternal(new ClassEOJ("0130"), EPC.xB6, new ReadableConverterUnsignedInteger("\u2103"));
        putInternal(new ClassEOJ("0130"), EPC.xB7, new ReadableConverterUnsignedInteger("\u2103"));
        putInternal(new ClassEOJ("0130"), EPC.xB8, new ReadableConverterUnsignedInteger("W"));
        putInternal(new ClassEOJ("0130"), EPC.xB9, new ReadableConverterReal(10, "A"));
        putInternal(new ClassEOJ("0130"), EPC.xBA, new ReadableConverterPercentage());
        putInternal(new ClassEOJ("0130"), EPC.xBB, new ReadableConverterInteger("\u2103"));
        putInternal(new ClassEOJ("0130"), EPC.xBC, new ReadableConverterUnsignedInteger("\u2103"));
        putInternal(new ClassEOJ("0130"), EPC.xBD, new ReadableConverterInteger("\u2103"));
        putInternal(new ClassEOJ("0130"), EPC.xBE, new ReadableConverterInteger("\u2103"));
        putInternal(new ClassEOJ("0130"), EPC.xBF, new ReadableConverterReal(10, "\u2103"));
        
        HashMap<Byte, String> fanModeMap = new HashMap<Byte, String>();
        operationModeMap.put((byte)0x41, "AUTO");
        for (int i=1; i<=8; i++) {
            fanModeMap.put((byte)(0x30 + i), String.format("LEVEL-%d", i));
        }
        putInternal(new ClassEOJ("0130"), EPC.xA0, new ReadableConverterMultipleToggle(fanModeMap));
        
        HashMap<Byte, String> directionModeMap = new HashMap<Byte, String>();
        directionModeMap.put((byte)0x41, "AUTO");
        directionModeMap.put((byte)0x42, "MANUAL");
        directionModeMap.put((byte)0x43, "AUTO VERTICAL");
        directionModeMap.put((byte)0x44, "AUTO HORIZONTAL");
        putInternal(new ClassEOJ("0130"), EPC.xA1, new ReadableConverterMultipleToggle(directionModeMap));
        
        HashMap<Byte, String> swingModeMap = new HashMap<Byte, String>();
        swingModeMap.put((byte)0x31, "OFF");
        swingModeMap.put((byte)0x41, "VERTICAL");
        swingModeMap.put((byte)0x42, "HORIZONTAL");
        swingModeMap.put((byte)0x43, "BOTH");
        putInternal(new ClassEOJ("0130"), EPC.xA3, new ReadableConverterMultipleToggle(swingModeMap));
        
        
        HashMap<Byte, String> verticalMap = new HashMap<Byte, String>();
        verticalMap.put((byte)0x41, "HIGHEST");
        verticalMap.put((byte)0x42, "LOWEST");
        verticalMap.put((byte)0x43, "MIDDLE");
        verticalMap.put((byte)0x44, "HIGHER");
        verticalMap.put((byte)0x45, "LOWER");
        putInternal(new ClassEOJ("0130"), EPC.xA4, new ReadableConverterMultipleToggle(verticalMap));
        
        
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
        putInternal(new ClassEOJ("0130"), EPC.xA5, new ReadableConverterMultipleToggle(horizontalMap));
        
        HashMap<Byte, String> specialModeMap = new HashMap<Byte, String>();
        specialModeMap.put((byte)0x40, "NORMAL");
        specialModeMap.put((byte)0x41, "DEFROST");
        specialModeMap.put((byte)0x42, "PREHEAT");
        specialModeMap.put((byte)0x43, "EXHAUST");
        putInternal(new ClassEOJ("0130"), EPC.xAA, new ReadableConverterMultipleToggle(specialModeMap));
        
        putInternal(new ClassEOJ("0130"), EPC.xAB, new ReadableConverterToggle((byte)0x40, "NORMAL", (byte)0x41, "NONPRIORITY"));
        
        HashMap<Byte, String> ventilationModeMap = new HashMap<Byte, String>();
        ventilationModeMap.put((byte)0x41, "ON(EVACUATION)");
        ventilationModeMap.put((byte)0x42, "OFF");
        ventilationModeMap.put((byte)0x43, "ON(INTAKE)");
        putInternal(new ClassEOJ("0130"), EPC.xC0, new ReadableConverterMultipleToggle(ventilationModeMap));
        
        putInternal(new ClassEOJ("0130"), EPC.xC1, new ReadableConverterToggle((byte)0x41, "ON", (byte)0x42, "OFF"));
        
        putInternal(new ClassEOJ("0ef0"), EPC.xD3, new ReadableConverterUnsignedInteger());
        putInternal(new ClassEOJ("0ef0"), EPC.xD4, new ReadableConverterUnsignedInteger());
        putInternal(new ClassEOJ("0ef0"), EPC.xD5, new ReadableConverterInstanceList());
        putInternal(new ClassEOJ("0ef0"), EPC.xD6, new ReadableConverterInstanceList());
        putInternal(new ClassEOJ("0ef0"), EPC.xD7, new ReadableConverterClassList());
    }
    
    private String code2str(int c) {
        char[] cs = new char[5];
        for (int i=0; i<5; i++) {
            cs[i] = ((c & (0x10 >> i)) != 0)?'O':'-';
        }
        
        return String.format("%c%c%c%c%c", cs[0], cs[1], cs[2], cs[3], cs[4]);
    }
    
    private void put(Node node, ClassEOJ ceoj, EOJ eoj, EPC epc, ReadableConverter converter) {
        ReadableConverterMapKey key = new ReadableConverterMapKey(node, ceoj, eoj, epc);
        converterMap.put(key, converter);
        cachedMap.clear();
    }
    
    public void put(Node node, ClassEOJ ceoj, EPC epc, ReadableConverter converter) {
        put(node, ceoj, null, epc, converter);
    }
    
    public void put(Node node, EOJ eoj, EPC epc, ReadableConverter converter) {
        put(node, eoj.getClassEOJ(), eoj, epc, converter);
    }
    
    public void put(Node node, EPC epc, ReadableConverter converter) {
        put(node, null, null, epc, converter);
    }
    
    public void put(ClassEOJ ceoj, EPC epc, ReadableConverter converter) {
        put(null, ceoj, null, epc, converter);
    }
    
    public void put(EOJ eoj, EPC epc, ReadableConverter converter) {
        put(null, eoj.getClassEOJ(), eoj, epc, converter);
    }
    
    public void put(EPC epc, ReadableConverter converter) {
        put(null, null, null, epc, converter);
    }
    
    private ReadableConverter get(Node node, ClassEOJ ceoj, EOJ eoj, EPC epc) {
        ReadableConverterMapKey targetKey = new ReadableConverterMapKey(node, ceoj, eoj, epc);
        
        ReadableConverter converter = cachedMap.get(targetKey);
        
        if (converter != null) {
            return converter;
        }
        
        ReadableConverterMapKey foundKey = null;
        
        for (ReadableConverterMapKey newKey : converterMap.keySet()) {
            if (newKey.includes(targetKey) && newKey.isBetterThan(foundKey)) {
                foundKey = newKey;
            }
        }
        
        if (foundKey != null) {
            converter = converterMap.get(foundKey);
        }
        
        if (converter == null) {
            converter = defaultReadableConverter;
        }
        
        cachedMap.put(targetKey, converter);
        
        return converter;
    }
    
    public ReadableConverter get(Node node, ClassEOJ ceoj, EPC epc) {
        return get(node, ceoj, null, epc);
    }
    
    public ReadableConverter get(Node node, EOJ eoj, EPC epc) {
        return get(node, eoj.getClassEOJ(), eoj, epc);
    }
    
    public ReadableConverter get(Node node, EPC epc) {
        return get(node, null, null, epc);
    }
    
    public ReadableConverter get(ClassEOJ ceoj, EPC epc) {
        return get(null, ceoj, null, epc);
    }
    
    public ReadableConverter get(EOJ eoj, EPC epc) {
        return get(null, eoj.getClassEOJ(), eoj, epc);
    }
    
    public ReadableConverter get(EPC epc) {
        return get(null, null, null, epc);
    }
    
    public ReadableConverter getDefaultConverter() {
        return defaultReadableConverter;
    }
    
    public void setDefaultConverter(ReadableConverter converter) {
        defaultReadableConverter = converter;
    }
    
    public void clear() {
        converterMap.clear();
        cachedMap.clear();
    }
}
