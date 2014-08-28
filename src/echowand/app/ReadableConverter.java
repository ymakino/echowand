package echowand.app;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.common.PropertyMap;
import echowand.object.ObjectData;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 *
 * @author Yoshiki Makino
 */
public abstract class ReadableConverter {
    public static final String INVALID = "Invalid";
    public abstract String dataToString(ObjectData data);
}


class ReadableConverterSimple extends ReadableConverter {
    @Override
    public String dataToString(ObjectData data) {
        return data.toString();
    }
}


class ReadableConverterString extends ReadableConverter {
    @Override
    public String dataToString(ObjectData data) {
        return new String(data.toBytes());
    }
}


class ReadableConverterInteger extends ReadableConverter {
    private String unit;
    
    public ReadableConverterInteger() {
        this("");
    }
    
    public ReadableConverterInteger(String unit) {
        this.unit = unit;
    }
    
    @Override
    public String dataToString(ObjectData data) {
        return new BigInteger(data.toBytes()).toString() + unit;
    }
}


class ReadableConverterUnsignedInteger extends ReadableConverter {
    private String unit;
    
    public ReadableConverterUnsignedInteger() {
        this("");
    }
    
    public ReadableConverterUnsignedInteger(String unit) {
        this.unit = unit;
    }
    
    @Override
    public String dataToString(ObjectData data) {
        int dataSize = data.size();
        byte[] unsignedBytes = new byte[dataSize + 1];
        unsignedBytes[0] = 0x00;
        System.arraycopy(data.toBytes(), 0, unsignedBytes, 1, dataSize);
        return new BigInteger(unsignedBytes).toString() + unit;
    }
}


class ReadableConverterReal extends ReadableConverter {
    private int precision;
    private String unit;
    
    public ReadableConverterReal(int precision) {
        this(precision, "");
    }
    
    public ReadableConverterReal(int precision, String unit) {
        this.precision = precision;
        this.unit = unit;
    }
    
    @Override
    public String dataToString(ObjectData data) {
        boolean is_negative = false;
        int dataSize = data.size();
        BigDecimal up = BigDecimal.valueOf(0x0100);
        BigDecimal dec = new BigDecimal(0);
        
        for (int i=0; i<dataSize; i++) {
            if ((i == 0) && (data.get(i) < 0)) {
                is_negative = true;
            }
            
            int pos_data;
            if (is_negative) {
                pos_data = 0xff & ((int)((byte)-1) ^ data.get(i));
            } else {
                pos_data = 0x00ff & (int)data.get(i);
            }
            
            BigDecimal cur = BigDecimal.valueOf(pos_data);
            dec = dec.multiply(up).add(cur);
        }
        
        if (is_negative) {
            dec = dec.add(BigDecimal.valueOf(1));
            dec = dec.negate();
        }
        
        dec = dec.divide(BigDecimal.valueOf(precision));
        return dec.toPlainString() + unit;
    }
}



class ReadableConverterPercentage extends ReadableConverter {
    @Override
    public String dataToString(ObjectData data) {
        int dataSize = data.size();
        if (dataSize != 1) {
            return INVALID;
        }
        
        return String.format("%d%%", data.get(0));
    }
}


class ReadableConverterDate extends ReadableConverter {
    private int b2i(byte b) {
        return (0x000000ff) & b;
    }
    
    @Override
    public String dataToString(ObjectData data) {
        int dataSize = data.size();
        if (dataSize != 4) {
            return INVALID;
        }
        
        int d0 = b2i(data.get(0));
        int d1 = b2i(data.get(1));
        int d2 = b2i(data.get(2));
        int d3 = b2i(data.get(3));
        return String.format("%04d-%02d-%02d", (d0<<8) + d1, d2, d3);
    }
}


class ReadableConverterTime extends ReadableConverter {
    private int b2i(byte b) {
        return (0x000000ff) & b;
    }
    
    @Override
    public String dataToString(ObjectData data) {
        int dataSize = data.size();
        if (dataSize != 2) {
            return INVALID;
        }
        
        int d0 = b2i(data.get(0));
        int d1 = b2i(data.get(1));
        return String.format("%02d:%02d", d0, d1);
    }
}

class ReadableConverterMultipleToggle extends ReadableConverter {
    private HashMap<Byte, String> readableMap;
    
    public ReadableConverterMultipleToggle(Map<Byte, String> readableMap) {
        this.readableMap = new HashMap<Byte, String>(readableMap);
    }
    
    public void put(byte key, String value) {
        readableMap.put(key, value);
    }

    @Override
    public String dataToString(ObjectData data) {
        int dataSize = data.size();

        if (dataSize != 1) {
            return INVALID;
        }
        
        String string = readableMap.get(data.get(0));
        if (string == null) {
            return INVALID;
        }
        
        return string;
    }
}


