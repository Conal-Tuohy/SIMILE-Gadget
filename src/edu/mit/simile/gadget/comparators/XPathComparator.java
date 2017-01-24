package edu.mit.simile.gadget.comparators;

import java.util.Comparator;

import edu.mit.simile.gadget.Result;
import edu.mit.simile.gadget.utils.XPathUtils;

/**
 *
 */
public class XPathComparator implements Comparator {

    Comparator next;
    boolean ascending;

    public XPathComparator(boolean ascending) {
        this.next = null;
        this.ascending = ascending;
    }

    public XPathComparator(boolean ascending, Comparator next) {
        this.next = next;
        this.ascending = ascending;
    }

    public int compare(Object o1, Object o2) {
        Result r1 = (Result) o1;
        Result r2 = (Result) o2;
        int d1 = XPathUtils.getDepth(r1.getValue());
        int d2 = XPathUtils.getDepth(r2.getValue());
        int delta = d1 - d2;
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? delta : -delta;
    }

}
