/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package echowand.util;

import echowand.util.ConstraintByte;
import echowand.util.ConstraintComplement;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class ConstraintComplementTest {
    @Test
    public void testComplement() {
        ConstraintByte c = new ConstraintByte((byte)0x10, (byte)0x20);
        ConstraintComplement complement = new ConstraintComplement(c);
        
        assertTrue(complement.isValid(new byte[2]));
        assertTrue(complement.isValid(new byte[1]));
        assertTrue(complement.isValid(new byte[]{(byte)0x09}));
        assertFalse(complement.isValid(new byte[]{(byte)0x10}));
        assertFalse(complement.isValid(new byte[]{(byte)0x11}));
        assertFalse(complement.isValid(new byte[]{(byte)0x19}));
        assertFalse(complement.isValid(new byte[]{(byte)0x20}));
        assertTrue(complement.isValid(new byte[]{(byte)0x21}));
    }
}
