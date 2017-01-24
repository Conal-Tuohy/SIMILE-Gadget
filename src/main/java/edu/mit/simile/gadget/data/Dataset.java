package edu.mit.simile.gadget.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredIterator;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentStats;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;

import edu.mit.simile.gadget.bindings.ClusteringKeyCreator;
import edu.mit.simile.gadget.bindings.FrequencyKeyCreator;
import edu.mit.simile.gadget.bindings.LengthKeyCreator;
import edu.mit.simile.gadget.comparators.value.FrequencyComparator;
import edu.mit.simile.gadget.comparators.value.LengthComparator;
import edu.mit.simile.gadget.utils.StringUtils;

/** 
 * This class encapsulates all the functionality required to access and
 * generated the berkeleydb databases and indices.
 * 
 * @author Stefano Mazzocchi 
 */
public class Dataset {
    
    public static final String LOCK = "je.lck";
    
    public static final int ELEMENT = 0;
    public static final int ATTRIBUTE = 1;
    
    Logger logger = Logger.getLogger(Dataset.class);
    Logger statsLogger = Logger.getLogger(Dataset.class + "-stats");
    
    boolean readOnly;
    File folder;
    Environment env;
    Database pathsDatabase;
    Database namespacesDatabase;
    StoredMap paths;
    StoredMap namespaces;
    Properties properties;
    Map pathDatabases;
    Map frequencyDatabases;
    Map lengthDatabases;
    Map clustersDatabases;
    Map pathMaps;
    Map frequencyMaps;
    Map lengthMaps;
    Map clustersMaps;
    List namespacesPrefixes;
    List pathsList;
    Node root;
    
    EntryBinding stringBinder = TupleBinding.getPrimitiveBinding(String.class);
    EntryBinding intBinder = TupleBinding.getPrimitiveBinding(Integer.class);
    
    EntryBinding pathBinder = Path.getBinding();
    EntryBinding namespaceBinder = stringBinder;
    EntryBinding valueBinder = Value.getBinding();
    
    public static boolean isData(File folder) {
        File results = new File(folder, LOCK);
        return folder.isDirectory() && results.exists() && results.canRead();
    }
    
    public static Dataset readData(File f) throws DatabaseException {
        return new Dataset(f,null,true);
    }

    public static Dataset writeData(File f) throws DatabaseException {
        return new Dataset(f,null,false);
    }
    
    public static Dataset writeData(File f, Properties p) throws DatabaseException {
        return new Dataset(f,p,false);
    }
    
    private Dataset(File f, Properties p, boolean ro) throws DatabaseException {
        folder = f;
        logger.debug("Opening dataset [" + ((ro) ? "readOnly" : "read/write") + " mode]");
        readOnly = ro;
        logger = Logger.getLogger(Dataset.class);

        logger.debug("Opening environment: " + f);
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(false); 
        envConfig.setAllowCreate(!readOnly);
        envConfig.setReadOnly(readOnly);
        env = new Environment(f, envConfig);

        File props = new File(folder, "dataset.properties");
        if (!ro && p != null) {
            this.properties = p;
            try {
                FileOutputStream fos = new FileOutputStream(props);
                p.store(fos,null);
                fos.close();
            } catch (IOException e) {
                logger.warn("Error saving dataset properties", e);
            }
        } else {
            if (props.exists()) {
                try {
                    Properties pr = new Properties();
                    FileInputStream fis = new FileInputStream(props);
                    pr.load(fis);
                    fis.close();
                    this.properties = pr;
                } catch (IOException e) {
                    logger.warn("Error reading dataset properties", e);
                }
            }
        }
        
        getPaths();
        getNamespaces();
        getTree();
        
        pathDatabases = new HashMap();
        frequencyDatabases = new HashMap();
        lengthDatabases = new HashMap();
        clustersDatabases = new HashMap();
        
        pathMaps = new HashMap();
        frequencyMaps = new HashMap();
        lengthMaps = new HashMap();
        clustersMaps = new HashMap();
    }
    
    public Environment getEnvironment()  {
        return env;
    }
    
    public Map getProperties() {
        return this.properties;
    }
    
    private File getTempDir() {
        File temp = new File(folder, "__temp__");
        temp.mkdirs();
        return temp;
    }
    
    public StoredMap getPaths() throws DatabaseException {
        if (paths == null) {
            logger.debug("Opening path database");
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTransactional(false); 
            dbConfig.setAllowCreate(!readOnly);
            dbConfig.setReadOnly(readOnly);
            pathsDatabase = env.openDatabase(null, "paths", dbConfig);
            paths = new StoredMap(pathsDatabase, stringBinder, pathBinder, !readOnly);
        }
        return paths;
    }
    
