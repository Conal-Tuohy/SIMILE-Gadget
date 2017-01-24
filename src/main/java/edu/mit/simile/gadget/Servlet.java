package edu.mit.simile.gadget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import edu.mit.simile.gadget.comparators.path.AverageLengthComparator;
import edu.mit.simile.gadget.comparators.path.FrequencyComparator;
import edu.mit.simile.gadget.comparators.path.UnicityComparator;
import edu.mit.simile.gadget.comparators.path.UniquesComparator;
import edu.mit.simile.gadget.comparators.path.XPathComparator;
import edu.mit.simile.gadget.comparators.path.XPathDepthComparator;
import edu.mit.simile.gadget.data.Dataset;
import edu.mit.simile.gadget.data.Path;
import edu.mit.simile.gadget.utils.ScreenUtils;

/**
 * Gadget is an XML inspector.
 * 
 * This is the servlet that is used as the user interface to access, 
 * browse and manipulate the indices created with the command line tool.
 * 
 * This servlet is modeled with the Apache Cocoon sitemap in mind, but written
 * with the full power of the Java language instead of using a special XML
 * language.
 *
 * @author Stefano Mazzocchi
 */
public class Servlet extends HttpServlet {
    
    private static final String VELOCITY = "velocity";
    private static final String DATA = "data";
    
    protected Logger logger;
    
