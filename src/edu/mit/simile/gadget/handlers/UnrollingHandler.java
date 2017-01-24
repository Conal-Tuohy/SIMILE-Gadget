/*
 * 
 */
package edu.mit.simile.gadget.handlers;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * 
 */
public class UnrollingHandler extends AbstractHandler {
	
	public List getResults() {
		return null;
	}
	
    /** Start element. */
    public void startElement(String uri, String local, String raw, Attributes attrs) throws SAXException {
    		super.startElement(uri,local,raw,attrs);
    		for (int i = 0; i < attrs.getLength(); i++) {
    			System.out.print(this.path.toPath());
    			System.out.print('@');
    			System.out.print(attrs.getQName(i));
    			System.out.print("/\"");
    			System.out.print(attrs.getValue(i));
    			System.out.println('"');
    		}
    }
    
    public void processText(String str) {
		String trimmed = str.trim();
		if (trimmed.length() > 0) {
			System.out.print(this.path.toPath());
			System.out.print('"');
			System.out.print(trimmed.replaceAll("\n", "\\n"));
			System.out.println('"');
		}
    }
    	
}
