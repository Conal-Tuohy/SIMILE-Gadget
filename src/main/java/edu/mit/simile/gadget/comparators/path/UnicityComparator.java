package edu.mit.simile.gadget.comparators.path;

import java.util.Comparator;

import edu.mit.simile.gadget.data.Path;

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
        Path p1 = (Path) o1;
        Path p2 = (Path) o2;
        int delta = p1.getUnicity() - p2.getUnicity();
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? (int) delta : (int) -delta;
    }
    
}
