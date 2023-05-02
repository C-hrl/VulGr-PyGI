

package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;


public class PlainTextResult extends StrutsResultSupport {

    public static final int BUFFER_SIZE = 1024;

    private static final Logger LOG = LogManager.getLogger(PlainTextResult.class);

    private static final long serialVersionUID = 3633371605905583950L;

    private String charSet;

    public PlainTextResult() {
        super();
    }

    public PlainTextResult(String location) {
        super(location);
    }

    
    public String getCharSet() {
        return charSet;
    }

    
    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        
        Charset charset = readCharset();

        HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);

        applyCharset(charset, response);
        applyAdditionalHeaders(response);
        String location = adjustLocation(finalLocation);

        try (PrintWriter writer = response.getWriter();
                InputStream resourceAsStream = readStream(invocation, location);
                InputStreamReader reader = new InputStreamReader(resourceAsStream, charset == null ? Charset.defaultCharset() : charset)) {
            logWrongStream(finalLocation, resourceAsStream);
            sendStream(writer, reader);
        }
    }

    protected InputStream readStream(ActionInvocation invocation, String location) {
        ServletContext servletContext = (ServletContext) invocation.getInvocationContext().get(SERVLET_CONTEXT);
        return servletContext.getResourceAsStream(location);
    }

    protected void logWrongStream(String finalLocation, InputStream resourceAsStream) {
        if (resourceAsStream == null) {
            LOG.warn("Resource at location [{}] cannot be obtained (return null) from ServletContext !!!", finalLocation);
        }
    }

    protected void sendStream(PrintWriter writer, InputStreamReader reader) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        int charRead;
        while((charRead = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, charRead);
        }
    }

    protected String adjustLocation(String location) {
        if (location.charAt(0) != '/') {
            return "/" + location;
        }
        return location;
    }

    protected void applyAdditionalHeaders(HttpServletResponse response) {
        response.setHeader("Content-Disposition", "inline");
    }

    protected void applyCharset(Charset charset, HttpServletResponse response) {
        if (charset != null) {
            response.setContentType("text/plain; charset=" + charSet);
        } else {
            response.setContentType("text/plain");
        }
    }

    protected Charset readCharset() {
        Charset charset = null;
        if (charSet != null) {
            if (Charset.isSupported(charSet)) {
                charset = Charset.forName(charSet);
            } else {
                LOG.warn("charset [{}] is not recognized", charset);
                charset = null;
            }
        }
        return charset;
    }
}