    public Node getTree() throws DatabaseException {
        if (root == null) {
            logger.debug("Building tree from list of paths");
            root = new Node();
            List paths = getPathsAsList();
            Iterator i = paths.iterator();
            while (i.hasNext()) {
                Path path = (Path) i.next();
                logger.debug(" found path: " + path.getXpath());
                root.add(path);
            }
        }
        return root;
    }
    
    public Database getNamespacesDatabase() throws DatabaseException {
        if (namespacesDatabase == null) {
            logger.debug("Opening namespaces database");
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTransactional(false); 
            dbConfig.setAllowCreate(!readOnly);
            dbConfig.setReadOnly(readOnly);
            namespacesDatabase = env.openDatabase(null, "namespaces", dbConfig);
        }
        return namespacesDatabase;
    }
    
    public StoredMap getNamespaces() throws DatabaseException {
        if (namespaces == null) {
            Database namespacesDatabase = getNamespacesDatabase();
            namespaces = new StoredMap(namespacesDatabase, stringBinder, namespaceBinder, !readOnly);
        }
        return namespaces;
    }
    
    public Database getPathDatabase(String xpath) throws DatabaseException {
        if (pathDatabases.containsKey(xpath)) {
            return (Database) pathDatabases.get(xpath);
        } else {
            logger.debug("Opening path database for: " + xpath);
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTransactional(false); 
            dbConfig.setAllowCreate(!readOnly);
            dbConfig.setReadOnly(readOnly);
            Database d = env.openDatabase(null, xpath, dbConfig);
            pathDatabases.put(xpath, d);
            return d;
        }
    }
    
    public StoredMap getPathMap(String xpath) throws DatabaseException {
        if (pathMaps.containsKey(xpath)) {
            return (StoredMap) pathMaps.get(xpath);
        } else {
            Database d = getPathDatabase(xpath);
            StoredMap m = new StoredMap(d, stringBinder, valueBinder, !readOnly);
            pathMaps.put(xpath, m);
            // VERY IMPORTANT - leave the two calls below to initialize the secondary databases!
            getFrequencyDatabase(xpath);
            getLengthDatabase(xpath); 
            getClustersDatabase(xpath); 
            return m;
        }
    }
    
    public Database getFrequencyDatabase(String xpath) throws DatabaseException {
        if (frequencyDatabases.containsKey(xpath)) {
            return (Database) frequencyDatabases.get(xpath);
        } else {
            logger.debug("Opening frequency database for: " + xpath);
            Database database = getPathDatabase(xpath);
            SecondaryConfig secConfig = new SecondaryConfig();
            secConfig.setSortedDuplicates(true);
            secConfig.setTransactional(false); 
            secConfig.setAllowCreate(!readOnly);
            secConfig.setReadOnly(readOnly);
            secConfig.setBtreeComparator(FrequencyComparator.class);
            secConfig.setKeyCreator(new FrequencyKeyCreator());
            SecondaryDatabase d = env.openSecondaryDatabase(null, xpath + "-frequency", database, secConfig);
            frequencyDatabases.put(xpath, d);
            return d;
        }
    }
    
    public StoredMap getFrequencyMap(String xpath) throws DatabaseException {
        if (frequencyMaps.containsKey(xpath)) {
            return (StoredMap) frequencyMaps.get(xpath);
        } else {
            Database database = getFrequencyDatabase(xpath);
            StoredMap m = new StoredMap(database, intBinder, valueBinder, !readOnly);
            frequencyMaps.put(xpath, m);
            return m;
        }
    }
    
    public Database getLengthDatabase(String xpath) throws DatabaseException {
        if (lengthDatabases.containsKey(xpath)) {
            return (Database) lengthDatabases.get(xpath);
        } else {
            logger.debug("Opening length database for: " + xpath);
            Database database = getPathDatabase(xpath);
            SecondaryConfig secConfig = new SecondaryConfig();
            secConfig.setSortedDuplicates(true);
            secConfig.setTransactional(false); 
            secConfig.setAllowCreate(!readOnly);
            secConfig.setReadOnly(readOnly);
            secConfig.setKeyCreator(new LengthKeyCreator());
            secConfig.setBtreeComparator(LengthComparator.class);
            SecondaryDatabase d = env.openSecondaryDatabase(null, xpath + "-length", database, secConfig);
            lengthDatabases.put(xpath, d);
            return d;
        }
    }
    
