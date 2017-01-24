package edu.mit.simile.gadget.tests;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;

import edu.mit.simile.gadget.data.Dataset;
import edu.mit.simile.gadget.data.Path;
import edu.mit.simile.gadget.data.Value;
import edu.mit.simile.gadget.handlers.InspectingHandler;

public class GadgetTest extends TestCase {
    
    Dataset dataset;
    Logger logger;
    
    public GadgetTest() {
        logger = Logger.getLogger(GadgetTest.class);
        
        File folder = new File("./target/test-tmp");
        
        try {
            logger.info("Creating the index...");
            
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            
            SAXParser parser = factory.newSAXParser();
            if (!folder.exists()) {
                folder.mkdirs();
            } else {
                File[] files = folder.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            InspectingHandler handler = new InspectingHandler(folder, null, true);
            InputStream stream = this.getClass().getResourceAsStream("/sample1.xml");
            parser.parse(stream,handler);
            handler.getDataset().close();
            logger.info("...done");
        } catch (Exception e) {
            //logger.error("Error: ", e);
        }
        
        try {
            logger.info("Opening the created index...");
            dataset = Dataset.readData(folder);
            logger.info("...done");
        } catch (Exception e) {
            logger.error("Error reading index", e);
        }
    }
    
    boolean isEqual(Path p1, List l) {
        try {
            Map paths = this.dataset.getPaths();
            String xpath = p1.getXpath();
            Path p2 = (Path) paths.get(xpath);
            Map values = (StoredMap) dataset.getPathMap(xpath);
            int count = 0;
            Iterator i =  l.iterator();
            while (i.hasNext()) {
                Value v1 = (Value) i.next();
                Value v2 = (Value) values.get(v1.getValue());
                if (v1.isEqual(v2)) count++;
            }
            return (p1.isEqual(p2) && count == l.size());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public void testRoot() throws Exception {
        String xpath = "/a";
        Map paths = this.dataset.getPaths();
        Path p1 = (Path) paths.get(xpath);
        Path p2 = new Path("/a",1,1,0);
        assertTrue(p1.isEqual(p2));
    }
    
    public void testIdentifiers() {
        Path p = new Path("/a/b",3,3,3);
        List v = new ArrayList();
        v.add(new Value("1",1));
        v.add(new Value("2",1));
        v.add(new Value("3",1));
        assertTrue(isEqual(p,v));
    }
    
    public void testCounting() {
        Path p = new Path("/a/c",6,3,17);
        List v = new ArrayList();
        v.add(new Value("aaaaaaaa",1));
        v.add(new Value("bbb",2));
        v.add(new Value("c",3));
        assertTrue(isEqual(p,v));
    }
    
    public void testConstant() {
        Path p = new Path("/a/d",3,1,15);
        List v = new ArrayList();
        v.add(new Value("zzzzz",3));
        assertTrue(isEqual(p,v));
    }
    
    public void testAttributes() {
        Path p = new Path("/a/e/@f",1,1,1);
        List v = new ArrayList();
        v.add(new Value("a",1));
        assertTrue(isEqual(p,v));
    }
    
    public void testNamespaces1() {
        Path p = new Path("/a/g:h/@h",1,1,1);
        List v = new ArrayList();
        v.add(new Value("a",1));
        boolean n1 = isEqual(p,v);
        
        p = new Path("/a/g:h",1,1,1);
        v = new ArrayList();
        v.add(new Value("b",1));
        boolean n2 = isEqual(p,v);
        
        assertTrue(n1 && n2);
    }
    
    public void testNamespaces2() {
        Path p = new Path("/a/0:i",2,1,6);
        List v = new ArrayList();
        v.add(new Value("foo",2));
        boolean n1 = isEqual(p,v);
        
        p = new Path("/a/1:i",1,1,3);
        v = new ArrayList();
        v.add(new Value("bar",1));
        boolean n2 = isEqual(p,v);
        
        p = new Path("/a/1:i/@k",1,1,1);
        v = new ArrayList();
        v.add(new Value("a",1));
        boolean n3 = isEqual(p,v);
        
        assertTrue(n1 & n2 & n3);
    }
    
    public void testNamespaces3() {
        Path p = new Path("/a/x/2:y/2:z",1,1,1);
        List v = new ArrayList();
        v.add(new Value("a",1));
        boolean n1 = isEqual(p,v);
        
        p = new Path("/a/x/2:y/2:z/@w",1,1,1);
        v = new ArrayList();
        v.add(new Value("b",1));
        boolean n2 = isEqual(p,v);
        
        assertTrue(n1 & n2);
    }
    
    // FIXME(SM) Fix mixed mode
    
//  public void testMixedContent() {
//  Path p = new Path("/a/l/m",1,1,1);
//  List v = new ArrayList();
//  v.add(new Value("c",1));
//  boolean n1 = isEqual(p,v);
//  
//  p = new Path("/a/l/m/@n",1,1,1);
//  v = new ArrayList();
//  v.add(new Value("b",1));
//  boolean n2 = isEqual(p,v);
//  
//  p = new Path("/a/l/n:o",1,1,1);
//  v = new ArrayList();
//  v.add(new Value("e",1));
//  boolean n3 = isEqual(p,v);
//  
//  p = new Path("/a/l",1,1,1);
//  v = new ArrayList();
//  v.add(new Value("adf",1)); // I know, I know, but I don't know what else to do :-(
//  boolean n4 = isEqual(p,v);
//  
//  assertTrue(n1 & n2 & n3 & n4);
//  }

    // FIXME(SM) Fix encoding

//    public void testEncodingValueLatin() {
//        Path p = new Path("/a/o",1,1,1);
//        List v = new ArrayList();
//        v.add(new Value("???????A??",10));
//        assertTrue(isEqual(p,v));
//    }
//
//    public void testEncodingValueJapanese() {
//        Path p = new Path("/a/p",1,1,1);
//        List v = new ArrayList();
//        v.add(new Value("??????",6));
//        assertTrue(isEqual(p,v));
//    }
    
    public void testJSON() throws Exception {
        Database database = dataset.getPathDatabase("/a/c");
        StringWriter writer = new StringWriter();
        dataset.getJSON(writer, database, "", Dataset.ASCENDING, 3, Dataset.VALUES);
        String a = writer.toString();
        String b = "var data = { " + 
        "\"aaaaaaaa\" : [ [\"aaaaaaaa\" , 1] ]," +
        "\"bbb\" : [ [\"bbb\" , 2] ]," +
        "\"c\" : [ [\"c\" , 3] ]" +
        "};";
        assertTrue(isEqualNoWhitespace(a,b));
    }
    
    public void testJSONandSize() throws Exception {
        Database database = dataset.getPathDatabase("/a/c");
        StringWriter writer = new StringWriter();
        dataset.getJSON(writer, database, "bbb", Dataset.ASCENDING, 1, Dataset.VALUES);
        String a = writer.toString();
        String b = "var data = { " + 
        "\"bbb\" : [ [\"bbb\" , 2] ]" +
        "};";
        assertTrue(isEqualNoWhitespace(a,b));
    }
    
    public void testJSONandDescending() throws Exception {
        Database database = dataset.getPathDatabase("/a/c");
        StringWriter writer = new StringWriter();
        dataset.getJSON(writer, database, "bbb", Dataset.DESCENDING, 2, Dataset.VALUES);
        String a = writer.toString();
        String b = "var data = { " + 
        "\"bbb\" : [ [\"bbb\" , 2] ]," +
        "\"aaaaaaaa\" : [ [\"aaaaaaaa\" , 1] ]" +
        "};";
        assertTrue(isEqualNoWhitespace(a,b));
    }
    
    public void testJSONandFrequencies() throws Exception {
        Database database = dataset.getFrequencyDatabase("/a/c");
        StringWriter writer = new StringWriter();
        dataset.getJSON(writer, database, "", Dataset.ASCENDING, 10, Dataset.FREQUENCIES);
        String a = writer.toString();
        String b = "var data = { " + 
        "\"3\" : [ [\"c\" , 3] ], " +
        "\"2\" : [ [\"bbb\" , 2] ], " +
        "\"1\" : [ [\"aaaaaaaa\" , 1] ]" +
        "};";
        assertTrue(isEqualNoWhitespace(a,b));
    }
    
    public void testJSONandLenghts() throws Exception {
        Database database = dataset.getLengthDatabase("/a/c");
        StringWriter writer = new StringWriter();
        dataset.getJSON(writer, database, "", Dataset.ASCENDING, 10, Dataset.LENGTHS);
        String a = writer.toString();
        String b = "var data = { " + 
        "\"8\" : [ [\"aaaaaaaa\" , 1] ], " +
        "\"3\" : [ [\"bbb\" , 2] ], " +
        "\"1\" : [ [\"c\" , 3] ]" +
        "};";
        assertTrue(isEqualNoWhitespace(a,b));
    }
    
    public void testJSONandClustering1() throws Exception {
        Database database = dataset.getClustersDatabase("/a/m");
        StringWriter writer = new StringWriter();
        dataset.getJSON(writer, database, "", Dataset.ASCENDING, 1, Dataset.CLUSTERS);
        String a = writer.toString();
        String b = "var data = { " + 
        "\"bushgeorge\" : [ [\"Bush, George W.\" , 1] , [\"George W. Bush\" , 1 ] ]" +
        "};";
        assertTrue(isEqualNoWhitespace(a,b));
    }
    
    public void testJSONandClustering2() throws Exception {
        Database database = dataset.getClustersDatabase("/a/m");
        StringWriter writer = new StringWriter();
        dataset.getJSON(writer, database, "", Dataset.ASCENDING, 10, Dataset.CLUSTERS);
        String a = writer.toString();
        String b = "var data = { " + 
        "\"bushgeorge\" : [ [\"Bush, George W.\" , 1] , [\"George W. Bush\" , 1 ] ], " +
        "\"johnsmith\" : [ [\"Dr. John Smith\" , 1] , [\"John Smith\" , 1 ] , [\"Smith, John, 1925-1989\" , 1] ]" +
        "};";
        assertTrue(isEqualNoWhitespace(a,b));
    }
    
    public void testJSONandEscapting() throws Exception {
        Database database = dataset.getPathDatabase("/a/n");
        StringWriter writer = new StringWriter();
        dataset.getJSON(writer, database, "", Dataset.ASCENDING, 10, Dataset.VALUES);
        String a = writer.toString();
        String b = "var data = { " + 
        "\"\\\"blah\\\" : [ \\\"blah\\\" , 3]\" : [ [\"\\\"blah\\\" : [ \\\"blah\\\" , 3]\" , 1] ], " +
        "\"blah\\nblah\" : [ [ \"blah\\nblah\" , 1] ] " +
        "};";
        assertTrue(isEqualNoWhitespace(a,b));
    }
    
    boolean isEqualNoWhitespace(String a, String b) {
        String a2 = a.replaceAll("\\s","");
        String b2 = b.replaceAll("\\s","");
        //System.out.println(a2);
        //System.out.println(b2);
        return a2.equals(b2);
    }
    
}
