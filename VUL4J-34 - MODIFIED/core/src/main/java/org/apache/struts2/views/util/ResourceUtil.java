

package org.apache.struts2.views.util;

import org.apache.struts2.RequestUtils;
import javax.servlet.http.HttpServletRequest;

public class ResourceUtil {
    public static String getResourceBase(HttpServletRequest req) {
        String path = RequestUtils.getServletPath(req);
        if (path == null || "".equals(path)) {
            return "";
        }

        return path.substring(0, path.lastIndexOf('/'));
    }
}
