package edu.mit.simile.gadget.tests.comparators;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.mit.simile.gadget.Result;

public class ValueComparatorTest extends TestCase {

	protected List list;
	
	public void setUp() {
		this.list = new ArrayList();
		this.list.add(new Result("Stefano",45,5));
		this.list.add(new Result("Mazzocchi",534,2));
		this.list.add(new Result("steph-ah-noh",223,3));
		this.list.add(new Result("madzoc-key",123,3));
	}
	
}
