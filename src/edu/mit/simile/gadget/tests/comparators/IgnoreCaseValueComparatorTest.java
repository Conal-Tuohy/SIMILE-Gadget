package edu.mit.simile.gadget.tests.comparators;

import java.util.Collections;
import java.util.Comparator;

import edu.mit.simile.gadget.Result;
import edu.mit.simile.gadget.comparators.IgnoreCaseValueComparator;

public class IgnoreCaseValueComparatorTest extends ValueComparatorTest {

	public void testAscendingComparator() {
		Comparator comparator = new IgnoreCaseValueComparator(true);
		Collections.sort(list,comparator);
		Result r = (Result) list.get(0);
		assertTrue(r.getValue().equals("madzoc-key"));
	}

	public void testDescendingComparator() {
		Comparator comparator = new IgnoreCaseValueComparator(false);
		Collections.sort(list,comparator);
		Result r = (Result) list.get(0);
		assertTrue(r.getValue().equals("steph-ah-noh"));
	}
	
}
