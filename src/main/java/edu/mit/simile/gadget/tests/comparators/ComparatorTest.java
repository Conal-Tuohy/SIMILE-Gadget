package edu.mit.simile.gadget.tests.comparators;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.mit.simile.gadget.Result;

public class ComparatorTest extends TestCase {

	protected List list;
	
	public void setUp() {
		this.list = new ArrayList();
		this.list.add(new Result("a/a/a/a",10,5));
		this.list.add(new Result("A/A/A",8,2));
		this.list.add(new Result("b/b",6,3));
		this.list.add(new Result("C",3,3));
	}
	
}
