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
import edu.mit.simile.gadget.comparators.UniquesComparator;
import edu.mit.simile.gadget.comparators.XPathComparator;
import edu.mit.simile.gadget.utils.ScreenUtils;

/**
 * 
 */
public class FrequencyScreen implements Screen {

	public void output(List results, PrintStream comments, PrintStream data) {

		Result prev = null;
		
		comments.println("\n Frequency Uniques Unicity XPath");
		comments.println("-----------------------------------------------------------------");

		Comparator comparator = new FrequencyComparator(false, new UniquesComparator(false, new XPathComparator(false)));
		Collections.sort(results,comparator);
		
		Result max1 = (Result) Collections.max(results, new FrequencyComparator(true));
		int col1 = ScreenUtils.getColumns(max1.getFrequency());
		Result max2 = (Result) Collections.max(results, new UniquesComparator(true));
		int col2 = ScreenUtils.getColumns(max2.getUniques());
		
		Iterator iterator = results.iterator();
		while (iterator.hasNext()) {
			Result res = (Result) iterator.next();

			data.print(" ");
			
			int frequency = res.getFrequency();
			int prevFreq = (prev != null) ? prev.getFrequency() : 0;
			if (frequency == prevFreq) {
				data.print(ScreenUtils.spaces(col1));
			} else {
				data.print(ScreenUtils.alignRight(frequency,col1));
			}	
			data.print(" ");

			int uniques = res.getUniques();
			data.print(ScreenUtils.alignRight(uniques,col2));
			data.print(" ");

			data.println(res.getValue());
			
			prev = res;
		}		
	}
}
