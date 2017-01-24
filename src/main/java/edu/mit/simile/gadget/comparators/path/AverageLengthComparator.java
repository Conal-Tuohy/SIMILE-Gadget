package edu.mit.simile.gadget.comparators.path;

import java.util.Comparator;

import edu.mit.simile.gadget.data.Path;

/**
 *
 */
public class AverageLengthComparator implements Comparator {
    
    Comparator next;
    boolean ascending;
    
    public AverageLengthComparator(boolean ascending) {
        this.next = null;
        this.ascending = ascending;
    }
    
    public AverageLengthComparator(boolean ascending, Comparator next) {
        this.next = next;
        this.ascending = ascending;
    }
    
    public int compare(Object o1, Object o2) {
        Path p1 = (Path) o1;
        Path p2 = (Path) o2;
        int delta = p1.getAverageLength() - p2.getAverageLength();
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? (int) delta : (int) -delta;
    }
    
}
