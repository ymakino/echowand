package echowand.util;

import java.util.LinkedList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class UniqueNumberGeneratorTest {

    @Test
    public void testIsAllocated() throws RunOutOfNumbersException {
        UniqueNumberGenerator generator = new UniqueNumberGenerator(1, 10);
        
        assertFalse(generator.isAllocated(1));
        generator.allocate();
        assertTrue(generator.isAllocated(1));
        generator.release(1);
        assertFalse(generator.isAllocated(1));
        
        assertFalse(generator.isAllocated(2));
        generator.allocate();
        assertTrue(generator.isAllocated(2));
        generator.release(2);
        assertFalse(generator.isAllocated(2));
    }

    @Test
    public void testRelease() throws RunOutOfNumbersException {
        UniqueNumberGenerator generator = new UniqueNumberGenerator(1, 10);
        
        generator.allocate();
        generator.release(1);
        
        LinkedList<Long> nums = new LinkedList<Long>();
        for (int i=1; i<=10; i++) {
            long l = generator.allocate();
            if (i == 10) {
                assertEquals(1, l);
            } else {
                assertEquals(i+1, l);
            }
            nums.add(l);
        }
        
        for (long i=1; i<=10; i++) {
            assertTrue(nums.contains(i));
        }
    }

    @Test
    public void testAllocate() throws RunOutOfNumbersException {
        UniqueNumberGenerator generator = new UniqueNumberGenerator(1, 10);
        
        LinkedList<Long> nums = new LinkedList<Long>();
        for (int i=1; i<=10; i++) {
            long l = generator.allocate();
            assertEquals(i, l);
            nums.add(l);
        }
        
        for (long i=1; i<=10; i++) {
            assertTrue(nums.contains(i));
        }
    }
    
    @Test(expected=RunOutOfNumbersException.class)
    public void testAllocationException() throws RunOutOfNumbersException {
        UniqueNumberGenerator generator = new UniqueNumberGenerator(1, 10);
        for (int i=0; i<11; i++) {
            generator.allocate();
        }
    }
}
