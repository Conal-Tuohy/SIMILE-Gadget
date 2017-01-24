package edu.mit.simile.gadget.tests.utils;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import edu.mit.simile.gadget.data.CollidingString;
import edu.mit.simile.gadget.utils.StringUtils;

public class StringUtilsTest extends TestCase {
    
    public void testKeyfy1() {
        String a = StringUtils.keyfy("Harris, W. L. (Wesley Leroy)");
        String b = StringUtils.keyfy("Wesley Leroy Harris");
        assertTrue(b.equals(a));
    }
    
    public void testKeyfy2() {
        String a = StringUtils.keyfy("Greitzer, Edward M.");
        String b = StringUtils.keyfy("Greitzer, Edward");
        assertTrue(b.equals(a));
    }
    
    public void testKeyfy3() {
        String a = StringUtils.keyfy("Spearing, S Mark");
        String b = StringUtils.keyfy("Mark Spearing");
        assertTrue(b.equals(a));
    }
    
    public void testKeyfy4() {
        String a = StringUtils.keyfy("Berinsky, Adam J., 1970-");
        String b = StringUtils.keyfy("Adam Berinsky");
        assertTrue(b.equals(a));
    }
    
    public void testKeyfy5() {
        String a = StringUtils.keyfy("Dr. Thomas Consi");
        String b = StringUtils.keyfy("Consi, Thomas");
        assertTrue(b.equals(a));
    }
    
    public void testKeyfy6() {
        int a = StringUtils.keyfy("   Fischer, Michael M.").hashCode();
        int b = StringUtils.keyfy("Fischer, Michael M. J., 1946-").hashCode();
        assertTrue(a == b);
    }
    
    public void testKeyfy7() {
        Map map = new HashMap();
        String a = "John Smith";
        String b = "Smith, John";
        CollidingString ca = new CollidingString(a);
        CollidingString cb = new CollidingString(b);
        String c = "blah";
        map.put(ca,"blah");
        String d = (String) map.get(cb);
        assertTrue(c.equals(d));
    }
    
    public void testKeyfy8() {
        String a = StringUtils.keyfy("Dr. John Smith");
        String b = StringUtils.keyfy("Smith, John, 1925-1989");
        assertTrue(b.equals(a));
    }
    
    public void testKeyfy9() {
        String a = StringUtils.keyfy("1837-1848");
        String b = StringUtils.keyfy("  , [2001-2003 ]: ");
        assertTrue(a.equals(b));
    }
    
    public void testJSONEscape() {
        String a = StringUtils.jsonEscape("\"blah\"\n");
        String b = "\\\"blah\\\"\\n";
        assertTrue(a.equals(b));
    }
    
}
