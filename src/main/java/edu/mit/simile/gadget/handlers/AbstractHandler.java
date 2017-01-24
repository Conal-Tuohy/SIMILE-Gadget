/*
 * 
 */
package edu.mit.simile.gadget.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 */
public abstract class AbstractHandler extends DefaultHandler {
	
	protected transient StringBuffer buffer = new StringBuffer();
    protected transient boolean textMode = false;
	protected transient Path path = new Path();

	public abstract List getResults();
    public abstract void processText(String str);

    public class Path extends ArrayList {
    	
    		StringBuffer buffer = new StringBuffer();
    	
	    	void descend(String path) {
	    		this.add(path);
	    	}
	    	
	    	void ascend() {
	    		this.remove(this.size() - 1);
	    	}
	    	
	    	/*
	    	 * NOTE(SM): this method is a hotspot for garbage creation, as we shouldn't be needing
	    	 * to generate a new string for every path since there are not so many xpaths
	    	 * in the thing (which is the reason for this program to exist, actually).
	    	 * 
	    	 * One way to remove the problem is to create a lookup-table for xpaths where
	    	 * a new one is created only when not encountered before. One way of doing it is
	    	 * to create a tree of tokens with the generated path as a leaf so that
	    	 * ascend/descend crawl this tree and toPath() just does a lookup or
	    	 * puts the string in place if required.
	    	 */
	    	String toPath() {
	    		buffer.setLength(0);
	    		buffer.append('/');
	    		Iterator iterator = this.iterator();
	    		while (iterator.hasNext()) {
	    			String path = (String) iterator.next();
	    			buffer.append(path);
	    			buffer.append('/');
	    		}
	    		return buffer.toString();
	    	}
    }
    
    /** Start element. */
    public void startElement(String uri, String local, String raw, Attributes attrs) throws SAXException {
    		this.path.descend(raw);
    		checkText();
    }

    /** Start element. */
    public void endElement(String uri, String local, String raw) throws SAXException {
		checkText();
		this.path.ascend();
    }
    
    /** Characters. */
    public void characters(char chars[], int start, int length) throws SAXException {
    		this.textMode = true;
    		this.buffer.append(chars, start, length);
    }

    protected void checkText() {
		if (this.textMode) {
			this.textMode = false;
			processText(buffer.toString());
			buffer.setLength(0);
		}
    }
    
    /** Warning. */
    public void warning(SAXParseException exception) throws SAXException {
        printError("Warning", exception);
    }
    
    /** Error. */
    public void error(SAXParseException exception) throws SAXException {
        printError("Error", exception);
    }
    
    /** Fatal error. */
    public void fatalError(SAXParseException exception) throws SAXException {
        printError("Fatal Error", exception);
        //throw ex;
    }
    
    /** Prints the error message. */
    protected void printError(String type, SAXParseException exception) {
        System.err.print("[");
        System.err.print(type);
        System.err.print("] ");
        if (exception == null) {
            System.out.println("!!!");
        }
        String systemId = exception.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
            System.err.print(systemId);
        }
        System.err.print(':');
        System.err.print(exception.getLineNumber());
        System.err.print(':');
        System.err.print(exception.getColumnNumber());
        System.err.print(": ");
        System.err.print(exception.getMessage());
        System.err.println();
        System.err.flush();
    }
	
}
