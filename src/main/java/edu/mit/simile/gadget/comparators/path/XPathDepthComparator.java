package edu.mit.simile.gadget.comparators.path;

import java.util.Comparator;

import edu.mit.simile.gadget.data.Path;
import edu.mit.simile.gadget.utils.XPathUtils;

/**
 *
 */
public class XPathDepthComparator implements Comparator {
    
    Comparator next;
    boolean ascending;
    
    public XPathDepthComparator(boolean ascending) {
        this.next = null;
        this.ascending = ascending;
    }
    
    public XPathDepthComparator(boolean ascending, Comparator next) {
        this.next = next;
        this.ascending = ascending;
    }
    
    public int compare(Object o1, Object o2) {
        Path p1 = (Path) o1;
        Path p2 = (Path) o2;
        int d1 = XPathUtils.getDepth(p1.getXpath());
        int d2 = XPathUtils.getDepth(p2.getXpath());
        int delta = d1 - d2;
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? delta : -delta;
    }
    
}
