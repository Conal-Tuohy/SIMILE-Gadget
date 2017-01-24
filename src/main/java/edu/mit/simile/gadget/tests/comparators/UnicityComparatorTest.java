package edu.mit.simile.gadget.tests.comparators;

import java.util.Collections;
import java.util.Comparator;

import edu.mit.simile.gadget.Result;
import edu.mit.simile.gadget.comparators.UnicityComparator;

public class UnicityComparatorTest extends ComparatorTest {

	public void testAscendingComparator() {
		Comparator comparator = new UnicityComparator(true);
		Collections.sort(list,comparator);
		Result r = (Result) list.get(0);
		assertTrue(r.getUnicity() == 25);
	}

	public void testDescendingComparator() {
		Comparator comparator = new UnicityComparator(false);
		Collections.sort(list,comparator);
		Result r = (Result) list.get(0);
		assertTrue(r.getUnicity() == 100);
	}
	
}
