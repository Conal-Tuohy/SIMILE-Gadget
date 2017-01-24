package edu.mit.simile.gadget.comparators.path;

import java.util.Comparator;

import edu.mit.simile.gadget.data.Path;

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
        Path p1 = (Path) o1;
        Path p2 = (Path) o2;
        String f1 = p1.getXpath();
        String f2 = p2.getXpath();
        int delta = f1.compareTo(f2);
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? delta : -delta;
    }
    
}
