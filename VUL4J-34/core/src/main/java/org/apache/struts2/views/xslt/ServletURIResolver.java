

package org.apache.struts2.views.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;



public class ServletURIResolver implements URIResolver {

    private Logger log = LogManager.getLogger(getClass());
    static final String PROTOCOL = "response:";

    private ServletContext sc;

    public ServletURIResolver(ServletContext sc) {
        log.trace("ServletURIResolver: {}", sc);
        this.sc = sc;
    }

    public Source resolve(String href, String base) throws TransformerException {
        log.debug("ServletURIResolver resolve(): href={}, base={}", href, base);
        if (href.startsWith(PROTOCOL)) {
            String res = href.substring(PROTOCOL.length());
            log.debug("Resolving resource <{}>", res);

            InputStream is = sc.getResourceAsStream(res);

            if (is == null) {
                throw new TransformerException(
                        "Resource " + res + " not found in resources.");
            }

            return new StreamSource(is);
        }

        throw new TransformerException(
                "Cannot handle protocol of resource " + href);
    }
}
