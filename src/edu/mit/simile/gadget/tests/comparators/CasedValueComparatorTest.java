package edu.mit.simile.gadget.tests.comparators;

import java.util.Collections;
import java.util.Comparator;

import edu.mit.simile.gadget.Result;
import edu.mit.simile.gadget.comparators.ValueComparator;

public class CasedValueComparatorTest extends ValueComparatorTest {

	public void testAscendingComparator() {
		Comparator comparator = new ValueComparator(true);
		Collections.sort(list,comparator);
		Result r = (Result) list.get(0);
		assertTrue(r.getValue().equals("Mazzocchi"));
	}

	public void testDescendingComparator() {
		Comparator comparator = new ValueComparator(false);
		Collections.sort(list,comparator);
		Result r = (Result) list.get(0);
		assertTrue(r.getValue().equals("steph-ah-noh"));
	}
	
}