    protected VelocityEngine engine;
    protected Map datasets;
    protected ServletContext context;
    protected Date lastModified;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        try {
            context = config.getServletContext();
            lastModified = new Date();
            
            Logger.getRootLogger().removeAllAppenders();
            PropertyConfigurator.configure(context.getRealPath("WEB-INF/log4j.properties"));        
            logger = Logger.getLogger(Servlet.class);
            
            logger.info("Initializing " + Gadget.NAME + "...");
            
            this.datasets = (Map) context.getAttribute(DATA);
            
            if (this.datasets == null) {
                this.datasets = new HashMap();
                File base = new File(context.getInitParameter(DATA));
                logger.info("Loading data from "+ base.getAbsolutePath());
                String[] folders = base.list();
                for (int i = 0; i < folders.length; i++) {
                    File file = new File(base, folders[i]);
                    if (Dataset.isData(file)) {
                        this.datasets.put(folders[i], Dataset.readData(file));
                    }
                }
                context.setAttribute(DATA, this.datasets);
            }
            
            this.engine = (VelocityEngine) context.getAttribute(VELOCITY);
            
            if (this.engine == null) {
                logger.info("Creating a new Velocity Engine");
                this.engine = new VelocityEngine();
                this.engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,"org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
                this.engine.setProperty("runtime.log.logsystem.log4j.category", "Velocity");
                this.engine.addProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, context.getRealPath("templates"));
                this.engine.addProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, Boolean.FALSE);
                this.engine.addProperty(RuntimeConstants.VM_LIBRARY_AUTORELOAD, Boolean.TRUE);
                this.engine.init();            
                context.setAttribute(VELOCITY, this.engine);
            }
        } catch (Exception e) {
            logger.error("Error initializing the servlet", e);
        }
    }
    
    public void destroy() {
        Iterator i = this.datasets.values().iterator();
        while (i.hasNext()) {
            Dataset d = (Dataset) i.next();
            d.close();
        }
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        
        try {
            process(request, response);
        } catch (Exception e) {
            logger.error("Error processing the request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        if (logger.isInfoEnabled()) logger.info("Time to process request: " + ScreenUtils.format(endTime - startTime));
    }
    
    Pattern images_pattern     = Pattern.compile("^/images/(.*)\\.(jpg|gif|png)$");
    Pattern scripts_pattern    = Pattern.compile("^/scripts/(.*)\\.(js)$");
    Pattern styles_pattern     = Pattern.compile("^/styles/(.*)\\.(css)$");
    Pattern charts_pattern     = Pattern.compile("^/(.*)/chart$");
    Pattern data_pattern       = Pattern.compile("^/(.*)/data$");
    Pattern values_pattern     = Pattern.compile("^/(.*)/values$");
    Pattern table_pattern      = Pattern.compile("^/(.*)/table$");
    Pattern tree_pattern       = Pattern.compile("^/(.*)/tree$");
    Pattern dataset_pattern    = Pattern.compile("^/(.*)/$");
    Pattern home_pattern       = Pattern.compile("^/$");
    
    public void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getServletPath();
        String urlQuery = request.getQueryString();
        if (logger.isInfoEnabled()) logger.info("Path Requested: " + path + ((urlQuery != null) ? "?" + urlQuery : ""));
        Matcher m = null;
        
        // ------------------------- static ---------------------------
        
        m = images_pattern.matcher(path);
        if (m.matches()) {
            read(request, response, "/images/" + m.group(1) + "." + m.group(2), "images/" + m.group(2));
            return;
        }
        
        m = styles_pattern.matcher(path);
        if (m.matches()) {
            read(request, response, "/styles/" + m.group(1) + "." + m.group(2), "text/css");
            return;
        }
        
        m = scripts_pattern.matcher(path);
        if (m.matches()) {
            read(request, response, "/scripts/" + m.group(1) + "." + m.group(2), "text/javascript");
            return;
        }
        
        // ------------------------- dynamic (no velocity) ------------------------
        
        String xpath = request.getParameter("xpath");
        String sort = request.getParameter("sort");
        String order = request.getParameter("order");
        String query = request.getParameter("query");
        String type = request.getParameter("type");
        String size = request.getParameter("size");
        
        m = charts_pattern.matcher(path);
        if (m.matches()) {
            String name = m.group(1);
            Dataset d = (Dataset) this.datasets.get(name);
            if (d != null) {
                File chart = d.getChart(xpath, type, size, false);
                response.setDateHeader("Expires", System.currentTimeMillis() + 1*60*60*1000);
                read(request, response, chart.toURL(), "image/png");
                return;
            }
        }
        
        m = data_pattern.matcher(path);
        if (m.matches()) {
            String name = m.group(1);
            Dataset d = (Dataset) this.datasets.get(name);
            if (d != null) {
                int sizeInt;
                if (size != null) {
                    try {
                        sizeInt = Integer.parseInt(size);
                    } catch (Exception e) {
                        sizeInt = 10;
                    }
                } else {
                    sizeInt = 10;
                }
                int orderInt = ("descending".equals(order)) ? Dataset.DESCENDING : Dataset.ASCENDING;
                response.setHeader("Content-Type", "text/javascript ; charset= UTF-8");
                if ("frequency".equals(sort)) {
                    d.getJSON(response.getWriter(), d.getFrequencyDatabase(xpath), query, orderInt, sizeInt, Dataset.FREQUENCIES);
                } else if ("length".equals(sort)) {
                    d.getJSON(response.getWriter(), d.getLengthDatabase(xpath), query, orderInt, sizeInt, Dataset.LENGTHS);
                } else if ("clusters".equals(sort)) {
                    d.getJSON(response.getWriter(), d.getClustersDatabase(xpath), query, orderInt, sizeInt, Dataset.CLUSTERS);
                } else {
                    d.getJSON(response.getWriter(), d.getPathDatabase(xpath), query, orderInt, sizeInt, Dataset.VALUES);
                }
                return;
            }
        }
        
        m = tree_pattern.matcher(path);
        if (m.matches()) {
            String name = m.group(1);
            Dataset d = (Dataset) this.datasets.get(name);
            if (d != null) {
                response.setHeader("Content-Type", "text/javascript ; charset= UTF-8");
                d.getTree().toJSON(response.getWriter());
                return;
            }
        }
        
        // --------------------- dynamic (with velocity) ------------------------
        
        VelocityContext velocity = new VelocityContext();
        
        velocity.put("datasets", this.datasets);
        
        m = home_pattern.matcher(path);
        if (m.matches()) {
            template(request, response, velocity, "datasets.vt", "text/html", "UTF-8");
            return;
        }
        
        m = dataset_pattern.matcher(path);
        if (m.matches()) {
            String name = m.group(1);
            velocity.put("dataset", name);
            Dataset d = (Dataset) this.datasets.get(name);
            if (d != null) {
                velocity.put("properties", d.getProperties());
                velocity.put("namespacesPrefixes", d.getNamespacesPrefixes());
                velocity.put("namespacesMap", d.getNamespaces());
                template(request, response, velocity, "tree.vt", "text/html", "UTF-8");
                return;
            }
        }
        
        m = table_pattern.matcher(path);
        if (m.matches()) {
            String name = m.group(1);
            velocity.put("dataset", name);
            Dataset d = (Dataset) this.datasets.get(name);
            if (d != null) {
                List paths = d.getPathsAsList();
                velocity.put("paths", paths);
                Comparator comparator = null;
                if ("xpath".equals(sort)) {
                    comparator = new XPathComparator(true);
                } else if ("uniques".equals(sort)) {
                    comparator = new UniquesComparator(false, new FrequencyComparator(false, new XPathDepthComparator(false)));
                } else if ("length".equals(sort)) {
                    comparator = new AverageLengthComparator(false, new UniquesComparator(false, new FrequencyComparator(false, new XPathDepthComparator(true, new XPathComparator(true)))));
                } else if ("unicity".equals(sort)) {
                    comparator = new UnicityComparator(false, new FrequencyComparator(false, new UniquesComparator(false, new XPathDepthComparator(true, new XPathComparator(true)))));
                } else {
                    comparator = new FrequencyComparator(false, new UniquesComparator(false, new XPathDepthComparator(false)));
                }
                Collections.sort(paths, comparator);
                template(request, response, velocity, "table.vt", "text/html", "UTF-8");
                return;
            }
        }
        
        velocity.put("xpath", (xpath != null) ? xpath : "");
        velocity.put("sort", (sort != null) ? sort : "value");
        velocity.put("order", (order != null) ? order : "ascending");
        velocity.put("query", (query != null) ? query : "");
        velocity.put("size", (size != null) ? size : "20");
        
        m = values_pattern.matcher(path);
        if (m.matches()) {
            String name = m.group(1);
            velocity.put("dataset", name);
            Dataset d = (Dataset) this.datasets.get(name);
            if (d != null) {
                Path p = (Path) d.getPaths().get(xpath);
                velocity.put("path", p);
                template(request, response, velocity, "values.vt", "text/html", "UTF-8");
                return;
            }
        }
        
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
    public void template(HttpServletRequest request, HttpServletResponse response, VelocityContext velocity, String template, String mimeType, String encoding) throws Exception {
        response.setHeader("Content-Type", mimeType + ";charset=" + encoding);
        this.engine.mergeTemplate(template, velocity, response.getWriter());
    }
    
    public void read(HttpServletRequest request, HttpServletResponse response, String file, String mimeType) throws Exception {
        read(request, response, context.getResource(file), mimeType);
    }
    
    public void read(HttpServletRequest request, HttpServletResponse response, URL resource, String mimeType) throws Exception {
        
        URLConnection urlConnection = resource.openConnection();
        long lastModified = urlConnection.getLastModified();
        
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifModifiedSince / 1000 < lastModified / 1000) {
            response.setDateHeader("Last-Modified", lastModified);
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new BufferedInputStream(urlConnection.getInputStream());
                response.setHeader("Content-Type", mimeType);
                output = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int length = 0;
                while ((length = input.read(buffer)) > -1) {
                    output.write(buffer, 0, length);
                }
            } catch (Exception e) {
                logger.error("Error processing " + resource, e);
            } finally {
                if (input != null) input.close();
                if (output != null) output.close();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }
    
    public long getLastModified(HttpServletRequest req) {
        return lastModified.getTime() / 1000 * 1000;
    }
    
}
