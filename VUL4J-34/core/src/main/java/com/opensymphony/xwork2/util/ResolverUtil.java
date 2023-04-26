
package com.opensymphony.xwork2.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


public class ResolverUtil<T> {
    
    private static final Logger LOG = LogManager.getLogger(ResolverUtil.class);

    
    public static interface Test {
        
        boolean matches(Class type);
        
        boolean matches(URL resource);

        boolean doesMatchClass();
        boolean doesMatchResource();
    }
    
    public static abstract class ClassTest implements Test {
        public boolean matches(URL resource) {
            throw new UnsupportedOperationException();
        }

        public boolean doesMatchClass() {
            return true;
        }
        public boolean doesMatchResource() {
            return false;
        }
    }
    
    public static abstract class ResourceTest implements Test {
        public boolean matches(Class cls) {
            throw new UnsupportedOperationException();
        }

        public boolean doesMatchClass() {
            return false;
        }
        public boolean doesMatchResource() {
            return true;
        }
    }

    
    public static class IsA extends ClassTest {
        private Class parent;

        
        public IsA(Class parentType) { this.parent = parentType; }

        
        public boolean matches(Class type) {
            return type != null && parent.isAssignableFrom(type);
        }

        @Override public String toString() {
            return "is assignable to " + parent.getSimpleName();
        }
    }
    
    
    public static class NameEndsWith extends ClassTest {
        private String suffix;

        
        public NameEndsWith(String suffix) { this.suffix = suffix; }

        
        public boolean matches(Class type) {
            return type != null && type.getName().endsWith(suffix);
        }

        @Override public String toString() {
            return "ends with the suffix " + suffix;
        }
    }

    
    public static class AnnotatedWith extends ClassTest {
        private Class<? extends Annotation> annotation;

        
        public AnnotatedWith(Class<? extends Annotation> annotation) { this.annotation = annotation; }

        
        public boolean matches(Class type) {
            return type != null && type.isAnnotationPresent(annotation);
        }

        @Override public String toString() {
            return "annotated with @" + annotation.getSimpleName();
        }
    }
    
    public static class NameIs extends ResourceTest {
        private String name;
        
        public NameIs(String name) { this.name = "/" + name; }
        
        public boolean matches(URL resource) {
            return (resource.getPath().endsWith(name));
        }
        
        @Override public String toString() {
            return "named " + name;
        }
    }

    
    private Set<Class<? extends T>> classMatches = new HashSet<Class<?extends T>>();
    
    
    private Set<URL> resourceMatches = new HashSet<>();

    
    private ClassLoader classloader;

    
    public Set<Class<? extends T>> getClasses() {
        return classMatches;
    }
    
    public Set<URL> getResources() {
        return resourceMatches;
    }
    

    
    public ClassLoader getClassLoader() {
        return classloader == null ? Thread.currentThread().getContextClassLoader() : classloader;
    }

    
    public void setClassLoader(ClassLoader classloader) { this.classloader = classloader; }

    
    public void findImplementations(Class parent, String... packageNames) {
        if (packageNames == null) return;

        Test test = new IsA(parent);
        for (String pkg : packageNames) {
            findInPackage(test, pkg);
        }
    }
    
    
    public void findSuffix(String suffix, String... packageNames) {
        if (packageNames == null) return;

        Test test = new NameEndsWith(suffix);
        for (String pkg : packageNames) {
            findInPackage(test, pkg);
        }
    }

    
    public void findAnnotated(Class<? extends Annotation> annotation, String... packageNames) {
        if (packageNames == null) return;

        Test test = new AnnotatedWith(annotation);
        for (String pkg : packageNames) {
            findInPackage(test, pkg);
        }
    }
    
    public void findNamedResource(String name, String... pathNames) {
        if (pathNames == null) return;
        
        Test test = new NameIs(name);
        for (String pkg : pathNames) {
            findInPackage(test, pkg);
        }
    }
    
    
    public void find(Test test, String... packageNames) {
        if (packageNames == null) return;

        for (String pkg : packageNames) {
            findInPackage(test, pkg);
        }
    }

    
    public void findInPackage(Test test, String packageName) {
        packageName = packageName.replace('.', '/');
        ClassLoader loader = getClassLoader();
        Enumeration<URL> urls;

        try {
            urls = loader.getResources(packageName);
        }
        catch (IOException ioe) {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("Could not read package: " + packageName, ioe);
            }
            return;
        }

        while (urls.hasMoreElements()) {
            try {
                String urlPath = urls.nextElement().getFile();
                urlPath = URLDecoder.decode(urlPath, "UTF-8");

                
                if ( urlPath.startsWith("file:") ) {
                    urlPath = urlPath.substring(5);
                }

                
                if (urlPath.indexOf('!') > 0) {
                    urlPath = urlPath.substring(0, urlPath.indexOf('!'));
                }

                if (LOG.isInfoEnabled()) {
                    LOG.info("Scanning for classes in [" + urlPath + "] matching criteria: " + test);
                }
                File file = new File(urlPath);
                if ( file.isDirectory() ) {
                    loadImplementationsInDirectory(test, packageName, file);
                }
                else {
                    loadImplementationsInJar(test, packageName, file);
                }
            }
            catch (IOException ioe) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("could not read entries", ioe);
                }
            }
        }
    }


    
    private void loadImplementationsInDirectory(Test test, String parent, File location) {
        File[] files = location.listFiles();
        StringBuilder builder = null;

        for (File file : files) {
            builder = new StringBuilder(100);
            builder.append(parent).append("/").append(file.getName());
            String packageOrClass = ( parent == null ? file.getName() : builder.toString() );

            if (file.isDirectory()) {
                loadImplementationsInDirectory(test, packageOrClass, file);
            }
            else if (isTestApplicable(test, file.getName())) {
                addIfMatching(test, packageOrClass);
            }
        }
    }

    private boolean isTestApplicable(Test test, String path) {
        return test.doesMatchResource() || path.endsWith(".class") && test.doesMatchClass();
    }

    
    private void loadImplementationsInJar(Test test, String parent, File jarfile) {

        try {
            JarEntry entry;
            JarInputStream jarStream = new JarInputStream(new FileInputStream(jarfile));

            while ( (entry = jarStream.getNextJarEntry() ) != null) {
                String name = entry.getName();
                if (!entry.isDirectory() && name.startsWith(parent) && isTestApplicable(test, name)) {
                    addIfMatching(test, name);
                }
            }
        }
        catch (IOException ioe) {
            LOG.error("Could not search jar file '" + jarfile + "' for classes matching criteria: " +
                      test + " due to an IOException", ioe);
        }
    }

    
    protected void addIfMatching(Test test, String fqn) {
        try {
            ClassLoader loader = getClassLoader();
            if (test.doesMatchClass()) {
                String externalName = fqn.substring(0, fqn.indexOf('.')).replace('/', '.');
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Checking to see if class " + externalName + " matches criteria [" + test + "]");
                }
    
                Class type = loader.loadClass(externalName);
                if (test.matches(type) ) {
                    classMatches.add( (Class<T>) type);
                }
            }
            if (test.doesMatchResource()) {
                URL url = loader.getResource(fqn);
                if (url == null) {
                    url = loader.getResource(fqn.substring(1));
                }
                if (url != null && test.matches(url)) {
                    resourceMatches.add(url);
                }
            }
        }
        catch (Throwable t) {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("Could not examine class '" + fqn + "' due to a " +
                     t.getClass().getName() + " with message: " + t.getMessage());
            }
        }
    }
}