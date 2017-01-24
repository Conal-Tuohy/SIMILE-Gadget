/*
 * 
 */
package edu.mit.simile.gadget.utils;

/**
 * 
 */
public class ScreenUtils {

    static final float SECOND = 1000;
    static final float MINUTE = 60 * SECOND;
    static final float HOUR   = 60 * MINUTE;
    
    public static int getColumns(int number) {
    		return (number == 0) ? 1 : (int) (Math.log(number)/Math.log(10)) + 1;
    }
    
    public static String spaces(String str) {
    		return spaces(str.length());
    }
    
    public static String spaces(int number) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < number; i++) {
			buffer.append(' ');
		}
		return buffer.toString();
    }
    
    public static String alignRight(int number, int colsize) {
    		return spaces(colsize - getColumns(number)) + number;
    }

    public static String format(long time) {
        StringBuffer out = new StringBuffer();
        if (time <= SECOND) {
            out.append(time);
            out.append(" milliseconds");
        } else if (time <= MINUTE) {
            out.append(time / SECOND);
            out.append(" seconds");
        } else if (time <= HOUR) {
            out.append(time / MINUTE);
            out.append(" minutes");
        } else {
            out.append(time / HOUR);
            out.append(" hours");
        }
        return out.toString();
    }
    
}
