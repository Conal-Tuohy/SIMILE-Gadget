package edu.mit.simile.gadget.comparators;

import java.util.Comparator;

import edu.mit.simile.gadget.Result;

/**
 *
 */
public class UnicityComparator implements Comparator {

    Comparator next;
    boolean ascending;

    public UnicityComparator(boolean ascending) {
        this.next = null;
        this.ascending = ascending;
    }

    public UnicityComparator(boolean ascending, Comparator next) {
        this.next = next;
        this.ascending = ascending;
    }

    public int compare(Object o1, Object o2) {
        Result r1 = (Result) o1;
        Result r2 = (Result) o2;
        int u1 = (100 * r1.getUniques()) / r1.getFrequency();
        int u2 = (100 * r2.getUniques()) / r2.getFrequency();
        int delta = u1 - u2;
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? delta : -delta;
    }

}
