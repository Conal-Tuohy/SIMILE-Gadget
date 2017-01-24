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
import edu.mit.simile.gadget.comparators.ValueComparator;
import edu.mit.simile.gadget.comparators.FrequencyComparator;
import edu.mit.simile.gadget.comparators.UnicityComparator;
import edu.mit.simile.gadget.comparators.UniquesComparator;
import edu.mit.simile.gadget.comparators.XPathComparator;
import edu.mit.simile.gadget.utils.ScreenUtils;

/**
 * 
 */
public class UnicityScreen implements Screen {

	public void output(List results, PrintStream comments, PrintStream data) {

		Result prev = null;
		
		comments.println("\n Unicity Frequency Uniques XPath");
		comments.println("-----------------------------------------------------------------");

		Comparator comparator = new UnicityComparator(false, new FrequencyComparator(false, new UniquesComparator(false, new XPathComparator(true, new ValueComparator(true)))));
		Collections.sort(results,comparator);
		
		Result max2 = (Result) Collections.max(results, new FrequencyComparator(true));
		int col2 = ScreenUtils.getColumns(max2.getFrequency());
		Result max3 = (Result) Collections.max(results, new UniquesComparator(true));
		int col3 = ScreenUtils.getColumns(max3.getUniques());
		
		Iterator iterator = results.iterator();
		while (iterator.hasNext()) {
			Result res = (Result) iterator.next();

			System.out.print(" ");
			
			int unicity = res.getUnicity();
			int prevUnicity = (prev != null) ? prev.getUnicity() : -1;
			if (unicity == prevUnicity) {
				data.print(ScreenUtils.spaces(4));
			} else {
				data.print(ScreenUtils.alignRight(unicity,3));
				data.print("%");
			}	

			data.print(" ");
			data.print(ScreenUtils.alignRight(res.getFrequency(),col2));
			data.print(" ");
			data.print(ScreenUtils.alignRight(res.getUniques(),col3));
			data.print(" ");
			data.println(res.getValue());
			
			prev = res;
		}		
	}
}
