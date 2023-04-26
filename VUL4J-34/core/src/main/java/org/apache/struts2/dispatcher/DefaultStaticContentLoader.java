
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;


public class DefaultStaticContentLoader implements StaticContentLoader {

    
    private Logger LOG = LogManager.getLogger(DefaultStaticContentLoader.class);

    
    protected List<String> pathPrefixes;

    
    protected boolean serveStatic;

    
    protected boolean serveStaticBrowserCache;

    
    protected final Calendar lastModifiedCal = Calendar.getInstance();

    
    protected String encoding;


    
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_CONTENT)
    public void setServeStaticContent(String serveStaticContent) {
        this.serveStatic = BooleanUtils.toBoolean(serveStaticContent);
    }

    
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE)
    public void setServeStaticBrowserCache(String serveStaticBrowserCache) {
        this.serveStaticBrowserCache = BooleanUtils.toBoolean(serveStaticBrowserCache);
    }

    
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    
    public void setHostConfig(HostConfig filterConfig) {
        String param = filterConfig.getInitParameter("packages");
        String packages = getAdditionalPackages();
        if (param != null) {
            packages = param + " " + packages;
        }
        this.pathPrefixes = parse(packages);
    }

    protected String getAdditionalPackages() {
        return "org.apache.struts2.static template org.apache.struts2.interceptor.debugging static";
    }

    
    protected List<String> parse(String packages) {
        if (packages == null) {
            return Collections.emptyList();
        }
        List<String> pathPrefixes = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(packages, ", \n\t");
        while (st.hasMoreTokens()) {
            String pathPrefix = st.nextToken().replace('.', '/');
            if (!pathPrefix.endsWith("/")) {
                pathPrefix += "/";
            }
            pathPrefixes.add(pathPrefix);
        }

        return pathPrefixes;
    }

    
    public void findStaticResource(String path, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String name = cleanupPath(path);
        for (String pathPrefix : pathPrefixes) {
            URL resourceUrl = findResource(buildPath(name, pathPrefix));
            if (resourceUrl != null) {
                InputStream is = null;
                try {
                    
                    String pathEnding = buildPath(name, pathPrefix);
                    if (resourceUrl.getFile().endsWith(pathEnding))
                        is = resourceUrl.openStream();
                } catch (IOException ex) {
                    
                    continue;
                }

                
                if (is != null) {
                    process(is, path, request, response);
                    return;
                }
            }
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    protected void process(InputStream is, String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (is != null) {
            Calendar cal = Calendar.getInstance();

            
            long ifModifiedSince = 0;
            try {
                ifModifiedSince = request.getDateHeader("If-Modified-Since");
            } catch (Exception e) {
                LOG.warn("Invalid If-Modified-Since header value: '{}', ignoring", request.getHeader("If-Modified-Since"));
            }
            long lastModifiedMillis = lastModifiedCal.getTimeInMillis();
            long now = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            long expires = cal.getTimeInMillis();

            if (ifModifiedSince > 0 && ifModifiedSince <= lastModifiedMillis) {
                
                
                response.setDateHeader("Expires", expires);
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                is.close();
                return;
            }

            
            String contentType = getContentType(path);
            if (contentType != null) {
                response.setContentType(contentType);
            }

            if (serveStaticBrowserCache) {
                
                response.setDateHeader("Date", now);
                response.setDateHeader("Expires", expires);
                response.setDateHeader("Retry-After", expires);
                response.setHeader("Cache-Control", "public");
                response.setDateHeader("Last-Modified", lastModifiedMillis);
            } else {
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "-1");
            }

            try {
                copy(is, response.getOutputStream());
            } finally {
                is.close();
            }
        }
    }

    
    protected URL findResource(String path) throws IOException {
        return ClassLoaderUtil.getResource(path, getClass());
    }

    
    protected String buildPath(String name, String packagePrefix) throws UnsupportedEncodingException {
        String resourcePath;
        if (packagePrefix.endsWith("/") && name.startsWith("/")) {
            resourcePath = packagePrefix + name.substring(1);
        } else {
            resourcePath = packagePrefix + name;
        }

        return URLDecoder.decode(resourcePath, encoding);
    }



    
    protected String getContentType(String name) {
        
        
        if (name.endsWith(".js")) {
            return "text/javascript";
        } else if (name.endsWith(".css")) {
            return "text/css";
        } else if (name.endsWith(".html")) {
            return "text/html";
        } else if (name.endsWith(".txt")) {
            return "text/plain";
        } else if (name.endsWith(".gif")) {
            return "image/gif";
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (name.endsWith(".png")) {
            return "image/png";
        } else {
            return null;
        }
    }

    
    protected void copy(InputStream input, OutputStream output) throws IOException {
        final byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.flush();
    }

    public boolean canHandle(String resourcePath) {
        return serveStatic && (resourcePath.startsWith("/struts/") || resourcePath.startsWith("/static/"));
    }

    
    protected String cleanupPath(String path) {
        
        return path.substring(7);
    }
}
