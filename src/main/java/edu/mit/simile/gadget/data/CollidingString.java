package edu.mit.simile.gadget.data;

import edu.mit.simile.gadget.utils.StringUtils;

public class CollidingString {
    
    String str;
    int hashCode;
    
    public CollidingString(String s) {
        this.str = s;
        this.hashCode = StringUtils.keyfy(s).hashCode();
    }
    
    public String toString() {
        return str;
    }
    
    public int hashCode() {
        return this.hashCode;
    }
    
    public boolean equals(Object o) {
        return o.hashCode() == this.hashCode;
    }
    
}