class ReadableConverterToggle extends ReadableConverter {
    public byte key1;
    public String value1;
    public byte key2;
    public String value2;
    
    public ReadableConverterToggle(byte key1, String value1, byte key2, String value2) {
        this.key1 = key1;
        this.value1 = value1;
        this.key2 = key2;
        this.value2 = value2;
    }

    @Override
    public String dataToString(ObjectData data) {
        int dataSize = data.size();

        if (dataSize != 1) {
            return INVALID;
        }
        
        byte v = data.get(0);
        if (v == key1) {
            return value1;
        } else if (v == key2) {
            return value2;
        }
        
        return INVALID;
    }
}

class ReadableConverterStandardVersion extends ReadableConverter {
    @Override
    public String dataToString(ObjectData data) {
        int dataSize = data.size();

        if (dataSize != 4) {
            return INVALID;
        }

        if (data.get(0) != 0 || data.get(1) != 0 || data.get(3) != 0) {
            return INVALID;
        }

        String releaseString = new String(new byte[]{(byte) data.get(2)});
        String versionString = String.format("%s", releaseString);

        return versionString;
    }
}

class ReadableConverterVersion extends ReadableConverter {
    @Override
    public String dataToString(ObjectData data) {
        int dataSize = data.size();

        if (dataSize != 4) {
            return INVALID;
        }
        
        int major = (0x000000ff) & data.get(0);
        int minor = (0x000000ff) & data.get(1);
        byte b3 = data.get(2);
        byte b4 = data.get(3);
        
        String supported;
        if ((b3 & 0xfc) == 0 && b4 == 0) {
            boolean specified = (b3 & 0x01) != 0;
            boolean arbitrary = (b3 & 0x02) != 0;
            supported = (specified ? "S" : "") + (arbitrary ? "A" : "");
        } else {
            supported = INVALID;
        }
        
        String versionString = String.format("%d.%d (%s)", major, minor, supported);
        
        return versionString;
    }
}

class ReadableConverterPropertyMap extends ReadableConverter {
    @Override
    public String dataToString(ObjectData data) {
        StringBuilder builder = new StringBuilder();
        
        PropertyMap map = new PropertyMap(data.toBytes());
        String sep = "";
        for (int i=0x80; i<=0xff; i++) {
            if (map.isSet(EPC.fromByte((byte)i))) {
                builder.append(sep);
                builder.append(String.format("%x", i));
                sep = ", ";
            }
        }
        
        if (builder.length() == 0) {
            return "NONE";
        }
        
        return builder.toString();
    }
}

abstract class ReadableConverterEntityList extends ReadableConverter {

    private class  EntityList {

        private LinkedList<Object> list = new LinkedList<Object>();

        public void addEntity(Object entity) {
            String entityStr = entity.toString();

            int i;
            for (i = 0; i < list.size(); i++) {
                int cmp = entityStr.compareTo(list.get(i).toString());
                if (cmp == 0) {
                    return;
                }

                if (cmp < 0) {
                    break;
                }
            }

            list.add(i, entity);
        }
        
        public boolean isEmpty() {
            return list.isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                if (i != 0) {
                    builder.append(", ");
                }

                builder.append(list.get(i).toString());
            }

            return builder.toString();
        }
    }
    
    public abstract Object newEntity(ObjectData data, int offset);
    public abstract int getEntitySize();
    
    @Override
    public String dataToString(ObjectData data) {
        int dataSize = data.size();

        if (dataSize == 0) {
            return INVALID;
        }
        
        int num = 0xff & data.get(0);
        if (dataSize != 1 + getEntitySize() * num) {
            return INVALID;
        }
        
        EntityList entityList = new EntityList();
        for (int i=0; i<num; i++) {
            int offset = 1 + getEntitySize() * i;
            Object entity = newEntity(data, offset);
            
            if (entity == null) {
                return INVALID;
            }
            
            entityList.addEntity(entity);
        }
        
        if (entityList.isEmpty()) {
            return "NONE";
        }
        
        return entityList.toString();
    }
}

class ReadableConverterInstanceList extends ReadableConverterEntityList {
    @Override
    public Object newEntity(ObjectData data, int offset) {
            return new EOJ(data.get(offset), data.get(offset+1), data.get(offset+2));
    }
    
    @Override
    public int getEntitySize() {
        return 3;
    }
}

class ReadableConverterClassList extends ReadableConverterEntityList {
    @Override
    public Object newEntity(ObjectData data, int offset) {
            return new ClassEOJ(data.get(offset), data.get(offset+1));
    }
    
    @Override
    public int getEntitySize() {
        return 2;
    }
}