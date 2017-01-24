package edu.mit.simile.gadget;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.xml.sax.SAXException;

import com.sleepycat.je.DatabaseException;

import edu.mit.simile.gadget.handlers.Handler;
import edu.mit.simile.gadget.handlers.InspectingHandler;
import edu.mit.simile.gadget.utils.RegexpFileFilter;
import edu.mit.simile.gadget.utils.ScreenUtils;

/**
 * This is the command line tool that generates the indices from the XML dataset. 
 *
 * @author Stefano Mazzocchi
 */
public class Index extends Gadget {
    
    public static final String CLI = CLINAME + " index [options] folder [folder ...]\n";
    
    protected boolean recursive = false;
    protected boolean trim = true;
    protected String pattern = ".*\\.(xml)";
    protected File data = new File("./data");
    protected Properties props = new Properties();
    
    public Index() {
        super();
    }
    
    public void process(String args[]) {
        
        options.addOption("p", "pattern <pattern>", true, "regexp used to match the files that will be processed [defaults to " + pattern + "]");
        options.addOption("o", "output <folder>", true, "where to dump the data that results out of the inspection [defaults to " + data + "]");
        options.addOption("d", "description <file>", true, "used the given properties file to describe the dataset");
        options.addOption("r", "recursive", false, "recurse into subfolders");
        options.addOption("t", "no-trimming", false, "do not remove whitespace around captured text but leave as-is");
        
        try {
            CommandLine line = cliParser.parse(options, args);
            HelpFormatter formatter = new HelpFormatter();
            
            recursive = line.hasOption('r');
            trim = !line.hasOption('t');
            
            if (line.hasOption('p')) {
                pattern = line.getOptionValue('p');
            }
            
            if (line.hasOption('o')) {
                data = new File(line.getOptionValue('o'));
                if (!data.exists()) {
                    data.mkdirs();
                }
            }

            if (line.hasOption('d')) {
                File d = new File(line.getOptionValue('d'));
                FileInputStream fis = new FileInputStream(d);
                Properties p = new Properties();
                p.load(fis);
                fis.close();
                props = p;
            }
            
            if (line.getArgs().length == 0) {
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
        } catch (DatabaseException e) {
            fatal("[Error] Unexpected problem handling the results database: " + e.getMessage());
        } catch (IOException e) {
            fatal("[Error] IO error: " + e.getMessage());
        } catch (Exception e) {
            fatal("[Error] error", e);
        }
    }
    
    void execute(List args) 
    throws ParserConfigurationException, SAXException, IOException, DatabaseException {
        
        long globalStart = System.currentTimeMillis();
        
        log(NAME + " " + VERSION);
        log("===================================================\n");
        
        log("Generating indices:");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        
        SAXParser parser = factory.newSAXParser();
        InspectingHandler handler = new InspectingHandler(data, props, trim);
        FileFilter filter = new RegexpFileFilter(pattern);
        
        Iterator iterator = args.iterator();
        while (iterator.hasNext()) {
            File file = new File((String) iterator.next());
            process(file, filter, recursive, parser, handler);
        }

        long globalEnd = System.currentTimeMillis();
        log("\nProcessing took " + ScreenUtils.format(globalEnd - globalStart));
    }
    
    void process(File file, FileFilter filter, boolean recursive, SAXParser parser, Handler handler)
    throws IOException, SAXException {
        File[] currentFiles = file.listFiles();
        if (currentFiles != null) {
            for (int j = 0; j < currentFiles.length; j++) {
                File currentFile = currentFiles[j];
                if (currentFile.isDirectory() && recursive) {
                    process(currentFile, filter, recursive, parser, handler);
                } else if (filter.accept(currentFile) && currentFile.length() > 0) {
                    processFile(currentFile, parser, handler);
                }
            }
        } else {
            processFile(file, parser, handler);
        }
    }
    
    void processFile(File file, SAXParser parser, Handler handler) 
    throws IOException, SAXException {
        log(" Parsing " + file + "...");
        long start = System.currentTimeMillis();
        parser.parse(file,handler);
        long end = System.currentTimeMillis();
        log("   took " + ScreenUtils.format(end - start));
    }
        
}
