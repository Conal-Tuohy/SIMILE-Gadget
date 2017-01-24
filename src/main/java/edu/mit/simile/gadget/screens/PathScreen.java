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
import edu.mit.simile.gadget.comparators.ValueComparator;
import edu.mit.simile.gadget.comparators.ValueSizeComparator;
import edu.mit.simile.gadget.utils.ScreenUtils;
import edu.mit.simile.gadget.utils.XPathUtils;

/**
 * 
 */
public class PathScreen implements Screen {

	public void output(List results, PrintStream comments, PrintStream data) {

		Result prev = new Result("/",0,0);
		
		comments.println("\n  xpath                   frequency uniques unicity");
		comments.println("----------------------------------------------------");
		
		Comparator comparator = new ValueComparator(true);
		Collections.sort(results,comparator);

		Result max1 = (Result) Collections.max(results, new ValueSizeComparator(true));
		int col1 = max1.getValue().length();
		Result max3 = (Result) Collections.max(results, new FrequencyComparator(true));
		int col3 = ScreenUtils.getColumns(max3.getFrequency());
		Result max4 = (Result) Collections.max(results, new UniquesComparator(true));
		int col4 = ScreenUtils.getColumns(max4.getUniques());
	
		Iterator iterator = results.iterator();
		while (iterator.hasNext()) {
			Result res = (Result) iterator.next();

			String path = res.getValue();
			int depth = XPathUtils.getDepth(path);

			int changeLevel = XPathUtils.levelChange(path, prev.getValue());
			for (int i = changeLevel; i < depth; i++) {
				data.println(XPathUtils.getLevel(path, i));
			}

			String str = XPathUtils.getLevel(path,depth);
			data.print(str);
			data.print(ScreenUtils.spaces(col1 - str.length()));
			data.print(" ");
			data.print(ScreenUtils.alignRight(res.getFrequency(),col3));
			data.print(" ");
			data.print(ScreenUtils.alignRight(res.getUniques(),col4));
			data.print(" ");
			data.print(ScreenUtils.alignRight(res.getUnicity(),3));
			data.println("%");
			
			prev = res;
			
		}
	}
}
