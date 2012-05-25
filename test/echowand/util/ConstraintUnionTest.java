/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package echowand.util;

import echowand.util.ConstraintByte;
import echowand.util.ConstraintUnion;
import echowand.util.Constraint;
import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class ConstraintUnionTest {
    
    @Test
    public void testSimple() {
        Constraint c1 = new ConstraintByte();
        Constraint c2 = new ConstraintByte();
        ConstraintUnion union = new ConstraintUnion(c1, c2);
        assertFalse(union.isValid(new byte[0]));
        assertFalse(union.isValid(new byte[10]));
        assertFalse(union.isValid(new byte[100]));
    }
    
    @Test
    public void testUnion() {
        Constraint c1 = new ConstraintByte((byte)0x01, (byte)0x10);
        Constraint c2 = new ConstraintByte((byte)0x12, (byte)0x20);
        ConstraintUnion union = new ConstraintUnion(c1, c2);
        assertFalse(union.isValid(new byte[]{0x00}));
        assertTrue(union.isValid(new byte[]{0x01}));
        assertTrue(union.isValid(new byte[]{0x10}));
        assertFalse(union.isValid(new byte[]{0x11}));
        assertTrue(union.isValid(new byte[]{0x12}));
        assertTrue(union.isValid(new byte[]{0x20}));
        assertFalse(union.isValid(new byte[]{0x21}));
    }
}
