package edu.mit.simile.gadget.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * 
 */
public class RegexpFileFilter implements FileFilter {
    
    private transient String regexp;
    
    public RegexpFileFilter(String regexp) {
        this.regexp = regexp;
    }
    
    public boolean accept(File file) {
        return file.getName().matches(this.regexp);
    }
    
}
