/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package echowand.util;

import echowand.util.ConstraintByte;
import echowand.util.Constraint;
import echowand.util.ConstraintIntersection;
import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author ymakino
 */
public class ConstraintIntersectionTest {
    
    @Test
    public void testSimple() {
        Constraint c1 = new ConstraintByte();
        Constraint c2 = new ConstraintByte();
        ConstraintIntersection intersection = new ConstraintIntersection(c1, c2);
        assertFalse(intersection.isValid(new byte[0]));
        assertFalse(intersection.isValid(new byte[10]));
        assertFalse(intersection.isValid(new byte[100]));
    }
    
    @Test
    public void testNoOverwrap() {
        Constraint c1 = new ConstraintByte((byte)0x01, (byte)0x10);
        Constraint c2 = new ConstraintByte((byte)0x12, (byte)0x20);
        ConstraintIntersection intersection = new ConstraintIntersection(c1, c2);
        assertFalse(intersection.isValid(new byte[]{0x00}));
        assertFalse(intersection.isValid(new byte[]{0x01}));
        assertFalse(intersection.isValid(new byte[]{0x10}));
        assertFalse(intersection.isValid(new byte[]{0x11}));
        assertFalse(intersection.isValid(new byte[]{0x12}));
        assertFalse(intersection.isValid(new byte[]{0x20}));
        assertFalse(intersection.isValid(new byte[]{0x21}));
    }
    
    @Test
    public void testOverwrap() {
        Constraint c1 = new ConstraintByte((byte)0x01, (byte)0x15);
        Constraint c2 = new ConstraintByte((byte)0x10, (byte)0x20);
        ConstraintIntersection intersection = new ConstraintIntersection(c1, c2);
        assertFalse(intersection.isValid(new byte[]{0x00}));
        assertFalse(intersection.isValid(new byte[]{0x01}));
        assertTrue(intersection.isValid(new byte[]{0x10}));
        assertTrue(intersection.isValid(new byte[]{0x11}));
        assertTrue(intersection.isValid(new byte[]{0x12}));
        assertTrue(intersection.isValid(new byte[]{0x15}));
        assertFalse(intersection.isValid(new byte[]{0x16}));
        assertFalse(intersection.isValid(new byte[]{0x20}));
        assertFalse(intersection.isValid(new byte[]{0x21}));
    }
}
