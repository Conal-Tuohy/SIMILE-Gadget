/*
 * 
 */
package edu.mit.simile.gadget.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.mit.simile.gadget.Result;

/**
 * 
 */
public class ProjectingHandler extends InspectingHandler {
			
	private transient Map results = new HashMap();
	private transient String facet = null;
	
	public ProjectingHandler(String facet) {
		this.facet = facet;
	}
		
	public List getResults() {
		List endResults = new ArrayList();
		Iterator iterator = results.keySet().iterator();
		while (iterator.hasNext()) {
			String facet = (String) iterator.next();
			Use use = (Use) results.get(facet);
			int frequency = use.getUse();
			Result result = new Result(facet,frequency,0);
			endResults.add(result);
		}
		return endResults;		
	}

    public void record(String xpath, String value) {
    		if (xpath.indexOf(facet) > 0) {
    			if (results.containsKey(value)) {
        			Use use = (Use) results.get(value);
        			use.recordUse();
    			} else {
    				results.put(value, new Use());
    			}
    		}
    }    	
}
