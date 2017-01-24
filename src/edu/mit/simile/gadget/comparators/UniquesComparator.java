package edu.mit.simile.gadget.comparators;

import java.util.Comparator;

import edu.mit.simile.gadget.Result;

/**
 *
 */
public class UniquesComparator implements Comparator {

    Comparator next;
    boolean ascending;

    public UniquesComparator(boolean ascending) {
        this.next = null;
        this.ascending = ascending;
    }

    public UniquesComparator(boolean ascending, Comparator next) {
        this.next = next;
        this.ascending = ascending;
    }

    public int compare(Object o1, Object o2) {
        Result r1 = (Result) o1;
        Result r2 = (Result) o2;
        int u1 = r1.getUniques();
        int u2 = r2.getUniques();
        int delta = u1 - u2;
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? delta : -delta;
    }

}
