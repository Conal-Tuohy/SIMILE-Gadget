package edu.mit.simile.gadget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.xml.sax.SAXException;

import edu.mit.simile.gadget.handlers.AbstractHandler;
import edu.mit.simile.gadget.handlers.InspectingHandler;
import edu.mit.simile.gadget.handlers.ProjectingHandler;
import edu.mit.simile.gadget.handlers.UnrollingHandler;
import edu.mit.simile.gadget.screens.CSVScreen;
import edu.mit.simile.gadget.screens.DummyScreen;
import edu.mit.simile.gadget.screens.FacetFrequencyScreen;
import edu.mit.simile.gadget.screens.FacetScreen;
import edu.mit.simile.gadget.screens.FrequencyScreen;
import edu.mit.simile.gadget.screens.PathScreen;
import edu.mit.simile.gadget.screens.Screen;
import edu.mit.simile.gadget.screens.UnicityScreen;
import edu.mit.simile.gadget.utils.RegexpFileFilter;
import edu.mit.simile.gadget.utils.ScreenUtils;

/**
 * Gadget is an XML analyzing tool.
 *
 * @author Stefano Mazzocchi
 */
public class Main {

    public static final String VERSION = "Gadget 1.0";

    public static final int GLOBAL_MODE = 0;
    public static final int UNROLL_MODE = 1;
    public static final int FACET_MODE = 2;

    public static final int ORDER_BY_UNICITY = 0;
    public static final int ORDER_BY_FREQUENCY = 1;
    public static final int ORDER_BY_PATH = 2;
    public static final int ORDER_BY_FACET = 3;
    public static final int JUST_PRINT_RESULTS = 4;

    private static transient boolean recursive = false;
    private static transient String pattern = ".*\\.(xml)";
    private static transient String facet = null;
    private static transient String externalResults = null;
    private static transient int mode = GLOBAL_MODE;
    private static transient int order = ORDER_BY_UNICITY;

