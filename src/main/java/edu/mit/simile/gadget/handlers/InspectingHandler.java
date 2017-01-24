package edu.mit.simile.gadget.handlers;

import java.io.File;
import java.util.Properties;

import org.xml.sax.SAXException;

import com.sleepycat.je.DatabaseException;

import edu.mit.simile.gadget.data.Dataset;
import edu.mit.simile.gadget.data.Namespaces;

/** 
 * This is the SAX handler that accepts SAX events from the XML parser
 * and records useful events into the databases.
 * 
 * @author Stefano Mazzocchi 
 */
public class InspectingHandler extends Handler {
    
    Dataset dataset;
    
    public InspectingHandler(File folder, Properties p, boolean t) throws DatabaseException {
        super(t);
        dataset = Dataset.writeData(folder, p);
        namespaces = new Namespaces(dataset.getNamespaces());
    }
    
    public Dataset getDataset() {
        return dataset;
    }
    
    // --------------------------------------------------------------
    
    public void attribute(String uri, String name, String qname, String value) {
        if (logger.isDebugEnabled()) logger.debug(" Attribute(" + uri + "," + name + "," + qname + "," + value + ")");
        try {
            cursor = cursor.descend(uri, name, qname, Dataset.ATTRIBUTE);
            dataset.record(this.cursor.getPath(), value);
            cursor = cursor.ascend();
        } catch (DatabaseException e) {
            logger.error("Problem saving attribute: " + qname + "=\"" + value + "\"", e);
        }
    }
    
    public void endElement(String uri, String name, String qname) throws SAXException {
        if (logger.isDebugEnabled()) logger.debug(" End Element(" + uri + "," + name + "," + qname + ")");
        String value = getText();
        try {
            dataset.record(this.cursor.getPath(),value);
        } catch (DatabaseException e) {
            logger.error("Problem saving element: " + this.cursor.getPath() + "=\"" + value + "\"", e);
        }
        cursor = cursor.ascend();
    }
}
