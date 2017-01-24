package edu.mit.simile.gadget.tests.utils;

import junit.framework.TestCase;
import edu.mit.simile.gadget.utils.BytesUtils;

public class BytesUtilsTest extends TestCase {
    
    int n1 = (int) 0x0;
    byte[] b1 = { (byte) 0x0 , (byte) 0x0 , (byte) 0x0 , (byte) 0x0 };
    
    public void test1a() {
        assertTrue(equal(BytesUtils.intToByteArray(n1), b1));
    }
    
    public void test1b() {
        assertTrue(BytesUtils.byteArrayToInt(b1) == n1);
    }
    
    int n2 = (int) 0x4587987;
    byte[] b2 = { (byte) 0x04 , (byte) 0x58 , (byte) 0x79 , (byte) 0x87 };
    
    public void test2a() {
        assertTrue(equal(BytesUtils.intToByteArray(n2), b2));
    }
    
    public void test2b() {
        assertTrue(BytesUtils.byteArrayToInt(b2) == n2);
    }
    
    static boolean equal(byte[] a, byte[] b) {
        return a[0] == b[0] && a[1] == b[1] && a[2] == b[2] && a[3] == b[3];
    }
}
