package edu.mit.simile.gadget.comparators.path;

import java.util.Comparator;

import edu.mit.simile.gadget.data.Path;

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
        Path p1 = (Path) o1;
        Path p2 = (Path) o2;
        int delta = p1.getUniques() - p2.getUniques();
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? delta : -delta;
    }
    
}
