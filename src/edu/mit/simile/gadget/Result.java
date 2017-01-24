/*
 * 
 */
package edu.mit.simile.gadget;



/**
 * 
 */
public class Result {

	private transient String value;
	private transient int frequency;
	private transient int uniques;
	
	public Result(String value, int frequency, int uniques) {
		this.value = value;
		this.frequency = frequency;
		this.uniques = uniques;
	}
	
	public String getValue() {
		return this.value;
	}

	public int getFrequency() {
		return this.frequency;
	}
	
	public int getUniques() {
		return this.uniques;
	}
	
	public int getUnicity() {
		return (100 * uniques) / frequency;
	}
	
}