    public StoredMap getLengthMap(String xpath) throws DatabaseException {
        if (lengthMaps.containsKey(xpath)) {
            return (StoredMap) lengthMaps.get(xpath);
        } else {
            Database database = getLengthDatabase(xpath);
            StoredMap m = new StoredMap(database, intBinder, valueBinder, !readOnly);
            lengthMaps.put(xpath, m);
            return m;
        }
    }
    
    public Database getClustersDatabase(String xpath) throws DatabaseException {
        if (clustersDatabases.containsKey(xpath)) {
            return (Database) clustersDatabases.get(xpath);
        } else {
            logger.debug("Opening clusters database for: " + xpath);
            Database database = getPathDatabase(xpath);
            SecondaryConfig secConfig = new SecondaryConfig();
            secConfig.setSortedDuplicates(true);
            secConfig.setTransactional(false); 
            secConfig.setAllowCreate(!readOnly);
            secConfig.setReadOnly(readOnly);
            secConfig.setKeyCreator(new ClusteringKeyCreator());
            SecondaryDatabase d = env.openSecondaryDatabase(null, xpath + "-clusters", database, secConfig);
            clustersDatabases.put(xpath, d);
            return d;
        }
    }
    
    public StoredMap getClustersMap(String xpath) throws DatabaseException {
        if (clustersMaps.containsKey(xpath)) {
            return (StoredMap) clustersMaps.get(xpath);
        } else {
            Database database = getClustersDatabase(xpath);
            StoredMap m = new StoredMap(database, stringBinder, valueBinder, !readOnly);
            clustersMaps.put(xpath, m);
            return m;
        }
    }
    
    public List getPathsAsList() throws DatabaseException {
        if (pathsList == null) {
            pathsList = new ArrayList();
            StoredMap m = getPaths();
            Iterator i = m.values().iterator();
            while(i.hasNext()) {
                pathsList.add(i.next());
            }
            StoredIterator.close(i);
        }
        return pathsList;
    }
    
    public List getNamespacesPrefixes() throws DatabaseException {
        if (namespacesPrefixes == null) {
            namespacesPrefixes = new ArrayList();
            StoredMap m = getNamespaces();
            Iterator i = m.keySet().iterator();
            while (i.hasNext()) {
                namespacesPrefixes.add(i.next());
            }
            StoredIterator.close(i);
        }
        return namespacesPrefixes;
    }
    
    // ------------------------------------------------------------------------
    
    private int counter = 0;
    private int slot = 1000;
    
    public void record(String xpath, String value) throws DatabaseException {
        if (logger.isDebugEnabled()) logger.debug("  Record xpath (" + xpath + "," + value + ")");
        
        Map paths = getPaths();
        Path p = (Path) paths.get(xpath);
        if (p == null) {
            p = new Path(xpath);
        }
        p.incFrequency();
        p.incLength(value);
        
        Map values = getPathMap(xpath);
        Value v = (Value) values.get(value);
        if (v == null) {
            v = new Value(value);
            p.incUniques();
        }
        v.incFrequency();
        
        paths.put(xpath, p);
        values.put(value,v);

        counter++;
        
        if (counter % slot == 0) {
            EnvironmentStats stats = env.getStats(null);
            if (logger.isInfoEnabled()) logger.info("  Cache (" + stats.getCacheDataBytes() + "/" + stats.getCacheTotalBytes() + ") Cache Miss Ratio: " + (stats.getNCacheMiss() / counter * 100) + "%)");
        }
    }
    
    // ------------------------------------------------------------------------
    
    final static Color gridColor = new Color(0xcc, 0xcc, 0xcc, 100);
    final static Color drawColor = Color.black;

    public File getChart(String xpath, String type, String size, boolean regenerate) 
    throws DatabaseException, IOException {
        File temp = getTempDir();
        String hash = DigestUtils.md5Hex(xpath + type + size);
        File chart = new File(temp,hash+".png");
        
        if (regenerate || !chart.exists()) {
            boolean big = ("big".equals(size));
            double w = (big) ? 200.0d : 30.0d;
            double h = (big) ? 100.0d : 15.0d;
            double px = (big) ? 2.0f : 1.5f;
            
            BufferedImage image = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2 = (Graphics2D) image.getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.0f));
            AffineTransform t = AffineTransform.getTranslateInstance(0,h);
            t.concatenate(AffineTransform.getScaleInstance(1.0d,-1.0d));
            t.concatenate(AffineTransform.getTranslateInstance(px / 2.0d, px / 2.0d));
            g2.setTransform(t);
            
            Path p = (Path) getPaths().get(xpath);
            StoredMap values = null;
            
