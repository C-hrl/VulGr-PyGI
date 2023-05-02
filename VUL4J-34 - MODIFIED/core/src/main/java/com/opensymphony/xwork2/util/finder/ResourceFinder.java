
package com.opensymphony.xwork2.util.finder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ResourceFinder {
    private static final Logger LOG = LogManager.getLogger(ResourceFinder.class);

    private final URL[] urls;
    private final String path;
    private final ClassLoaderInterface classLoaderInterface;
    private final List<String> resourcesNotLoaded = new ArrayList<>();

    public ResourceFinder(URL... urls) {
        this(null, new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()), urls);
    }

    public ResourceFinder(String path) {
        this(path, new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()), null);
    }

    public ResourceFinder(String path, URL... urls) {
        this(path, new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()), urls);
    }

    public ResourceFinder(String path, ClassLoaderInterface classLoaderInterface) {
        this(path, classLoaderInterface, null);
    }

    public ResourceFinder(String path, ClassLoaderInterface classLoaderInterface, URL... urls) {
        path = StringUtils.trimToEmpty(path);
        if (!StringUtils.endsWith(path, "/")) {
            path += "/";
        }
        this.path = path;

        this.classLoaderInterface = classLoaderInterface == null ? new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()) : classLoaderInterface ;

        for (int i = 0; urls != null && i < urls.length; i++) {
            URL url = urls[i];
            if (url == null || isDirectory(url) || "jar".equals(url.getProtocol())) {
                continue;
            }
            try {
                urls[i] = new URL("jar", "", -1, url.toString() + "!/");
            } catch (MalformedURLException e) {
            }
        }
        this.urls = (urls == null || urls.length == 0)? null : urls;
    }

    private static boolean isDirectory(URL url) {
        String file = url.getFile();
        return (file.length() > 0 && file.charAt(file.length() - 1) == '/');
    }

    
    public List<String> getResourcesNotLoaded() {
        return Collections.unmodifiableList(resourcesNotLoaded);
    }

    
    
    
    
    

    public URL find(String uri) throws IOException {
        String fullUri = path + uri;

        return getResource(fullUri);
    }

    public List<URL> findAll(String uri) throws IOException {
        String fullUri = path + uri;

        Enumeration<URL> resources = getResources(fullUri);
        List<URL> list = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            list.add(url);
        }
        return list;
    }


    
    
    
    
    

    
    public String findString(String uri) throws IOException {
        String fullUri = path + uri;

        URL resource = getResource(fullUri);
        if (resource == null) {
            throw new IOException("Could not find a resource in: " + fullUri);
        }

        return readContents(resource);
    }

    
    public List<String> findAllStrings(String uri) throws IOException {
        String fulluri = path + uri;

        List<String> strings = new ArrayList<>();

        Enumeration<URL> resources = getResources(fulluri);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String string = readContents(url);
            strings.add(string);
        }
        return strings;
    }

    
    public List<String> findAvailableStrings(String uri) throws IOException {
        resourcesNotLoaded.clear();
        String fulluri = path + uri;

        List<String> strings = new ArrayList<>();

        Enumeration<URL> resources = getResources(fulluri);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try {
                String string = readContents(url);
                strings.add(string);
            } catch (IOException notAvailable) {
                resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return strings;
    }

    
    public Map<String, String> mapAllStrings(String uri) throws IOException {
        Map<String, String> strings = new HashMap<>();
        Map<String, URL> resourcesMap = getResourcesMap(uri);
        for (Map.Entry<String, URL> entry : resourcesMap.entrySet()) {
            String name = entry.getKey();
            URL url = entry.getValue();
            String value = readContents(url);
            strings.put(name, value);
        }
        return strings;
    }

    
    public Map<String, String> mapAvailableStrings(String uri) throws IOException {
        resourcesNotLoaded.clear();
        Map<String, String> strings = new HashMap<>();
        Map<String, URL> resourcesMap = getResourcesMap(uri);
        for (Map.Entry<String, URL> entry  : resourcesMap.entrySet()) {
            String name = entry.getKey();
            URL url = entry.getValue();
            try {
                String value = readContents(url);
                strings.put(name, value);
            } catch (IOException notAvailable) {
                resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return strings;
    }

    
    
    
    
    

    
    public Class findClass(String uri) throws IOException, ClassNotFoundException {
        String className = findString(uri);
        return (Class) classLoaderInterface.loadClass(className);
    }

    
    public List<Class> findAllClasses(String uri) throws IOException, ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        List<String> strings = findAllStrings(uri);
        for (String className : strings) {
            Class clazz = classLoaderInterface.loadClass(className);
            classes.add(clazz);
        }
        return classes;
    }

    
    public List<Class> findAvailableClasses(String uri) throws IOException {
        resourcesNotLoaded.clear();
        List<Class> classes = new ArrayList<>();
        List<String> strings = findAvailableStrings(uri);
        for (String className : strings) {
            try {
                Class clazz = classLoaderInterface.loadClass(className);
                classes.add(clazz);
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(className);
            }
        }
        return classes;
    }

    
    public Map<String, Class> mapAllClasses(String uri) throws IOException, ClassNotFoundException {
        Map<String, Class> classes = new HashMap<>();
        Map<String, String> map = mapAllStrings(uri);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String className = entry.getValue();
            Class clazz = classLoaderInterface.loadClass(className);
            classes.put(string, clazz);
        }
        return classes;
    }

    
    public Map<String, Class> mapAvailableClasses(String uri) throws IOException {
        resourcesNotLoaded.clear();
        Map<String, Class> classes = new HashMap<>();
        Map<String, String> map = mapAvailableStrings(uri);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String className = entry.getValue();
            try {
                Class clazz = classLoaderInterface.loadClass(className);
                classes.put(string, clazz);
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(className);
            }
        }
        return classes;
    }

    
    
    
    
    

    
    public Class findImplementation(Class interfase) throws IOException, ClassNotFoundException {
        String className = findString(interfase.getName());
        Class impl = classLoaderInterface.loadClass(className);
        if (!interfase.isAssignableFrom(impl)) {
            throw new ClassCastException("Class not of type: " + interfase.getName());
        }
        return impl;
    }

    
    public List<Class> findAllImplementations(Class interfase) throws IOException, ClassNotFoundException {
        List<Class> implementations = new ArrayList<>();
        List<String> strings = findAllStrings(interfase.getName());
        for (String className : strings) {
            Class impl = classLoaderInterface.loadClass(className);
            if (!interfase.isAssignableFrom(impl)) {
                throw new ClassCastException("Class not of type: " + interfase.getName());
            }
            implementations.add(impl);
        }
        return implementations;
    }

    
    public List<Class> findAvailableImplementations(Class interfase) throws IOException {
        resourcesNotLoaded.clear();
        List<Class> implementations = new ArrayList<>();
        List<String> strings = findAvailableStrings(interfase.getName());
        for (String className : strings) {
            try {
                Class impl = classLoaderInterface.loadClass(className);
                if (interfase.isAssignableFrom(impl)) {
                    implementations.add(impl);
                } else {
                    resourcesNotLoaded.add(className);
                }
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(className);
            }
        }
        return implementations;
    }

    
    public Map<String, Class> mapAllImplementations(Class interfase) throws IOException, ClassNotFoundException {
        Map<String, Class> implementations = new HashMap<>();
        Map<String, String> map = mapAllStrings(interfase.getName());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String className = entry.getValue();
            Class impl = classLoaderInterface.loadClass(className);
            if (!interfase.isAssignableFrom(impl)) {
                throw new ClassCastException("Class not of type: " + interfase.getName());
            }
            implementations.put(string, impl);
        }
        return implementations;
    }

    
    public Map<String, Class> mapAvailableImplementations(Class interfase) throws IOException {
        resourcesNotLoaded.clear();
        Map<String, Class> implementations = new HashMap<>();
        Map<String, String> map = mapAvailableStrings(interfase.getName());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String className = entry.getValue();
            try {
                Class impl = classLoaderInterface.loadClass(className);
                if (interfase.isAssignableFrom(impl)) {
                    implementations.put(string, impl);
                } else {
                    resourcesNotLoaded.add(className);
                }
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(className);
            }
        }
        return implementations;
    }

    
    
    
    
    

    
    public Properties findProperties(String uri) throws IOException {
        String fulluri = path + uri;

        URL resource = getResource(fulluri);
        if (resource == null) {
            throw new IOException("Could not find command in : " + fulluri);
        }

        return loadProperties(resource);
    }

    
    public List<Properties> findAllProperties(String uri) throws IOException {
        String fulluri = path + uri;

        List<Properties> properties = new ArrayList<>();

        Enumeration<URL> resources = getResources(fulluri);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            Properties props = loadProperties(url);
            properties.add(props);
        }
        return properties;
    }

    
    public List<Properties> findAvailableProperties(String uri) throws IOException {
        resourcesNotLoaded.clear();
        String fulluri = path + uri;

        List<Properties> properties = new ArrayList<>();

        Enumeration<URL> resources = getResources(fulluri);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try {
                Properties props = loadProperties(url);
                properties.add(props);
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return properties;
    }

    
    public Map<String, Properties> mapAllProperties(String uri) throws IOException {
        Map<String, Properties> propertiesMap = new HashMap<>();
        Map<String, URL> map = getResourcesMap(uri);
        for (Map.Entry<String, URL> entry : map.entrySet()) {
            String string = entry.getKey();
            URL url = entry.getValue();
            Properties properties = loadProperties(url);
            propertiesMap.put(string, properties);
        }
        return propertiesMap;
    }

    
    public Map<String, Properties> mapAvailableProperties(String uri) throws IOException {
        resourcesNotLoaded.clear();
        Map<String, Properties> propertiesMap = new HashMap<>();
        Map<String, URL> map = getResourcesMap(uri);
        for (Map.Entry<String, URL> entry : map.entrySet()) {
            String string = entry.getKey();
            URL url = entry.getValue();
            try {
                Properties properties = loadProperties(url);
                propertiesMap.put(string, properties);
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return propertiesMap;
    }

    
    
    
    
    

    public Map<String, URL> getResourcesMap(String uri) throws IOException {
        String basePath = path + uri;

        Map<String, URL> resources = new HashMap<>();
        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        Enumeration<URL> urls = getResources(basePath);

        while (urls.hasMoreElements()) {
            URL location = urls.nextElement();

            try {
                if ("jar".equals(location.getProtocol())) {
                    readJarEntries(location, basePath, resources);
                } else if ("file".equals(location.getProtocol())) {
                    readDirectoryEntries(location, resources);
                }
            } catch (Exception e) {
                LOG.debug("Got exception loading resources for {}", uri, e);
            }
        }

        return resources;
    }

    
    public Set<String> findPackages(String uri) throws IOException {
        String basePath = path + uri;

        Set<String> resources = new HashSet<>();
        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        Enumeration<URL> urls = getResources(basePath);

        while (urls.hasMoreElements()) {
            URL location = urls.nextElement();

            try {
                if ("jar".equals(location.getProtocol())) {
                    readJarDirectoryEntries(location, basePath, resources);
                } else if ("file".equals(location.getProtocol())) {
                    readSubDirectories(new File(location.toURI()), uri, resources);
                }
            } catch (Exception e) {
                LOG.debug("Got exception search for subpackages for {}", uri, e);
            }
        }

        return convertPathsToPackages(resources);
    }

    
    public Map<URL, Set<String>> findPackagesMap(String uri) throws IOException {
        String basePath = path + uri;

        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        Enumeration<URL> urls = getResources(basePath);
        Map<URL, Set<String>> result = new HashMap<>();

        while (urls.hasMoreElements()) {
            URL location = urls.nextElement();

            try {
                if ("jar".equals(location.getProtocol())) {
                    Set<String> resources = new HashSet<>();
                    readJarDirectoryEntries(location, basePath, resources);
                    result.put(location, convertPathsToPackages(resources));
                } else if ("file".equals(location.getProtocol())) {
                    Set<String> resources = new HashSet<>();
                    readSubDirectories(new File(location.toURI()), uri, resources);
                    result.put(location, convertPathsToPackages(resources));
                }
            } catch (Exception e) {
                LOG.debug("Got exception finding subpackages for {}", uri, e);
            }
        }

        return result;
    }

    private Set<String> convertPathsToPackages(Set<String> resources) {
        Set<String> packageNames = new HashSet<>(resources.size());
        for(String resource : resources) {
            packageNames.add(StringUtils.removeEnd(StringUtils.replace(resource, "/", "."), "."));
        }

        return packageNames;
    }

    private static void readDirectoryEntries(URL location, Map<String, URL> resources) throws MalformedURLException {
        File dir = new File(URLDecoder.decode(location.getPath()));
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    String name = file.getName();
                    URL url = file.toURL();
                    resources.put(name, url);
                }
            }
        }
    }

    
     private static void readSubDirectories(File dir, String basePath, Set<String> resources) throws MalformedURLException {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    String name = file.getName();
                    String subName = StringUtils.removeEnd(basePath, "/") + "/" + name;
                    resources.add(subName);
                    readSubDirectories(file, subName, resources);
                }
            }
        }
    }

    private static void readJarEntries(URL location, String basePath, Map<String, URL> resources) throws IOException {
        JarURLConnection conn = (JarURLConnection) location.openConnection();
        JarFile jarfile;
        jarfile = conn.getJarFile();

        Enumeration<JarEntry> entries = jarfile.entries();
        while (entries != null && entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (entry.isDirectory() || !name.startsWith(basePath) || name.length() == basePath.length()) {
                continue;
            }

            name = name.substring(basePath.length());

            if (name.contains("/")) {
                continue;
            }

            URL resource = new URL(location, name);
            resources.put(name, resource);
        }
    }

    
    private static void readJarDirectoryEntries(URL location, String basePath, Set<String> resources) throws IOException {
        JarURLConnection conn = (JarURLConnection) location.openConnection();
        JarFile jarfile;
        jarfile = conn.getJarFile();

        Enumeration<JarEntry> entries = jarfile.entries();
        while (entries != null && entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (entry.isDirectory() && StringUtils.startsWith(name, basePath)) {
                resources.add(name);
            }
        }
    }

    private Properties loadProperties(URL resource) throws IOException {
        try (InputStream reader = new BufferedInputStream(resource.openStream())) {
            Properties properties = new Properties();
            properties.load(reader);

            return properties;
        }
    }

    private String readContents(URL resource) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (InputStream reader = new BufferedInputStream(resource.openStream())) {
            int b = reader.read();
            while (b != -1) {
                sb.append((char) b);
                b = reader.read();
            }

            return sb.toString().trim();
        }
    }

    private URL getResource(String fullUri) {
        if (urls == null){
            return classLoaderInterface.getResource(fullUri);
        }
        return findResource(fullUri, urls);
    }

    private Enumeration<URL> getResources(String fulluri) throws IOException {
        if (urls == null) {
            return classLoaderInterface.getResources(fulluri);
        }
        Vector<URL> resources = new Vector<>();
        for (URL url : urls) {
            URL resource = findResource(fulluri, url);
            if (resource != null){
                resources.add(resource);
            }
        }
        return resources.elements();
    }

    private URL findResource(String resourceName, URL... search) {
        for (int i = 0; i < search.length; i++) {
            URL currentUrl = search[i];
            if (currentUrl == null) {
                continue;
            }
            JarFile jarFile;
            try {
                String protocol = currentUrl.getProtocol();
                if ("jar".equals(protocol)) {
                    
                    URL jarURL = ((JarURLConnection) currentUrl.openConnection()).getJarFileURL();
                    try {
                        JarURLConnection juc = (JarURLConnection) new URL("jar", "", jarURL.toExternalForm() + "!/").openConnection();
                        jarFile = juc.getJarFile();
                    } catch (IOException e) {
                        
                        search[i] = null;
                        throw e;
                    }

                    String entryName;
                    if (currentUrl.getFile().endsWith("!/")) {
                        entryName = resourceName;
                    } else {
                        String file = currentUrl.getFile();
                        int sepIdx = file.lastIndexOf("!/");
                        if (sepIdx == -1) {
                            
                            search[i] = null;
                            continue;
                        }
                        sepIdx += 2;
                        StringBuilder sb = new StringBuilder(file.length() - sepIdx + resourceName.length());
                        sb.append(file.substring(sepIdx));
                        sb.append(resourceName);
                        entryName = sb.toString();
                    }
                    if ("META-INF/".equals(entryName) && jarFile.getEntry("META-INF/MANIFEST.MF") != null){
                        return targetURL(currentUrl, "META-INF/MANIFEST.MF");
                    }
                    if (jarFile.getEntry(entryName) != null) {
                        return targetURL(currentUrl, resourceName);
                    }
                } else if ("file".equals(protocol)) {
                    String baseFile = currentUrl.getFile();
                    String host = currentUrl.getHost();
                    int hostLength = 0;
                    if (host != null) {
                        hostLength = host.length();
                    }
                    StringBuilder buf = new StringBuilder(2 + hostLength + baseFile.length() + resourceName.length());

                    if (hostLength > 0) {
                        buf.append("
                    }
                    
                    buf.append(baseFile);
                    String fixedResName = resourceName;
                    
                    while (fixedResName.startsWith("/") || fixedResName.startsWith("\\")) {
                        fixedResName = fixedResName.substring(1);
                    }
                    buf.append(fixedResName);
                    String filename = buf.toString();
                    File file = new File(filename);
                    File file2 = new File(URLDecoder.decode(filename));

                    if (file.exists() || file2.exists()) {
                        return targetURL(currentUrl, fixedResName);
                    }
                } else {
                    URL resourceURL = targetURL(currentUrl, resourceName);
                    URLConnection urlConnection = resourceURL.openConnection();

                    try {
                        urlConnection.getInputStream().close();
                    } catch (SecurityException e) {
                        return null;
                    }
                    
                    
                    if (!"http".equals(resourceURL.getProtocol())) {
                        return resourceURL;
                    }

                    int code = ((HttpURLConnection) urlConnection).getResponseCode();
                    if (code >= 200 && code < 300) {
                        return resourceURL;
                    }
                }
            } catch (IOException | SecurityException e) {
                
            }
        }
        return null;
    }

    private URL targetURL(URL base, String name) throws MalformedURLException {
        StringBuilder sb = new StringBuilder(base.getFile().length() + name.length());
        sb.append(base.getFile());
        sb.append(name);
        String file = sb.toString();
        return new URL(base.getProtocol(), base.getHost(), base.getPort(), file, null);
    }
}
