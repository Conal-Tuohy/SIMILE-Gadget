package edu.mit.simile.gadget.utils;

import java.util.Arrays;

public class StringUtils {
    
    /*
     * NOTE(SM): I've tried to keep this as unicode-friendly as possible,
     *           but the lowercase transformation I think it's only ASCII
     */
    
    public static String keyfy(String str) {
        str = str.trim();   // removing spaces front and back
        str = str.toLowerCase(); // project to lowercase (WARNING: this is locale sensitive!) 
        str = str.replaceAll("[\\p{Punct}]","  "); // make punctuation whitespace
        str = " " + str + " "; // this trick makes the regexps below work on the edge tokens as well
        str = str.replaceAll(" (mrs|mr|dr|miss|phd|md) "," "); // remove non-identifying tokens
        str = str.replaceAll(" [^ ] "," "); // discard single-char tokens
        str = str.replaceAll(" [0-9]+ "," "); // discard isolated digits
        str = str.replaceAll("[\\p{Space}]+"," "); // condense multiple whitespace into one
        str = str.trim(); // remove the trick now
        String[] tokens = str.split(" "); // tokenize
        Arrays.sort(tokens);
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            b.append(tokens[i]);
        }
        return b.toString();
    }
    
    public static String jsonEscape(String str) {
        str = str.replaceAll("\"","\\\\\\\"");
        str = str.replaceAll("\n","\\\\\\n");
        return str;
    }
}
