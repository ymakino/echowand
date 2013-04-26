package echowand.object;

import echowand.object.LocalObjectDateTimeDelegate;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.common.EPC;
import echowand.info.DeviceObjectInfo;
import echowand.info.TemperatureSensorInfo;
import java.util.Calendar;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class LocalObjectDateTimeDelegateTest {

    /**
     * Test of getData method, of class LocalObjectDateTimeDelegate.
     */
    @Test
    public void testGetData() {
        DeviceObjectInfo objectInfo = new TemperatureSensorInfo();
        objectInfo.add(EPC.x97, true, false, false, 2);
        objectInfo.add(EPC.x98, true, false, false, 4);
        LocalObject object = new LocalObject(objectInfo);
        LocalObjectDateTimeDelegate delegate = new LocalObjectDateTimeDelegate();
        ObjectData dataDate1 = delegate.getData(object, EPC.x98);
        ObjectData dataTime1 = delegate.getData(object, EPC.x97);
        ObjectData dataDate2 = delegate.getData(object, EPC.x98);
        ObjectData dataTime2 = delegate.getData(object, EPC.x97);
        
        Calendar now1 = Calendar.getInstance();
        Calendar now2 = ((Calendar)now1.clone());
        now2.add(Calendar.SECOND, -1);
        
        now1.set(Calendar.SECOND, 0);
        now1.set(Calendar.MILLISECOND, 0);
        now2.set(Calendar.SECOND, 0);
        now2.set(Calendar.MILLISECOND, 0);
        
        Calendar cal1 = dataToCal(dataDate1, dataTime1);
        Calendar cal2 = dataToCal(dataDate2, dataTime2);
        
        assertTrue(eq(now1, cal1) || eq(now1, cal2) || eq(now2, cal1) || eq(now2, cal2));
    }
    
    boolean eq(Calendar c1, Calendar c2) {
        return c1.equals(c2);
    }
    
    Calendar dataToCal(ObjectData dataDate, ObjectData dataTime) {
        Calendar cal = Calendar.getInstance();
        int year1 = byteToInt(dataDate.get(0));
        int year2 = byteToInt(dataDate.get(1));
        int year = (year1 << 8) | year2;
        int month = byteToInt(dataDate.get(2)) - 1;
        int day = byteToInt(dataDate.get(3));
        int hour = byteToInt(dataTime.get(0));
        int min = byteToInt(dataTime.get(1));
        cal.set(year, month, day, hour, min, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
    
    int byteToInt(byte b) {
        return 0x000000ff & (int)b;
    }

    /**
     * Test of setData method, of class LocalObjectDateTimeDelegate.
     */
    @Test
    public void testSetData() {
        LocalObject object = new LocalObject(new TemperatureSensorInfo());
        LocalObjectDateTimeDelegate delegate = new LocalObjectDateTimeDelegate();
        assertEquals(false, delegate.setData(object, EPC.x97, new ObjectData((byte)0x41), new ObjectData((byte)0x40)));
        assertEquals(false, delegate.setData(object, EPC.x98, new ObjectData((byte)0x41), new ObjectData((byte)0x40)));
    }
}
