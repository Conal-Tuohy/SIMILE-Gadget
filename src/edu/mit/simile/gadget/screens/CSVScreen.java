/*
 * 
 */
package edu.mit.simile.gadget.screens;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import edu.mit.simile.gadget.Result;

/**
 * 
 */
public class CSVScreen implements Screen {

	public void output(List results, PrintStream comments, PrintStream data) {

		data.println("xpath,frequency,uniques");
		
		Iterator iterator = results.iterator();
		while (iterator.hasNext()) {
			Result res = (Result) iterator.next();
			data.print(res.getValue());
			data.print(',');
			data.print(res.getFrequency());
			data.print(',');
			data.println(res.getUniques());
		}
		
	}
}