            if (type.equals("frequency")) {
                values = getFrequencyMap(xpath);
                
                // since keys are ordered by the b-tree, the first is the highest value
                Iterator i = values.keySet().iterator();
                double maxY = (i.hasNext()) ? ((Integer) i.next()).intValue() : 1;
                maxY = Math.log(maxY) * 1.1d;
                StoredIterator.close(i);
                
                double maxX = Math.log(p.getUniques()) * 1.1d;

                if (big) { // draw grid
                    g2.setColor(gridColor);
                    int xdecades = (int) Math.round(maxX / Math.log(10.0d));
                    for (int d = 0; d < xdecades; d++) {
                        for (int u = 0; u < 10; u++) {
                            double ox = u * Math.pow(10.0d,d);
                            double x = Math.log(ox) * w / maxX;
                            if (x < w) g2.draw(new Line2D.Double(x,0.0d,x,h));
                        }
                    }
                    int ydecades = (int) Math.round(maxY / Math.log(10.0d));
                    for (int d = 0; d < ydecades; d++) {
                        for (int u = 0; u < 10; u++) {
                            double oy = u * Math.pow(10.0d,d);
                            double y = Math.log(oy) * h / maxY;
                            if (y < h) g2.draw(new Line2D.Double(0.0d,y,w,y));
                        }
                    }
                }
                
                g2.setColor(Color.black);
                g2.setPaint(Color.black);
                
                i = values.keySet().iterator();
                int count = 1;
                while (i.hasNext()) {
                    Integer frequency = (Integer) i.next();
                    Collection dups = values.duplicates(frequency);
                    Iterator j = dups.iterator();
                    while (j.hasNext()) {
                        j.next();
                        double x = Math.log(count++) * w / maxX - px / 2.0d;
                        double y = Math.log(frequency.intValue()) * h / maxY - px / 2.0d;
                        g2.fill(new Rectangle2D.Double(x, y, px, px));
                    }
                    StoredIterator.close(j);
                }
                StoredIterator.close(i);
            } else if (type.equals("length")) {
                g2.setColor(drawColor);
                g2.setPaint(drawColor);
                values = getLengthMap(xpath);
                
                // since keys are ordered by the b-tree, the first is the highest value
                Iterator i = values.keySet().iterator();
                double maxX = 0.0d;
                double maxY = 0.0d;
                while (i.hasNext()) {
                    Integer key = (Integer) i.next();
                    double x = key.doubleValue();
                    if (x > maxX) maxX = x;
                    Collection dups = (Collection) values.duplicates(key);
                    double y = (double) dups.size();
                    if (y > maxY) maxY = y;
                }
                StoredIterator.close(i);
                maxX *= 1.1d;
                maxY *= 1.1d;

                if (big) { // draw grid
                    g2.setColor(gridColor);
                    for (int u = 0; u < maxX; u += 10) {
                        double x = u * w / maxX;
                        if (x < w) g2.draw(new Line2D.Double(x,0.0d,x,h));
                    }
                    for (int u = 0; u < maxY; u += 10) {
                        double y = u * h / maxY;
                        if (y < h) g2.draw(new Line2D.Double(0.0d,y,w,y));
                    }
                }
                
                g2.setColor(Color.black);
                g2.setPaint(Color.black);
                
                i = values.keySet().iterator();
                while (i.hasNext()) {
                    Integer length = (Integer) i.next();
                    Collection dups = values.duplicates(length);
                    double x = (double) (length.intValue()) * w / maxX - px / 2.0d;
                    double y = (double) ((dups.size() - 1)) * h / maxY - px / 2.0d;
                    g2.fill(new Rectangle2D.Double(x, y, px, px));
                }
                StoredIterator.close(i);
            } else {
                throw new RuntimeException("Chart type '" + type + "' is not supported.");
            }
            
