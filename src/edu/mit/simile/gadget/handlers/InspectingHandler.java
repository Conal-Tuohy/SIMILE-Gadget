/*
 *
 */
package edu.mit.simile.gadget.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.mit.simile.gadget.Result;

/**
 *
 */
public class InspectingHandler extends AbstractHandler {

    public class Use {
        private transient int frequency;

        public Use() {
            this.frequency = 1;
        }

        public void recordUse() {
            this.frequency++;
        }

        public int getUse() {
            return this.frequency;
        }
    }

    public class XPath extends Use {
        private transient Map uses;

        public XPath() {
            super();
            this.uses = new HashMap(0,0.75f);
        }

        public Map getUses() {
            return this.uses;
        }
    }

    private transient Map results = new HashMap();

    public List getResults() {
        List endResults = new ArrayList();
        Iterator iterator = results.keySet().iterator();
        while (iterator.hasNext()) {
            String xpathStr = (String) iterator.next();
            XPath xpath = (XPath) results.get(xpathStr);
            int frequency = xpath.getUse();
            Map uses = xpath.getUses();
            int uniques = uses.keySet().size();
            Result result = new Result(xpathStr,frequency,uniques);
            endResults.add(result);
        }
        return endResults;
    }

    /** Start element. */
    public void startElement(String uri, String local, String raw, Attributes attrs) throws SAXException {
            super.startElement(uri,local,raw,attrs);
            //if (this.textMode) {
                // record this is a mixed mode element
            //}
            for (int i = 0; i < attrs.getLength(); i++) {
                record(this.path.toPath() + "@" + attrs.getQName(i), attrs.getValue(i));
            }
    }

    public void processText(String str) {
        String trimmed = str.trim();
        if (trimmed.length() > 0) {
            record(this.path.toPath(),trimmed);
        }
    }

    public void record(String xpathStr, String valueStr) {
        XPath xpath;
        if (results.containsKey(xpathStr)) {
            xpath = (XPath) results.get(xpathStr);
            xpath.recordUse();
        } else {
            xpath  = new XPath();
            results.put(xpathStr, xpath);
        }
        Map uses = xpath.getUses();
        String md5 = DigestUtils.md5Hex(valueStr);
        if (uses.containsKey(md5)) {
            Use use = (Use) uses.get(md5);
            use.recordUse();
        } else {
            uses.put(md5,new Use());
        }
    }

}
