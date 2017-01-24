package edu.mit.simile.gadget;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import com.sleepycat.collections.StoredIterator;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.DatabaseException;

import edu.mit.simile.gadget.data.Dataset;
import edu.mit.simile.gadget.utils.ScreenUtils;

/**
 * This is the command line tool that generates the distribution charts and sparklines.
 *
 * @author Stefano Mazzocchi
 */
public class Chart extends Gadget {
    
    public static final String CLI = CLINAME + " chart [options] database [database ...]\n";
        
    public void process(String args[]) {
        try {
            CommandLine line = cliParser.parse(options, args);
            HelpFormatter formatter = new HelpFormatter();
                        
            if (line.getArgs().length == 0) {
                formatter.printHelp(CLI, options);
                System.exit(1);
            }
            
            execute(line.getArgList());
            
        } catch (ParseException e) {
            fatal("[Error] Unexpected problem in parsing the command line: " + e.getMessage());
        } catch (IOException e) {
            fatal("[Error] IO error: " + e.getMessage());
        } catch (Exception e) {
            fatal("[Error] error", e);
        }
    }
    
    void execute(List args) throws IOException, DatabaseException {
        
        long globalStart = System.currentTimeMillis();
        
        log(NAME + " " + VERSION);
        log("===================================================\n");
        
        Iterator i = args.iterator();
        while (i.hasNext()) {
            File f = new File((String) i.next());
            if (Dataset.isData(f)) {
                log("Generating charts and sparklines for " + f);
                Dataset dataset = Dataset.readData(f);
                StoredMap paths = dataset.getPaths();
                Iterator j = paths.keySet().iterator();
                while (j.hasNext()) {
                    String xpath = (String) j.next();
                    processChart(dataset, xpath, "frequency", "small");
                    processChart(dataset, xpath, "length", "small");
                    processChart(dataset, xpath, "frequency", "big");
                    processChart(dataset, xpath, "length", "big");
                }
                StoredIterator.close(j);
                dataset.close();
            } else {
                log("Skipping " + f + " since it doesn't contain a gadget index or it can't be read");
            }
        }
        
        long globalEnd = System.currentTimeMillis();
        log("\nProcessing took " + ScreenUtils.format(globalEnd - globalStart));
    }
        
    void processChart(Dataset dataset, String xpath, String type, String size)
    throws IOException, DatabaseException {
        log(" Processing chart for " + xpath + " [" + type + "," + size + "]");
        long start = System.currentTimeMillis();
        dataset.getChart(xpath, type, size, true);
        long end = System.currentTimeMillis();
        log("   took " + ScreenUtils.format(end - start));
    }
    
}
