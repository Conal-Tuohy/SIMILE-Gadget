package edu.mit.simile.gadget;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is the main command line tool that is extended by all Gadget commands and
 * is used to invoke them from the command line. 
 *
 * @author Stefano Mazzocchi
 */
public class Gadget {

    public static final String NAME = "SIMILE Gadget";
    public static final String CLINAME = "gadget";
    public static final String VERSION = "2.0";

    protected Logger logger;
    protected Options options;
    protected CommandLineParser cliParser;
    
    public Gadget() {
        Logger.getRootLogger().removeAllAppenders();
        PropertyConfigurator.configure(this.getClass().getResource("/log4j.properties"));
        logger = Logger.getLogger(Gadget.class);
        
        cliParser = new PosixParser();
        
        options = new Options();
    }

    /**
     * This is the method that commands need to extend to execute their processing
     */
    public void process(String[] args) {
        System.out.println(NAME + " " + VERSION);
    }
    
    void fatal(String str, Exception e) {
        logger.error(str, e);
        e.printStackTrace(System.err);
        System.exit(1);
    }
    
    void fatal(String str) {
        logger.error(str);
        System.exit(1);
    }
    
    void log(String str) {
        logger.info(str);
    }

    // --------------------------- static ------------------------------------
    
    private static Class getClass(String command) throws Exception {
        StringBuffer b = new StringBuffer();
        b.append(command.toUpperCase().charAt(0));
        b.append(command.substring(1));
        String name = "edu.mit.simile.gadget." + b.toString();
        return Class.forName(name);
    }
    
    private static String[] removeCommand(String[] fullArgs) {
        String[] args = new String[fullArgs.length - 1];
        System.arraycopy(fullArgs,1,args,0,args.length);
        return args;
    }
    
    public static void main(String args[]) throws Exception {
        if (args.length == 0 || "help".equals(args[0])) {
           System.out.println("usage: gadget <command> [options] [args]");
           System.out.println(NAME + " command line tool, version " + VERSION);
           System.out.println();
           System.out.println("available commands:");
           System.out.println();
           System.out.println("  index     generate indices for the web application");
           System.out.println("  chart     precalculates the chart distributions and sparklines");
           System.out.println("  compact   compact the indices");
           System.out.println("  overlap   find overlap between datasets");
           System.out.println();
           System.out.println("Gadget is a tool for inspecting large quantities of well-formed XML data.");
           System.out.println("For additional information, see http://simile.mit.edu/gadget/");
        } else if ("--version".equals(args[0])) {
            System.out.println(NAME + " " + VERSION);
        } else {
            String command = args[0];
            try {
                Gadget g = (Gadget) getClass(command).newInstance();
                g.process(removeCommand(args));
            } catch (ClassNotFoundException e) {
                System.err.println("Command '" + command + "' is not available.\nType 'gadget help' for info on the available commands");
                System.exit(1);
            }
        }
    }
    
}
