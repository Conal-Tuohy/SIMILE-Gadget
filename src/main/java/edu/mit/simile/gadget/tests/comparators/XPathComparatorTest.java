package edu.mit.simile.gadget.tests.comparators;

import java.util.Collections;
import java.util.Comparator;

import edu.mit.simile.gadget.Result;
import edu.mit.simile.gadget.comparators.XPathComparator;

public class XPathComparatorTest extends ComparatorTest {

	public void testAscendingComparator() {
		Comparator comparator = new XPathComparator(true);
		Collections.sort(list,comparator);
		Result r = (Result) list.get(0);
		assertTrue(r.getValue().equals("C"));
	}

	public void testDescendingComparator() {
		Comparator comparator = new XPathComparator(false);
		Collections.sort(list,comparator);
		Result r = (Result) list.get(0);
		assertTrue(r.getValue().equals("a/a/a/a"));
	}
	
}
