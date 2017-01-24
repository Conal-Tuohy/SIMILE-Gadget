package edu.mit.simile.gadget.comparators;

import java.util.Comparator;

import edu.mit.simile.gadget.Result;

/**
 *
 */
public class FrequencyComparator implements Comparator {

    Comparator next;
    boolean ascending;

    public FrequencyComparator(boolean ascending) {
        this.next = null;
        this.ascending = ascending;
    }

    public FrequencyComparator(boolean ascending, Comparator next) {
        this.next = next;
        this.ascending = ascending;
    }

    public int compare(Object o1, Object o2) {
        Result r1 = (Result) o1;
        Result r2 = (Result) o2;
        int delta = r1.getFrequency() - r2.getFrequency();
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? delta : -delta;
    }

}
