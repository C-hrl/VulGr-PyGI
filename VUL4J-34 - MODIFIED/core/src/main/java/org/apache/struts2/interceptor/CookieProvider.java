

package org.apache.struts2.interceptor;

import javax.servlet.http.Cookie;
import java.util.Set;


public interface CookieProvider {

    
    Set<Cookie> getCookies();

}