package edu.mit.simile.gadget.tests.utils;

import edu.mit.simile.gadget.utils.ScreenUtils;
import junit.framework.TestCase;

public class ScreenUtilsTest extends TestCase {

	public void testGetColumns() {
		boolean t1 = ScreenUtils.getColumns(0) == 1;
		boolean t2 = ScreenUtils.getColumns(1) == 1;
		boolean t3 = ScreenUtils.getColumns(10) == 2;
		boolean t4 = ScreenUtils.getColumns(100) == 3;
		assertTrue(t1 && t2 && t3 && t4);
	}

	public void testSpaces() {
		boolean t1 = ScreenUtils.spaces(0).equals("");
		boolean t2 = ScreenUtils.spaces(1).equals(" ");
		boolean t3 = ScreenUtils.spaces(2).equals("  ");
		assertTrue(t1 && t2 && t3);
	}

	public void testAlignRight() {
		boolean t1 = ScreenUtils.alignRight(0,5).equals("    0");
		boolean t2 = ScreenUtils.alignRight(1,5).equals("    1");
		boolean t3 = ScreenUtils.alignRight(10,5).equals("   10");
		assertTrue(t1 && t2 && t3);
	}
		
}
