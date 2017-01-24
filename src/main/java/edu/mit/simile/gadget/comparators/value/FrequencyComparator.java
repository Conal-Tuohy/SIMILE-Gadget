package edu.mit.simile.gadget.comparators.value;

import java.util.Comparator;

import edu.mit.simile.gadget.utils.BytesUtils;

public class FrequencyComparator implements Comparator {
    
    boolean ascending;
    Comparator next;
    
    public FrequencyComparator() {
        this.ascending = false;
    }
    
    public FrequencyComparator(boolean ascending) {
        this.next = null;
        this.ascending = ascending;
    }
    
    public FrequencyComparator(boolean ascending, Comparator next) {
        this.next = next;
        this.ascending = ascending;
    }
    
    public int compare(Object o1, Object o2) {
        byte[] b1 = (byte[]) o1;
        byte[] b2 = (byte[]) o2;
        int delta = BytesUtils.byteArrayToInt(b1) - BytesUtils.byteArrayToInt(b2);
        if (delta == 0 && next != null) {
            return next.compare(o1,o2);
        }
        return (ascending) ? (int) delta : (int) -delta;
    }
    
}
