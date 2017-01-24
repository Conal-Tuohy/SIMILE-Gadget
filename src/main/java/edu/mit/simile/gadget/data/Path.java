/*
 * 
 */
package edu.mit.simile.gadget.data;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * 
 */
public class Path {
    
    private transient String xpath;
    private transient int frequency;
    private transient int uniques;
    private transient int length;
    
    public static EntryBinding getBinding() {
        return new PathBinding();
    }
    
    public Path(String xpath) {
        this(xpath,0,0,0);
    }
    
    public Path(String xpath, int frequency, int uniques, int length) {
        this.xpath = xpath;
        this.frequency = frequency;
        this.uniques = uniques;
        this.length = length;
    }
    
    public void incFrequency() {
        this.frequency++;
    }
    
    public void incUniques() {
        this.uniques++;
    }
    
    public void incLength(String s) {
        this.length += s.length();
    }
    
    public String getXpath() {
        return this.xpath;
    }
    
    public int getFrequency() {
        return this.frequency;
    }
    
    public int getUniques() {
        return this.uniques;
    }
    
    public int getTotalLength() {
        return this.length;
    }
    
    public int getUnicity() {
        return (int) (this.uniques * 100 / this.frequency);
    }
    
    public int getAverageLength() {
        return (int) (this.length / this.frequency);
    }
    
    public boolean isIdentifier() {
        return this.uniques == this.frequency;
    }
    
    public boolean isConstant() {
        return this.uniques == 1;
    }
    
    public boolean isEmpty() {
        return this.length == 0;
    }
    
    public boolean isAttribute() {
        return this.xpath.indexOf('@') > -1;
    }
    
    public String getType() {
        if (this.isEmpty()) {
            return "empty";
        } else if (this.isConstant()) {
            return "constant";
        } else if (this.isIdentifier()) {
            return "identifier";
        } else {
            return "";
        }
    }
    
    public boolean isEqual(Path p) {
        return (
                p.getFrequency() == this.getFrequency() &&
                p.getUniques() == this.getUniques() &&
                p.getTotalLength() == this.getTotalLength() &&
                p.getXpath().equals(this.getXpath()) 
        );
    }
    
    public String toString() {
        return this.xpath + " [" + this.frequency + "," + this.uniques + "," + this.length + "]";
    }
}

class PathBinding extends TupleBinding {
    
    public void objectToEntry(Object object, TupleOutput to) {
        Path path = (Path) object;
        to.writeString(path.getXpath());
        to.writeInt(path.getFrequency());
        to.writeInt(path.getUniques());
        to.writeInt(path.getTotalLength());
    }
    
    public Object entryToObject(TupleInput ti) {
        String xpath = ti.readString();
        int frequency = ti.readInt();
        int uniques = ti.readInt();
        int length = ti.readInt();
        return new Path(xpath, frequency, uniques, length);
    }
} 
