package edu.mit.simile.gadget.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.mit.simile.gadget.utils.XPathUtils;

public class Namespaces {
    
    Logger logger = Logger.getLogger(Dataset.class);
    
    Map namespaces;
    
    public Namespaces() {
        this.namespaces = new HashMap();
    }
    
    public Namespaces(Map n) {
        this.namespaces = n;
    }
    
    public String getNamespacePrefix(String uri, String qname, int type) {
        if (logger.isDebugEnabled()) logger.debug("  Get namespace prefix for " + ((type == Dataset.ELEMENT) ? "element" : "attribute") + " (" + uri + "," + qname + ")");
        String prefix = XPathUtils.getNamespacePrefix(qname);
        if (!(type == Dataset.ATTRIBUTE && "".equals(prefix))) {
            String namespaceURI = (String) namespaces.get(prefix);
            if (namespaceURI != null) {
                if (!namespaceURI.equals(uri)) {
                    if (logger.isDebugEnabled()) logger.debug("   Prefix \"" + prefix + "\" is already assigned to a different uri \"" + namespaceURI + "\"");
                    int i = 0;
                    while (true) {
                        String newPrefix = prefix + i++;
                        namespaceURI = (String) namespaces.get(newPrefix);
                        if (namespaceURI == null) {
                            if (logger.isDebugEnabled()) logger.debug("    Returning new prefix \"" + newPrefix + "\" for uri \"" + uri + "\"");
                            namespaces.put(newPrefix, uri);
                            prefix = newPrefix;
                            break;
                        } else if (namespaceURI.equals(uri)) {
                            if (logger.isDebugEnabled()) logger.debug("    Returning another prefix \"" + newPrefix + "\" that was already used for \"" + uri + "\"");
                            prefix = newPrefix;
                            break;
                        }
                    }
                } else {
                    if (logger.isDebugEnabled()) logger.debug("   Returning prefix \"" + prefix + "\"");
                }
            } else {
                if (logger.isDebugEnabled()) logger.debug("   Return prefix \"" + prefix + "\" as it was not previously used");
                namespaces.put(prefix, uri);
            }
        }
        return prefix;
    }
    
}
