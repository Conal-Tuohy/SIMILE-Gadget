/*
 * 
 */
package edu.mit.simile.gadget.utils;


/**
 * 
 */
public class XPathUtils {

	public static String difference(String xpath1, String xpath2) {
		String[] paths1 = split(xpath1);
		String[] paths2 = split(xpath2);
		int length = Math.min(paths1.length, paths2.length);
		int index = 0;
		while (index < length && paths1[index].equals(paths2[index])) {
			index++;
		}
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < index; i++) {
			b.append(ScreenUtils.spaces(paths1[i]));
		}
		while (index < paths1.length) {
			b.append(paths1[index++]);
			b.append('/');
		}
		return b.toString();
	}
	
	public static String[] split(String xpath) {
		String[] paths = xpath.split("/");
		if (paths.length == 0) {
			paths = new String[1];
			paths[0] = "";
		}
		return paths;
	}
	
	public static int levelChange(String xpath1, String xpath2) {
		String[] paths1 = split(xpath1);
		String[] paths2 = split(xpath2);
		int length = Math.min(paths1.length, paths2.length);
		int index = 0;
		while (index < length && paths1[index].equals(paths2[index])) {
			index++;
		}
		return index;
	}

	public static String getLevel(String xpath, int level) {
		String[] paths = split(xpath);
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < level; i++) {
			b.append(ScreenUtils.spaces(paths[i]));
			b.append(' '); // compensates the slash
		}
		b.append(paths[level]);
		b.append('/');
		return b.toString();
	}
	
	public static int getDepth(String value) {
		int len = value.length();
		int levels = 0;
		char[] chars = new char[len];
		value.getChars(0,len,chars,0);
		for (int i = 0; i < len; i++) {
			if (chars[i] == '/' && (i+1 < len) && chars[i+1] != '/') {
				levels++;
			}
		}
		return levels;
	}	
}
