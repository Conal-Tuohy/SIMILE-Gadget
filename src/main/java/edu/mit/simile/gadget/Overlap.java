package edu.mit.simile.gadget;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.xml.sax.SAXException;

import edu.mit.simile.gadget.data.CollidingString;
import edu.mit.simile.gadget.handlers.FilteringHandler;
import edu.mit.simile.gadget.handlers.Handler;
import edu.mit.simile.gadget.utils.RegexpFileFilter;

/**
 * This is the command line tool that discovers overlap between xpath values
 * in different datasets.
 *
 * @author Stefano Mazzocchi
 */
public class Overlap extends Index {
    
    public static final String CLI = Gadget.CLINAME + " overlap [options] folder xpath [folder xpath ...]\n";
    
    boolean byString = true;
    boolean byFile = true;
    
    int cutoff = 0;
    
    public Overlap() {
        super();
    }
    
    public void process(String args[]) {
        
        options.addOption("p", "pattern <pattern>", true, "regexp used to match the files that will be processed [defaults to " + pattern + "]");
        options.addOption("c", "cutoff <number>", true, "how many datasets the data needs to found in to be output [defaults to all]");
        options.addOption("r", "recursive", false, "recurse into subfolders");
        options.addOption("t", "no-trimming", false, "do not remove whitespace around captured text but leave as-is");
        options.addOption("s", "by-strings", false, "output the results projecting by string only");
        options.addOption("f", "by-files", false, "output the results projecting by file only");
        
        try {
            CommandLine line = cliParser.parse(options, args);
            HelpFormatter formatter = new HelpFormatter();
            
            recursive = line.hasOption('r');
            trim = !line.hasOption('t');
            byFile = !line.hasOption('s');
            byString = !line.hasOption('f');
            
            if (line.hasOption('p')) {
                pattern = line.getOptionValue('p');
            }
            
            if (line.hasOption('c')) {
                cutoff = Integer.parseInt(line.getOptionValue('p'));
            }
            
            if (line.getArgs().length == 0) {
                formatter.printHelp(CLI, options);
                System.exit(1);
            }
            
            if (line.getArgs().length % 2 != 0) {
                System.out.println("Error: you have to specify an xpath for every folder\n");
                formatter.printHelp(CLI, options);
                System.exit(1);
            }
            
            execute(line.getArgList());
            
        } catch (ParseException e) {
            fatal("[Error] Unexpected problem in parsing the command line: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            fatal("[Error] XML parser error: " + e.getMessage());
        } catch (SAXException e) {
            fatal("[Error] XML parsing error: " + e.getException().getMessage());
        } catch (IOException e) {
            fatal("[Error] IO error: " + e.getMessage());
        } catch (Exception e) {
            fatal("[Error] error", e);
        }
    }
    
    Map valuesInFiles = new HashMap();
    Map valuesInDatasets = new HashMap();
    File folder = null;
    
    void execute(List args) 
    throws ParserConfigurationException, SAXException, IOException {
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        
        SAXParser parser = factory.newSAXParser();
        FileFilter filter = new RegexpFileFilter(pattern);
        
        int counter = 0;
        Iterator i = args.iterator();
        while (i.hasNext()) {
            counter++;
            folder = new File((String) i.next());
            String xpath = (String) i.next();
            FilteringHandler handler = new FilteringHandler(xpath, trim);
            process(folder, filter, recursive, parser, handler);
        }
        
        cutoff = (cutoff == 0) ? counter : cutoff;
        
        if (byString) {
            i = valuesInFiles.keySet().iterator();
            while (i.hasNext()) {
                CollidingString s = (CollidingString) i.next();
                Set datasets = (Set) valuesInDatasets.get(s);
                if (datasets.size() >= cutoff) { // we are not interested 
                    Set files = (Set) valuesInFiles.get(s);
                    System.out.println(files.size() + " " + s);
                }
            }
        }
        
        if (byString && byFile) System.out.println("\n---------------------------------------------------------------------\n");
        
        if (byFile) {
            Map filesWithValues = new HashMap();
            i = valuesInFiles.keySet().iterator();
            while (i.hasNext()) {
                CollidingString s = (CollidingString) i.next();
                Set datasets = (Set) valuesInDatasets.get(s);
                if (datasets.size() >= cutoff) { // we are not interested
                    Set files = (Set) valuesInFiles.get(s);
                    Iterator j = files.iterator();
                    while (j.hasNext()) {
                        File f = (File) j.next();
                        Set values = (Set) filesWithValues.get(f);
                        if (values == null) {
                            values = new HashSet();
                            filesWithValues.put(f,values);
                        }
                        values.add(s);
                    }
                }
            }
            
            i = filesWithValues.keySet().iterator();
            while (i.hasNext()) {
                File f = (File) i.next();
                Set l = (Set) filesWithValues.get(f);
                System.out.println(l.size() + " " + f.getAbsolutePath());
            }
        }
    }
    
    void processFile(File file, SAXParser parser, Handler handler) 
    throws IOException, SAXException {
        System.err.println(file);
        Set set = new HashSet();
        ((FilteringHandler) handler).setValueContainer(set);
        parser.parse(file,handler);
        if (set.size() > 0) {
            Iterator i = set.iterator();
            while (i.hasNext()) {
                String value = (String) i.next();
                CollidingString s = new CollidingString(value);
                Set files = (Set) valuesInFiles.get(s);
                if (files == null) {
                    files = new HashSet();
                    valuesInFiles.put(s,files);
                }
                files.add(file);
                
                Set datasets = (Set) valuesInDatasets.get(s);
                if (datasets == null) {
                    datasets = new HashSet();
                    valuesInDatasets.put(s,datasets);
                }
                datasets.add(folder); // this is set above globally
            }
        }
    }
}
