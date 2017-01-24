package edu.mit.simile.gadget.comparators;

import java.util.Comparator;

import edu.mit.simile.gadget.Result;

/**
 *
 */
public class IgnoreCaseValueComparator implements Comparator {

    Comparator next;
    boolean ascending;

    public IgnoreCaseValueComparator(boolean ascending) {
        this.next = null;
        this.ascending = ascending;
    }

    public IgnoreCaseValueComparator(boolean ascending, Comparator next) {
        this.next = next;
        this.ascending = ascending;
    }

    public int compare(Object o1, Object o2) {
        Result r1 = (Result) o1;
        Result r2 = (Result) o2;
        String f1 = r1.getValue();
        String f2 = r2.getValue();
        int delta = f1.compareToIgnoreCase(f2);
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? delta : -delta;
    }

}
