

package org.apache.struts2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



public class RequestUtils {

    private static final Logger LOG = LogManager.getLogger(RequestUtils.class);

    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private static final String FORMAT_PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String FORMAT_PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";
    private static final String FORMAT_PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";

    private static final FastDateFormat[] IF_MODIFIED_SINCE_FORMATS = {
            FastDateFormat.getInstance(FORMAT_PATTERN_RFC1123, GMT, Locale.US),
            FastDateFormat.getInstance(FORMAT_PATTERN_RFC1036, GMT, Locale.US),
            FastDateFormat.getInstance(FORMAT_PATTERN_ASCTIME, GMT, Locale.US)
    };

    
    public static String getServletPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        
        String requestUri = request.getRequestURI();
        
        if (requestUri != null && servletPath != null && !requestUri.endsWith(servletPath)) {
            int pos = requestUri.indexOf(servletPath);
            if (pos > -1) {
                servletPath = requestUri.substring(requestUri.indexOf(servletPath));
            }
        }

        if (StringUtils.isNotEmpty(servletPath)) {
            return servletPath;
        }
        
        int startIndex = request.getContextPath().equals("") ? 0 : request.getContextPath().length();
        int endIndex = request.getPathInfo() == null ? requestUri.length() : requestUri.lastIndexOf(request.getPathInfo());

        if (startIndex > endIndex) { 
            endIndex = startIndex;
        }

        return requestUri.substring(startIndex, endIndex);
    }

    
    public static String getUri(HttpServletRequest request) {
        
        String uri = (String) request.getAttribute("javax.servlet.include.servlet_path");
        if (uri != null) {
            return uri;
        }

        uri = getServletPath(request);
        if (StringUtils.isNotEmpty(uri)) {
            return uri;
        }

        uri = request.getRequestURI();
        return uri.substring(request.getContextPath().length());
    }

    
    public static Date parseIfModifiedSince(String headerValue) {
        for (FastDateFormat fastDateFormat : IF_MODIFIED_SINCE_FORMATS) {
            try {
                return fastDateFormat.parse(headerValue);
            } catch (ParseException ignore) {
                LOG.debug("Error parsing value [{}] as [{}]!", headerValue, fastDateFormat);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Error parsing value [{}] as date!", headerValue);
        }
        return null;
    }

}
