package edu.mit.simile.gadget.tests.data;

import java.util.Map;

import junit.framework.TestCase;
import edu.mit.simile.gadget.data.Node;
import edu.mit.simile.gadget.data.Path;

public class NodeTest extends TestCase {
    
    Node root;
    
    protected void setUp() {
        root = new Node();
        root.add(new Path("/a"));
        root.add(new Path("/a/b"));
        root.add(new Path("/a/b/@c"));
        root.add(new Path("/z"));
        root.add(new Path("/z/y"));
    }
    
    public void testRoots() {
        Map kids = root.getChildren();
        assertTrue(kids.containsKey("a") && kids.containsKey("z"));
    }
    
    public void testNested() {
        Map kids = root.getChildren();
        Node a = (Node) kids.get("a");
        Node z = (Node) kids.get("z");
        assertTrue(a.getChildren().containsKey("b") && z.getChildren().containsKey("y"));
    }
    
    public void testAttribute() {
        Node a = (Node) root.getChildren().get("a");
        Node b = (Node) a.getChildren().get("b");
        assertTrue(b.getChildren().containsKey("@c"));
    }
}
