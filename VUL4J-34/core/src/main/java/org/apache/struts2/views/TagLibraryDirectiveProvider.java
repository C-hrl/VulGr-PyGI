

package org.apache.struts2.views;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;


public interface TagLibraryDirectiveProvider {

    
    public List<Class> getDirectiveClasses();

}
