

package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.util.FastByteArrayOutputStream;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;


@StrutsTag(name="include", tldTagClass="org.apache.struts2.views.jsp.IncludeTag", description="Include a servlet's output " +
                "(result of servlet or a JSP page)")
public class Include extends Component {

    private static final Logger LOG = LogManager.getLogger(Include.class);

    private static String systemEncoding = System.getProperty("file.encoding");

    protected String value;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private static String defaultEncoding;

    public Include(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
    }

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String encoding) {
        defaultEncoding = encoding;
    }

    public boolean end(Writer writer, String body) {
        String page = findString(value, "value", "You must specify the URL to include. Example: /foo.jsp");
        StringBuilder urlBuf = new StringBuilder();

        
        urlBuf.append(page);

        
        if (parameters.size() > 0) {
            urlBuf.append('?');

            String concat = "";

            
            for (Object next : parameters.entrySet()) {
                Map.Entry entry = (Map.Entry) next;
                Object name = entry.getKey();
                List values = (List) entry.getValue();

                for (Object value : values) {
                    urlBuf.append(concat);
                    urlBuf.append(name);
                    urlBuf.append('=');

                    try {
                        urlBuf.append(URLEncoder.encode(value.toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        LOG.warn("Unable to url-encode {}, it will be ignored", value);
                    }

                    concat = "&";
                }
            }
        }

        String result = urlBuf.toString();

        
        try {
            include(result, writer, req, res, defaultEncoding);
        } catch (ServletException | IOException e) {
            LOG.warn("Exception thrown during include of {}", result, e);
        }

        return super.end(writer, body);
    }

    @StrutsTagAttribute(description="The jsp/servlet output to include", required=true)
    public void setValue(String value) {
        this.value = value;
    }

    public static String getContextRelativePath(ServletRequest request, String relativePath) {
        String returnValue;

        if (relativePath.startsWith("/")) {
            returnValue = relativePath;
        } else if (!(request instanceof HttpServletRequest)) {
            returnValue = relativePath;
        } else {
            HttpServletRequest hrequest = (HttpServletRequest) request;
            String uri = (String) request.getAttribute("javax.servlet.include.servlet_path");

            if (uri == null) {
                uri = RequestUtils.getServletPath(hrequest);
            }

            returnValue = uri.substring(0, uri.lastIndexOf('/')) + '/' + relativePath;
        }

        
        
        if (returnValue.contains("..")) {
            Stack stack = new Stack();
            StringTokenizer pathParts = new StringTokenizer(returnValue.replace('\\', '/'), "/");

            while (pathParts.hasMoreTokens()) {
                String part = pathParts.nextToken();

                if (!part.equals(".")) {
                    if (part.equals("..")) {
                        stack.pop();
                    } else {
                        stack.push(part);
                    }
                }
            }

            StringBuilder flatPathBuffer = new StringBuilder();

            for (int i = 0; i < stack.size(); i++) {
                flatPathBuffer.append("/").append(stack.elementAt(i));
            }

            returnValue = flatPathBuffer.toString();
        }

        return returnValue;
    }

    public void addParameter(String key, Object value) {
        
        
        
        if (value != null) {
            List currentValues = (List) parameters.get(key);

            if (currentValues == null) {
                currentValues = new ArrayList();
                parameters.put(key, currentValues);
            }

            currentValues.add(value);
        }
    }

    
    public static void include( String relativePath, Writer writer, ServletRequest request,
                                HttpServletResponse response, String encoding ) throws ServletException, IOException {
        String resourcePath = getContextRelativePath(request, relativePath);
        RequestDispatcher rd = request.getRequestDispatcher(resourcePath);

        if (rd == null) {
            throw new ServletException("Not a valid resource path:" + resourcePath);
        }

        PageResponse pageResponse = new PageResponse(response);

        
        rd.include(request, pageResponse);

        if (encoding != null) {
            
            pageResponse.getContent().writeTo(writer, encoding);
        } else {
            
            pageResponse.getContent().writeTo(writer, systemEncoding);
        }
    }

    
    static final class PageOutputStream extends ServletOutputStream {

        private FastByteArrayOutputStream buffer;


        public PageOutputStream() {
            buffer = new FastByteArrayOutputStream();
        }


        
        public FastByteArrayOutputStream getBuffer() throws IOException {
            flush();

            return buffer;
        }

        public void close() throws IOException {
            buffer.close();
        }

        public void flush() throws IOException {
            buffer.flush();
        }

        public void write(byte[] b, int o, int l) throws IOException {
            buffer.write(b, o, l);
        }

        public void write(int i) throws IOException {
            buffer.write(i);
        }

        public void write(byte[] b) throws IOException {
            buffer.write(b);
        }
    }


    
    static final class PageResponse extends HttpServletResponseWrapper {

        protected PrintWriter pagePrintWriter;
        protected ServletOutputStream outputStream;
        private PageOutputStream pageOutputStream = null;


        
        public PageResponse(HttpServletResponse response) {
            super(response);
        }


        
        public FastByteArrayOutputStream getContent() throws IOException {
            
            
            
            if (pagePrintWriter != null) {
                pagePrintWriter.flush();
            }

            return ((PageOutputStream) getOutputStream()).getBuffer();
        }

        
        public ServletOutputStream getOutputStream() throws IOException {
            if (pageOutputStream == null) {
                pageOutputStream = new PageOutputStream();
            }

            return pageOutputStream;
        }

        
        public PrintWriter getWriter() throws IOException {
            if (pagePrintWriter == null) {
                pagePrintWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
            }

            return pagePrintWriter;
        }
    }
}