    public static void main(String args[]) {

            CommandLineParser cliParser = new PosixParser();

            Options options = new Options();
            options.addOption("p", "pattern", true, "regexp used to match the files that will be processed [defaults to " + pattern + "]");
            options.addOption("f", "facet", true, "project just the given facet [no default]");
            options.addOption("x", "external-results", true, "process previous results from a comma separated values file [no default]");
            options.addOption("C", "just-print-values", false, "write results as comma separated values");
            options.addOption("U", "order-by-unicity", false, "order results by unicity [default]");
            options.addOption("F", "order-by-frequency", false, "order results by frequency");
            options.addOption("P", "order-by-path", false, "order results by path");
            options.addOption("V", "order-by-facet-value", false, "order results by facet value");
            options.addOption("r", "recursive", false, "recurse into subfolders");
            options.addOption("u", "unroll", false, "don't do processing, just print the unrolled xpaths to STDOUT");
            options.addOption("h", "help", false, "print the usage help");
            options.addOption("v", "version", false, "print version information and exit");

            try {
                CommandLine line = cliParser.parse(options, args);
                HelpFormatter formatter = new HelpFormatter();

                if (line.hasOption('p')) {
                    pattern = line.getOptionValue('p');
                }
                if (line.hasOption('x')) {
                    externalResults = line.getOptionValue('x');
                }
                if (line.hasOption('F')) {
                    order = ORDER_BY_FREQUENCY;
                }
                if (line.hasOption('U')) {
                    order = ORDER_BY_UNICITY;
                }
                if (line.hasOption('C')) {
                    order = JUST_PRINT_RESULTS;
                }
                if (line.hasOption('P')) {
                    order = ORDER_BY_PATH;
                }
                if (line.hasOption('r')) {
                        recursive = true;
                }
                if (line.hasOption('u')) {
                        mode = UNROLL_MODE;
                }
                if (line.hasOption('f')) {
                    facet = line.getOptionValue('f');
                    if (facet != null) {
                        mode = FACET_MODE;
                    } else {
                        fatal("You must indicate a facet after the -f option [example dc:title]");
                    }
                }
                if (line.hasOption('h')) {
                        formatter.printHelp("gadget [options] folder", options);
                        System.exit(0);
                }
                if (line.hasOption('v')) {
                        System.out.println(VERSION);
                        System.exit(0);
                }
            if ((line.getArgs().length == 0) && (externalResults == null)) {
                formatter.printHelp("gadget [options] folder", options);
                System.exit(1);
            }

            execute(line.getArgList());

            } catch (ParseException e) {
                fatal("[Error] Unexpected exception: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                fatal("[Error] Illegal argument: " + e.getMessage());
            } catch (ParserConfigurationException e) {
                fatal("[Error] XML parser error: " + e.getMessage());
            } catch (SAXException e) {
                fatal("[Error] XML parsing error: " + e.getException().getMessage());
            } catch (IOException e) {
                fatal("[Error] IO error: " + e.getMessage());
            }
    }

    private static void execute(List args) throws ParserConfigurationException, SAXException, IOException {

        long globalStart = System.currentTimeMillis();

        System.err.println(VERSION);
        System.err.println("============================================\n");

        AbstractHandler handler = null;
        Screen screen = null;

        switch (mode) {
            case GLOBAL_MODE:
                handler = new InspectingHandler();
                switch (order) {
                    case ORDER_BY_UNICITY:
                        screen = new UnicityScreen();
                        break;
                    case ORDER_BY_FREQUENCY:
                        screen = new FrequencyScreen();
                        break;
                    case ORDER_BY_PATH:
                        screen = new PathScreen();
                        break;
                    case JUST_PRINT_RESULTS:
                        screen = new CSVScreen();
                        break;
                    default:
                        throw new IllegalArgumentException("Order method not supported by this mode");
                }
                break;
            case FACET_MODE:
                handler = new ProjectingHandler(facet);
                switch (order) {
                    case ORDER_BY_FREQUENCY:
                        screen = new FacetFrequencyScreen();
                        break;
                    case ORDER_BY_FACET:
                        screen = new FacetScreen();
                        break;
                    default:
                        throw new IllegalArgumentException("Order method not supported by this mode");
                }
                break;
            case UNROLL_MODE:
                handler = new UnrollingHandler();
                screen = new DummyScreen();
        }

        List results = null;

        if (externalResults == null) {
            List files = getFiles(args, pattern, recursive);

            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

            int fileCounter = 0;

            Iterator iterator = files.iterator();
            while (iterator.hasNext()) {
                File file = (File) iterator.next();
                System.err.print(" Parsing " + file + "...");
                long start = System.currentTimeMillis();
                parser.parse(file,handler);
                long end = System.currentTimeMillis();
                System.err.println(" took " + ScreenUtils.format(end - start));
                fileCounter++;
            }

            results = handler.getResults();
        } else {
            results = externalResults(externalResults);
        }

        if (results.size() > 0) {
            screen.output(results, System.err, System.out);
        }

        long globalEnd = System.currentTimeMillis();
        System.err.println("\nProcessing took " + ScreenUtils.format(globalEnd - globalStart));
    }

    private static List getFiles(List filePaths, String pattern, boolean recursive) {
            ArrayList files = new ArrayList();
            FileFilter filter = new RegexpFileFilter(pattern);
            Iterator iterator = filePaths.iterator();
            while (iterator.hasNext()) {
                File file = null;
                Object object = iterator.next();
                if (object instanceof File) {
                    file = (File) object;
                } else if (object instanceof String) {
                    file = new File((String) object);
                }
                files.addAll(getFiles(file, filter, recursive));
            }
            return files;
    }

    private static List getFiles(File file, FileFilter filter, boolean recursive) {
        ArrayList files = new ArrayList();
        File[] currentFiles = file.listFiles();
        if (currentFiles != null) {
            for (int j = 0; j < currentFiles.length; j++) {
                File currentFile = currentFiles[j];
                if (currentFile.isDirectory() && recursive) {
                    files.addAll(getFiles(currentFile,filter,recursive));
                } else if (filter.accept(currentFile) && currentFile.length() > 0) {
                    files.add(currentFile);
                }
            }
        } else {
            files.add(file);
        }
        return files;
    }

    private static void fatal(String str) {
            System.err.println(str);
            System.exit(1);
    }

    private static List externalResults(String file) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            List results = new ArrayList();
            reader.readLine(); // skip first line since it's the header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Result res = new Result(parts[0],Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                results.add(res);
            }
            reader.close();
            return results;
    }

}
