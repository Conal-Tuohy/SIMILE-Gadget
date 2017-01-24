/*
 * 
 */
package edu.mit.simile.gadget.tests.utils;

import junit.framework.TestCase;
import edu.mit.simile.gadget.utils.XPathUtils;

/**
 * 
 */
public class XPathUtilsTest extends TestCase {

	String xpath1 = "/";
	String xpath2 = "/this/is/an/xpath/";
	String xpath3 = "/this/is/another/xpath/a/little/longer/";

	public void testGetDepth1() {
		assertTrue(XPathUtils.getDepth(xpath1) == 0);
	}

	public void testGetDepth2() {
		assertTrue(XPathUtils.getDepth(xpath2) == 4);
	}
	
	public void testGetDepth3() {
		assertTrue(XPathUtils.getDepth(xpath3) == 7);
	}
	
	public void testLevelChange1() {
		assertTrue(XPathUtils.levelChange(xpath1,xpath2) == 1);
	}
	
	public void testLevelChange2() {
		assertTrue(XPathUtils.levelChange(xpath2,xpath1) == 1);
	}

	public void testLevelChange3() {
		assertTrue(XPathUtils.levelChange(xpath2,xpath3) == 3);
	}
	
	public void testLevelChange4() {
		assertTrue(XPathUtils.levelChange(xpath3,xpath2) == 3);
	}
	
	public void testGetLevel1() {
		assertEquals(XPathUtils.getLevel(xpath2,0),"/");
	}	

	public void testGetLevel2() {
		assertEquals(XPathUtils.getLevel(xpath2,1)," this/");
	}	

	public void testGetLevel3() {
		assertEquals(XPathUtils.getLevel(xpath2,2),"      is/");
	}	

	public void testGetLevel4() {
		assertEquals(XPathUtils.getLevel(xpath2,3),"         an/");
	}	

	public void testGetLevel5() {
		assertEquals(XPathUtils.getLevel(xpath2,4),"            xpath/");
	}	
	
}
