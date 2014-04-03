package echowand.object;

import echowand.common.ClassEOJ;
import echowand.common.EOJ;
import echowand.logic.TooManyObjectsException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Yoshiki Makino
 */
public class UnusedEOJGeneratorTest {

    /**
     * Test of generate method, of class UnusedEOJGenerator.
     */
    @Test
    public void testGenerate() throws Exception {
        ClassEOJ ceoj1 = new ClassEOJ("0011");
        ClassEOJ ceoj2 = new ClassEOJ("0130");
        
        UnusedEOJGenerator instance = new UnusedEOJGenerator();
        
        assertEquals(new EOJ("001101"), instance.generate(ceoj1));
        assertEquals(new EOJ("001102"), instance.generate(ceoj1));
        assertEquals(new EOJ("001103"), instance.generate(ceoj1));
        assertEquals(new EOJ("013001"), instance.generate(ceoj2));
        assertEquals(new EOJ("013002"), instance.generate(ceoj2));
        assertEquals(new EOJ("013003"), instance.generate(ceoj2));
        
        assertEquals(new EOJ("001104"), instance.generate(ceoj1));
        assertEquals(new EOJ("013004"), instance.generate(ceoj2));
        assertEquals(new EOJ("001105"), instance.generate(ceoj1));
        assertEquals(new EOJ("013005"), instance.generate(ceoj2));
    }

    @Test(expected = TooManyObjectsException.class)
    public void testTooMany() throws TooManyObjectsException {
        ClassEOJ ceoj1 = new ClassEOJ("0011");

        UnusedEOJGenerator instance = new UnusedEOJGenerator();
        try {
            for (int i = 0x01; i <= 0x7f; i++) {
                instance.generate(ceoj1);
            }
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
            fail();
        }
        instance.generate(ceoj1);
    }
    
    @Test
    public void testAddUsed() throws TooManyObjectsException {
        ClassEOJ ceoj1 = new ClassEOJ("0011");

        UnusedEOJGenerator instance = new UnusedEOJGenerator();
        
        instance.addUsed(new EOJ("001101"));
        instance.addUsed(new EOJ("001103"));
        
        EOJ eoj1 = instance.generate(ceoj1);
        assertEquals(new EOJ("001102"), eoj1);
        
        EOJ eoj2 = instance.generate(ceoj1);
        assertEquals(new EOJ("001104"), eoj2);
    }
    
    @Test
    public void testIsUsed() throws TooManyObjectsException {

        UnusedEOJGenerator instance = new UnusedEOJGenerator();
        instance.addUsed(new EOJ("001102"));
        
        ClassEOJ ceoj = new ClassEOJ("0011");
        EOJ eoj1 = instance.generate(ceoj);
        EOJ eoj2 = instance.generate(ceoj);
        
        instance.addUsed(new EOJ("00117f"));
        
        for (byte i=0x01; i<0x7f; i++) {
            switch (i) {
            case 0x01: case 0x02: case 0x03: case 0x7f:
                assertTrue(instance.isUsed(ceoj.getEOJWithInstanceCode(i)));
                break;
            default:
                assertFalse(instance.isUsed(ceoj.getEOJWithInstanceCode(i)));
                break;
            }
        }
    }
}
