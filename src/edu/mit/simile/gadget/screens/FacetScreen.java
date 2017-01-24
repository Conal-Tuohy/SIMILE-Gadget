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
import edu.mit.simile.gadget.comparators.IgnoreCaseValueComparator;
import edu.mit.simile.gadget.utils.ScreenUtils;

/**
 *
 */
public class FacetScreen implements Screen {

    public void output(List results, PrintStream comments, PrintStream data) {

        comments.println("  facet    frequency");
        comments.println("-----------------------------------------------");

        Comparator comparator = new IgnoreCaseValueComparator(true, new FrequencyComparator(true));
        Collections.sort(results,comparator);

        Result max2 = (Result) Collections.max(results, new FrequencyComparator(true));
        int col2 = ScreenUtils.getColumns(max2.getFrequency());

        Iterator iterator = results.iterator();
        while (iterator.hasNext()) {
            Result res = (Result) iterator.next();
            String facet = res.getValue();
            data.print(facet);
            data.print(ScreenUtils.spaces(facet.length() - col2 + 2));
            data.println(ScreenUtils.alignRight(res.getFrequency(),col2));
        }
    }
}