            FileOutputStream output = new FileOutputStream(chart);
            ImageIO.write(image, "png", output);
            output.close();
        }
        
        return chart;
    }
    
    // ------------------------------------------------------------------------
    
    public static final int VALUES = 10;
    public static final int FREQUENCIES = 11;
    public static final int LENGTHS = 12;
    public static final int CLUSTERS = 13;
    
    public static final int ASCENDING = 0;
    public static final int DESCENDING = 1;
    
    public void getJSON(Writer output, Database database, String query, int order, int size, int type) throws Exception {
        Cursor cursor = null;
        int counter = 0;
        output.write("var data = {\n");
        
        try {
            cursor = database.openCursor(null, null);
            DatabaseEntry value = new DatabaseEntry();
            DatabaseEntry key;
            
            OperationStatus retVal;
            String keyString = null;
            boolean itemOpened = false;
            
            if (query == null || "".equals(query)) {
                key = new DatabaseEntry();
                if (order == DESCENDING) {
                    retVal = cursor.getPrev(key, value, LockMode.DEFAULT);
                } else {
                    retVal = cursor.getNext(key, value, LockMode.DEFAULT);
                }
            } else {
                if (type == FREQUENCIES || type == LENGTHS) {
                    key = new DatabaseEntry();
                    intBinder.objectToEntry(new Integer(query), key);
                } else {
                    key = new DatabaseEntry();
                    stringBinder.objectToEntry(query, key);
                }
                retVal = cursor.getSearchKeyRange(key, value, LockMode.DEFAULT);
            }
            
            if (type == VALUES || type == FREQUENCIES || type == LENGTHS || type == CLUSTERS) {
                while (true) {
                    if (retVal == OperationStatus.SUCCESS && counter < size) {
                        if (type == VALUES || type == CLUSTERS) {
                            keyString = (String) stringBinder.entryToObject(key);
                        } else {
                            keyString = ((Integer) intBinder.entryToObject(key)).toString();
                        }
                        if (type != CLUSTERS || (type == CLUSTERS && !("".equals(keyString)) && cursor.count() > 1)) {
                            output.write("\"" + StringUtils.jsonEscape(keyString) + "\" : [ ");
                            itemOpened = true;
                            while (true) {
                                if (retVal == OperationStatus.SUCCESS && counter < size) {
                                    if (type != CLUSTERS) counter++;
                                    Value valueObj = (Value) valueBinder.entryToObject(value);
                                    output.write(valueObj.toJSON());
                                }
                                if (order == DESCENDING) {
                                    retVal = cursor.getPrevDup(key, value, LockMode.DEFAULT);
                                } else {
                                    retVal = cursor.getNextDup(key, value, LockMode.DEFAULT);
                                }
                                if (retVal == OperationStatus.SUCCESS && counter < size) {
                                    output.write(" , ");
                                } else {
                                    output.write(" ]");
                                    break;
                                }
                            }
                            if (type == CLUSTERS) counter++;
                        }
                    }
                    if (order == DESCENDING) {
                        retVal = cursor.getPrev(key, value, LockMode.DEFAULT);
                    } else {
                        retVal = cursor.getNext(key, value, LockMode.DEFAULT);
                    }
                    if (retVal == OperationStatus.SUCCESS && counter < size) {
                        if (itemOpened) {
                            output.write(",\n");
                            itemOpened = false;
                        }
                    } else {
                        output.write("\n");
                        break;
                    }
                }
            } else {
                throw new RuntimeException("Serialization type '" + type + "' not implemented.");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (cursor != null) cursor.close();
            output.write("};\n");
        }    	
    }
    
    // ------------------------------------------------------------------------
    
    public void close() {
        try {
            logger.debug("Closing namespaces database");
            namespacesDatabase.close();
        } catch (Exception e) {
            logger.error("Error closing namespaces database", e);
        }
        
        try {
            logger.debug("Closing paths database");
            pathsDatabase.close();
        } catch (Exception e) {
            logger.error("Error closing paths database", e);
        }
        
        Iterator i = clustersDatabases.values().iterator();
        logger.debug("Closing clusters databases");
        while (i.hasNext()) {
            SecondaryDatabase d2 = (SecondaryDatabase) i.next();
            try {
                d2.close();
            } catch (Exception e) {
                logger.error("Error closing database " + d2, e);
            }
        }
        
        i = lengthDatabases.values().iterator();
        logger.debug("Closing length databases");
        while (i.hasNext()) {
            SecondaryDatabase d2 = (SecondaryDatabase) i.next();
            try {
                d2.close();
            } catch (Exception e) {
                logger.error("Error closing database " + d2, e);
            }
        }
        
        i = frequencyDatabases.values().iterator();
        logger.debug("Closing frequency databases");
        while (i.hasNext()) {
            SecondaryDatabase d2 = (SecondaryDatabase) i.next();
            try {
                d2.close();
            } catch (Exception e) {
                logger.error("Error closing database " + d2, e);
            }
        }
        
        i = pathDatabases.values().iterator();
        logger.debug("Closing path databases");
        while (i.hasNext()) {
            Database d = (Database) i.next();
            try {
                d.close();
            } catch (Exception e) {
                logger.error("Error closing database " + d, e);
            }
        }
        
        try {
            logger.debug("Closing environment " + env.getHome().getAbsolutePath());
            env.close();
        } catch (Exception e) {
            logger.error("Error closing environment " + env, e);
        }
    }
    
}
