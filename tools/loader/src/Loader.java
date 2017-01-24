/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;

/**
 * @author Stefano Mazzocchi
 */
public class Loader {

    static boolean verbose = false;

    static final String REPOSITORIES = "loader.jar.repositories";
    static final String VERBOSE = "loader.verbose";
    static final String MAIN_CLASS = "loader.main.class";
    static final String LOG = "loader.log";

    class RepositoryClassLoader extends URLClassLoader {

        BufferedWriter _log = null;

        public RepositoryClassLoader(ClassLoader parent) {
            this(parent,null);
        }

        public RepositoryClassLoader(ClassLoader parent, File logFile) {
            super(new URL[0], parent);
            if (logFile != null && logFile.canWrite()) {
                try {
                    this._log = new BufferedWriter(new FileWriter(logFile));
                } catch (IOException e) {
                    throw new RuntimeException("Error creating classloading log file: " + e.getMessage());
                }
            }
        }

        protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try {
                if (this._log != null) this._log.write(name + "\n");
            } catch (IOException e) {
                throw new RuntimeException("Error writing to the log file: " + e.getMessage());
            }
            return super.loadClass(name,resolve);
        }

        public void addRepository(File repository) {
            if (verbose) System.out.println("Processing repository: " + repository);

            if (repository.exists()) {
                if (repository.isDirectory()) {
                    File[] jars = repository.listFiles();
                    try  {
                        if (verbose) System.out.println("Adding folder: " + repository);
                        super.addURL(repository.toURL());
                    } catch (MalformedURLException e) {
                        throw new IllegalArgumentException(e.toString());
                    }

                    for (int i = 0; i < jars.length; i++) {
                        if (jars[i].getAbsolutePath().endsWith(".jar")) {
                            addJar(jars[i]);
                        }
                    }
                } else {
                    addJar(repository);
                }
            } else if (verbose) {
                System.out.println("WARNING: repository " + repository + " does not exist");
            }
        }

        private void addJar(File file) {
            try  {
                URL url = file.toURL();
                if (verbose) System.out.println("Adding jar: " + file);
                
                super.addURL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e.toString());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Loader().run(args);
    }

    void run(String[] args) throws Exception
    {
        String repositories = System.getProperty(REPOSITORIES);
        if (repositories == null) {
            System.out.println("Loader requires the '" + REPOSITORIES + "' property to be set");
            System.exit(1);
        }

        String mainClass = System.getProperty(MAIN_CLASS);
        if (mainClass == null) {
            System.out.println("Loader requires the '" + MAIN_CLASS + "' property to be set");
            System.exit(1);
        }

        String verboseProp = System.getProperty(VERBOSE);
        verbose = verboseProp != null && "true".equals(verboseProp);

        File log = null;
        String logFile = System.getProperty(LOG);
        if (logFile != null) {
            log = new File(logFile);
        }

        if (verbose) System.out.println("-------------------- Loading --------------------");

        RepositoryClassLoader classLoader = new RepositoryClassLoader(this.getClass().getClassLoader(), log);

        StringTokenizer st = new StringTokenizer(repositories, File.pathSeparator);
        while (st.hasMoreTokens()) {
            classLoader.addRepository(new File(st.nextToken()));
        }

        Thread.currentThread().setContextClassLoader(classLoader);

        if (verbose) System.out.println("-------------------- Executing -----------------");
        if (verbose) System.out.println("Main Class: " + mainClass);

        invokeMain(classLoader, mainClass, args);
    }

    void invokeMain(ClassLoader classloader, String classname, String[] args)
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException
    {
        Class invokedClass = classloader.loadClass(classname);

        Class[] methodParamTypes = new Class[1];
        methodParamTypes[0] = args.getClass();

        Method main = invokedClass.getDeclaredMethod("main", methodParamTypes);

        Object[] methodParams = new Object[1];
        methodParams[0] = args;

        main.invoke(null, methodParams);
    }
}
