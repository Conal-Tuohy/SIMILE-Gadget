package edu.mit.simile.gadget.comparators.path;

import java.util.Comparator;

import edu.mit.simile.gadget.data.Path;

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
        Path r1 = (Path) o1;
        Path r2 = (Path) o2;
        int delta = r1.getFrequency() - r2.getFrequency();
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? delta : -delta;
    }
    
}
