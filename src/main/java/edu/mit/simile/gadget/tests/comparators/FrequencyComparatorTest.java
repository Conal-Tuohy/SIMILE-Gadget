package edu.mit.simile.gadget.tests.comparators;

import java.util.Collections;
import java.util.Comparator;

import edu.mit.simile.gadget.Result;
import edu.mit.simile.gadget.comparators.FrequencyComparator;

public class FrequencyComparatorTest extends ComparatorTest {

	public void testAscendingComparator() {
		Comparator comparator = new FrequencyComparator(true);
		Collections.sort(list,comparator);
		Result r = (Result) list.get(0);
		assertTrue(r.getFrequency() == 3);
	}

	public void testDescendingComparator() {
		Comparator comparator = new FrequencyComparator(false);
		Collections.sort(list,comparator);
		Result r = (Result) list.get(0);
		assertTrue(r.getFrequency() == 10);
	}

}
