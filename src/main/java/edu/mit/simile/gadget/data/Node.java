package edu.mit.simile.gadget.data;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import edu.mit.simile.gadget.utils.XPathUtils;

public class Node {
    private Map children;
    private Path path;
    
    public Node() {
        children = new TreeMap();
        path = null;
    }
    
    public Map getChildren() {
        return children;
    }
    
    public void setPath(Path r) {
        path = r;
    }
    
    public Path getPath() {
        return path;
    }
    
    public void add(Path path) {
        Node node = this;
        String[] pieces = XPathUtils.split(path.getXpath());
        for (int i = 0 ; i < pieces.length ; i++) {
            if (pieces[1] != null && !"".equals(pieces[i])) {
                Map children = node.getChildren();
                if (children.containsKey(pieces[i])) {
                    node = (Node) children.get(pieces[i]); 
                } else {
                    node = new Node();
                    children.put(pieces[i], node);
                }
            }
        }
        node.setPath(path);
    }
    
    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            toJSON(writer);
        } catch (IOException e) {
            return super.toString();
        }
        return writer.toString();
    }
    
    public void toJSON(Writer writer) throws IOException {
        writer.write("var tree = {\n");
        Iterator i = children.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            Node n = (Node) children.get(key);
            n.toJSON(key,writer,1);
            if (i.hasNext()) writer.write(", \n");
        }
        writer.write("\n}\n");
    }
    
    private void toJSON(String name, Writer writer, int indent) throws IOException {
        indent(writer,indent++);
        writer.write('"');
        writer.write(name);
        writer.write("\": {\n");
        indent(writer,indent);
        writer.write("\"xpath\": "); 
        writer.write('"');
        if (path != null) writer.write(path.getXpath());
        writer.write('"'); 
        writer.write(',');
            
        writer.write("\"type\": "); 
        writer.write('"');
        if (path != null) writer.write(path.getType()); 
        writer.write('"'); 
        writer.write(',');
        
        writer.write("\"uniques\": "); 
        if (path != null) {
            writer.write(String.valueOf(path.getUniques())); 
        } else {
            writer.write("1");
        }
        writer.write(',');
        
        writer.write("\"frequency\": "); 
        if (path != null) {
            writer.write(String.valueOf(path.getFrequency())); 
        } else {
            writer.write("1");
        }
        writer.write(',');
        
        writer.write("\"length\": "); 
        if (path != null) {
            writer.write(String.valueOf(path.getAverageLength()));
        } else {
            writer.write("0");
        }
        
        if (children.size() > 0) {
            writer.write(", \n");
            indent(writer,indent++);
            writer.write("\"children\": {\n");
            Iterator i = children.keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                Node n = (Node) children.get(key);
                n.toJSON(key,writer,indent);
                if (i.hasNext()) writer.write(",");
                writer.write('\n');
            }
            indent(writer,--indent);
            writer.write("}\n");
        } else {
            writer.write('\n');
        }
        indent(writer,--indent);
        writer.write("}");
    }
    
    private void indent(Writer writer, int indent) throws IOException {
        for (int i = 0; i < indent; i++) {
            writer.write(' ');
            writer.write(' ');
        }
    }
}

