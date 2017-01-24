package edu.mit.simile.gadget;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;

import edu.mit.simile.gadget.data.Dataset;
import edu.mit.simile.gadget.utils.ScreenUtils;

/**
 * This is the command line tool that compacts the indices generated with the
 * 'index' command.
 *
 * @author Stefano Mazzocchi
 */
public class Compact extends Gadget {
    
    public static final String CLI = CLINAME + " clean [options] database [database ...]\n";
        
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
        } catch (DatabaseException e) {
            fatal("[Error] Database error: " + e.getMessage());
        } catch (Exception e) {
            fatal("[Error] error", e);
        }
    }
    
    void execute(List args) throws DatabaseException {
        
        long globalStart = System.currentTimeMillis();
        
        log(NAME + " " + VERSION);
        log("===================================================\n");
        
        Iterator i = args.iterator();
        while (i.hasNext()) {
            File f = new File((String) i.next());
            if (Dataset.isData(f)) {
                log("Compacting indices " + f);
                Dataset dataset = Dataset.writeData(f);
                Environment environment = dataset.getEnvironment();
                environment.cleanLog();
                dataset.close();
            } else {
                log("Skipping " + f + " since it doesn't contain a gadget index or it can't be read");
            }
        }
        
        long globalEnd = System.currentTimeMillis();
        log("\nProcessing took " + ScreenUtils.format(globalEnd - globalStart));
    }
    
}
