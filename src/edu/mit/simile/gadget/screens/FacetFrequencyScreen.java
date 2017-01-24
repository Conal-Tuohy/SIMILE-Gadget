/*
 * 
 */
package edu.mit.simile.gadget.screens;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.mit.simile.gadget.Result;
import edu.mit.simile.gadget.comparators.FrequencyComparator;
import edu.mit.simile.gadget.utils.ScreenUtils;

/**
 * 
 */
public class FacetFrequencyScreen implements Screen {

	public void output(List results, PrintStream comments, PrintStream data) {

		comments.println(" frequency  facet");
		comments.println("-----------------------------------------------");
		
		Comparator comparator = new FrequencyComparator(true);
		Collections.sort(results,comparator);

		Result max = (Result) results.get(0);
		int col = ScreenUtils.getColumns(max.getFrequency());
	
		Iterator iterator = results.iterator();
		while (iterator.hasNext()) {
			Result res = (Result) iterator.next();
			String facet = res.getValue();
			data.print(" ");
			data.print(ScreenUtils.alignRight(res.getFrequency(),col));
			data.println(facet);
		}
	}

}
