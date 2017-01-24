package edu.mit.simile.gadget.handlers;

import java.util.Set;

import org.xml.sax.SAXException;

import edu.mit.simile.gadget.data.Dataset;

/** 
 * This is the SAX handler that accepts SAX events from the XML parser
 * and dumps strings belonging to a particular xpath to a given output stream
 * along with the information about what file contained it
 * 
 * @author Stefano Mazzocchi 
 */
public class FilteringHandler extends Handler {
    
    Set set;
    String xpath;
    
    public FilteringHandler(String p, boolean t) {
        super(t);
        xpath = p;
    }
    
    public void setValueContainer(Set l) {
        this.set = l;
    }
    
    public void attribute(String uri, String name, String qname, String value) throws SAXException {
        if (logger.isDebugEnabled()) logger.debug(" Attribute(" + uri + "," + name + "," + qname + "," + value + ")");
        cursor = cursor.descend(uri, name, qname, Dataset.ATTRIBUTE);
        if (this.cursor.getPath().equals(xpath)) {
            record(value);
        }
        cursor = cursor.ascend();
    }
    
    public void endElement(String uri, String name, String qname) throws SAXException {
        if (logger.isDebugEnabled()) logger.debug(" End Element(" + uri + "," + name + "," + qname + ")");
        String value = getText();
        if (this.cursor.getPath().equals(xpath)) {
            record(value);
        }
        cursor = cursor.ascend();
    }
    
    public void record(String value) {
        set.add(value);
    }
}
