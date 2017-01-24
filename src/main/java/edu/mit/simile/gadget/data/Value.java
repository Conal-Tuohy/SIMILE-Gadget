package edu.mit.simile.gadget.data;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import edu.mit.simile.gadget.utils.StringUtils;

public class Value {
    
    private String value;
    private int frequency;
    
    public static EntryBinding getBinding() {
        return new ValueBinding();
    }
    
    public Value(String v) {
        this(v,0);
    }
    
    public Value(String v, int f) {
        this.value = v;
        this.frequency = f;
    }
    
    public void incFrequency() {
        this.frequency += 1;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public int getFrequency() {
        return this.frequency;
    }
    
    public boolean isEqual(Value v) {
        return (
            v.getFrequency() == this.getFrequency() &&
            v.getValue().equals(this.getValue())
        );
    }
    
    public String toString() {
        return this.value + " [" + this.frequency + "]";
    }
    
    public String toJSON() {
        return "[\"" + StringUtils.jsonEscape(this.value) + "\" , " + this.frequency + "]";
    }
    
}

class ValueBinding extends TupleBinding {
    
    public void objectToEntry(Object object, TupleOutput to) {
        Value value = (Value) object;
        to.writeString(value.getValue());
        to.writeInt(value.getFrequency());
    }
    
    public Object entryToObject(TupleInput ti) {
        String value = ti.readString();
        int frequency = ti.readInt();
        return new Value(value, frequency);
    }
} 
